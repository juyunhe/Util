package com.rf.gjframe.base.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import com.cnpc.richfit.bpm.client.service.ServiceFactory;

public class ServiceFactoryUtil {
	
	private static ServiceFactory instance;	

	public static ServiceFactory getInstance() {
		if (instance == null) {
			Properties prop= new Properties();
			String path=ServiceFactoryUtil.class.getResource("/").getPath()+"workflow.properties";
			FileReader reader;
			try {
				reader = new FileReader(path);
				prop.load(reader);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			instance = ServiceFactory.newInstance(prop);
		}
		return instance;
	}
}