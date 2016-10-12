package com.adryan.app.comunes;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * Created by vquispe on 04/06/2015.
 */
public class LogFile {

    //public static FileHandler logger = null;
    private static String fileName = "Adryan_Log";
    private static Context myContext = null;

    //static boolean isStorageAvailable = true;
    //static boolean isStorageWriteable = false;
    static String pathFile = null;
    static String state = null;

    public LogFile(Context context) {
        myContext = context;
        pathFile = myContext.getExternalFilesDir(null).getAbsolutePath();
        //state = Environment.getStorageState(new File(pathFile));
    }

    public static void addRecordLog(String message) {
        /*
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            isStorageAvailable = isStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            isStorageAvailable = true;
            isStorageWriteable = false;
        } else {
            isStorageAvailable = isStorageWriteable = false;
        }
        */
        File dir = new File(pathFile);

        //if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File logFile = new File(pathFile + fileName + ".txt");

            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            try {
                BufferedWriter buffer = new BufferedWriter(new FileWriter(logFile, true));

                buffer.write(message + "\r\n");
                buffer.newLine();
                buffer.flush();
                buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}

    }

}
