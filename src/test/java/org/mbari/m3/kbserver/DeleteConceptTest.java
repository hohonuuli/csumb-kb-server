package org.mbari.m3.kbserver;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.mbari.m3.kbserver.actions.DeleteConcept;
import org.mbari.m3.kbserver.actions.CreateConcept;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;

public class DeleteConceptTest {
    private static String NEW_CONCEPT_NAME = "testConcept";

    ToolBelt toolBelt;
    UserAccount userAccount;
    ConceptDAO dao;
    DeleteConcept deleteConcept;

    public DeleteConceptTest() {
        toolBelt = Initializer.getToolBelt();
        userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("Lucas");
        dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();
    }

    @Test 
    public void testApply() {
        // Create new concept
        CreateConcept fn = new CreateConcept("object", NEW_CONCEPT_NAME, userAccount);
        fn.apply(toolBelt);

        // Verify parent is changed
        try {
            Concept newConcept = dao.findByName(NEW_CONCEPT_NAME);
            deleteConcept = new DeleteConcept(NEW_CONCEPT_NAME, userAccount);
            assertTrue(deleteConcept.apply(toolBelt));
        }
        catch (Exception e) {
            assertTrue(false);
        }
    }
}
