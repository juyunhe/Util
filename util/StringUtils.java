package com.rf.gjframe.base.util;


import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.rf.gjframe.base.mvc.SessionUser;

/**
 * 字符串工具类
 * @author wangjw
 *
 */
public class StringUtils{
  private static final int fillchar = 61;
  private static final String cvt = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  /**
   * 切分字符串
   * @param line
   * @param delim
   * @return
   */
  public static String [] split(String line, String delim)
  {
      List<String> list = new ArrayList<String>();
      StringTokenizer t = new StringTokenizer(line, delim);
      while (t.hasMoreTokens())
      {
          list.add(t.nextToken());
      }
      return (String []) list.toArray(new String[list.size()]);
  }
  
  public static String xmlEscaping(String text)
  {
    text = text.replaceAll("&", "&amp;");
    text = text.replaceAll("<", "&lt;");
    text = text.replaceAll(">", "&gt;");
    text = text.replaceAll("\"", "&quot;");
    text = text.replaceAll("'", "&apos;");
    return text;
  }

  public static String xmlUnescaping(String text) {
    text = text.replaceAll("&amp;", "&");
    text = text.replaceAll("&lt;", "<");
    text = text.replaceAll("&gt;", ">");
    text = text.replaceAll("&quot;", "\"");
    text = text.replaceAll("&apos;", "'");
    return text;
  }

  
  public static final String replace(String line, String oldString, String newString)
  {
    int i = 0;
    if ((i = line.indexOf(oldString, i)) >= 0) {
      char[] line2 = line.toCharArray();
      char[] newString2 = newString.toCharArray();
      int oLength = oldString.length();
      StringBuffer buf = new StringBuffer(line2.length);
      buf.append(line2, 0, i).append(newString2);
      i += oLength;
      int j = i;
      while ((i = line.indexOf(oldString, i)) > 0) {
        buf.append(line2, j, i - j).append(newString2);
        i += oLength;
        j = i;
      }
      buf.append(line2, j, line2.length - j);
      return buf.toString();
    }
    return line;
  }
  

  public static final String extractPath(String path)
  {
    int index = path.lastIndexOf("/");
    String dirPath = path;
    if (index != -1)
      dirPath = path.substring(0, index);
    return dirPath;
  }
  
  public static String collapseSpaces(String argStr)
  {
    char last = argStr.charAt(0);
    StringBuffer argBuf = new StringBuffer();

    for (int cIdx = 0; cIdx < argStr.length(); ++cIdx)
    {
      char ch = argStr.charAt(cIdx);
      if ((ch == ' ') && (last == ' '))
        continue;
      argBuf.append(ch);
      last = ch;
    }

    return argBuf.toString();
  }
  
  public static final String extractFilename(String path) {
	    int index = path.lastIndexOf("/");
	    String filename = path;
	    if (index != -1)
	      filename = path.substring(index + 1);
	    return filename;
	  }
  
  /**
   * 截取源字符串length长度的字符作为主要内容，后边以...做补充
   * @param str源字符串
   * @param length 要截取的长度
   * @return
   */
  public static final String summary(String str, int length) {
	    if ((str != null) && (str.length() > length)) {
	      str = str.substring(0, length);
	      str = str + "...";
	      return str;
	    }
	    return str;
  }
  
  /**
   * 获取文件后缀名
   **/
  public static String getFileExtension(String filename) {
	    int index = filename.lastIndexOf(".");
	    String fileExt = "";
	    if (index != -1)
	      fileExt = filename.substring(index + 1);
	    return fileExt;
  }
  
  /**
   * 字符为空时返回""
   * @param str
   * @return
   */
  public static String nullFilter(String str)
  {
    if (str == null)
      return "";
    return str;
  }
  
  public static boolean isNotEmptyStr(String str){
	  if(str !=null && !str.equals("")){
		  return true;
	  }
	  return false;
  }
  
  public static enum ImgSelect {
	  jpg, png, jpeg, gif;   
  }

  public static boolean isImg(String name){
	  for(ImgSelect img:ImgSelect.values()){
		if(name.equals(img.toString())){
			return true;
		}
	  }
	  return false;
  }
  /**
   * 判断获取的字段值是否为Null或者空
   * @param object
   * @return
   */
  public static boolean isNotNullOrEmpty(Object object){
	  if(object != null && !(object.toString()).trim().isEmpty() && !"null".equals(object)){
		  return true;
	  }
	  return false;
  }

public static String[] addStrings(String[] fill_item_names, String new_string) {
	List list = new ArrayList();
	for (String str : fill_item_names) {
		list.add(str);
	}
	list.add(new_string);
	
	return (String[]) list.toArray(new String[1]);
}
/**获取tomcat根目录
 * @param request
 * @return
 */
	public static String getTomcatUrl(HttpServletRequest request){
		String path = request.getSession().getServletContext().getRealPath("/");
		StringBuilder sb = new StringBuilder();
		String[] paths = path.split("\\\\");
		for (int i = 0; i < paths.length-1; i++) {
			sb.append(paths[i]+File.separator);
		}
		return sb.toString()+"appFile"+File.separator;
	}
	
	public static String getSqlByCustomer(SessionUser session,String table_name,String sql){
		if(isNotNullOrEmpty(session.getCustomerId())){
			sql += " and "+(isNotNullOrEmpty(table_name)?(table_name+"."):"")+"customer_id = '"+session.getCustomerId()+"'";
		}
		return sql;
	}
	
	public static String getHqlByCustomer(SessionUser session, String table_name, String hql) {
		if(isNotNullOrEmpty(session.getCustomerId())){
			hql += " and "+(isNotNullOrEmpty(table_name)?(table_name+"."):"")+"customerId = '"+session.getCustomerId()+"'";
		}
		return hql;
	}
	/**根据语言 获取对应名称
	 * @param table_name
	 * @param lan
	 * @return
	 */
	public static String getLan(String table_name,String lan){
		String lanColumn="";
		if(table_name==null||"".equals(table_name)) return "";
		if(table_name.equalsIgnoreCase("base_comm_menu")){
			if("cn".equalsIgnoreCase(lan)||"zh_CN".equalsIgnoreCase(lan)){
				lanColumn = "menu_name";
			}else if("gb".equalsIgnoreCase(lan)||"en".equalsIgnoreCase(lan)){
				lanColumn = "menu_en_name";
			}else if("es".equalsIgnoreCase(lan)){
				lanColumn = "menu_es_name";
			}else if("fr".equalsIgnoreCase(lan)){
				lanColumn = "menu_fr_name";
			}else if("ru".equalsIgnoreCase(lan)){
				lanColumn = "menu_ru_name";
			}
		}else if(table_name.equalsIgnoreCase("base_comm_func")){
			if("cn".equalsIgnoreCase(lan)||"zh_CN".equalsIgnoreCase(lan)){
				lanColumn = "func_name";
			}else if("gb".equalsIgnoreCase(lan)||"en".equalsIgnoreCase(lan)){
				lanColumn = "func_en_name";
			}else if("es".equalsIgnoreCase(lan)){
				lanColumn = "func_es_name";
			}else if("fr".equalsIgnoreCase(lan)){
				lanColumn = "func_fr_name";
			}else if("ru".equalsIgnoreCase(lan)){
				lanColumn = "func_ru_name";
			}
		}else if(table_name.equalsIgnoreCase("base_coding_sort_det_alias")){
			if("cn".equalsIgnoreCase(lan)||"zh_CN".equalsIgnoreCase(lan)){
				lanColumn = "coding_name";
			}else if("gb".equalsIgnoreCase(lan)||"en".equalsIgnoreCase(lan)){
				lanColumn = "coding_en_name";
			}else if("fr".equalsIgnoreCase(lan)){
				lanColumn = "coding_fr_name";
			}else if("ru".equalsIgnoreCase(lan)){
				lanColumn = "coding_ru_name";
			}else if("es".equalsIgnoreCase(lan)){
				lanColumn = "coding_es_name";
			}
		}else if(table_name.equalsIgnoreCase("base_comm_mobile_menu")){
			if("cn".equalsIgnoreCase(lan)||"zh_CN".equalsIgnoreCase(lan)){
				lanColumn = "menu_name";
			}else{
				lanColumn = "menu_en_name";
			}
		}else if(table_name.equalsIgnoreCase("base_coding_sort_detail")){
			if("cn".equalsIgnoreCase(lan)||"zh_CN".equalsIgnoreCase(lan)){
				lanColumn = "coding_name";
			}else if("gb".equalsIgnoreCase(lan)||"en".equalsIgnoreCase(lan)){
				lanColumn = "coding_en_name";
			}else if("fr".equalsIgnoreCase(lan)){
				lanColumn = "coding_fr_name";
			}else if("ru".equalsIgnoreCase(lan)){
				lanColumn = "coding_ru_name";
			}else if("es".equalsIgnoreCase(lan)){
				lanColumn = "coding_es_name";
			}
		}else if(table_name.equalsIgnoreCase("base_coding_sort")){
			if("cn".equalsIgnoreCase(lan)||"zh_CN".equalsIgnoreCase(lan)){
				lanColumn = "coding_sort_name";
			}else if("gb".equalsIgnoreCase(lan)||"en".equalsIgnoreCase(lan)){
				lanColumn = "coding_sort_en_name";
			}else if("fr".equalsIgnoreCase(lan)){
				lanColumn = "coding_sort_fr_name";
			}else if("ru".equalsIgnoreCase(lan)){
				lanColumn = "coding_sort_ru_name";
			}else if("es".equalsIgnoreCase(lan)){
				lanColumn = "coding_sort_es_name";
			}
		}else if(table_name.equalsIgnoreCase("base_org_info")){
			if("cn".equalsIgnoreCase(lan)||"zh_CN".equalsIgnoreCase(lan)){
				lanColumn = "org_name";
			}else if("gb".equalsIgnoreCase(lan)||"en".equalsIgnoreCase(lan)){
				lanColumn = "en_org_name";
			}else if("es".equalsIgnoreCase(lan)){
				lanColumn = "es_org_name";
			}else if("fr".equalsIgnoreCase(lan)){
				lanColumn = "fr_org_name";
			}else if("ru".equalsIgnoreCase(lan)){
				lanColumn = "ru_org_name";
			}
		}
		return lanColumn;
	}

	/**
	 * @throws ParseException 
	 * @Description: 字符串转化成日期
	 * @Author: fudong01
	 * @Create 2017年7月26日下午3:52:06
	 */ 	
	public static Date strConvDate(String date) throws ParseException{
		if(StringUtils.isNotNullOrEmpty(date)){
			DateFormat format = null;
			if(date.length() == "yyyy-MM-dd HH:mm:ss".length()){
				format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}else if(date.length() == "yyyy-MM-dd HH:mm".length()){
				format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			}else if(date.length() == "yyyy-MM-dd".length()){
				format = new SimpleDateFormat("yyyy-MM-dd");
			}else{
				return null;
			}
			return format.parse(date);	
		}
		return null;
	}
}