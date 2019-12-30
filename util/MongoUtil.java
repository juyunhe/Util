package com.rf.gjframe.base.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.rf.gjframe.base.mvc.Constant;

/**
 *  MongoDB工具类
 * @author wangjw
 * 
 */
public class MongoUtil {

	static Mongo mongo = null;  
	private static Log log = LogFactory.getLog(MongoUtil.class);
	
	public MongoUtil(){}
	
	@SuppressWarnings("deprecation")
	public static GridFS getGridFS(String file_type){
		if(mongo==null){
			mongo = new Mongo(getMongodbIp("mongodb.ip"),Integer.parseInt(getMongodbIp("mongodb.port")));
		}
		DB db = mongo.getDB(getMongodbIp("mongodb.db")); 
		DBObject options = new BasicDBObject();
		options.put("capped", true);
		options.put("max", 10000);//最大条数
		options.put("size", 2000000);//集合空间大小
		switch(file_type){
			case Constant.PICTURE:
				if(db.getCollection(Constant.PICTURE)==null){
					db.createCollection(Constant.PICTURE, options);
				}
			break;
			case Constant.AUDIO:
				if(db.getCollection(Constant.AUDIO)==null){
					db.createCollection(Constant.AUDIO, options);
				}
			break;
			case Constant.VIDEO:
				if(db.getCollection(Constant.VIDEO)==null){
					db.createCollection(Constant.VIDEO, options);
				}
			break;
			case Constant.ATTACHMENT:
				if(db.getCollection(Constant.ATTACHMENT)==null){
					db.createCollection(Constant.ATTACHMENT, options);
				}
			break;
			default:
				if(db.getCollection(Constant.OTHERFILE)==null){
					db.createCollection(Constant.OTHERFILE, options);
				}
		}
        GridFS gridFS = new GridFS(db, file_type);
        return gridFS;
	}
	
	@SuppressWarnings("deprecation")
	public static DBCollection getDBCollection (String collect_name){
		if(mongo==null){
			mongo = new Mongo(getMongodbIp("mongodb.ip"),Integer.parseInt(getMongodbIp("mongodb.port")));
		}
		DB db = mongo.getDB(getMongodbIp("mongodb.db")); 
		return db.getCollection(collect_name);
	}
	/**
	 * @Description: 单个文件保存
	 * @Author: fudong01
	 * @Create 2017年8月14日下午2:21:05
	 */ 	
	public static void saveFile(String file_type,String file_id,String file_name,byte[] content) throws IOException{
		 GridFS gridFS = getGridFS(file_type);
		 
		 log.info("file_type:"+file_type);
		 log.info("file_id:"+file_id);
		 log.info("file_name:"+file_name);
		 log.info("content:"+content.length);
		 
         GridFSInputFile gfsFile = gridFS.createFile(content);
         gfsFile.setId(file_id);
         gfsFile.setFilename(file_name);
         gfsFile.save(); 
	}
	/**
	 * @Description: 获取一条记录的相关图片附件
	 * @Author: fudong01
	 * @Create 2017年8月14日下午2:27:38
	 */ 	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map> getPicFileList(String file_type,String file_fk_id) throws IOException{
		List<Map> fileList = new ArrayList<Map>();
		
		List<String> fileTypeList = new ArrayList<String>();
		fileTypeList.add(Constant.PICTURE);
		//fileTypeList.add(Constant.AUDIO);
		//fileTypeList.add(Constant.OTHERFILE);
		if(fileTypeList.contains(file_type)){
			fileTypeList.clear();
			fileTypeList.add(file_type);
		}
		for(int i=0;i<fileTypeList.size();i++){
			GridFS gridFS = getGridFS(fileTypeList.get(i));
			
			List<GridFSDBFile> list = gridFS.find(file_fk_id);
			for(int j=0;j<list.size();j++){
				GridFSDBFile dbFile = list.get(j);
			
				Map m = new HashMap();
				m.put("file_index_id", dbFile.getId());
				m.put("fk_record_id", dbFile.getFilename());
				
				InputStream in = list.get(j).getInputStream();
		        int count = Integer.valueOf(String.valueOf(list.get(j).getLength()));
		        byte[] b = new byte[count];
		        int readCount = 0;
		        while(readCount<count){
		        	readCount += in.read(b, readCount, count-readCount);
		        }
		        m.put("file_content", b);
		        m.put("file_type", fileTypeList.get(i));
		        
		        fileList.add(m);
			}
		}
		return fileList;
	}
	/**
	 * @Description: 获取一条图片信息
	 * @Author: fudong01
	 * @Create 2017年8月23日上午10:55:56
	 */ 	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getPicFile(String file_index_id,HttpServletRequest request) throws IOException{
		log.info("file_index_id:"+file_index_id);
		GridFS gridFS = getGridFS(Constant.PICTURE);
		DBObject query = new BasicDBObject("_id", file_index_id);
		GridFSDBFile dbFile = gridFS.findOne(query);
		Map m = new HashMap();
		if(dbFile!=null){
			m.put("file_index_id", dbFile.getId());
			m.put("fk_record_id", dbFile.getFilename());
			
			InputStream in = dbFile.getInputStream();
	        int count = Integer.valueOf(String.valueOf(dbFile.getLength()));
	        byte[] b = new byte[count];
	        int readCount = 0;
	        while(readCount<count){
	        	readCount += in.read(b, readCount, count-readCount);
	        }
	        m.put("file_content", b);
	        m.put("file_type", Constant.PICTURE);
		}
        return m;
	}
	/**
	 * @Description:获取一条音视频文件
	 * @Author: fudong01
	 * @Create 2017年8月16日上午9:39:56
	 */ 	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getAudioAndVideoFile(String file_type, String file_index_id,HttpServletRequest request) throws IOException{
		log.info("file_index_id:"+file_index_id);
		GridFS gridFS = getGridFS(file_type);
		DBObject query = new BasicDBObject("_id", file_index_id);
		GridFSDBFile dbFile = gridFS.findOne(query);
		Map m = new HashMap();
		if(dbFile!=null){
			m.put("file_index_id", dbFile.getId());
			m.put("file_name", dbFile.getFilename());
			
			InputStream in = dbFile.getInputStream();
	        int count = Integer.valueOf(String.valueOf(dbFile.getLength()));
	        byte[] b = new byte[count];
	        int readCount = 0;
	        while(readCount<count){
	        	readCount += in.read(b, readCount, count-readCount);
	        }
	        m.put("file_content", b);
	        m.put("file_type", file_type);
	        in.close();
		}
        return m;
	}
	
	public static  String getMongodbIp(String type) {
		//从配置文件中读取名称
		Properties properties=new Properties(); 
		//输入流
		InputStream inputStream = MongoUtil.class.getClassLoader().getResourceAsStream("workflow.properties");
		//加载文件
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//获取ip
		String ip = properties.getProperty(type);
		return ip;
	}
	
	public static void saveFile(String imgName,String file_type,byte[] imgContent){
		 GridFS gfsPhoto = getGridFS(file_type);    
        GridFSInputFile gfsFile = gfsPhoto.createFile(imgContent);    
        gfsFile.setFilename(imgName);    
        gfsFile.save();
	}
	
	public static void removeFile(String filename,String file_type){
		GridFS gfsPhoto = getGridFS(file_type); 
		gfsPhoto.remove(filename);
	}
	
	public static byte[] getBytesByFileName(String filename,String file_type) throws IOException{
		GridFS gfsPhoto = getGridFS(file_type);
        GridFSDBFile imageForOutput = gfsPhoto.findOne(filename);
        InputStream in = imageForOutput.getInputStream();
        return CommonUtil.inputStreamToByte(in);
	}
	
	
	
	public static String getTempFileFromMB(String fileId, String fileName,String fileType, HttpServletRequest request) throws Exception{
		   
		GridFS gfsPhoto = getGridFS(fileType);
        GridFSDBFile imageForOutput = gfsPhoto.findOne(fileId);
        InputStream in = imageForOutput.getInputStream();
        byte[] bfile  = CommonUtil.inputStreamToByte(in);
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
	        FileOutputStream fos = null;  
	        File file = null;
	        
	        String savePath=request.getServletContext().getRealPath("/") + File.separator +"tempfiles";
	        File saveDir = new File(savePath);
			if(!saveDir.exists()){
				saveDir.mkdir();
			}
	        
	        try {  
	         
	            file = new File(savePath + File.separator + fileId +  "."+suffix);
	            fos = new FileOutputStream(file);  
	            fos.write(bfile);  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	            if (fos != null) {  
	                try {  
	                    fos.close();  
	                } catch (IOException e1) {  
	                    e1.printStackTrace();  
	                }  
	            }  
	        }  

		
		return  "http://" + request.getServerName() //服务器地址
                + ":" 
                + request.getServerPort()           //端口号
                + request.getContextPath() + "/tempfiles/" +  fileId +  "."+suffix;
	}
	
}
