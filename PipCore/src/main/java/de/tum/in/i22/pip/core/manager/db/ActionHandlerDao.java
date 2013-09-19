package de.tum.in.i22.pip.core.manager.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

/**
 * Action handler data access object class.
 * @author Stoimenov
 *
 */
public class ActionHandlerDao {
	
	private static final Logger _logger = Logger
			.getLogger(ActionHandlerDao.class);
	
	private static final String PERSISTENCE_UNIT_NAME = "PIP";
	private EntityManagerFactory _emFactory;
	private EntityManager _entityManager;
	
	public ActionHandlerDao() {
		// TODO Auto-generated constructor stub
	}
	
	public void initialize() {
		_logger.debug("Create entity manager for: " + PERSISTENCE_UNIT_NAME);
		_emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		_entityManager = _emFactory.createEntityManager();
		_logger.debug("Entity manager created.");
	}
	
	public EventHandlerDefinition getActionHandlerDefinition(String className) {
		_logger.debug("Get action handler definition for " + className);
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

	public void saveActionHandlerDefinition(
			EventHandlerDefinition actionHandlerDefinition) {
		
		_logger.debug("Save action handler definition: " + actionHandlerDefinition.getClassName());
		// check if action handler definition already exists
		EventHandlerDefinition existingActionHandlerDef = 
				getActionHandlerDefinition(actionHandlerDefinition.getClassName());
		
		if (existingActionHandlerDef == null) {
			_logger.debug("Create new table entry");
			_entityManager.getTransaction().begin();
			_entityManager.persist(actionHandlerDefinition);
			_entityManager.getTransaction().commit();
		} else {
			existingActionHandlerDef.setClassName(actionHandlerDefinition.getClassName());
			existingActionHandlerDef.setClassFile(actionHandlerDefinition.getClassFile());
			existingActionHandlerDef.setSourceFile(actionHandlerDefinition.getSourceFile());
			
			_logger.debug("Update existing table entry");
			_entityManager.getTransaction().begin();
			_entityManager.merge(existingActionHandlerDef);
			_entityManager.getTransaction().commit();
		}
	}

	public List<EventHandlerDefinition> getCurrentActionHandlerDefinitions() {
		_logger.debug("Get current action handler definitions");
		TypedQuery<EventHandlerDefinition> q = _entityManager.createQuery(
				"select t from EventHandlerDefinition t where t.currentlyActive=true",
				EventHandlerDefinition.class);
		return q.getResultList();
	}
}
