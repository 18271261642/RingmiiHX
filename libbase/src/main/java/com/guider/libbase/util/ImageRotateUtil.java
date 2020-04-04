package com.guider.libbase.util;

import android.media.ExifInterface;

import java.io.IOException;

/**
 * 图片旋转的工具类
 * Created by WXK on 2016/11/29.
 */
public class ImageRotateUtil {
    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static double readPictureDegree(String path) {
        double degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90.0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180.0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270.0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return degree;
    }


    /**
     * 设置图片属性：图片的旋转角度,有三种值,90,180,270
     * @param path 图片绝对路径
     * @param degree 图片角度
     * @return 成功与否 0成功,-1异常失败
     */
    public static double setPictureDegree(String path, double degree) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int rotateDegree = 0;
            switch ((int)degree) {
                case 90 :
                    rotateDegree = ExifInterface.ORIENTATION_ROTATE_90;
                    break;
                case 180 :
                    rotateDegree = ExifInterface.ORIENTATION_ROTATE_180;
                    break;
                case 270 :
                    rotateDegree = ExifInterface.ORIENTATION_ROTATE_270;
                    break;
                default:
                    rotateDegree = ExifInterface.ORIENTATION_NORMAL;
            }
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(rotateDegree));
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }


    /**
     * 根据当前图片的旋转角度设置旋转90度后的角度
     * @param degree  图片的当前旋转角度
     * @param flag 1表示左旋转,2表示右旋转
     * @return
     */
    public static double getRotatedDegree(double degree, double flag) {
        int rotateDegree = 0;
        if (1 == flag) {//左旋转
            switch ((int)degree) {
                case 0:
                    rotateDegree = 270;
                    break;
                case 90 :
                    rotateDegree = 0;
                    break;
                case 180 :
                    rotateDegree = 90;
                    break;
                case 270 :
                    rotateDegree = 180;
                    break;
            }
        } else {//右旋转
            switch ((int)degree) {
                case 0:
                    rotateDegree = 90;
                    break;
                case 90 :
                    rotateDegree = 180;
                    break;
                case 180 :
                    rotateDegree = 270;
                    break;
                case 270 :
                    rotateDegree = 0;
                    break;
            }
        }
        return  rotateDegree;
    }

    /**
     * 获取图片的宽
     * @param path
     * @return
     */
    public static int getImageWidth(String path) {
        int width = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            width = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return width;
    }

    /**
     * 获取图片的高
     * @param path 图片路径
     * @return
     */
    public static int getImageHeight(String path) {
        int height = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            height = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return height;
    }
}
