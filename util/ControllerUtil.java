package com.rf.gjframe.base.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.apache.geronimo.mail.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.support.RequestContext;

import com.rf.gjframe.base.mvc.Constant;
import com.rf.gjframe.base.mvc.SessionUser;
import com.rf.gjframe.base.pojo.CommonEntity;
import com.rf.gjframe.base.pojo.User;

/** Controller工具
 * @author wangjw
 *
 */
public class ControllerUtil {

	/**
	 * pojo对象的修改
	 * t1为要修改成的数据，t2为原始数据
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static void editPojo(Object t1, Object t2) throws IllegalArgumentException, IllegalAccessException {

		// 获取Pojo的所有属性
		Field[] ss = t1.getClass().getDeclaredFields();
		Field[] st = t2.getClass().getDeclaredFields();
		for (Field fs : ss) {
			fs.setAccessible(true);// 对private 属性字符的处理
			for (Field ft : st) {
				if (fs.getName().equals(ft.getName())) {
					if (fs.get(t1) != null || (fs.getType().equals(Integer.class) || fs.getType().equals(Float.class))) {
						ft.setAccessible(true);
						ft.set(t2, fs.get(t1));
					}
				}
			}
		}

	}

	public static List pojoToList(Object obj, int length) throws Exception {
		List<Object> list = new ArrayList<Object>();
		Field[] st = obj.getClass().getDeclaredFields();
		for (int i = 0; i < length; i++) {
			Object o = obj.getClass().newInstance();
			for (Field field : st) {
				field.setAccessible(true);
				if (field.get(obj) != null) {
					String value = field.get(obj).toString().split(",", -1)[i];
					if (StringUtils.isNotEmptyStr(value)) {
						Class c = field.getType();
						if (c.equals(Integer.class)) {
							field.set(o, Integer.parseInt(value));
						} else if (c.equals(Float.class)) {
							field.set(o, Float.parseFloat(value));
						} else if (c.equals(String.class)) {
							field.set(o, value);
						}
					}
				}
			}
			list.add(o);
		}
		return list;
	}

	/**
	 * @param obj
	 * @return 获取POJO主键值
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String getPojoKeyValue(Object obj) throws IllegalArgumentException, IllegalAccessException {

		// 获取Pojo的所有属性
		Field[] ss = obj.getClass().getDeclaredFields();
		for (Field field : ss) {
			field.setAccessible(true);// 对private 属性字符的处理
			field.isAnnotationPresent(Id.class);
			return (String) field.get(obj);
		}
		return null;
	}

	/**
	 * @param obj
	 * @return 获取主键名称
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String getPojoKeyName(Object obj) throws IllegalArgumentException, IllegalAccessException {

		// 获取Pojo的所有属性
		Field[] ss = obj.getClass().getDeclaredFields();
		for (Field field : ss) {
			field.isAnnotationPresent(Id.class);
			return field.getName();
		}
		return null;
	}

	public static String getPojoKeyColumnName(Object obj) throws Exception {
		// 获取Pojo的所有属性
		Field[] ss = obj.getClass().getDeclaredFields();
		for (Field field : ss) {
			field.isAnnotationPresent(Id.class);

			return getFileColumnName(obj.getClass(), field);
		}
		return null;
	}

	public static void clearPojo(Object obj) throws IllegalArgumentException, IllegalAccessException {

		// 获取Pojo的所有属性
		Field[] ss = obj.getClass().getDeclaredFields();
		for (Field field : ss) {
			field.setAccessible(true);// 对private 属性字符的处理
			field.set(obj, null);
		}

	}

	@SuppressWarnings("unchecked")
	public static String getFileColumnName(Class pojoClass, Field field) throws Exception {

		String fn = field.getName();

		fn = fn.replaceFirst(fn.substring(0, 1), fn.substring(0, 1).toUpperCase());
		Method method = pojoClass.getMethod("get" + fn);

		Column column = method.getAnnotation(Column.class);
		if (column != null) {
			return column.name();
		}
		return "";
	}

	public static void addPojo(Object pojo, JSONObject jsonObj) throws Exception {
		Field[] ss = pojo.getClass().getDeclaredFields();
		for (Field field : ss) {
			String field_name = field.getName();
			field.setAccessible(true);// 对private 属性字符的处理
			Class c = field.getType();
			if (c.equals(String.class) || c.equals(Date.class) || c.equals(Integer.class) || "[B".equals(c.getName())) {
				Object field_value = jsonObj.get(getFileColumnName(pojo.getClass(), field).toUpperCase());
				if (field_value instanceof JSONNull) {
					continue;
				}
				if (field_value != null && !"".equals(field_value) && !"null".equals(field_value)) {
					if ("[B".equals(c.getName())) {
						field.set(pojo, new Base64().decode((String) field_value));
					} else if (c.equals(Date.class)) {
						Long datelong = (Long) field_value;
						field.set(pojo, new Date(datelong));
					} else {
						field.set(pojo, field_value);
					}
				}
			} else {
				if (!c.equals(Set.class)) {
					Object classObj = c.newInstance();
					Field[] cf = classObj.getClass().getDeclaredFields();
					for (Field field2 : cf) {
						Object field_value = jsonObj.get(getFileColumnName(classObj.getClass(), field2).toUpperCase());
						if (field_value instanceof JSONNull) {
							continue;
						}
						Class c2 = field2.getType();
						if (field_value != null && !"".equals(field_value) && !"null".equals(field_value)) {
							field2.setAccessible(true);// 对private 属性字符的处理
							if (c2.equals(Date.class)) {
								Long datelong = (Long) field_value;
								field2.set(classObj, new Date(datelong));
							} else {
								field2.set(classObj, field_value);
							}

						}
					}
					if (getPojoKeyValue(classObj) == null || "".equals(getPojoKeyValue(classObj))) {
						classObj = null;
					}
					field.set(pojo, classObj);
				}
			}
		}

	}

	public static void encodePojo(Object object) throws Exception {

		// 获取Pojo的所有属性
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);// 对private 属性字符的处理
			if (field.getType().getSimpleName().equals("String")) {
				// Method m = (Method) object.getClass().getMethod("get" +
				// getMethodName(field.getName()));
				String val = (String) field.get(object);
				if (val != null && !"".equals(val)) {
					val = new String(val.getBytes("iso-8859-1"), "utf-8");
					field.set(object, val);
				}
			}
		}

	}

	// 把一个字符串的第一个字母大写、效率是最高的、
	@SuppressWarnings("unused")
	private static String getMethodName(String fildeName) {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}

	/** 组织结构拼接成jSon字符串
	 * @param results
	 * @return
	 */
	public static String orgNodeJson(List<Map> results) {

		results = results == null ? new ArrayList<Map>() : results;

		StringBuilder treeData = new StringBuilder();

		treeData.append("[");

		for (int i = 0, length = results.size(); i < length; i++) {
			Map result = results.get(i);
			treeData.append("{");

			treeData.append("\"id\":\"" + result.get("org_id") + "\",");
			treeData.append("\"text\":\"" + result.get("org_name") + "\",");
			treeData.append("\"attributes\":{\"orgCode\":\"" + result.get("org_code") + "\",\"org_level\":\"" + result.get("org_level") + "\"}");
			treeData.append(",\"state\":\"closed\"");
			treeData.append("}");
			// 判断i是否最后一个数据，“，”分隔数据
			if (i != length - 1) {
				treeData.append(",");
			}
		}

		treeData.append("]");
		return treeData.toString();
	}

	/** 新增时必要的属性值
	 * @param request 
	 * @param comme 
	 * 
	 */
	public static void addMustFields(CommonEntity comme, HttpServletRequest request) {
		SessionUser sessionInfo = null;
		try {
			sessionInfo = (SessionUser) request.getSession().getAttribute(Constant.SESSION_NAME);
		} catch (Exception e) {
			System.out.println("移动请求访问，有类型转换异常，位置：ControllerUtil.java 原因：performancefilter");

		}
		comme.setBsFlag(Constant.BSFLAG_ENABLE);
		comme.setCreateDate(new Date());
		System.out.println("################"+sessionInfo.getOrgInfo());
		comme.setCreateOrgId(sessionInfo.getOrgInfo() != null ? sessionInfo.getOrgInfo().getOrgId() : null);
		comme.setUpdateDate(new Date());
		if (sessionInfo != null) {
			comme.setCreateOrgId(sessionInfo.getOrgInfo().getOrgId());
			if (sessionInfo.getCustomerId() != null) {
				comme.setCustomerId(sessionInfo.getCustomerId());
			}
			if (sessionInfo != null) {
				comme.setCreateUserId(sessionInfo.getUserId());
			}
		}
	}

	/** 更新时必要的属性值
	 * @param request 
	 * @param comme 
	 * 
	 */
	public static void updateMustFields(CommonEntity comme, HttpServletRequest request) {
		SessionUser sessionInfo = (SessionUser) request.getSession().getAttribute(Constant.SESSION_NAME);
		comme.setBsFlag(Constant.BSFLAG_ENABLE);
		comme.setUpdateDate(new Date());
		comme.setUpdateUserId(sessionInfo.getUserId());
		comme.setUpdateOrgId(sessionInfo.getOrgInfo() != null ? sessionInfo.getOrgInfo().getOrgId() : null);
	}

	/** 修改时必要的属性值
	 * @param request 
	 * @param comme 
	 * 
	 */
	public static void editMustFields(CommonEntity comme, HttpServletRequest request) {
		SessionUser sessionInfo = (SessionUser) request.getSession().getAttribute(Constant.SESSION_NAME);
		comme.setUpdateDate(new Date());
		comme.setUpdateUserId(sessionInfo.getUserId());
		comme.setUpdateOrgId(sessionInfo.getOrgInfo() != null ? sessionInfo.getOrgInfo().getOrgId() : null);
	}

	// 判断是否为机关单位
	public boolean isJGunit(String orgType) throws IOException {
		if (getOrgUnit().contains(orgType))
			return true;
		return false;
	}

	public String getJGStrings() throws IOException {
		List<String> list = getOrgUnit();
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			s += "'" + list.get(i) + "'";
			if (i != list.size() - 1) {
				s += ",";
			}
		}
		return s;
	}

	/**
	 * 获取机关单位编码集合
	 * @return
	 * @throws IOException 
	 */
	public List<String> getOrgUnit() throws IOException {

		List<String> orgUnitList = new ArrayList<String>();

		// 从配置文件中读取url和公共队列名称omes
		Properties properties = new Properties();
		// 输入流
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("org_type.properties");
		// 加载文件
		properties.load(inputStream);
		// 获取机关单位编码
		String orgUnit = properties.getProperty("orgUnit");
		// 机关单位数组
		String[] orgUnits = orgUnit.split(",");

		for (int i = 0; i < orgUnits.length; i++) {
			orgUnitList.add(orgUnits[i]);
		}
		return orgUnitList;
	}

	/**
	 * 机关单位 json
	 * @param request 
	 * @param list
	 * @return
	 */
	public String organJson(List<Map> results, String porgId, boolean fictitious, HttpServletRequest request) {

		results = results == null ? new ArrayList<Map>() : results;
		String lan = ControllerUtil.getLanByRequest(request);
		String jiguan = null;
		switch (lan) {
		case "gb":
			jiguan = "Office Company";
			break;
		case "es":
			jiguan = "Las unidades";
			break;
		case "fr":
			jiguan = "L'unité de l'Autorité";
			break;
		case "ru":
			jiguan = "Учреждение группы";
			break;
		default:
			jiguan = "机关单位";
			break;
		}

		StringBuilder treeData = new StringBuilder();

		if (fictitious) {
			treeData.append("{");
			treeData.append("\"id\":1,");
			treeData.append("\"text\":\"" + jiguan + "\"");
			treeData.append(",\"attributes\":{\"porgId\":\"" + porgId + "\"}");
			// treeData.append(",\"iconCls\":\"icon-ok\"");
			treeData.append(",\"state\":\"closed\"");
			treeData.append(",\"children\":");
			treeData.append("[");
		}

		for (int i = 0, length = results.size(); i < length; i++) {
			Map result = results.get(i);

			treeData.append("{");

			treeData.append("\"id\":\"" + result.get("org_id") + "\",");
			treeData.append("\"text\":\"" + result.get("org_name") + "\"");
			treeData.append(",\"attributes\":{\"orgCode\":\"" + result.get("org_code") + "\",\"org_level\":\"" + result.get("org_level") + "\"");
			treeData.append(",\"isUnit\":\"" + "true\"");
			treeData.append("}");
			treeData.append(",\"state\":\"closed\"");
			treeData.append("}");
			// 判断i是否最后一个数据，“，”分隔数据
			if (i != length - 1) {
				treeData.append(",");
			}
		}
		if (fictitious) {
			treeData.append("]");
			treeData.append("}");
		}

		String jsonDatas = treeData.toString();
		if (fictitious) {
			if (results.size() == 0) {
				int index = jsonDatas.indexOf(",\"children\"");
				jsonDatas = jsonDatas.substring(0, index);
				jsonDatas += "}";
			}
		}

		return jsonDatas;
	}

	/**
	 * 下属单位 json
	 * @param list
	 * @return
	 */
	public String subOrgJson(List<Map> results, String porgId, boolean fictitious, HttpServletRequest request) {
		results = results == null ? new ArrayList<Map>() : results;
		String lan = ControllerUtil.getLanByRequest(request);
		String xiashu = null;
		switch (lan) {
		case "gb":
			xiashu = "Subordinate Company";
			break;
		case "es":
			xiashu = "Las unidades subordinadas";
			break;
		case "fr":
			xiashu = "Sous - unités";
			break;
		case "ru":
			xiashu = "подчиненных подразделений";
			break;
		default:
			xiashu = "下属单位";
			break;
		}
		StringBuilder treeData = new StringBuilder();

		if (fictitious) {
			treeData.append("{");
			treeData.append("\"id\":2,");
			treeData.append("\"text\":\"" + xiashu + "\"");
			treeData.append(",\"attributes\":{\"porgId\":\"" + porgId + "\"}");
			// treeData.append(",\"iconCls\":\"icon-ok\"");
			treeData.append(",\"state\":\"open\"");
			treeData.append(",\"children\":");
			treeData.append("[");
		}

		for (int i = 0, length = results.size(); i < length; i++) {
			Map result = results.get(i);

			treeData.append("{");

			treeData.append("\"id\":\"" + result.get("org_id") + "\",");
			treeData.append("\"text\":\"" + result.get("org_name") + "\",");
			treeData.append("\"attributes\":{\"orgCode\":\"" + result.get("org_code") + "\",\"org_level\":\"" + result.get("org_level") + "\"}");
			treeData.append(",\"state\":\"closed\"");
			treeData.append("}");
			// 判断i是否最后一个数据，“，”分隔数据
			if (i != length - 1) {
				treeData.append(",");
			}
		}
		if (fictitious) {
			treeData.append("]");
			treeData.append("}");
		}

		String jsonDatas = treeData.toString();
		if (fictitious) {
			if (results.size() == 0) {

				int index = jsonDatas.indexOf(",\"children\"");
				jsonDatas = jsonDatas.substring(0, index);
				jsonDatas += "}";
			}
		}

		return jsonDatas;
	}

	/**
	 * 下属单位 json
	 * @param list
	 * @return
	 */
	public String subOrgJson(List<Map> results, String porg_id, boolean fictitious, HttpServletRequest request, List<Map> users) {
		results = results == null ? new ArrayList<Map>() : results;

		StringBuilder treeData = new StringBuilder();
		String lan = ControllerUtil.getLanByRequest(request);
		String xiashu = null;
		switch (lan) {
		case "gb":
			xiashu = "Subordinate Company";
			break;
		case "es":
			xiashu = "Las unidades subordinadas";
			break;
		case "fr":
			xiashu = "Sous - unités";
			break;
		case "ru":
			xiashu = "подчиненных подразделений";
			break;
		default:
			xiashu = "下属单位";
			break;
		}
		if (fictitious) {
			treeData.append("{");
			treeData.append("\"id\":2,");
			treeData.append("\"text\":\"" + xiashu + "\"");
			treeData.append(",\"attributes\":{\"porg_id\":\"" + porg_id + "\"}");
			// treeData.append(",\"iconCls\":\"icon-ok\"");
			treeData.append(",\"state\":\"open\"");
			treeData.append(",\"children\":");
			treeData.append("[");
		}

		for (int i = 0, length = results.size(); i < length; i++) {
			Map result = results.get(i);
			treeData.append("{");
			treeData.append("\"id\":\"" + result.get("org_id") + "\",");
			treeData.append("\"text\":\"" + result.get("org_name") + "\",");
			treeData.append("\"attributes\":{\"org_code\":\"" + result.get("org_code") + "\"}");
			treeData.append(",\"state\":\"closed\"");
			treeData.append("}");
			// 判断i是否最后一个数据，“，”分隔数据
			if (i != length - 1) {
				treeData.append(",");
			}
		}

		if (users != null && users.size() > 0) {
			if (results != null && results.size() > 0) {
				treeData.append(",");
			}
			for (int j = 0, userlength = users.size(); j < userlength; j++) {
				treeData.append("{");
				treeData.append("\"id\":\"" + users.get(j).get("user_id") + "\",");
				treeData.append("\"text\":\"" + users.get(j).get("user_name") + "\"");
				treeData.append("}");
				// 判断i是否最后一个数据，“，”分隔数据
				if (j != userlength - 1) {
					treeData.append(",");
				}
			}
		}
		if (fictitious) {
			treeData.append("]");
			treeData.append("}");
		}

		String jsonDatas = treeData.toString();
		if (fictitious) {
			if ((users == null || users.size() == 0) && results.size() == 0) {

				int index = jsonDatas.indexOf(",\"children\"");
				jsonDatas = jsonDatas.substring(0, index);
				jsonDatas += "}";
			}
		}

		return jsonDatas;
	}

	/**
	 * 虚拟单位 json
	 * @param list
	 * @return
	 */
	public String xnOrgJson(List<Map> results, String porg_id, HttpServletRequest request) {
		results = results == null ? new ArrayList<Map>() : results;

		StringBuilder treeData = new StringBuilder();

		for (int i = 0, length = results.size(); i < length; i++) {
			Map result = results.get(i);

			treeData.append("{");

			treeData.append("\"id\":\"" + result.get("org_id") + "\",");
			treeData.append("\"text\":\"" + result.get("org_name") + "\",");
			treeData.append("\"attributes\":{\"org_code\":\"" + result.get("org_code") + "\",\"org_level\":\"" + result.get("org_level") + "\"}");
			treeData.append(",\"state\":\"closed\"");
			treeData.append("}");
			// 判断i是否最后一个数据，“，”分隔数据
			if (i != length - 1) {
				treeData.append(",");
			}
		}

		String jsonDatas = treeData.toString();

		return jsonDatas;
	}

	/** 删除时必要的属性值
	 * @param request 
	 * @param comme 
	 * 
	 */
	public static void delMustFields(CommonEntity comme, HttpServletRequest request) {
		SessionUser sessionInfo = (SessionUser) request.getSession().getAttribute(Constant.SESSION_NAME);
		comme.setUpdateDate(new Date());
		comme.setUpdateUserId(sessionInfo.getUserId());
		comme.setBsFlag(Constant.BSFLAG_DISABLE);
	}

	public static void addMustFields(Object obj, User user) {
		CommonEntity comme = (CommonEntity) obj;
		comme.setBsFlag(Constant.BSFLAG_ENABLE);
		comme.setCreateDate(new Date());
		comme.setUpdateDate(new Date());
		comme.setCreateUserId(user.getUserId());
		if (user.getOrgInfo() != null) {
			comme.setCreateOrgId(user.getOrgInfo().getOrgId());
		}
	}

	public static void addMustFieldsPojo(Object obj, User user) {
		CommonEntity comme = (CommonEntity) obj;
		comme.setBsFlag(Constant.BSFLAG_ENABLE);
		comme.setCreateUserId(user.getUserId());
		if (user.getOrgInfo() != null) {
			comme.setCreateOrgId(user.getOrgInfo().getOrgId());
		}
	}

	public static void editMustFields(Object obj, User user) {
		CommonEntity comme = (CommonEntity) obj;
		comme.setUpdateDate(new Date());
		comme.setUpdateUserId(user.getUserId());
	}

	/**
	 * 生成缩略图
	 * @param b 源图片文件;
	 * @throws Exception
	 */
	public static byte[] makeSmallImage(byte[] b) throws Exception {
		BufferedImage tagImage = null;
		Image srcImage = null;
		InputStream in = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		in = byteTOInputStream(b);
		srcImage = ImageIO.read(in);
		int srcWidth = srcImage.getWidth(null);// 原图片宽度
		int srcHeight = srcImage.getHeight(null);// 原图片高度
		int dstMaxSize = 80;// 目标缩略图的最大宽度/高度，宽度与高度将按比例缩写
		int dstWidth = srcWidth;// 缩略图宽度
		int dstHeight = srcHeight;// 缩略图高度
		float scale = 0;
		// 计算缩略图的宽和高
		if (srcWidth > dstMaxSize) {
			dstWidth = dstMaxSize;
			scale = (float) srcWidth / (float) dstMaxSize;
			dstHeight = Math.round((float) srcHeight / scale);
		}
		srcHeight = dstHeight;
		if (srcHeight > dstMaxSize) {
			dstHeight = dstMaxSize;
			scale = (float) srcHeight / (float) dstMaxSize;
			dstWidth = Math.round((float) dstWidth / scale);
		}
		// 生成缩略图
		tagImage = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_RGB);
		tagImage.getGraphics().drawImage(srcImage, 0, 0, dstWidth, dstHeight, null);

		try {
			ImageIO.write(tagImage, "jpeg", out);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return out.toByteArray();
	}

	public static InputStream byteTOInputStream(byte[] in) throws Exception {

		ByteArrayInputStream is = new ByteArrayInputStream(in);
		return is;
	}

	/** 获取lan
	 * @param rquest
	 * @return
	 */
	public static String getLanByRequest(HttpServletRequest rquest) {
		String lan = (String) rquest.getSession().getAttribute(Constant.Lan);
		return lan;
	}

	/**
	 * 获取国际化消息
	 * @param request
	 * @param code 国际化key值
	 * @return
	 * @throws Exception
	 */
	public static String getMessage(HttpServletRequest request, String code) {
		RequestContext requestContext = new RequestContext(request);
		String result = requestContext.getMessage(code);
		return result;
	}

	/**获取SessionInfo
	 * @param request
	 * @return
	 */
	public static SessionUser getSessionUser(HttpServletRequest request) {
		if (request.getSession().getAttribute(Constant.SESSION_NAME) instanceof SessionUser) {
			return (SessionUser) request.getSession().getAttribute(Constant.SESSION_NAME);
		}
		return null;
	}

	/**将list全部转化为大写的key
	 * @param list
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Map> changKeyToUpperCase(List<Map> list) {
		List<Map> newList = new ArrayList<Map>();
		for (Map map : list) {
			Map m = new HashMap();
			Set keySet = map.keySet();
			for (Object object : keySet) {
				String newobj = ((String) object).toUpperCase();
				Object value = map.get(object);
				if (value == null || "null".equals(value))
					value = "";
				if (!newobj.equals(object)) {
					m.put(newobj, value);
				} else {
					m.put(object, value);
				}
			}
			newList.add(m);
		}
		return newList;
	}

	public static String getRealIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String getDeleteString(String tableStr, HttpServletRequest request) {
		SessionUser se = getSessionUser(request);
		String sql = " " + tableStr + ".bsflag='1'," + tableStr + ".update_date=now()," + tableStr + ".update_user_id = '" + se.getUserId() + "'," + tableStr + ".update_org_id='" + se.getOrgInfo().getOrgId() + "' ";
		return sql;
	}

	public static String multiLanHandle(String str, HttpServletRequest request) {
		if (str.equals(Constant.ORG_NAME)) {
			if ("en".equals(request.getParameter("lan"))) {
				str = "en_org_name";
				return str;
			}
		}
		if (str.equals(Constant.CODING_NAME)) {
			if ("en".equals(request.getParameter("lan"))) {
				str = "coding_en_name";
				return str;
			}
		}
		return str;
	}

	/**
	 * 生成缩略图,特殊处理下透明背景的图片
	 * @param b 源图片文件;
	 * @throws Exception
	 */
	public static byte[] makeSmallImageSP(byte[] b) throws Exception {
		BufferedImage tagImage = null;
		Image srcImage = null;
		InputStream in = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		in = byteTOInputStream(b);
		srcImage = ImageIO.read(in);
		int srcWidth = srcImage.getWidth(null);// 原图片宽度
		int srcHeight = srcImage.getHeight(null);// 原图片高度
		int dstMaxSize = 80;// 目标缩略图的最大宽度/高度，宽度与高度将按比例缩写
		int dstWidth = srcWidth;// 缩略图宽度
		int dstHeight = srcHeight;// 缩略图高度
		float scale = 0;
		// 计算缩略图的宽和高
		if (srcWidth > dstMaxSize) {
			dstWidth = dstMaxSize;
			scale = (float) srcWidth / (float) dstMaxSize;
			dstHeight = Math.round((float) srcHeight / scale);
		}
		srcHeight = dstHeight;
		if (srcHeight > dstMaxSize) {
			dstHeight = dstMaxSize;
			scale = (float) srcHeight / (float) dstMaxSize;
			dstWidth = Math.round((float) dstWidth / scale);
		}
		// 生成缩略图
		BufferedImage to = new BufferedImage(dstWidth, dstHeight,

		BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = to.createGraphics();

		to = g2d.getDeviceConfiguration().createCompatibleImage(dstWidth, dstHeight,

		Transparency.TRANSLUCENT);

		g2d.dispose();

		g2d = to.createGraphics();

		Image from = srcImage.getScaledInstance(dstWidth, dstHeight, srcImage.SCALE_AREA_AVERAGING);
		g2d.drawImage(from, 0, 0, null);
		g2d.dispose();

		try {
			ImageIO.write(to, "png", out);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return out.toByteArray();
	}

	public static String getExportString(String str) {
		String[] speStrs = { "<B>", "</B>", "<BR>", "</BR>" };
		for (String speStr : speStrs) {
			if (speStr.equals("<BR>") || speStr.equals("</BR>"))
				str = str.replaceAll(speStr, "\n");
			else
				str = str.replaceAll(speStr, "");
		}

		return str;
	}

	public static CellStyle getTitleStyle(Workbook workBook, short fill_color) {
		CellStyle style = workBook.createCellStyle();
		Font font = workBook.createFont();
		font.setFontName("Arial");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFillForegroundColor(fill_color);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		font.setFontHeightInPoints((short) 14);// 设置字体大小
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setWrapText(true);
		style.setFont(font);
		return style;
	}

	public static CellStyle getContentStyle(Workbook workBook, short styleShort) {
		CellStyle style = workBook.createCellStyle();
		Font font = workBook.createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 9);// 设置字体大小
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 指定单元格居中对齐
		// style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//
		// 指定单元格垂直居中对齐
		style.setVerticalAlignment(styleShort);// 指定单元格垂直居中对齐
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setWrapText(true);
		style.setFont(font);
		return style;
	}

	/**将list全部转化为大写的key
	 * @param list
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Map> changKeyToUpperCase(Object obj) {

		List<Map> newList = new ArrayList<Map>();
		List<Map> list = new ArrayList<Map>();
		if (obj instanceof List) {
			list.addAll((List<Map>) obj);
		} else if (obj instanceof Map) {
			list.add((Map) obj);
		}
		for (Map map : list) {
			Map m = new HashMap();
			Set keySet = map.keySet();
			for (Object object : keySet) {
				String newobj = ((String) object).toUpperCase();
				Object value = map.get(object);
				if (value == null || "null".equals(value))
					value = "";
				if (!newobj.equals(object)) {
					m.put(newobj, value);
				} else {
					m.put(object, value);
				}
			}
			newList.add(m);
		}
		return newList;
	}

	public String getTuiSongPort() throws IOException {
		// 从配置文件中读取url和公共队列名称
		Properties properties = new Properties();
		// 输入流
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("workflow.properties");
		// 加载文件
		properties.load(inputStream);
		// 获取端口
		String port = properties.getProperty("notify.port");

		return port;
	}

	public static String messageContentHandle(String type, String content, HttpServletRequest request) {
		if (type.equals(Constant.DATAREPORT_CATEGORY)) {
			if ("en".equals(request.getParameter("lan"))) {
				content = "You have a report data <" + content + "> that needs to be processed!";
			} else {
				content = "你有一条上报数据  <" + content + "> 需要处理!";
			}
		} else if (type.equals(Constant.CHECKMODEL_CATEGORY)) {
			if ("en".equals(request.getParameter("lan"))) {
				content = "You have a template data <" + content + ">  that needs to be processed!";
			} else {
				content = "你有一条模板数据 <" + content + "> 需要处理！";
			}
		} else if (type.equals(Constant.TASKPERMIT_CATEGORY)) {
			if ("en".equals(request.getParameter("lan"))) {
				content = "You have a task permit data <" + content + ">  that needs to be processed!";
			} else {
				content = "你有一条作业许可数据 <" + content + "> 需要处理！";
			}
		}
		return content;
	}

	/**
	 * <pre>
	 * 方法体说明：向远程接口发起请求，返回字符串类型结果
	 * @param url 接口地址
	 * @param requestMethod 请求类型
	 * @param params 传递参数
	 * @return String 返回结果
	 * </pre>
	 */
	public static String httpRequestToString(String url, String requestMethod, Map<String, String> params, String... auth) {
		// 接口返回结果
		String methodResult = null;
		try {
			String parameters = "";
			boolean hasParams = false;
			// 将参数集合拼接成特定格式，如name=zhangsan&age=24
			for (String key : params.keySet()) {
				String value = URLEncoder.encode(params.get(key), "UTF-8");
				parameters += key + "=" + value + "&";
				hasParams = true;
			}
			if (hasParams) {
				parameters = parameters.substring(0, parameters.length() - 1);
			}
			// 是否为GET方式请求
			boolean isGet = "get".equalsIgnoreCase(requestMethod);
			boolean isPost = "post".equalsIgnoreCase(requestMethod);
			boolean isPut = "put".equalsIgnoreCase(requestMethod);
			boolean isDelete = "delete".equalsIgnoreCase(requestMethod);

			// 创建HttpClient连接对象
			DefaultHttpClient client = new DefaultHttpClient();

			/*
			 * String proxyHost = "proxy3.bj.petrochina"; int proxyPort = 8080;
			 * String proxyuserName = ""; String proxypassword = "";
			 * client.getCredentialsProvider().setCredentials( new
			 * AuthScope(proxyHost, proxyPort), new
			 * UsernamePasswordCredentials(proxyuserName, proxypassword));
			 * HttpHost proxy = new HttpHost(proxyHost,proxyPort);
			 * client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
			 * proxy);
			 */

			HttpRequestBase method = null;
			if (isGet) {
				url += "?" + parameters;
				method = new HttpGet(url);
			} else if (isPost) {
				method = new HttpPost(url);
				HttpPost postMethod = (HttpPost) method;
				StringEntity entity = new StringEntity(parameters);
				postMethod.setEntity(entity);
			} else if (isPut) {
				method = new HttpPut(url);
				HttpPut putMethod = (HttpPut) method;
				StringEntity entity = new StringEntity(parameters);
				putMethod.setEntity(entity);
			} else if (isDelete) {
				url += "?" + parameters;
				method = new HttpDelete(url);
			}
			method.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);
			// 设置参数内容类型
			method.addHeader("Content-Type", "application/x-www-form-urlencoded");
			// httpClient本地上下文
			HttpClientContext context = null;
			if (!(auth == null || auth.length == 0)) {
				String username = auth[0];
				String password = auth[1];
				UsernamePasswordCredentials credt = new UsernamePasswordCredentials(username, password);
				// 凭据提供器
				CredentialsProvider provider = new BasicCredentialsProvider();
				// 凭据的匹配范围
				provider.setCredentials(AuthScope.ANY, credt);
				context = HttpClientContext.create();
				context.setCredentialsProvider(provider);
			}
			// 访问接口，返回状态码
			HttpResponse response = client.execute(method, context);
			// 返回状态码200，则访问接口成功
			if (response.getStatusLine().getStatusCode() == 200) {
				methodResult = EntityUtils.toString(response.getEntity());
			}
			client.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return methodResult;
	}
}
