package com.rf.gjframe.base.util;

import java.io.Serializable;
import java.util.UUID;

/**
 * 生成UUID 工具类
 * @author wangjw
 *
 */
public class IdGenerater {


  private static synchronized Serializable  createId()
  {
    return UUID.randomUUID().toString();
  }

  /**
   * UUID 带"-"的
   * @return
   */
  public static String createStringId() {
    return ((String)createId());
  }
  
  /**
   * UUID 不带"-"的
   * @return
   */
  public static String createSimpleId() {
	  String id = createStringId();
	  id = id.replace("-", "");
	    return id;
  }

}