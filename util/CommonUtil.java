package com.rf.gjframe.base.util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;


/**
 * 公共工具类
 * @author wangjw
 *
 */
public class CommonUtil {
	
	private static Log log = LogFactory.getLog(CommonUtil.class);
	  /**
	   * 是否是中文
	   * @param c
	   * @return
	   */
	  public static boolean isChinese(char c) {
	    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
	    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
	        || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
	        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
	        || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
	        || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
	        || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
	      return true;
	    }
	    return false;
	  }

	/** 判断给定字符串是否为乱码
	* @Title: isMessyCode2 
	* @param @param strName
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws 
	*/ 
	public static boolean isMessyCode2(String strName) {
	    Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
	    Matcher m = p.matcher(strName);
	    String after = m.replaceAll("");
	    String temp = after.replaceAll("\\p{P}", "");
	    char[] ch = temp.trim().toCharArray();
	    float chLength = ch.length;
	    float count = 0;
	    for (int i = 0; i < ch.length; i++) {
	      char c = ch[i];
	      if (!Character.isLetterOrDigit(c)) {

	        if (!isChinese(c)) {
	          count = count + 1;
	          System.out.print(c);
	        }
	      }
	    }
	    float result = count / chLength;
	    if (result > 0.4) {
	      return true;
	    } else {
	      return false;
	    }

	  }
	

	/** 判断字符串的首字母是否为中文或者字母，若是返回false；否则返回true(是乱码)
	* @Title: isCnorEn 
	* @param @param str
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws 
	*/ 
	public static boolean isMessyCode(String str) {
		if(str==null || "".equals(str)){
			return false;
		}
		char c = str.charAt(0);
		//字母, 数字
		if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
				|| (c >= 'A' && c <= 'Z')) {
				return false;
		}
		 String regEx = "[\\u4e00-\\u9fa5]";               
		 Pattern p = Pattern.compile(regEx);               
		 Matcher m = p.matcher(str);              
		 while (m.find()) {                  
			 for (int i = 0; i <= m.groupCount(); i++) {                       
				return false;                   
				 }               
		}       
		return true;
	}
	
	/** 将给定的字符串转码,主要针对前台页面通过js传递中文到后台时
	 * 出现乱码的问题，将乱码进行解码的方法
	* @Title: decodeParam 
	* @param  s			：需要转换的字符串
	* @param  codeSrc	：源编码，codeSrc为null默认为iso-8859-1
	* @param  codeDes	：目的编码，codeDes为null默认为utf-8
	* @return String    返回类型 
	* @throws 
	*/ 
	public static String decodeParam(String  s, String codeSrc, String codeDes){
		if(s == null){
			return null;
		}
		if(log.isDebugEnabled()){
			log.info("isMessyCode==" + isMessyCode2(s));
		}
		if(isMessyCode(s)){
			try{
				s =  new String(s.getBytes(codeSrc==null?"iso-8859-1":codeSrc),codeDes==null?"utf-8":codeDes);
			}catch(Exception e){
				e.printStackTrace();
				return s;
			}
		}
		return s;
	}
	
	/**
	 * InputStream转为Byte[]
	 * @param in
	 * @return 
	 * @throws IOException
	 */
	public static byte[] inputStreamToByte(InputStream in) throws IOException{
		ByteArrayOutputStream out=new ByteArrayOutputStream();
        byte[] buffer=new byte[1024*4];
        int n=0;
        while ( (n=in.read(buffer)) !=-1) {
            out.write(buffer,0,n);
        }
        return out.toByteArray();
	}
	
	/**获取单元格的值
	 * @param cell
	 * @return
	 */
	public static String getCellValue(Cell cell){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		//兼容各种日期格式
		String val="";
		if(cell==null) return val;
		if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC&&HSSFDateUtil.isCellDateFormatted(cell)){
			    Date d = cell.getDateCellValue();  
			    val=formater.format(d);
		}else if (cell.getCellType()==Cell.CELL_TYPE_FORMULA){//兼容各种公式
			try {  
				val = String.valueOf(cell.getStringCellValue());  
			} catch (IllegalStateException e) {  
				val = String.valueOf(cell.getNumericCellValue());  
			}  
		}else{
			cell.setCellType(Cell.CELL_TYPE_STRING);
			val=cell.getStringCellValue();
		}
		return val;
	}
	
	/**  
	* 判断指定的单元格是否是合并单元格  
	* @param sheet   
	* @param row 行下标  
	* @param column 列下标  
	* @return  
	*/  
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map getMergedRegion(Sheet sheet,int row ,int column) {  
	    int sheetMergeCount = sheet.getNumMergedRegions();  
	    for (int i = 0; i < sheetMergeCount; i++) {  
		    CellRangeAddress range = sheet.getMergedRegion(i);  
			int firstColumn = range.getFirstColumn();  
			int lastColumn = range.getLastColumn();  
			int firstRow = range.getFirstRow();  
			int lastRow = range.getLastRow();  
			if(row >= firstRow && row <= lastRow){  
				if(column >= firstColumn && column <= lastColumn){  
					Map m = new HashMap();
					m.put("firstColumn", firstColumn);
					m.put("lastColumn", lastColumn);
					m.put("firstRow", firstRow);
					m.put("lastRow", lastRow);
					return m;
				}  
			}  
	  }  
	  return null;  
	} 
}
