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
  * ͨ��get������ȡ����
  */
 public static String  getHttpJsonByhttpclient(String  fromurl)
 {
    
   try{
         Log.v("OA_SYSTEM_TEST","ʹ����httget");
            HttpGet geturl=new HttpGet(fromurl);
            DefaultHttpClient httpclient=new DefaultHttpClient();
            HttpResponse response=httpclient.execute(geturl);
            Log.v("OA_SYSTEM_TEST","��Ӧ��"+response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode()==200)
           {
               String returnStr=EntityUtils.toString(response.getEntity(),"utf-8");
            Log.v("OA_SYSTEM_TEST","����ֵ"+returnStr);
               return returnStr;
           } else
           {
            Log.v("zms","�������緵������ʧ�ܣ�������:"+response.getStatusLine().getStatusCode());
           }
        }
        catch(IOException e)
        {
         e.printStackTrace();
        }
   return null;
  
 }
 /**
  * ��ȡ����io�Ĳ���
  * @param fromurl
  * @return
  */
 public static String  getHttpJsonByurlconnection(String fromurl)
 {
  try
  {
   Log.v("zms","ʹ��httpurlconnection");
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
  * ��post��������
  * @param fromurl
  * @param params
  * @return
  */
 public static String  PostHttpJsonByhttpclient(String  fromurl,List<NameValuePair> params)
 {
		
   try{
         HttpPost httpRequest = new HttpPost(fromurl); // ����HttpPost����
         httpRequest.setEntity(new UrlEncodedFormEntity(params, "utf-8")); // ���ñ��뷽ʽ

         DefaultHttpClient httpclient=new DefaultHttpClient();
         HttpResponse response=httpclient.execute(httpRequest);
          Log.v("zms","��Ӧ��"+response.getStatusLine().getStatusCode());
           if (response.getStatusLine().getStatusCode()==200)
           {
               String returnStr=EntityUtils.toString(response.getEntity(),"utf-8");
            Log.v("zms","����ֵ"+returnStr);
               return returnStr;
           } else
           {
            Log.v("zms","�������緵������ʧ�ܣ�������:"+response.getStatusLine().getStatusCode());
           }
       
        }
        catch(IOException e)
        {
         e.printStackTrace();
        }
   return null;
  
 }

}