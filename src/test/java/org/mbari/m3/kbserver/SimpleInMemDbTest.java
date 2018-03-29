package org.mbari.m3.kbserver;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import vars.knowledgebase.*;
import vars.knowledgebase.ui.ToolBelt;

/**
 * Examples that use an in-memory derby database for testing. This allows you
 * to run unit tests with out running any external services.
 *
 * @author Brian Schlining
 * @since 2018-03-29T15:30:00
 */
public class SimpleInMemDbTest {

    @Ignore
    @Test
    public void test() {
        DatabaseLoader.load(); // load data in to database from a json file.
        KnowledgebaseDAOFactory daoFactory = Initializer.getToolBelt().getKnowledgebaseDAOFactory();
        ConceptDAO dao = daoFactory.newConceptDAO();
        Concept root = dao.findRoot();
        assertNotNull("Whoa ... didn't expect the kb root to be null", root);
    }

    /**
     * Assumes and empty database. Adds a single concept.
     * Don't run this if you are callign DatabaseLoader.load.  I disabled this
     * test with @Ignore. But you can delete that annotation and add @Ignore
     * to the test method above if you want to run this one instead.
     */
    @Ignore
    @Test
    public void testWithEmptyDatabase() {

        String name = "object";

        ToolBelt toolBelt = Initializer.getToolBelt();
        KnowledgebaseDAOFactory daoFactory = toolBelt.getKnowledgebaseDAOFactory();
        KnowledgebaseFactory factory = toolBelt.getKnowledgebaseFactory();

        ConceptName conceptName = factory.newConceptName();
        conceptName.setName(name);
        conceptName.setNameType(ConceptNameTypes.COMMON.getName());

        Concept concept = factory.newConcept();
        concept.addConceptName(conceptName);

        ConceptDAO dao = daoFactory.newConceptDAO();
        dao.startTransaction();
        dao.persist(concept);
        dao.endTransaction();

        Concept concept1 = dao.findByName(name);
        assertNotNull("Expected to find a concept", concept1);

    }


}
