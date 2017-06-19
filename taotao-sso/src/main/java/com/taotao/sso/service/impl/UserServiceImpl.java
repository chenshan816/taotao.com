package com.taotao.sso.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.Utils.CookieUtils;
import com.taotao.common.Utils.ExceptionUtil;
import com.taotao.common.Utils.JsonUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.CartInfo;
import com.taotao.pojo.TbCart;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.dao.JedisClient;
import com.taotao.sso.service.UserService;

/**
 * 单点登录用户controller
 * @author cs
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${REDIS_USER_SESSION_KEY}")
	private String REDIS_USER_SESSION_KEY;
	@Value("${SSO_SESSION_EXPIRE}")
	private Integer SSO_SESSION_EXPIRE;
	
	//activeMq对象
	@Autowired
	private JmsTemplate jmsTemplate;
	
	//校验数据的常量
	private int CHECK_USERNAME = 1;
	private int CHECK_PHONE = 2;
	private int CHECK_EMAIL = 3;
	
	/**
	 * 检查数据是否合法
	 */
	@Override
	public TaotaoResult checkData(String content, int type) {
		//创建查询条件
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//对校验数据进行判断
		if(CHECK_USERNAME == type){
			criteria.andUsernameEqualTo(content);
		}else if(CHECK_PHONE == type){
			criteria.andPhoneEqualTo(content);
		}else if(CHECK_EMAIL == type){
			criteria.andEmailEqualTo(content);
		}else{
			return TaotaoResult.build(400, "校验类型不正确");
		}
		List<TbUser> list = userMapper.selectByExample(example);
		if(list == null || list.size()==0){
			return TaotaoResult.ok(true);
		}
		return TaotaoResult.ok(false);
	}
	
	/**
	 * 用户注册
	 */
	@Override
	public TaotaoResult createUser(TbUser user) {
		//补全对象
		user.setCreated(new Date());
		user.setUpdated(new Date());
		//密码加密保存
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		userMapper.insert(user);
		return TaotaoResult.ok();
	}
	
	/**
	 * 用户登录
	 */
	@Override
	public TaotaoResult userLogin(String username, String password,
			HttpServletRequest request,HttpServletResponse response) {
		//查询用户信息
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = userMapper.selectByExample(example);
		//如果用户名不存在
		if(list == null || list.size() == 0){
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())){
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		//生成token令牌
		String token = UUID.randomUUID().toString();
		//将用户信息写入redis
		//防止密码泄漏
		user.setPassword(null);
		jedisClient.set(REDIS_USER_SESSION_KEY+":"+token,JsonUtils.objectToJson(user));
		//设置过期时间
		jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token, SSO_SESSION_EXPIRE);
		//在cookie中添加令牌
		CookieUtils.setCookie(request, response, "TT_TOKEN", token);
		//在activeMQ中添加，保存购物车至数据库
		//1.获取cookie中的购物车信息
		List<TbCart> cartItemList = getCartItemList(request);
		CartInfo cartInfo = new CartInfo(user, cartItemList);
		sendToActiveMq(cartInfo);
		//返回token
		return TaotaoResult.ok(token);
	}

	
	/**
	 * 根据令牌获取用户信息 
	 */
	@Override
	public TaotaoResult getUserInfoByToken(String token) {
		String json = jedisClient.get(REDIS_USER_SESSION_KEY+":"+token);
		if(!StringUtils.isBlank(json)){
			//将json转换为java对象
			TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
			//重新设置过期时间
			jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token, SSO_SESSION_EXPIRE);
			return TaotaoResult.ok(user);
		}
		//令牌不存在或者已经过期
		return TaotaoResult.build(500, "sesion过期，请重新登录");
	}
	
	/**
	 * 用户安全退出
	 */
	@Override
	public TaotaoResult userLogout(String token) {
		jedisClient.del(REDIS_USER_SESSION_KEY+":"+token);
		return TaotaoResult.ok();
	}
	
	/**
	 * 使用ActiveMQ更新
	 */
	public void sendToActiveMq(final CartInfo cartInfo){
		try{
			jmsTemplate.send(new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(cartInfo);
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取cookie中购物车的商品列表
	 */
	private List<TbCart> getCartItemList(HttpServletRequest request) {
		String cartJson = CookieUtils.getCookieValue(request, "TT_CART", true);
		List<TbCart> list = null;
		if (StringUtils.isBlank(cartJson)) {
			list = new ArrayList<TbCart>();
		} else {
			// 将json数据转换为列表
			list = JsonUtils.jsonToList(cartJson, TbCart.class);
		}
		return list;
	}

}
