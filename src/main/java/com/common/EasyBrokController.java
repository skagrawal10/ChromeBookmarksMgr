package com.common;


import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.common.EasyBrokEntityManagerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@Controller
@RequestMapping("/vrp")
public class EasyBrokController {
	private static Logger logger = Logger.getLogger(EasyBrokController.class);
	private static Logger syncLogger = Logger.getLogger("com.common");
	
	
	@RequestMapping(value = "/saveWebContent", method = RequestMethod.POST)
	public @ResponseBody String saveWebContent(ModelMap model, HttpServletRequest request)
	{
		try{
		//String path = "E:\\Invero\\apache-tomcat-6.0.43\\webapps\\PineappleEasyRealState\\output";
		String path = WebHistory.basePath + "../../output";
		System.out.println("Hiiiiii");
		logger.debug("EasyBrokController.VRP");
		String content = request.getParameter("data");
		
		String url = request.getParameter("url");
		
		String random_file_name = "data_" + (EasyBrokEntityManagerFactory.r.nextDouble() + "").replace('.', '_') + ".dat";
		//System.out.println(content);
		System.out.println(random_file_name);
		BufferedWriter fwriter = new BufferedWriter(new FileWriter(path + "/" + random_file_name));
		fwriter.write(content);
		fwriter.close();
		WebHistory.addToIndex(url, content, random_file_name);
		return "Success";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "Fail";
		
		}
		
		
		
	}
	
	@RequestMapping(value = "/getSearchResults", method = RequestMethod.GET)
	public @ResponseBody String getSearchResults(ModelMap model, HttpServletRequest request)
	{
		try{
		WebHistory.InitialiseLucene();
		String keyword = request.getParameter("keyword");
		ArrayList<String[]> results = WebHistory.getTopResults(keyword, 50);
		Gson gson = new GsonBuilder().create();
        String response = gson.toJson(results);
		return response;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "Fail";
		
		}
		
		
		
	}
	
	 
	}
