package cn.wuweikang;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {

    public static String saveToXMLFile(Map<String, String> listMaps,
                                       Map<String, StringBuffer> ecgDataMaps) {
        String path = null;
        try {
            File fileDir = new File(Environment.getExternalStorageDirectory() + "/ECGDATA/XML");
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            path = fileDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".xml";

            FileOutputStream fos = new FileOutputStream(path);
            XmlSerializer serXML = Xml.newSerializer();
            serXML.setOutput(fos, "UTF-8");
            serXML.startDocument("UTF-8", true);
            serXML.startTag(null, "ECG");
            serXML.attribute(null, "Version", "1");
            serXML.attribute(null, "LS", "12");
////			serXML.attribute(null, "SAMPLERATE", ecgAttr.get("SAMPLERATE"));
//			serXML.attribute(null, "FNotch", ecgAttr.get("FilterAC"));
//			serXML.attribute(null, "FHP", ecgAttr.get("FilterBase"));
//			serXML.attribute(null, "FLP", ecgAttr.get("FilterMC"));
//			serXML.startTag(null, "Patient");
//			serXML.startTag(null, "Name");
//			serXML.text(pInfo.get("Name"));
//			serXML.endTag(null, "Name");
//			serXML.startTag(null, "ID");
//			serXML.text(pInfo.get("ID"));
//			serXML.endTag(null, "ID");
//			serXML.startTag(null, "Age");
//			serXML.text(pInfo.get("AGE"));
//			serXML.endTag(null, "Age");
//			serXML.startTag(null, "Gender");
//			serXML.text(pInfo.get("Gender"));
//			serXML.endTag(null, "Gender");
////			serXML.startTag(null, "RecordSecond");
////			serXML.text(pInfo.get("RecordSecond"));
////			serXML.endTag(null, "RecordSecond");
//			serXML.startTag(null, "CheckDateTime");
//			serXML.text(DateUtils.sdf_yyyy_MM_dd_HH_mm_ss.format(new Date(timeLong)));
//			serXML.endTag(null, "CheckDateTime");
//			serXML.endTag(null, "Patient");
            if (ecgDataMaps != null) {

                serXML.startTag(null, "Ch0");
                serXML.text(ecgDataMaps.get("Ch0").toString());
                serXML.endTag(null, "Ch0");
                serXML.startTag(null, "Ch1");
                serXML.text(ecgDataMaps.get("Ch1").toString());
                serXML.endTag(null, "Ch1");
                serXML.startTag(null, "Ch2");
                serXML.text(ecgDataMaps.get("Ch2").toString());
                serXML.endTag(null, "Ch2");
                serXML.startTag(null, "Ch3");
                serXML.text(ecgDataMaps.get("Ch3").toString());
                serXML.endTag(null, "Ch3");
                serXML.startTag(null, "Ch4");
                serXML.text(ecgDataMaps.get("Ch4").toString());
                serXML.endTag(null, "Ch4");
                serXML.startTag(null, "Ch5");
                serXML.text(ecgDataMaps.get("Ch5").toString());
                serXML.endTag(null, "Ch5");
                serXML.startTag(null, "Ch6");
                serXML.text(ecgDataMaps.get("Ch6").toString());
                serXML.endTag(null, "Ch6");
                serXML.startTag(null, "Ch7");
                serXML.text(ecgDataMaps.get("Ch7").toString());
                serXML.endTag(null, "Ch7");
                serXML.startTag(null, "Ch8");
                serXML.text(ecgDataMaps.get("Ch8").toString());
                serXML.endTag(null, "Ch8");
                serXML.startTag(null, "Ch9");
                serXML.text(ecgDataMaps.get("Ch9").toString());
                serXML.endTag(null, "Ch9");
                serXML.startTag(null, "Ch10");
                serXML.text(ecgDataMaps.get("Ch10").toString());
                serXML.endTag(null, "Ch10");
                serXML.startTag(null, "Ch11");
                serXML.text(ecgDataMaps.get("Ch11").toString());
                serXML.endTag(null, "Ch11");
            }
            serXML.startTag(null, "Parameter");
//			serXML.attribute(null, "AnalysisState", pms.get("AnalysisState"));
//			serXML.attribute(null, "PWidth", pms.get("PWidth"));
//			serXML.attribute(null, "PExist", pms.get("PExist"));
            serXML.attribute(null, "RRInterval", listMaps.get("RRInterval"));
            serXML.attribute(null, "HeartRate", listMaps.get("HeartRate"));
            serXML.attribute(null, "PRInterval", listMaps.get("PRInterval"));
            serXML.attribute(null, "QRSDuration", listMaps.get("QRSDuration"));
            serXML.attribute(null, "QTD", listMaps.get("QTD"));
            serXML.attribute(null, "QTC", listMaps.get("QTC"));
            serXML.attribute(null, "PAxis", listMaps.get("PAxis"));
            serXML.attribute(null, "QRSAxis", listMaps.get("QRSAxis"));
            serXML.attribute(null, "TAxis", listMaps.get("TAxis"));
            serXML.attribute(null, "RV5SV1", listMaps.get("RV5SV1"));
            serXML.attribute(null, "RV5", listMaps.get("RV5"));
            serXML.attribute(null, "SV1", listMaps.get("SV1"));
            serXML.endTag(null, "Parameter");
//			Map<String, String> dmd = listMaps.get("DMData");
//			serXML.startTag(null, "DMData");
//			serXML.attribute(null, "PStart", dmd.get("PStart"));
//			serXML.attribute(null, "PEnd", dmd.get("PEnd"));
//			serXML.attribute(null, "QRSStart", dmd.get("QRSStart"));
//			serXML.attribute(null, "QRSEnd", dmd.get("QRSEnd"));
//			serXML.attribute(null, "TEnd", dmd.get("TEnd"));
//			serXML.attribute(null, "DMDataCount", dmd.get("DMDataCount"));
//			serXML.endTag(null, "DMData");
            String result = listMaps.get("Auto_Result");
            String Auto_Class = listMaps.get("Auto_Class");
            serXML.startTag(null, "AnalysisResult");
//			try{
//			    Iterator<Map.Entry<String, String>> itClass = ECGDataUtil.decodeResult(result).getClassfy().entrySet().iterator();
//			    Map.Entry<String, String> mapClass = itClass.next();
//                serXML.startTag(null, "Classfly");
//                serXML.attribute(null, "Code", mapClass.getKey());
//                serXML.text(mapClass.getValue());
//                serXML.endTag(null, "Classfly");
//                Iterator<Map.Entry<String, String>> itResut = ECGDataUtil.decodeResult(result).getResult().entrySet().iterator();
//                while (itResut.hasNext()){
//                    Map.Entry<String, String> itMap = itResut.next();
//                    serXML.startTag(null, "Result");
//                    serXML.attribute(null, "Code", itMap.getKey());
//                    serXML.text(itMap.getValue());
//                    serXML.endTag(null, "Result");
//                }
//			}catch (Exception e){
//				e.printStackTrace();
//                serXML.startTag(null, "Classfly");
//                serXML.attribute(null, "Code", "0");
//                serXML.text("分析失败");
//                serXML.endTag(null, "Classfly");
//                serXML.startTag(null, "Result");
//                serXML.attribute(null, "Code", "0");
//                serXML.text("分析失败");
//                serXML.endTag(null, "Result");
//			}
            serXML.startTag(null, "Classfly");
//                serXML.attribute(null, "Code", "0");
            serXML.text(Auto_Class);
            serXML.endTag(null, "Classfly");
            serXML.startTag(null, "Result");
//                serXML.attribute(null, "Code", "0");
            serXML.text(result);
            serXML.endTag(null, "Result");
            serXML.endTag(null, "AnalysisResult");

//			serXML.startTag(null, "WaveQuality");
//			serXML.text(analys.get("WaveQuality"));
//			serXML.endTag(null, "WaveQuality");
            serXML.endTag(null, "ECG");
            serXML.endDocument();
            fos.flush();
            fos.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }


    /**
     * 解析文件
     *
     * @param result
     * @param is
     * @return
     */
    @SuppressLint("UseSparseArrays")
    public static Map<String, String> parserLevelAdvice(String result, InputStream is) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();
            XmlPullParser pullParser = pullFactory.newPullParser();
            pullParser.setInput(is, "UTF-8");
            int eventType = pullParser.getEventType();
            while (XmlPullParser.END_DOCUMENT != eventType) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String[] r = result.split(" ");
                        for (int i = 0; i < r.length; i++) {
                            String name = pullParser.getAttributeValue(null, "name");
                            String level = pullParser.getAttributeValue(null, "level");
                            if (r[i].equals(name)) {
                                String strLevel = level;
                                map.put(strLevel, "");
                                break;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    default:
                        break;
                }
                eventType = pullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 向/sdcard/apmt路径下添加文本
     *
     * @param text 文本信息
     */
    public static void addFile(String text) {
        addFile(text, Environment.getExternalStorageDirectory().getPath() + "/apmt/", "wechat_pay.txt");
    }

    /**
     * 想该路径添加文本
     *
     * @param text 文本信息
     * @param path 路径
     */
    public static void addFile(String text, String path, String fileName) {
        File file = new File(path + fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(path + fileName);
            fileWriter.write(text);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (StringUtil.isEmpty(fileName))
            return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    /**
     * 清空文件夹或删除文件
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * 获取手机剩余内存
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringUtil.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 4.4版本以上处理选择图片Uri的问题
     *
     * @param context
     * @param uri
     * @return
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
