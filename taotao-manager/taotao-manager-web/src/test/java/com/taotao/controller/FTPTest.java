package com.taotao.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;

public class FTPTest {
	
	@Test
	public void testFtpClient() throws Exception{
		//创建一个FtpClient对象
		FTPClient ftpClient = new FTPClient();
		//创建ftp连接
		ftpClient.connect("192.168.27.130",21);
		//登录FTP服务器。使用用户名和密码
		ftpClient.login("ftpuser", "ftpuser");
		//上传文件
		//参数： 服务器端文档名  上传文档的inputStream
		FileInputStream in = new FileInputStream(new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\1.jpg"));
		//保存路径
		ftpClient.changeWorkingDirectory("/home/ftpuser/www/images");
		//改变上传文件类型(默认为txt)
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.storeFile("hello.jpg",in);
		//关闭连接
		ftpClient.logout();
	}
}
