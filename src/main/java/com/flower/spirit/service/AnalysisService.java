package com.flower.spirit.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.utils.ThreadConfig;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author flower
 *
 */
@Service
public class AnalysisService {
	
	private Logger logger = LoggerFactory.getLogger(AnalysisService.class);

	public void processingVideos(String token, String video) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
	        WebClient webClient = ThreadConfig.getWebClient();
	        HtmlPage page = null;
	        try {
	            page = webClient.getPage(this.findAddr(video));
	        } catch (Exception e) {
	        	logger.error(e.getMessage());
	        }finally {
	            webClient.close();
	        }
	        webClient.waitForBackgroundJavaScript(300);
	        String pageXml = page.asXml();
	        Document parse = Jsoup.parse(pageXml);
	        Element render_data = parse.getElementById("RENDER_DATA");
	        String encode = URLDecoder.decode(render_data.html().substring("//<![CDATA[".length(), render_data.html().length() - "//]]>".length()).trim(), "UTF-8");
	        JSONObject jsonObject = JSON.parseObject(encode);
	        System.out.println(jsonObject);
	}
	
	public String findAddr(String videourl) {
		return this.getUrl(videourl);
	}
	
	public  String getUrl(String input) {
        String regex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

	
	
	

}
