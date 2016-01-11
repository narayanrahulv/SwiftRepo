package org.marist.model;

import java.io.FileNotFoundException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class HelloWorld {

	private static Logger LOGGER = Logger.getLogger(HelloWorld.class);
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		
		LOGGER.info("Sample file to serve as a starting point for maven project setup and to experiment with logging");
		
		try {
			if(true){
				
				int a  = 10+20;
				LOGGER.debug("sample debug log: intermediate result of var a : " + a);
			}
			throw new Exception();
		}catch (Exception e) {
			LOGGER.error("sample exception log: Exception caught");
		}
		
		System.out.println("Hello this is just a test file");
	}
}
