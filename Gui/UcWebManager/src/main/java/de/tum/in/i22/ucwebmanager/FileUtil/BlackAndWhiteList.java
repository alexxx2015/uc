package de.tum.in.i22.ucwebmanager.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class BlackAndWhiteList {

	public static List<String> read(String path){
		List<String> list = new ArrayList<String>();
		try{
		if (!"".equals(path)) {
			File f = new File(path);
			FileInputStream fis = new FileInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		}
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public static void saveAndWrite(List<String> content, String path){
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path),"utf-8"))) {
			
			for (String s : content) bw.write(s + System.getProperty("line.separator").toString());
		    bw.close();
		    }catch (IOException ex) {
		    System.out.println(ex.toString());
		    }
	}
	public static void main(String[] args) {
		List<String> list = read("/home/dat/pdp/Gui/UcWebManager/src/main/webapp/whiteAndBlackList/blacklist.list");
		for (String s : list) 
			 System.out.println(s);
	}
	
}
