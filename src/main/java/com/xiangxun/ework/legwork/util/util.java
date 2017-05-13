package com.xiangxun.ework.legwork.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class util {
 /**
  * 通过get方法获取数据
  */
 public static String  getHttpJsonByhttpclient(String  fromurl)
 {
    
   try{
         Log.v("OA_SYSTEM_TEST","使用了httget");
            HttpGet geturl=new HttpGet(fromurl);
            DefaultHttpClient httpclient=new DefaultHttpClient();
            HttpResponse response=httpclient.execute(geturl);
            Log.v("OA_SYSTEM_TEST","响应码"+response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode()==200)
           {
               String returnStr=EntityUtils.toString(response.getEntity(),"utf-8");
            Log.v("OA_SYSTEM_TEST","返回值"+returnStr);
               return returnStr;
           } else
           {
            Log.v("zms","访问网络返回数据失败，错误码:"+response.getStatusLine().getStatusCode());
           }
        }
        catch(IOException e)
        {
         e.printStackTrace();
        }
   return null;
  
 }
 /**
  * 获取数据io的操作
  * @param fromurl
  * @return
  */
 public static String  getHttpJsonByurlconnection(String fromurl)
 {
  try
  {
   Log.v("zms","使用httpurlconnection");
   ByteArrayOutputStream os=new ByteArrayOutputStream();
   byte[] data =new byte[1024];
   int len=0; 
   URL url=new URL(fromurl);
   HttpURLConnection conn=(HttpURLConnection)url.openConnection();
   InputStream in=conn.getInputStream();
   while ((len=in.read(data))!=-1)
   {
    os.write(data,0,len);
   }
   in.close();
   return new String(os.toByteArray());
  } catch (Exception e)
  {
   e.printStackTrace();
  }
 
  return null;
 }
 /**
  * 用post请求数据
  * @param fromurl
  * @param params
  * @return
  */
 public static String  PostHttpJsonByhttpclient(String  fromurl,List<NameValuePair> params)
 {
		
   try{
         HttpPost httpRequest = new HttpPost(fromurl); // 创建HttpPost对象
         httpRequest.setEntity(new UrlEncodedFormEntity(params, "utf-8")); // 设置编码方式

         DefaultHttpClient httpclient=new DefaultHttpClient();
         HttpResponse response=httpclient.execute(httpRequest);
          Log.v("zms","响应码"+response.getStatusLine().getStatusCode());
           if (response.getStatusLine().getStatusCode()==200)
           {
               String returnStr=EntityUtils.toString(response.getEntity(),"utf-8");
            Log.v("zms","返回值"+returnStr);
               return returnStr;
           } else
           {
            Log.v("zms","访问网络返回数据失败，错误码:"+response.getStatusLine().getStatusCode());
           }
       
        }
        catch(IOException e)
        {
         e.printStackTrace();
        }
   return null;
  
 }

}