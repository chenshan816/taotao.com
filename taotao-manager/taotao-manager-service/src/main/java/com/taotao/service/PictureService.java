package com.taotao.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface PictureService {
	
	Map uploadPic(MultipartFile uploadFile);
}
