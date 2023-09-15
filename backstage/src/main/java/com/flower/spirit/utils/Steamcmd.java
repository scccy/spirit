package com.flower.spirit.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Steamcmd {
	
	private static Logger logger = LoggerFactory.getLogger(Steamcmd.class);
	
	private static String workshoplog ="/root/.steam/logs/workshop_log.txt";
	
	private static String workdown ="/root/.steam/SteamApps/workshop/content/431960/";
	
	public static boolean exec(String wallpaper) {		
		try {
			//清除挂掉的screen会话
			ProcessBuilder wipe = new ProcessBuilder("screen", "-wipe");
			wipe.start();
			//向指定名称的screen 会话执行
			ProcessBuilder processBuilder = new ProcessBuilder("screen","-x","steamcmd","-X","stuff","workshop_download_item","431960",wallpaper);
			Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
            	System.out.println(line);
            	if(line.contains("No screen")) {
            		return false;
            	}
            }
	        return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String execAndListening(String wallpepr) throws InterruptedException {
		boolean exec = exec(wallpepr);
		if(exec) {
			//先判断文件存不存在 超时2分钟不存在认为 失败
			int checklog=6;
			int i =0;
			boolean checkfile =  false;
			while(i <checklog) {
				File file = new File(workshoplog);
				if(file.exists()) {
					checkfile = true;
					break;
				}
				Thread.sleep(1000*30);
				i++;
			}
			if(checkfile) {
				//文件存在读日志 并监听 文件超过15分钟未完成 认为失败
				int checkstatus = 30;
				int y = 0;
				boolean success = false;
				while(y<checkstatus) {
					File file = new File(workshoplog);
					String a = "Download item "+wallpepr+" result : OK";
//					String b ="Finished Workshop download job : No Error";
					BufferedReader reader = null;
			        try {
			            reader = new BufferedReader(new FileReader(file));
			            String readStr;
			            while ((readStr = reader.readLine()) != null) {
			            	if(readStr.contains(a)) {
			            		success = true;
			            		break;
			            	}
			            }
			            reader.close();

			        } catch (IOException e) { e.printStackTrace();
			        } finally {if (reader != null) {try {reader.close(); } catch (IOException e1) {e1.printStackTrace();}}
			        }
			        if(success) {
			        	break;
			        }
					Thread.sleep(1000*30);
					y++;
				}
				if(success) {
					//静止十秒 等待IO完成
					Thread.sleep(1000*10);
					//文件路径为
					String path = workdown+wallpepr;
					//删除log
					FileUtils.deleteFile(workshoplog);
					logger.info("处理完成---前端处理");
					return path;
				}else {
					logger.info("15分钟都没下完 一定出问题了");
					return null;
				}
			}else {
				logger.info("workshop_log 不存在 请exec 进入容器查看steamcmd 并已经登录");
			}
			
			return null;
		}
		logger.info("no session 请查看文档新建会话");
		return null;
	}
	
	

}
