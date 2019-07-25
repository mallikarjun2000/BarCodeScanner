package com.example.barcodescanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
    public static String createjson(String URL){
        URL url = createUrl(URL);
        if(url == null)
        {
            return null;
        }
        String json = makeHttpRequest(url);

        return json;
    }
    public static URL createUrl(String url){
        if(url == null)
        {
            return null;
        }
        URL Url= null;
        try {
            Url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return Url;
    }
    public static String makeHttpRequest(URL url){
        String jsonResponce=null;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if(code  == 200)
            {
                InputStream inputStream = httpURLConnection.getInputStream();
                jsonResponce = readFromInput(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResponce;
    }
    public  static String readFromInput(InputStream inputStream) throws IOException
    {
        String data="";
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line="";
        while(line!=null)
        {

            line = bufferedReader.readLine();
            data = data + line;
        }
        return data;
    }
}
