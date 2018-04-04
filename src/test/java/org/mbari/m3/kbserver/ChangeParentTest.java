package org.mbari.m3.kbserver;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mbari.m3.kbserver.actions.ChangeParent;
import org.mbari.m3.kbserver.actions.CreateConcept;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;


/**
 * Unit test for simple App.
 */
public class ChangeParentTest 
    extends TestCase
{
    ToolBelt toolBelt;
    UserAccount userAccount;
    ConceptDAO dao;
    ChangeParent changeParent;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ChangeParentTest( String testName )
    {
        super( testName );

        toolBelt = Initializer.getToolBelt();
        userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("Lucas");
        dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ChangeParentTest.class );
    }

    public void testGetParentNull() {
        // TODO: Create object, test child of Object.
        changeParent = new ChangeParent("", "", userAccount);
        Concept thisConcept = dao.findByName("object");

        assertEquals(changeParent.getParentName(toolBelt, thisConcept), "ChangeParent: Found object, parent was null");
    }

    public void testGetParentNameIsObject() {
        // TODO: Create object, test child of Object.
        changeParent = new ChangeParent("", "", userAccount);
        Concept thisConcept = dao.findByName("behavior");

        assertEquals(changeParent.getParentName(toolBelt, thisConcept).toLowerCase(), "object");
    }

    public void testIsChildOfRootConcept() {
        changeParent = new ChangeParent("", "", userAccount);
        Concept concept = dao.findByName("behavior");

        assertTrue(changeParent.isChildOfRootConcept(toolBelt, concept));
    }

    // Bad test, what if Beggiatoa doesn't exist? 
    public void testIsNotChildOfRootConcept() {
        changeParent = new ChangeParent("", "", userAccount);
        Concept concept = dao.findByName("Beggiatoa");

        assertFalse(changeParent.isChildOfRootConcept(toolBelt, concept));
    }

    public void testApply() {
        // Create new concept
        CreateConcept fn = new CreateConcept("behavior", "ChangeParentTestCase", userAccount);
        fn.apply(toolBelt);

        // Set the parent
        // Is set to behavior

        // Change the parent
        changeParent = new ChangeParent("object", "ChangeParentTestCase", userAccount);
        changeParent.apply(toolBelt);

        // Verify parent is changed
        Concept thisNewConcept = dao.findByName("ChangeParentTestCase");
        boolean isChanged;
        
        if (changeParent.getParentName(toolBelt, thisNewConcept).equals("object")) {
            isChanged = true;
        } else {
            isChanged = false;
        }

        // TODO: Verify old parent doesn't have association
        //

        // Delete concept
        //

        assertTrue(isChanged);
    }
}
