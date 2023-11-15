package com.flower.spirit.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.utils.DouUtil;
import com.flower.spirit.utils.XbogusUtil;

@Service
public class DouYinService {

	private static String domain = "https://www.douyin.com/";
	
	private static String loginQRdomain = "https://sso.douyin.com/get_qrcode/?";
	
	private static String loginCheckDomain = "https://sso.douyin.com/check_qrconnect/?";
	
	private static String fp ="";
	
	private static String cookie_ttwid ="";
	
	
	public AjaxEntity getDouYinCodeLogin() throws NoSuchAlgorithmException, IOException {
		if(fp.equals("")) {
			fp = DouUtil.getFp();
		}
		if(cookie_ttwid.equals("")) {
			cookie_ttwid =domain+"ttwid="+DouUtil.getTtwid();
		}
		String xBogusString ="service=https%3A%2F%2Fwww.douyin.com&need_logo=false&need_short_url=true&device_platform=web_app&aid=6383&account_sdk_source=sso&sdk_version=2.2.5&language=zh&verifyFp="+fp+"&fp="+fp;
		String xBogus = xBogusString+"&X-Bogus="+XbogusUtil.getXBogus(xBogusString);
        HttpURLConnection connection = (HttpURLConnection) new URL(loginQRdomain+xBogus).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", DouUtil.ua);
        connection.setRequestProperty("Referer", DouUtil.referer);
        connection.setRequestProperty("Cookie",cookie_ttwid);
        int responseCode = connection.getResponseCode();
        StringBuilder responseStringBuilder = new StringBuilder();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseStringBuilder.append(inputLine);
                }
            }

            String responseData = responseStringBuilder.toString(); 
    		return new AjaxEntity(Global.ajax_success,"ok", JSONObject.parseObject(responseData));
        } else {
        	return new AjaxEntity(Global.ajax_uri_error,"error","二维码异常");
        }

	}
	public AjaxEntity checkLoginStatus(String token) throws Exception {
		if(fp.equals("")) {
			fp = DouUtil.getFp();
		}
		if(cookie_ttwid.equals("")) {
			cookie_ttwid =domain+"ttwid="+DouUtil.getTtwid();
		}
		String xBogusString ="token="+token+"&service=https%3A%2F%2Fwww.douyin.com&need_logo=false&need_short_url=true&device_platform=web_app&aid=6383&account_sdk_source=sso&sdk_version=2.2.5&language=zh&verifyFp="+fp+"&fp="+fp;
		String xBogus = xBogusString+"&X-Bogus="+XbogusUtil.getXBogus(xBogusString);
        HttpURLConnection connection = (HttpURLConnection) new URL(loginCheckDomain+xBogus).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", DouUtil.ua);
        connection.setRequestProperty("Referer", DouUtil.referer);
        connection.setRequestProperty("Cookie",cookie_ttwid);
        int responseCode = connection.getResponseCode();
        StringBuilder responseStringBuilder = new StringBuilder();
        if (responseCode == HttpURLConnection.HTTP_OK) {
        	fp ="";
        	cookie_ttwid="";
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseStringBuilder.append(inputLine);
                }
            }

            String responseData = responseStringBuilder.toString(); 
    		return new AjaxEntity(Global.ajax_success,"ok", JSONObject.parseObject(responseData));
        } else {
        	return new AjaxEntity(Global.ajax_uri_error,"error","二维码异常");
        }
	}
}
