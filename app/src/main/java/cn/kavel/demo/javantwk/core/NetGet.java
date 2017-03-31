package cn.kavel.demo.javantwk.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by wjw_w on 2017/3/25.
 */
public class NetGet {
    public static void main(String... args){
        GetResolved("http://www.szu.edu.cn/images/szu.png");
    }
    public static void GetResolved(String urlStr){
        try {
            URL url = new URL(urlStr);
            URLConnection uc = url.openConnection();
            uc.connect();
            InputStream inputStream = uc.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            String fileType = URLConnection.guessContentTypeFromStream(bis);
            System.out.println(fileType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
