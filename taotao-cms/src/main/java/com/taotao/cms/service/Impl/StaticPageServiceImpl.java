package com.taotao.cms.service.Impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.cms.service.StaticPageService;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 静态化页面
 * @author cs
 *
 */
public class StaticPageServiceImpl implements StaticPageService,ServletContextAware{
	
	private Configuration conf;
	public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer){
		this.conf = freeMarkerConfigurer.getConfiguration();
	}
	
	private ServletContext servletContext;
	
	//静态化--商品
	@Override
	public void itemStaticPage(Map<String,Object> root,String itemId){
		//静态页面的输出全路径
		String path = getPath("/html/item/"+itemId+".html");
		File file = new File(path);
		//保证路径的文件夹都存在
		File parentFile = file.getParentFile();
		if(!parentFile.exists()){
			parentFile.mkdirs();
		}
		Writer out = null;
		try {
			//加载指定模版 UTF-8
			Template template = conf.getTemplate("item.html");
			//输出流 UTF-8
			 out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			 //传入
			 template.process(root, out);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(out != null)
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		
	}
	
	private String getPath(String name){
		return servletContext.getRealPath(name);
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
