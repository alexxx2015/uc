package de.tum.in.i22.pip.core.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.manager.db.ActionHandlerDao;
import de.tum.in.i22.pip.core.manager.db.ActionHandlerDefinition;


public class PipManager implements IPipManager {
	
	
	private static final Logger _logger = Logger.getLogger(PipManager.class);
	
	
	private ActionHandlerManager _actionHandlerManager = null;
	private ActionHandlerDao _actionHandlerDao = null;
	
	public PipManager(ActionHandlerManager actionHandlerManager) {
		_actionHandlerManager = actionHandlerManager;
	}
	
	public boolean initialize() {
		boolean res = true;
		_actionHandlerDao = new ActionHandlerDao();
		try {
			_actionHandlerDao.initialize();
			res = true;
		} catch (Exception e) {
			_logger.error("Failed to initialize data access object" +
					" for action handlers. Error: " + e.getMessage());
			res = false;
		}
		
		return res;
	}

	@Override
	public void updateInformationFlowSemantics(
			IPipDeployer deployer, File jarFile,
			EConflictResolution flagForTheConflictResolution) {
		
		_logger.debug("Update information flow semantics");

		// class name to ActionHandlerDefinition
		Map<String, ActionHandlerDefinition> map = new HashMap<String, ActionHandlerDefinition>();
		
		try {
			Path destination = Files.createTempDirectory("PipTemp");
		    System.out.println(destination.toAbsolutePath().toString());
		    
		    ZipFile zipFile = new ZipFile(jarFile);
		    zipFile.extractAll(destination.toString());
		    
		    IOFileFilter javaFileFilter = new JavaFilesFileFilter();
		    Collection<File> fileList = FileUtils.listFiles(destination.toFile(), javaFileFilter, TrueFileFilter.INSTANCE);
		    
		    Iterator<File> iterator = fileList.iterator();
		    while (iterator.hasNext()) {
		    	File file = iterator.next();
		    	// package name + class name
		    	String fullClassName = convertToFullClassName(file, destination);
		    	_logger.trace("Class: " + fullClassName);
		    	
		    	if (file.getName().endsWith(".class")) {
		    		updateMapWithClassDefinition(map, fullClassName, file);
		    	} else if (file.getName().endsWith(".java")) {
		    		updateMapWithSrcDefinition(map, fullClassName, file);
		    	}
		    }
		    
		    Set<String> keySet = map.keySet();
			switch (flagForTheConflictResolution) {
			case KEEP_ALL:
				for (String className : keySet) {
					ActionHandlerDefinition actionHandlerDefinition = map.get(className);
					_actionHandlerDao.saveActionHandlerDefinition(actionHandlerDefinition);
					// load class, use the latest received?
					_actionHandlerManager.setClassToBeLoaded(actionHandlerDefinition);
				}
				break;

			case IGNORE_UPDATES:
				for (String className : keySet) {
					_actionHandlerDao.saveActionHandlerDefinitionIfNotPresent(map
							.get(className));
					// load new class
				}
				break;
			case OVERWRITE:
				for (String className : keySet) {
					_actionHandlerDao.saveActionHandlerDefinitionOverwrite(map
							.get(className));
					// load new class
				}
				break;
//			case TAKE_NEWEST:
//				for (String className : keySet) {
//					_actionHandlerDao.saveActionHandlerDefinition(map
//							.get(className));
//				}
//				break;
//				
//			case TAKE_OLDEST:
//				for (String className : keySet) {
//					_actionHandlerDao.saveActionHandlerDefinition(map
//							.get(className));
//				}
//				break;
			}
		    
		    // TODO delete temporary folder
		    
		    
		} catch (ZipException e) {
			_logger.error(e.toString());
		} catch (Exception e1) {
			_logger.error(e1.toString());
		}
		
		
		
	}
	
	private void updateMapWithSrcDefinition(
			Map<String, ActionHandlerDefinition> map, String fullClassName,
			File file) 
		throws IOException {
		
		ActionHandlerDefinition definition = getMapEntry(map, fullClassName);
		definition.setSourceFile(FileUtils.readFileToString(file));
	}
	
	private void updateMapWithClassDefinition(
			Map<String, ActionHandlerDefinition> map, String fullClassName,
			File file) 
		throws IOException {
		
		ActionHandlerDefinition definition = getMapEntry(map, fullClassName);
		definition.setClassFile(FileUtils.readFileToByteArray(file));
		
		definition.setClassFileLastModified(new Timestamp(file.lastModified()));
		java.util.Date today = new java.util.Date();
		definition.setDateReceived(new Timestamp(today.getTime()));
	}

	private ActionHandlerDefinition getMapEntry(Map<String, ActionHandlerDefinition> map,
			String fullClassName) {
		
		ActionHandlerDefinition actionHandlerDefinition = map.get(fullClassName);
		if (actionHandlerDefinition == null) {
			actionHandlerDefinition = new ActionHandlerDefinition();
			actionHandlerDefinition.setClassName(fullClassName);
			map.put(fullClassName, actionHandlerDefinition);
		}
		
		return actionHandlerDefinition;
	}


	/**
	 * Package name + class name
	 * @param file
	 * @param destination
	 * @return
	 */
	private String convertToFullClassName(File file, Path destination) {
		String temp = file.getAbsolutePath().substring(destination.toAbsolutePath().toString().length() + 1);
		temp = temp.replace("\\", ".");
		temp = temp.substring(0, temp.lastIndexOf("."));
		return temp;
	}
}
