package de.tum.in.i22.pip.core.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.manager.db.ActionHandlerDao;
import de.tum.in.i22.pip.core.manager.db.EventHandlerDefinition;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;


public class PipManager implements IPipManager {
	
	
	private static final Logger _logger = Logger.getLogger(PipManager.class);
	
	
	private EventHandlerManager _actionHandlerManager = null;
	private ActionHandlerDao _actionHandlerDao = null;
	
	private IMessageFactory _mf = MessageFactoryCreator.createMessageFactory();
	
	public PipManager(EventHandlerManager actionHandlerManager) {
		_actionHandlerManager = actionHandlerManager;
	}
	
	public void initialize() {
		_logger.info("Create data access object");
		_actionHandlerDao = new ActionHandlerDao();
		_actionHandlerDao.initialize();
		
		// read the database and store class definitions in the event handler manager
		List<EventHandlerDefinition> actionHandlerDefinitions = _actionHandlerDao.getCurrentActionHandlerDefinitions();
		for (EventHandlerDefinition actionHandlerDefinition : actionHandlerDefinitions) {
			_actionHandlerManager.setClassToBeLoaded(actionHandlerDefinition);
		}
	}

	@Override
	public IStatus updateInformationFlowSemantics(
			IPipDeployer deployer, File jarFile,
			EConflictResolution flagForTheConflictResolution) {
		
		_logger.debug("Update information flow semantics");

		// class name to ActionHandlerDefinition
		Map<String, EventHandlerDefinition> map = new HashMap<String, EventHandlerDefinition>();
		
		try {
			Path destination = Files.createTempDirectory("PipTemp");
		   _logger.trace("Temporary dir: " + destination.toAbsolutePath().toString());
		    
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
				case OVERWRITE:
					_logger.info("Owerwrite class definitions");
					for (String className : keySet) {
						EventHandlerDefinition actionHandlerDefinition = map.get(className);
						_actionHandlerDao.saveActionHandlerDefinition(actionHandlerDefinition);
						_actionHandlerManager.setClassToBeLoaded(actionHandlerDefinition);
					}
					break;
				case IGNORE_UPDATES: break; // currently not used
				case KEEP_ALL: break; // currently not used
				}
		    
		    
			try {
				// delete temporary folder
				FileUtils.deleteDirectory(destination.toFile());
			} catch (IOException e) {
				_logger.warn("Failed to delete temporary dir: " + destination);
			}
		    
		    
		} catch (ZipException e) {
			_logger.error(e.toString());
			return _mf.createStatus(EStatus.ERROR, "Error when unzipping jar file: " + e.getMessage());
		} catch (Exception e1) {
			_logger.error(e1.toString());
			return _mf.createStatus(EStatus.ERROR, e1.getMessage());
		}
		
		return _mf.createStatus(EStatus.OKAY);
	}
	
	private void updateMapWithSrcDefinition(
			Map<String, EventHandlerDefinition> map, String fullClassName,
			File file) 
		throws IOException {
		
		EventHandlerDefinition definition = getMapEntry(map, fullClassName);
		definition.setSourceFile(FileUtils.readFileToString(file));
	}
	
	private void updateMapWithClassDefinition(
			Map<String, EventHandlerDefinition> map, String fullClassName,
			File file) 
		throws IOException {
		
		EventHandlerDefinition definition = getMapEntry(map, fullClassName);
		definition.setClassFile(FileUtils.readFileToByteArray(file));
		
		definition.setClassFileLastModified(new Timestamp(file.lastModified()));
		java.util.Date today = new java.util.Date();
		definition.setDateReceived(new Timestamp(today.getTime()));
	}

	private EventHandlerDefinition getMapEntry(Map<String, EventHandlerDefinition> map,
			String fullClassName) {
		
		EventHandlerDefinition actionHandlerDefinition = map.get(fullClassName);
		if (actionHandlerDefinition == null) {
			actionHandlerDefinition = new EventHandlerDefinition();
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
		String filePath = file.toPath().toAbsolutePath().toString();
		_logger.debug("File path: " + filePath);
		String temp = filePath.substring(destination.toAbsolutePath().toString().length() + 1);
		// for windows
		temp = temp.replace("\\", ".");
		// for linux
		temp = temp.replace("/", ".");
		temp = temp.substring(0, temp.lastIndexOf("."));
		return temp;
	}
}
