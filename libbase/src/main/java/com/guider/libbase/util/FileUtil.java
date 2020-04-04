package com.guider.libbase.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件工具类
 */
public class FileUtil
{
	private static final String	TAG								= "FileUtil";
	
	/**
	 * 支持的图片类型
	 */
	public static final String	PICTURE_TYPE					= "|jpeg|jpg|JPEG|JPG|png|gif|bmp|BMP|";
	/**
	 * 支持的视频类型
	 */
	public static final String	VIDEO_TYPE						= "|mp4|MP4|";
	
	/**
	 * 头像等小图片大小限制
	 */
	public static final int		FILE_PICTURE_SMALL_LENGTH_LIMIT	= 1024 * 512;
	/**
	 * 正常上传图片大小限制
	 */
	public static final int		FILE_PICTURE_BIG_LENGTH_LIMIT	= 1024 * 1024 * 2;
	/**
	 * 微视频大小限制
	 */
	public static final int		FILE_VIDEO_LENGTH_LIMIT			= 1024 * 1024 * 4;
	
	/**	
	 * 测试是否是支持的图片类型
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 0 是支持的图片格式; -1 后缀无法获得; -2 不是支持的图片格式;
	 */
	public static int isPicture(String filePath)
	{
		String fileType = getFileSuffix(filePath);
		if (null == fileType)
		{
			Log.e(TAG, "file suffix error.");
			return -1;
		}
		Log.d(TAG, fileType);
		if (-1 == PICTURE_TYPE.indexOf("|" + fileType + "|"))
		{
			return -2;
		}
		
		return 0;
	}
	
	/**
	 * 测试文件是否大小在限制内
	 * 
	 * @param filePath
	 *            文件路径
	 * @param lengthLimit
	 *            大小限制
	 * @return 0 在限制内 -1 获取文件长度时发生错误 -2 不在限制内
	 */
	public static int isFileInLimit(String filePath, int lengthLimit)
	{
		int fileLength = (int) getFileLength(filePath);
		if (0 > fileLength)
		{
			Log.e(TAG, "file length error.");
			return -1;
		}
		if (lengthLimit < fileLength)
		{
			return -2;
		}
		
		return 0;
	}
	
	/**
	 * 测试是否是支持的视频格式
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 0 是支持的视频格式; -1 后缀无法获得; -2 不是支持的视频格式;
	 */
	public static int isVideo(String filePath)
	{
		String fileType = getFileSuffix(filePath);
		if (null == fileType)
		{
			Log.e(TAG, "file suffix error.");
			return -1;
		}
		Log.d(TAG, fileType);
		if (-1 == VIDEO_TYPE.indexOf("|" + fileType + "|"))
		{
			return -2;
		}
		
		return 0;
	}
	
	/**
	 * 获得文件的后缀
	 * 
	 * @param filePath
	 *            文件路径
	 * @return null 路径格式错误; !null 成功获得后缀;
	 */
	public static String getFileSuffix(String filePath)
	{
		int pos = filePath.lastIndexOf('.');
		if (-1 == pos)
		{
			Log.e(TAG, "not found suffix.");
			return null;
		}
		return filePath.substring(++pos);
	}
	
	/**
	 * 从文件路径中获得文件名
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 若发现'/'字符则从此处截取; 若没有发现'/'字符，则默认路径即为文件名;
	 */
	public static String getFileName(String filePath)
	{
		int pos = filePath.lastIndexOf('/');
		if (-1 == pos)
		{
			Log.e(TAG, "not found /.");
			return filePath;
		}
		String ex = getFileSuffix(filePath);
		if (null == ex && ex.isEmpty())
			return filePath.substring(++pos);
		return filePath.substring(++pos, filePath.length() - ex.length() - 1);
	}

	/**
	 * 从文件路径中获得文件名
	 *
	 * @param filePath
	 *            文件路径
	 * @return 若发现'/'字符则从此处截取; 若没有发现'/'字符，则默认路径即为文件名;
	 */
	public static String getFileFullName(String filePath)
	{
		int pos = filePath.lastIndexOf('/');
		if (-1 == pos)
		{
			Log.e(TAG, "not found /.");
			return filePath;
		}
		return filePath.substring(++pos, filePath.length());
	}

	/**
	 * 获得文件名之前的路径
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 若发现'/'字符则从此处截取; 若没有发现'/'字符，则默认路径即为文件名;
	 */
	public static String getFilePath(String filePath)
	{
		int pos = filePath.lastIndexOf('/');
		if (-1 == pos)
		{
			Log.e(TAG, "not found /.");
			return filePath;
		}
		return filePath.substring(0, pos + 1);
	}
	
	/**
	 * 获得文件大小
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 0<= 文件大小; -1 文件不存在或者不是文件;
	 */
	public static long getFileLength(String filePath)
	{
		File file = new File(filePath);
		if (file.exists() && file.isFile())
		{
			return file.length();
		} else
		{
			return -1;
		}
	}
	
	/**
	 * 读取文件文件
	 * 
	 * @param filePath
	 * @return null 读取失败; !null 读取成功;
	 */
	public static byte[] readFileByte(String filePath)
	{
		byte[] bytes = null;
		try
		{
			FileInputStream fileInputStream = new FileInputStream(filePath);
			ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[1024];
			int len = fileInputStream.read(buffer);
			while (-1 != len)
			{
				byteArrayInputStream.write(buffer, 0, len);
				len = fileInputStream.read(buffer);
			}
			bytes = byteArrayInputStream.toByteArray();
			
			byteArrayInputStream.close();
			fileInputStream.close();
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		return bytes;
	}
	
	/**
	 * byte数组写入文件
	 * @param filePath
	 * @param fileContent
	 * @return 0 写入成功; 0> 写入失败;
	 */
	public static int writeFileByte(String filePath, byte[] fileContent)
	{
		try
		{
			File file = new File(filePath);
			file.setWritable(true);
			if (file.exists()) // 文件存在删除
			{
				file.delete();
			}
			if (!file.createNewFile()) // 创建
			{
				Log.e("Web", TAG + filePath + " create error.");
				return -2;
			}
			
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(fileContent);
			fileOutputStream.close();
		} catch (Exception e)
		{
			e.printStackTrace();
			Log.e("Web", TAG + " writeFile " + e.toString());
			return -1;
		}
		
		return 0;
	}
	
	/**
	 * 读取文件
	 * @param filePath
	 * @return
	 */
	public static String readFileString(String filePath)
	{
		File file = new File(filePath);
		
		try
		{
			if (!file.exists())
			{
				return null;
			}
			
			FileInputStream fileInputStream = new FileInputStream(filePath);
			
			int length = fileInputStream.available();
			
			byte[] buffer = new byte[length];
			fileInputStream.read(buffer);
			fileInputStream.close();
			
			return new String(buffer);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 写入文件
	 * 
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	public static void writeFileString(String fileName, String content)
			throws IOException
	{
		
		File file = new File(fileName);
		
		FileOutputStream fos = new FileOutputStream(file);
		
		byte[] bytes = content.getBytes();
		
		fos.write(bytes);
		
		fos.close();
	}
	

	/**
	 * 拷贝文件
	 * @param input
	 * @param fileDest
	 */
	public static void copyFile(InputStream input, String fileDest)
	{
		OutputStream output = null;
        try
		{
			output = new FileOutputStream(fileDest);	
		} catch (Exception e)
		{
			Log.e(TAG, "copyFile dest error : " + e.toString());
			if (null != output)
			{
				try
				{
					output.close();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
        
        doCopyFile(input, output);
	}
	/**
	 * 拷贝文件
	 * @param fileSrc
	 * @param fileDest
	 */
	public static void copyFile(String fileSrc, String fileDest)
	{
        OutputStream output = null;
        try
		{
			output = new FileOutputStream(fileDest);	
		} catch (Exception e)
		{
			Log.e(TAG, "copyFile dest error : " + e.toString());
			if (null != output)
			{
				try
				{
					output.close();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
        
        InputStream input = null;  
        try
		{
			input = new FileInputStream(fileSrc);  
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			Log.e(TAG, "copyFile src error : " + e.toString());
			if (null != input)
			{
				try
				{
					input.close();
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
        
        doCopyFile(input, output);
	}

	/**
	 * 执行复制文件的操作
	 * @param input 输入流
	 * @param output 输出流
     */
	private static void doCopyFile(InputStream input, OutputStream output)
	{
		if (null == input || null == output)
		{
			Log.e(TAG, "doCopyFile param is null.");
			return;
		}
		try
		{
	        byte[] buffer = new byte[1024];  
	        int length = input.read(buffer);
	        while(0 < length)
	        {
	        	output.write(buffer, 0, length); 
	            length = input.read(buffer);
	        }
	        output.flush();  
	        input.close();  
	        output.close();  
		}catch(Exception e)
		{
			Log.e(TAG, "关闭输入输出流出错");
			if(null != input)
			{
				try
				{
					input.close();
				} catch (IOException e1)
				{
					Log.e(TAG, "关闭输入流出错");
				}
			}
			if(null != output)
			{
				try
				{
					output.close();
				} catch (IOException e1)
				{
					Log.e(TAG, "关闭输出流出错");
				}
			}
		}
	}
	
	/**
	 * 获得文件类型
	 * @param filePath
	 * @return 0 图片 1 短视频 null 其他
	 */
	public static String getMediaType(String filePath)
	{
		if (null == filePath || filePath.isEmpty())
		{
			Log.e("Web", TAG + " getMediaType 参数为空。");
			return null;
		}
		if (0 == FileUtil.isPicture(filePath))
		{
			return "0";
		} else if (0 == FileUtil.isVideo(filePath))
		{
			return "1";
		} else
		{
			return null;
		}
	}
	
	/**
	 * 追加文件：使用FileWriter
	 * @param fileName
	 * @param content
	 */
	public static void apendFile(String fileName, String content)
	{
		FileWriter writer = null;
		try
		{
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(fileName, true);
			writer.write(content);
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 测试文件是否在本地存在
	 * @param fielLocalPath 文件路径
	 * @return
	 */
	@Deprecated
	public static boolean isDownload(String fielLocalPath)
	{
		File file = new File(fielLocalPath);
		long length = file.length();
		return (file.exists() && (0 != length));
	}
	
	/**
	 * 删除本地文件
	 * @param fileLocalPath 文件路径
	 * @return
	 */
	public static boolean deleteFile(String fileLocalPath)
	{
		if (null == fileLocalPath || fileLocalPath.isEmpty())
		{
			return true;
		}
		File file = new File(fileLocalPath);
		if (!file.isFile())
			return true;
		if (!file.exists())
			return true;
		return file.delete();
	}
	
	
	
	/**
	 * 把bitmap保存到文件中
	 * @param bm bimap对象
	 * @param filePath 文件路径
	 * @throws IOException
	 */
	public static int saveFile(Bitmap bm, String filePath)
	{
		try
		{
			File myCaptureFile = new File(filePath);
			// myCaptureFile.deleteOnExit();
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (Exception e)
		{
			Log.e("Web132", e.toString());
			return -1;
		}
		
		return 0;
	}
	
	

	/**
	 * 文件改名
	 * @param oldPath 旧的文件名
	 * @param newPath 新的文件名
	 */
	public static void renameFile(String oldPath, String newPath)
	{
		File file = new File(oldPath);
		if (!file.isFile() || !file.exists())
			return;
		
		deleteFile(newPath);
		file.renameTo(new File(newPath));
	}

	/**
	 *创建文件
	 * @param path 文件路径
	 * @return
     */
	public static boolean createPath(String path)
	{
		File file = new File(path);
		if (!file.exists() && !file.mkdirs())
		{
			return false;
		}
		return true;
	}

	/**
	 * 是否存在
	 * @param path 文件路径
	 * @return
     */
	public static boolean isExists(String path)
	{
		File file = new File(path);
		return file.exists();
	}

	/**
	 * 是否为空
	 * @param path 文件路径
	 * @return
     */
	public static boolean isEmpty(String path)
	{
		File file = new File(path);
		long length = file.length();
		return (file.exists() && (0 != length));
	}

	/**
	 * 计算MD5校验值
	 * @param path 文件路径
	 * @return
     */
	public static String calculateMd5(String path)
	{
		return calculateMd5(new File(path));
	}

	/**
	 * 计算MD5校验值
	 * @param updateFile
	 * @return
     */
	public static String calculateMd5(File updateFile)
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "Exception while getting digest", e);
			return null;
		}

		InputStream is;
		try {
			is = new FileInputStream(updateFile);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Exception while getting FileInputStream", e);
			return null;
		}

		byte[] buffer = new byte[8192];
		int read;
		try
		{
			while ((read = is.read(buffer)) > 0)
			{
				digest.update(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			// Fill to 32 chars
			output = String.format("%32s", output).replace(' ', '0');
			return output;
		} catch (IOException e)
		{
			throw new RuntimeException("Unable to process file for MD5", e);
		} finally
		{
			try
			{
				is.close();
			} catch (IOException e)
			{
				Log.e(TAG, "Exception on closing MD5 input stream", e);
			}
		}
	}
}
