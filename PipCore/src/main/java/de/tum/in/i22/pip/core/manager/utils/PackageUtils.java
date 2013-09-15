package de.tum.in.i22.pip.core.manager.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageUtils {

	public static List<JarEntry> getClasseNamesInPackage(File jarFile) {

		ArrayList<JarEntry> classList = new ArrayList<>();

		// packageName = packageName.replaceAll("\\." , "/");
		try {
			JarFile jar = new JarFile(jarFile);
			Enumeration<JarEntry> enumeration = jar.entries();

			// JarEntry jarEntry;

			while (enumeration.hasMoreElements()) {
				JarEntry entry = enumeration.nextElement();
				if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
					classList.add(entry);
				}
			}
			return classList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classList;
	}
	
	// maps class name to JarEntry containing the source
	public static Map<String, JarEntry> getSourceFilesMap() {
		return null;
	}

	/**
*
*/
	public static void main(String[] args) {
//		List list = PackageUtils.getClasseNamesInPackage(
//				"C:/j2sdk1.4.1_02/lib/mail.jar", "com.sun.mail.handlers");
//		System.out.println(list);
		/*
		 * output :
		 * 
		 * Jar C:/j2sdk1.4.1_02/lib/mail.jar looking for com/sun/mail/handlers
		 * Found com.sun.mail.handlers.text_html.class Found
		 * com.sun.mail.handlers.text_plain.class Found
		 * com.sun.mail.handlers.text_xml.class Found
		 * com.sun.mail.handlers.image_gif.class Found
		 * com.sun.mail.handlers.image_jpeg.class Found
		 * com.sun.mail.handlers.multipart_mixed.class Found
		 * com.sun.mail.handlers.message_rfc822.class
		 * [com.sun.mail.handlers.text_html.class,
		 * com.sun.mail.handlers.text_xml.class, com
		 * .sun.mail.handlers.image_jpeg.class, ,
		 * com.sun.mail.handlers.message_rfc822.class]
		 */
	}
}