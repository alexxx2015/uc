package de.tum.in.i22.pip.core.manager.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Main {
  private static final String PERSISTENCE_UNIT_NAME = "PIP";
  private static EntityManagerFactory factory;

  public static void main(String[] args) {
	System.out.println(System.getProperty("derby.system.home"));
	
    factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    EntityManager em = factory.createEntityManager();
    // Read the existing entries and write to console
    Query q = em.createQuery("select t from ActionHandlerDefinition t");
    List<ActionHandlerDefinition> aList = q.getResultList();
    for (ActionHandlerDefinition actionHandlerDefinition : aList) {
      System.out.println(actionHandlerDefinition);
    }
    System.out.println("Size: " + aList.size());

    // Create new todo
    em.getTransaction().begin();
    ActionHandlerDefinition actionHandlerDefition = new ActionHandlerDefinition();
    actionHandlerDefition.setClassName("OpenFile");
    em.persist(actionHandlerDefition);
    em.getTransaction().commit();

    em.close();
  }
} 