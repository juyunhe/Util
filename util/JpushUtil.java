package com.rf.gjframe.base.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class JpushUtil {
	
	    private final static String hostIp = "140.143.143.133";
	  
	    private final static String port = "8080";
	 

	    
	    /**
	     * 推送给单个用户
	     * @param users  用户名(loginid)多个以,分隔  例如: admin1,admin2,admin3
	     * @param content  推送内容
	     * @return
	     */
	    public static boolean pushMessage( String users,String content ){
	    	
	    	
	    	try {
	    		content = URLEncoder.encode(content, "utf-8");
	    		
	    		String address = "http://"+hostIp+":"+port+"/pushservlet/push?content=" + content + "&users="+users;

	    		URL url = new URL(address);
	            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
	            httpConn.setRequestMethod("GET");
	            httpConn.connect();
	                
	            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
	            String line;
	            StringBuffer buffer = new StringBuffer();
	            while ((line = reader.readLine()) != null) {
	            	buffer.append(line);
	            }
	            reader.close();
	            httpConn.disconnect();
	           
	            String result = buffer.toString();
	            if("success".equals(result)){
	            	return true;
	            }
	            return false;
	    		
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	    	
	    }
	    
	    /**
	     * 推送给系统所有用户
	     * @param content
	     * @return
	     */
	    public static boolean pushMessageToAll(String content){
	    	return pushMessage("all", content);
	    	
	    }
	    
	    
}
