package cn.kavel.demo.javantwk.core;

import android.util.Log;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by wjw_w on 2017/3/25.
 */
public class NetHelper {

    private final URL url;

    public NetHelper(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public static void main(String... args) {

    }

    public String getUrlString() {
        return url.toString();
    }

    public URL getUrl() {
        return this.url;
    }

    public String getFileType() {
        try {
            URLConnection targetURLConnection = url.openConnection();
            targetURLConnection.connect();
            BufferedInputStream bis = new BufferedInputStream(targetURLConnection.getInputStream());
            ContentInfoUtil util = new ContentInfoUtil();
            ContentInfo targetFileInfo = util.findMatch(bis);
            Log.v("fileType", targetFileInfo.getMessage());
            return targetFileInfo.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ".Unknown";
    }

    public File download(InputStream in, File dir, String filename) throws IOException {
        if (!dir.exists())
            dir.mkdir();
        String saveFullPath = dir.getAbsolutePath() + "/" + filename;
        File saveFile = new File(saveFullPath);
        int len = -1;
        byte[] buffer = new byte[4096];
        OutputStream out = new FileOutputStream(saveFile);
        DataInputStream dataInputStream = new DataInputStream(in);
        String line;
        while ((line = dataInputStream.readLine()) != null) {
            Log.d("Response", line);
            if (line.equals(""))
                break;
        }
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
        out.close();
        return saveFile;
    }

}
