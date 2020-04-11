package com.palmapp.master.baselib.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.palmapp.master.baselib.GoCommonEnv;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VideoStreamWorkerTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        String data = params[0];
        // Application class where i did open DiskLruCache
        String key = FileUtils.hashKeyForDisk(data);
        File cache = GoCommonEnv.INSTANCE.getApplication().getCacheDir();
        if (cache == null || !cache.exists()) {
            return null;
        }
        File video = new File(cache.getAbsolutePath() + File.separator + key);
        if (!video.exists() || video.length() == 0) {
            try {
                video.createNewFile();
                downloadUrlToStream(data, video, new FileOutputStream(video));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean downloadUrlToStream(String urlString, File file, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = urlConnection.getInputStream();
            int n;
            byte[] buffer = new byte[8 * 1024];
            while (-1 != (n = stream.read(buffer))) {
                outputStream.write(buffer, 0, n);
            }
            outputStream.close();
            stream.close();
            Log.i(getTag(), "Stream closed all done");
            return true;
        } catch (final IOException e) {
            file.delete();
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return false;
    }

    private String getTag() {
        return getClass().getSimpleName();
    }

}