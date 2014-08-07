package de.tum.in.i22.uc.utilities;

	import java.io.*;
	 import java.io.File;
	 import java.util.*;
	 import javax.swing.filechooser.FileFilter;   
	 public class XMLSaveFilter extends FileFilter
	 { 
	    public boolean accept(File f)
	   {
	        if (f.isDirectory())
	          {
	            return false;
	          }

	         String s = f.getName();

	        return s.endsWith(".xml")||s.endsWith(".XML");
	   }

	   public String getDescription() 
	  {
	       return "*.xml,*.XML";
	  }

	
}
