package com.taotao.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.taotao.common.Utils.FtpUtil;
import com.taotao.common.Utils.IDUtils;
import com.taotao.service.PictureService;
/**
 * 图片上传服务
 * @author cs
 *
 */
@Service
public class PictureServiceImpl implements PictureService{

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
	@Value("${IMAGE_BASE_URL}")
	private String IMAGE_BASE_URL;
	
	@Override
	public Map uploadPic(MultipartFile uploadFile) {
		Map resultMap = new HashMap();
		try {
			//生成新的文件名
			//取出源文件的文件名
			String oldName = uploadFile.getOriginalFilename();
			String newName = IDUtils.genImageName();
			newName = newName+oldName.substring(oldName.lastIndexOf("."));
			//上传图片
			//读取配置文件
			String imagePath = new DateTime().toString("/yyyy/MM/dd");
			FtpUtil.uploadFile(FTP_ADDRESS, FTP_PORT, FTP_USERNAME, FTP_PASSWORD, FTP_BASEPATH, 
					imagePath, newName, uploadFile.getInputStream());
			//返回结果
			resultMap.put("error", 0);
			resultMap.put("url",IMAGE_BASE_URL+imagePath+"/"+newName);
		} catch (IOException e) {
			resultMap.put("error", 1);
			resultMap.put("message", "文件上传失败");
			e.printStackTrace();
		}finally{
			return resultMap;
		}
	}
}
