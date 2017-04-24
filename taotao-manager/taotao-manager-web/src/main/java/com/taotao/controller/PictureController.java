package com.taotao.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.taotao.common.Utils.JsonUtils;
import com.taotao.service.PictureService;

/**
 * 上传图片处理
 * @author cs
 *
 */
@Controller
public class PictureController {
	@Autowired
	private PictureService pictureService;
	
	@RequestMapping("/pic/upload")
	@ResponseBody
	public String PictureUpload(MultipartFile uploadFile){
		
		Map result = pictureService.uploadPic(uploadFile);
		//保证数据兼容性，将result转换为json格式的字符串
		return JsonUtils.objectToJson(result);
	}
	
}
