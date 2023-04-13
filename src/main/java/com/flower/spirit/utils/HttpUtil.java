package com.flower.spirit.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static void  downLoadFromUrl(String urlStr,String fileName,String savePath){
        try {
        	 URL url = new URL(urlStr); 
             HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
             conn.setConnectTimeout(5*1000);
             conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10; SM-G981B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.162 Mobile Safari/537.36");
             InputStream input = conn.getInputStream();  
             byte[] getData = readInputStream(input);    
             //文件保存位置
           
             File saveDir = new File(savePath);
             if(!saveDir.exists()){
            	  FileUtils.createDirectory(savePath);
             }
             File file = new File(saveDir+File.separator+fileName);    
             FileOutputStream output = new FileOutputStream(file);     
             output.write(getData); 
             if(output!=null){
             	output.close();  
             }
             if(input!=null){
                 input.close();
             }
		} catch (Exception e) {
			
		}
        System.out.println("download success!!");
    }

    public static  byte[] readInputStream(InputStream inputStream) throws IOException {  
        byte[] buffer = new byte[10240];  
        int len = 0;  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
        while((len = inputStream.read(buffer)) != -1) {  
            bos.write(buffer, 0, len);  
        }  
        bos.close();  
        return bos.toByteArray();  
    }
	
}
