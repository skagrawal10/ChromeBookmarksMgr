package com.common;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class EasyBrokEntityManagerFactory {
	private static ApplicationContext appContext ;
	private static Logger syncLogger = Logger.getLogger("com.common");
	public static Random r;
	public static WebHistory webHistory;
	static {
		try{
		appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		webHistory = new WebHistory();
		webHistory.InitialiseLucene();
		System.out.println("Inside Class");
		r = new Random();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static ApplicationContext getApplicationContext() {
		return appContext ;
	} 
	
	
	public static void main(String args[])
	{
	}
	
		
		
}
