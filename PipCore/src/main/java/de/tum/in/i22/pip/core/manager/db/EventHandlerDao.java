package de.tum.in.i22.pip.core.manager.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

/**
 * event handler data access object class.
 * @author Stoimenov
 *
 */
public class EventHandlerDao {
	
	private static final Logger _logger = Logger
			.getLogger(EventHandlerDao.class);
	
	private static final String PERSISTENCE_UNIT_NAME = "PIP";
	private EntityManagerFactory _emFactory;
	private EntityManager _entityManager;
	
	public EventHandlerDao() {
		// TODO Auto-generated constructor stub
	}
	
	public void initialize() {
		_logger.debug("Create entity manager for: " + PERSISTENCE_UNIT_NAME);
		_emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		_entityManager = _emFactory.createEntityManager();
		_logger.debug("Entity manager created.");
	}
	
	public EventHandlerDefinition getEventHandlerDefinition(String className) {
		_logger.debug("Get event handler definition for " + className);
		TypedQuery<EventHandlerDefinition> q = _entityManager.createQuery(
				"select t from EventHandlerDefinition t where t.className=:className",
				EventHandlerDefinition.class);
		q.setParameter("className", className);
		
		try {
			EventHandlerDefinition result =  q.getSingleResult();
			return result;
		} catch (NoResultException e) {
			return null;
		}
	}

	public void saveEventHandlerDefinition(
			EventHandlerDefinition eventHandlerDefinition) {
		
		_logger.debug("Save event handler definition: " + eventHandlerDefinition.getClassName());
		// check if event handler definition already exists
		EventHandlerDefinition existingEventHandlerDef = 
				getEventHandlerDefinition(eventHandlerDefinition.getClassName());
		
		if (existingEventHandlerDef == null) {
			_logger.debug("Create new table entry");
			_entityManager.getTransaction().begin();
			_entityManager.persist(eventHandlerDefinition);
			_entityManager.getTransaction().commit();
		} else {
			existingEventHandlerDef.setClassName(eventHandlerDefinition.getClassName());
			existingEventHandlerDef.setClassFile(eventHandlerDefinition.getClassFile());
			existingEventHandlerDef.setSourceFile(eventHandlerDefinition.getSourceFile());
			
			_logger.debug("Update existing table entry");
			_entityManager.getTransaction().begin();
			_entityManager.merge(existingEventHandlerDef);
			_entityManager.getTransaction().commit();
		}
	}

	public List<EventHandlerDefinition> getCurrentEventHandlerDefinitions() {
		_logger.debug("Get current event handler definitions");
		TypedQuery<EventHandlerDefinition> q = _entityManager.createQuery(
				"select t from EventHandlerDefinition t",
				EventHandlerDefinition.class);
		return q.getResultList();
	}
}
