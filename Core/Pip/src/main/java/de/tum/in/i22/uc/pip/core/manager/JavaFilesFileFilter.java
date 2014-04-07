package de.tum.in.i22.uc.pip.core.manager;

import java.io.File;

import org.apache.commons.io.filefilter.IOFileFilter;

class JavaFilesFileFilter implements IOFileFilter {

	@Override
	public boolean accept(File file) {
		String fileName = file.getName();
		return fileName.endsWith(".class") || fileName.endsWith(".java");
	}

	@Override
	public boolean accept(File dir, String name) {
		return true;
	}

}
