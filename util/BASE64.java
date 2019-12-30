package com.rf.gjframe.base.util;

/**
 * BASE64工具类，提供了编码和解码方法
 * @author wangjw
 *
 */
public class BASE64 {

	/**
	 * 编码字符串为base64
	 * @param data
	 * @return
	 */
	  public static String encodeBase64(String data)
	  {
	    return encodeBase64(data.getBytes());
	  }

	  /**
	   * 编码base64
	   * @param data
	   * @return
	   */
	  public static String encodeBase64(byte[] data)
	  {
	    int len = data.length;
	    StringBuffer ret = new StringBuffer((len / 3 + 1) * 4);
	    for (int i = 0; i < len; ++i) {
	      int c = data[i] >> 2 & 0x3F;
	      ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
	      c = data[i] << 4 & 0x3F;
	      if (++i < len) {
	        c |= data[i] >> 4 & 0xF;
	      }
	      ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
	      if (i < len) {
	        c = data[i] << 2 & 0x3F;
	        if (++i < len) {
	          c |= data[i] >> 6 & 0x3;
	        }
	        ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
	      } else {
	        ++i;
	        ret.append('=');
	      }

	      if (i < len) {
	        c = data[i] & 0x3F;
	        ret.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(c));
	      } else {
	        ret.append('=');
	      }
	    }
	    return ret.toString();
	  }

	  /**
	   * 解码base64字符串
	   * @param data
	   * @return
	   */
	  public static String decodeBase64(String data)
	  {
	    return decodeBase64(data.getBytes());
	  }

	  /**
	   * 解码base64
	   * @param data
	   * @return
	   */
	  public static String decodeBase64(byte[] data)
	  {
	    int len = data.length;
	    StringBuffer ret = new StringBuffer(len * 3 / 4);
	    for (int i = 0; i < len; ++i) {
	      int c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(data[i]);
	      ++i;
	      int c1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(data[i]);
	      c = c << 2 | c1 >> 4 & 0x3;
	      ret.append((char)c);
	      if (++i < len) {
	        c = data[i];
	        if (61 == c) {
	          break;
	        }
	        c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf((char)c);
	        c1 = c1 << 4 & 0xF0 | c >> 2 & 0xF;
	        ret.append((char)c1);
	      }

	      if (++i < len) {
	        c1 = data[i];
	        if (61 == c1) {
	          break;
	        }
	        c1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf((char)c1);
	        c = c << 6 & 0xC0 | c1;
	        ret.append((char)c);
	      }
	    }
	    return ret.toString();
	  }
}