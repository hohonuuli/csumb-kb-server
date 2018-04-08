package org.mbari.m3.kbserver;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.mbari.m3.kbserver.actions.DeleteConcept;
import org.mbari.m3.kbserver.actions.LinkRealizationUtil;
import org.mbari.m3.kbserver.actions.CreateConcept;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;

public class LinkRealizationUtilTest {
    private static String CONCEPT_NAME = "behvaior";

    ToolBelt toolBelt;
    UserAccount userAccount;
    ConceptDAO dao;
    DeleteConcept deleteConcept;

    public LinkRealizationUtilTest() {
        toolBelt = Initializer.getToolBelt();
        userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("Lucas");
        dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();
    }

    @Test 
    public void testGetLinkRealizations() {
        LinkRealizationUtil linkRealizations = new LinkRealizationUtil(userAccount);
        linkRealizations.getLinkRealizations(toolBelt, CONCEPT_NAME);

        assertTrue(true);
    }
}
