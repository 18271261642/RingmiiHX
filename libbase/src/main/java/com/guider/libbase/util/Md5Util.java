package com.guider.libbase.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5工具类
 * 
 * @author donggang
 * 		
 */
public class Md5Util
{
	private static final String TAG = "Md5Util";
	
	/**
	 * 加密
	 * 
	 * @param val
	 * @return
	 */
	public static String toMd5(String val)
	{
		return toMd5(val.getBytes());
	}
	
	private static String getString(byte[] b)
	{
		StringBuffer sb = new StringBuffer();
		int data;
		for (int i = 0; i < b.length; i++)
		{
			data = b[i];
			if (data < 0)
				data += 256;
			if (data < 16)
				sb.append("0");
			sb.append(Integer.toHexString(data));
		}
		return sb.toString().toUpperCase();
	}
	
	/**
	 * 转化为MD5
	 * 
	 * @param data
	 * @return
	 */
	public static String toMd5(byte[] data)
	{
		MessageDigest md5;
		try
		{
			md5 = MessageDigest.getInstance("MD5");
			md5.update(data);
			byte[] m = md5.digest();// 加密
			return getString(m);
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
