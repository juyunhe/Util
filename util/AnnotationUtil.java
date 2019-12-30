package com.rf.gjframe.base.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Id;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;



/**
 * 针对hibernate或者jpa 注解方式的bean的annotation帮助类
 * 
 * @author wangjw
 *
 */
public class AnnotationUtil {

	private static Log logger = LogFactory.getLog(AnnotationUtil.class);
	
	/**
	 * 获取hibernate基于注解的pojo bean 的对应的数据库表表名
	 * @param bean 实体bean
	 * @return
	 */
	public static  <T> String getTableName(Object bean){
		Class<T> clazz = (Class<T>) bean.getClass();
		Annotation[] clazzAnnotation = clazz.getAnnotations();
		String result = null;
		for(Annotation annotation:clazzAnnotation){
			if(Table.class.isInstance(annotation)){
				Table tableAnno =(Table)annotation;
				result = tableAnno.name();
			}
		}
		return result;
	}
	
	/**
	 * 获取hibernate基于注解的pojo bean 的对应的数据库表表名
	 * @param bean 实体bean
	 * @return
	 */
	public static  <T> String getTableName(Class<T> clazz){
		Annotation[] clazzAnnotation = clazz.getAnnotations();
		String result = null;
		for(Annotation annotation:clazzAnnotation){
			if(Table.class.isInstance(annotation)){
				Table tableAnno =(Table)annotation;
				result = tableAnno.name();
			}
		}
		return result;
	}
	
	/**
	 * 获得基于jpa、hibernate注解pojo的主键id
	 * @param bean
	 * @return Map<String,Object>
	 */
	public static <T> Map<String,Object> getId(Object bean){
		Map<String,Object> result =new HashMap<String,Object>();
		Class<T> clazz = (Class<T>) bean.getClass();
		BeanWrapper bw = new BeanWrapperImpl(bean);
	    PropertyDescriptor[] pd = bw.getPropertyDescriptors();//获得所有bean的属性
	    Map<String,String> beanNames= new HashMap<String,String>();
	    for(PropertyDescriptor property:pd){
	    	String name = property.getName();
	    	beanNames.put(name.toUpperCase(), name);
	    }
		Method[] methods = clazz.getMethods();
		for(Method method:methods){
			Column colAnno = method.getAnnotation(Column.class);
			Id id = method.getAnnotation(Id.class);
			if(colAnno!=null&&id!=null){
				String columnName = colAnno.name();//这里拿到的是数据库对应的字段名
				String tempColum = columnName.replaceAll("_", "");
				if(beanNames.containsKey(tempColum.toUpperCase())){
					try {
						Object value = method.invoke(bean);
						if(value!=null){
							result.put(columnName, value);	
						}
					} catch (Exception e) {
						logger.error("获取bean值出现异常", e);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 根据传入的pojo的Class类来获取pojo的主键,此方法适用于主键唯一的情况
	 * @param clazz
	 * @return
	 */
	public static <T> String getId(Class<T> clazz){
		Method[] methods = clazz.getMethods();
		String columnName = null;
		for(Method method:methods){
			Column colAnno = method.getAnnotation(Column.class);
			Id id = method.getAnnotation(Id.class);
			if(colAnno!=null&&id!=null){
				 columnName = colAnno.name();//这里拿到的是数据库对应的字段名
			}
		}
		
		return columnName;
	}
	
	/**
	 * 获取hibernate基础pojo bean 中基于注解的对应的数据库字段名和bean中对应的属性值
	 * @param bean
	 * @param hasPk 是否包含id字段
	 * @return Map<String,Object> key为bean映射数据库的字段名 value为此字段的值
	 */
	public static  <T> Map<String,Object> getColumAndValues(Object bean,boolean hasPk){
		Map<String,Object> result =new HashMap<String,Object>();
		Class<T> clazz = (Class<T>) bean.getClass();
		BeanWrapper bw = new BeanWrapperImpl(bean);
	    PropertyDescriptor[] pd = bw.getPropertyDescriptors();//获得所有bean的属性
	    Map<String,String> beanNames= new HashMap<String,String>();
	    for(PropertyDescriptor property:pd){
	    	String name = property.getName();
	    	beanNames.put(name.toUpperCase(), name);
	    }
		Method[] methods = clazz.getMethods();
		for(Method method:methods){
			Column colAnno = method.getAnnotation(Column.class);
			
			if(colAnno!=null){
				Id id = method.getAnnotation(Id.class);
				if(id!=null&&!hasPk){//如果遍历的是主键字段，并且在获取字段和字段值时要求的是不包含主键字段
					continue;
				}
				
				String columnName = colAnno.name();//这里拿到的是数据库对应的字段名
				String tempColum = columnName.replaceAll("_", "");
				if(beanNames.containsKey(tempColum.toUpperCase())){//如果bean属性包含
					try {
						Object value = method.invoke(bean);
						if(value!=null){
							result.put(columnName, value);
						}
						
					} catch (Exception e) {
						logger.error("获取bean值出现异常", e);
					}
				}
			}
		}
		
		return result;
		
	}
}
