package de.tum.in.i22.ucwebmanager.analysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil.Dir;

public class BlackAndWhiteList {
	final String contains = "contains";
	final String startswith = "startswith";
	String path = FileUtil.getPathBlackAndWhiteList();
	Properties propBlackList, propWhiteList;
	
	public BlackAndWhiteList() {
		propBlackList = new Properties();
		propWhiteList = new Properties();
		InputStream inputBlackList = null, inputWhiteList = null;
		try {
			inputBlackList = new FileInputStream(path + "/blacklist.list");
			propBlackList.load(inputBlackList);
			
			inputWhiteList = new FileInputStream(path + "/whitelist.list");
			propWhiteList.load(inputWhiteList);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputBlackList != null && inputWhiteList != null){
				try {
					inputBlackList.close();
					inputWhiteList.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String[] getBlackList(){
		String[] blacklists = new String[]{};
		blacklists = propBlackList.getProperty(contains).split(",");
		return blacklists;
	}
	public static void main(String[] args) {
		BlackAndWhiteList bwl = new BlackAndWhiteList();
		String [] s = bwl.getBlackList();
		for (String s1 : s){
			System.out.println(s1);
		}
	}
}
