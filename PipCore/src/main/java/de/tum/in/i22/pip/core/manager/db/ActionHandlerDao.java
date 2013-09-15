package de.tum.in.i22.pip.core.manager.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
	
	public List<ActionHandlerDefinition> getActionHandlerDefinitions(String className) {
		_logger.debug("Get action handler definition for " + className);
		TypedQuery<ActionHandlerDefinition> q = _entityManager.createQuery(
				"select t from ActionHandlerDefinition t where t.className=:className" +
				" order by version desc",
				ActionHandlerDefinition.class);
		q.setParameter("className", className);
		
		return q.getResultList();
	}
	
	public ActionHandlerDefinition getActionHandlerDefinitionInUse(String className) {
		_logger.debug("Get action handler definition for " + className);
		TypedQuery<ActionHandlerDefinition> q = _entityManager.createQuery(
				"select t from ActionHandlerDefinition t where t.className=:className" +
				" and t.currentlyActive=true order by version desc",
				ActionHandlerDefinition.class);
		q.setParameter("className", className);
		if (q.getResultList() != null && q.getResultList().size() >= 2) {
			_logger.warn("Inconsistency: " +
					"There is more than one class definition marked as current for class: " + className);
		}
		return q.getSingleResult();
	}

	public void saveActionHandlerDefinition(
			ActionHandlerDefinition actionHandlerDefinition) {
		_entityManager.getTransaction().begin();
		_entityManager.persist(actionHandlerDefinition);
		_entityManager.getTransaction().commit();
	}

	public void saveActionHandlerDefinitionIfNotPresent(
			ActionHandlerDefinition actionHandlerDefinition) {
		// TODO Auto-generated method stub
		
	}

	public void saveActionHandlerDefinitionOverwrite(
			ActionHandlerDefinition actionHandlerDefinition) {
		// TODO Auto-generated method stub
		
	}
}
