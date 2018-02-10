package org.mbari.m3.kbserver.examples;

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.jpa.ConceptImpl;
import vars.knowledgebase.ui.Initializer;

/**
 * FindConcepts
 */
public class FindConcepts {

    public static void main(String[] args) {
        example1();
        example2();
    }

    /**
     * Simplest possible case. Find by name
     */
    private static void example1() {
        System.out.println(">>> Finding by name");
        KnowledgebaseDAOFactory factory = Initializer.getToolBelt().getKnowledgebaseDAOFactory();
        ConceptDAO dao = factory.newConceptDAO();
        Concept concept = dao.findByName("shrimp");
        System.out.println("<<< Found " + concept.getPrimaryConceptName().getName());
        dao.close();
    }

    /**
     * Find bby primary key
     */
    private static void example2() {
        System.out.println(">>> Finding by primary key");
        KnowledgebaseDAOFactory factory = Initializer.getToolBelt().getKnowledgebaseDAOFactory();
        ConceptDAO dao = factory.newConceptDAO();
        Concept concept = dao.findByPrimaryKey(ConceptImpl.class, 5805L);
        System.out.println("<<< Found + " + concept.getPrimaryConceptName().getName());
    }

}