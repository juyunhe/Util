package com.rf.gjframe.base.util;


import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * 数据填充工具类
 * 通过调用populate方法来使ResultSet中的数据自动填充到给定对象中
 * @author wangjw
 *
 */
public class PopulateUtil {
	
	/**
	 * 将ResultSet中的数据填充到给定对象obj中
	 * @param obj 给定对象
	 * @param rs 数据库读取的数据结果集
	 * @return
	 * @throws Exception
	 */
	public static boolean populate(Object obj, ResultSet rs)throws SQLException{
		    return populate(obj, rs,  null);
	}

	/**
	 * 填充数据
	 * @param obj 目标对象
	 * @param rs 数据结果集
	 * @param ignoreProperties 忽略掉的属性
	 * @return
	 * @throws Exception
	 */
	 public static boolean populate(Object obj, ResultSet rs, String[] ignoreProperties)throws SQLException
			  {
			    try
			    {
			      List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

			      BeanWrapper bw = new BeanWrapperImpl(obj);

			      PropertyDescriptor[] pd = bw.getPropertyDescriptors();
			      Map<String,String> colNameList = new HashMap<String,String>();//存储所有的resultSet里保存的列名
			      int num = rs.getMetaData().getColumnCount();
			      for (int i = 0; i < num; ++i) {
			        String colName = rs.getMetaData().getColumnLabel(i + 1);
			        if (colName != null) {
			          String tempName = colName.replaceAll("_", "");
			          colNameList.put(tempName.toUpperCase(),colName);
			        }
			      }

			      for (int i = 0; i < pd.length; ++i) {//遍历对象属性
			        String name = pd[i].getName();//得到对象属性名字
			        String targetName = colNameList.get(name.toUpperCase());//得到数据库与之对应的列
			        if (("class".equals(name)) || (!(colNameList.containsKey(name.toUpperCase()))) || ((ignoreProperties != null) && (ignoreList.contains(name)))) {
			          continue;
			        }
			        try{
			          Object res = rs.getObject(targetName);//获取数据库对应的值
			          Class<?> targetTypeClazz = bw.getPropertyType(name);
			            if ((res != null) && (targetTypeClazz != null)){
			             BeanUtils.setProperty(obj, name, res);
			          }
			        } catch (Exception e) {
			        	throw e;
			        }

			      }

			    }catch (Exception e){
			      throw new SQLException(e);
			    }

			    return true;
			  }
	 
	 
}
