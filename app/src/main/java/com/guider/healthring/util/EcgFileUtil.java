package com.guider.healthring.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EcgFileUtil {
    private static final String ECG_FILE_NAME = "temp.lp4";

    public boolean saveEcgFile(Context context, ArrayList<Byte> datalist) {
        byte[] array = new byte[datalist.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = datalist.get(i);
        }
        try {
            FileOutputStream fos = new FileOutputStream(makeEcgFilePath(context, ECG_FILE_NAME));
            fos.write(array);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String makeEcgFilePath(Context context, String fileName) {
        return context.getFilesDir()
                + File.separator + "ECG" + File.separator + fileName;
    }
}
