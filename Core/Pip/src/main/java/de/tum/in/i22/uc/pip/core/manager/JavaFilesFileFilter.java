package de.tum.in.i22.uc.pip.core.manager;

import java.io.File;

import org.apache.commons.io.filefilter.IOFileFilter;

public class JavaFilesFileFilter implements IOFileFilter {

	@Override
	public boolean accept(File file) {
		String fileName = file.getName();
		boolean res = fileName.endsWith(".class") || fileName.endsWith(".java");
		return res;
	}

	@Override
	public boolean accept(File dir, String name) {
		return true;
	}

}
