package com.rf.gjframe.base.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 进行MD5加密工具类
 * @author wangjw
 *
 */
public class MD5 {
	private static Log log = LogFactory.getLog(MD5.class);
	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6','7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  
	/**
	 * 获得某义字符串的MD5值
	 * @param src
	 * @return
	 */
	public static String getMD5(String src){
		return getMD5(src.getBytes());
	}
	
	/**
	 * 获得某一字节数据数据的MD5值
	 * @param datas
	 * @return
	 */
	public static String getMD5(byte[] datas){
		String result = null;
		
				try { 
				   byte[] strTemp = datas;
				   //使用MD5创建MessageDigest对象 
				   MessageDigest mdTemp = MessageDigest.getInstance("MD5"); 
				   mdTemp.update(strTemp); 
				   byte[] md = mdTemp.digest(); 
				   int j = md.length; 
				   char stra[] = new char[j * 2]; 
				   int k = 0; 
				   for (int i = 0; i < j; i++) { 
				    byte b = md[i]; 
				    //System.out.println((int)b); 
				    //将没个数(int)b进行双字节加密 
				    stra[k++] = hexDigits[b >> 4 & 0xf]; 
				    stra[k++] = hexDigits[b & 0xf]; 
				   } 
				  result =   new String(stra); 
				
				} catch (Exception e) {
					result = null;
			  }
	   return result;
	}
	
	/**
	 * 获得返回大写MD5字符串
	 * @param datas
	 * @return
	 */
	public static String getUpperMD5(byte[] datas){
		String result = getMD5(datas);
		result = result.toUpperCase();
		return result;
	}
	
	/**
	 * 对某以文件进行MD5的获取
	 * @param file
	 * @return
	 */
	public static String getUpperFileMD5(File file){
		String result = null;
		 try {
			FileInputStream in = new FileInputStream(file);  
			 FileChannel ch = in.getChannel();  
			 MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,  file.length());  
			   MessageDigest mdTemp = MessageDigest.getInstance("MD5"); 
			   mdTemp.update(byteBuffer);  
			 byte[] md = mdTemp.digest(); 
			   int j = md.length; 
			   char stra[] = new char[j * 2]; 
			   int k = 0; 
			   for (int i = 0; i < j; i++) { 
			    byte b = md[i]; 
			    //System.out.println((int)b); 
			    //将没个数(int)b进行双字节加密 
			    stra[k++] = hexDigits[b >> 4 & 0xf]; 
			    stra[k++] = hexDigits[b & 0xf]; 
			   } 
			  result =   new String(stra);
			  in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.info(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			log.info(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.info(e.getMessage());
		} 

		  return result;
	}
	
	public static void main(String[] args){
		String a = "aaaa";
		String md5 = getMD5(a.getBytes());
		System.out.println(md5);
		String umd5 = getUpperMD5(a.getBytes());
		System.out.println(umd5);
	}
}
