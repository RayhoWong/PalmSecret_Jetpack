package com.picstudio.photoeditorplus.stasm;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

/**
 * @author ruanjiewei
 * @since 2018.04.11
 * 辅助操作工具类
 */
public class Utils {

    public static boolean saveStringToFile(final String str, final String filePathName) {
        boolean result = false;
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter writer = null;
        try {
            File newFile = createNewFile(filePathName, false);
            fileOutputStream = new FileOutputStream(newFile);
            writer = new OutputStreamWriter(fileOutputStream);
            writer.append(str);
            writer.flush();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }

    public static File createNewFile(String path, boolean append) {
        File newFile = new File(path);
        if (!append ) {
            if (newFile.exists()) {
                newFile.delete();
            }
        }
        if (!newFile.exists()) {
            try {
                File parent = newFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                newFile.createNewFile();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newFile;
    }

    public static void saveBitmapToFile(Bitmap bitmap, String targetPath) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(targetPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buf);
        return buf.array();
    }

    public static File getDirFile(Context context, String dirname) {
        return new File(context.getFilesDir(), dirname);
    }

    public static String tryExportResource(Context context, int resourceId, String dirname) {
        String fullName = context.getResources().getString(resourceId);
        String resName = fullName.substring(fullName.lastIndexOf("/") + 1);
        try {
            InputStream is = context.getResources().openRawResource(resourceId);
            File resDir = getDirFile(context, dirname);
            File resFile = new File(resDir, resName);
            if (resFile.exists()) {
                return resFile.getAbsolutePath();
            } else {
                resFile.createNewFile();
            }

            FileOutputStream os = new FileOutputStream(resFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            return resFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
