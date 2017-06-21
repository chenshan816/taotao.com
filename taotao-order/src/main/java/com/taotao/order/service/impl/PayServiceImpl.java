package com.taotao.order.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.taotao.common.Utils.FtpUtil;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbPayInfoMapper;
import com.taotao.order.constants.Const;
import com.taotao.order.service.PayService;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderExample;
import com.taotao.pojo.TbOrderExample.Criteria;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderItemExample;
import com.taotao.pojo.TbPayInfo;

@Service
public class PayServiceImpl implements PayService {

	@Autowired
	private TbPayInfoMapper payInfoMapper;
	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Value("${ALIPAY_CALLBACK_URL}")
	private String ALIPAY_CALLBACK_URL;
	@Value("${FTP_ADDRESS}")
	private String FTP_ADDRESS;
	@Value("${FTP_PORT}")
	private int FTP_PORT;
	@Value("${FTP_USERNAME}")
	private String FTP_USERNAME;
	@Value("${FTP_PASSWORD}")
	private String FTP_PASSWORD;
	@Value("${FTP_BASEPATH}")
	private String FTP_BASEPATH;
	@Value("${QR_BASE_URL}")
	private String QR_BASE_URL;

	public TaotaoResult pay(String orderNo,String path) {
		Map<String, String> resultMap = new HashMap<String, String>();

		// 判断支付账单是否存在
		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andOrderIdEqualTo(orderNo);
		List<TbOrder> orderList = orderMapper.selectByExample(example);
		if (orderList == null || orderList.size() <= 0) {
			return TaotaoResult.build(400, "无当前订单");
		}
		TbOrder order = orderList.get(0);
		resultMap.put("orderNo", order.getOrderId());
		// 组装生成支付宝订单
		return test_trade_precreate(order, resultMap,path);
	}

	// 调用支付宝的方法
	private TaotaoResult test_trade_precreate(TbOrder order,Map<String,String> resultMap,String path) {
		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
		// 需保证商户系统端不能重复，建议通过数据库sequence生成，
		String outTradeNo = order.getOrderId();

		// (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
		String subject = new StringBuilder().append("淘淘商城扫码支付，订单号：")
				.append(order.getOrderId()).toString();

		// (必填) 订单总金额，单位为元，不能超过1亿元
		// 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
		String totalAmount = order.getPayment();

		// (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
		// 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
		String undiscountableAmount = "0";

		// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
		// 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
		String sellerId = "";

		// 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
		String body = new StringBuilder().append("订单").append(outTradeNo)
				.append("购买商品共").append(totalAmount).toString();

		// 商户操作员编号，添加此参数可以为商户操作员做销售统计
		String operatorId = "test_operator_id";

		// (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		String storeId = "test_store_id";

		// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId("2088100200300400500");

		// 支付超时，定义为120分钟
		String timeoutExpress = "120m";

		// 商品明细列表，需填写购买商品详细信息，
		List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
		// 获取商品信息
		TbOrderItemExample example = new TbOrderItemExample();
		com.taotao.pojo.TbOrderItemExample.Criteria criteria = example
				.createCriteria();
		criteria.andOrderIdEqualTo(order.getOrderId());
		List<TbOrderItem> orderItemList = orderItemMapper
				.selectByExample(example);
		for (TbOrderItem orderItem : orderItemList) {
			GoodsDetail goods = GoodsDetail.newInstance(orderItem.getItemId(),
					orderItem.getTitle(), orderItem.getPrice(),
					orderItem.getNum());
			goodsDetailList.add(goods);
		}

		// 创建扫码支付请求builder，设置请求参数
		AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
				.setSubject(subject).setTotalAmount(totalAmount)
				.setOutTradeNo(outTradeNo)
				.setUndiscountableAmount(undiscountableAmount)
				.setSellerId(sellerId).setBody(body).setOperatorId(operatorId)
				.setStoreId(storeId).setExtendParams(extendParams)
				.setTimeoutExpress(timeoutExpress)
				.setNotifyUrl(ALIPAY_CALLBACK_URL)// 支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
				.setGoodsDetailList(goodsDetailList);

		/**
		 * 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
		 * Configs会读取classpath下的zfbinfo
		 * .properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
		 */
		Configs.init("zfbinfo.properties");
		/**
		 * 使用Configs提供的默认参数 AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
		 */
		AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder()
				.build();

		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
		switch (result.getTradeStatus()) {
		case SUCCESS:
			System.out.println("支付宝预下单成功: )");

			AlipayTradePrecreateResponse response = result.getResponse();
			dumpResponse(response);
			String date = new DateTime().toString("/yyyy/MM/dd");
	
			File folder = new File(path);
            if(!folder.exists()){
                folder.setWritable(true);
                folder.mkdirs();
            }

			// 需要修改为运行机器上的路径
			String qrPath = String.format(path + "/qr-%s.png",
					response.getOutTradeNo());
			String qrFileName = String.format("qr-%s.png",
					response.getOutTradeNo());
			ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

			File targetFile = new File(path, qrFileName);
			try {
				FtpUtil.uploadFile(FTP_ADDRESS, FTP_PORT, FTP_USERNAME,
						FTP_PASSWORD, FTP_BASEPATH, date, qrFileName,
						new FileInputStream(targetFile));
			} catch (Exception e) {
				e.printStackTrace();
			}

			String qrUrl = QR_BASE_URL + date+"/"+qrFileName;
			// 获取路径
			resultMap.put("qrUrl", qrUrl);
			return TaotaoResult.ok(resultMap);

		case FAILED:
			System.out.println("支付宝预下单失败!!!");
			return TaotaoResult.build(400, "支付宝预下单失败!!!");

		case UNKNOWN:
			System.out.println("系统异常，预下单状态未知!!!");
			return TaotaoResult.build(400, "系统异常，预下单状态未知!!!");

		default:
			System.out.println("不支持的交易状态，交易返回异常!!!");
			return TaotaoResult.build(400, "不支持的交易状态，交易返回异常!!!");
		}
	}

	// 简单打印应答
	private void dumpResponse(AlipayResponse response) {
		if (response != null) {
			System.out.println(String.format("code:%s, msg:%s",
					response.getCode(), response.getMsg()));
			if (StringUtils.isNotEmpty(response.getSubCode())) {
				System.out.println(String.format("subCode:%s, subMsg:%s",
						response.getSubCode(), response.getSubMsg()));
			}
			System.out.println("body:" + response.getBody());
		}
	}
	
	public TaotaoResult aliCallback(Map<String,String> params){
		String orderNo = params.get("out_trade_no");
		String tradeNo = params.get("trade_no");
		String tradeStatus = params.get("trade_status");
		//查询该订单是否存在
		TbOrder order = orderMapper.selectByPrimaryKey(orderNo);
		if(order == null){
			return TaotaoResult.build(400, "无此状态，回调忽略");
		}
		//判断回调状态
		if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
			return TaotaoResult.ok("支付宝重复调用");
		}
		//判断交易状态
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
		if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
			try {
				order.setPaymentTime(sdf.parse(params.get("gmt_payment")));
			} catch (Exception e) {
				e.printStackTrace();
			}
			order.setStatus(Const.OrderStatusEnum.PAID.getCode());
			orderMapper.updateByPrimaryKey(order);
		}
		
		//组装payInfo
		TbPayInfo payInfo = new TbPayInfo();
		payInfo.setOrderNo(order.getOrderId());
		payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
		payInfo.setPlatformNumber(tradeNo);
		payInfo.setPlatformStatus(tradeStatus);
		
		payInfoMapper.insert(payInfo);
		return TaotaoResult.ok();
	}
	
	
	public TaotaoResult queryOrderPayStatus(String orderNo){
		//查询订单是否存在
		TbOrder order = orderMapper.selectByPrimaryKey(orderNo);
		if(order == null){
			return TaotaoResult.build(400,"没有该订单");
		}
		if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
			return TaotaoResult.ok();
		}
		return TaotaoResult.build(400,"订单未支付");
	}
}
