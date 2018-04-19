package org.mbari.m3.kbserver;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.mbari.m3.kbserver.actions.ChangeParent;
import org.mbari.m3.kbserver.actions.CreateConcept;
import org.mbari.m3.kbserver.actions.DeleteConcept;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;

public class ChangeParentTest {
    static final String NEW_CONCEPT_NAME = "ChangeParentTestCase";
    static final String ORIGINAL_PARENT = "behavior";
    static final String NEW_PARENT = "object";
    
    ToolBelt toolBelt;
    UserAccount userAccount;
    ConceptDAO dao;
    ChangeParent changeParent;

    public ChangeParentTest() {
        toolBelt = Initializer.getToolBelt();
        userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("Lucas");
        dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();
    }

    @Test
    public void testGetParentNull() {
        // TODO: Create object, test child of Object.
        changeParent = new ChangeParent("", "", userAccount);
        Concept thisConcept = dao.findByName(NEW_PARENT);

        assertEquals(changeParent.getParentName(toolBelt, thisConcept), "ChangeParent: Found object, parent was null");
    }

    @Test
    public void testGetParentNameIsObject() {
        // TODO: Create object, test child of Object.
        changeParent = new ChangeParent("", "", userAccount);
        Concept thisConcept = dao.findByName(ORIGINAL_PARENT);

        assertEquals(changeParent.getParentName(toolBelt, thisConcept).toLowerCase(), NEW_PARENT);
    }

    @Test
    public void testIsChildOfRootConcept() {
        changeParent = new ChangeParent("", "", userAccount);
        Concept concept = dao.findByName(ORIGINAL_PARENT);

        assertTrue(changeParent.isChildOfRootConcept(toolBelt, concept));
    }

    @Test
    // Bad test, what if Beggiatoa doesn't exist? 
    public void testIsNotChildOfRootConcept() {
        changeParent = new ChangeParent("", "", userAccount);
        Concept concept = dao.findByName("Beggiatoa");

        assertFalse(changeParent.isChildOfRootConcept(toolBelt, concept));
    }

    @Test 
    public void testIsCircularNotCircleExample() {
        /*

        Behavior
            X
        Concept One
            |      
        Concept Two
            |      
        Concept Three
        
        */

        changeParent = new ChangeParent("", "", userAccount);

        CreateConcept concept1 = new CreateConcept("behavior", "CircleConceptOne", userAccount);
        concept1.apply(toolBelt);
        CreateConcept concept2 = new CreateConcept("CircleConceptOne", "CircleConceptTwo", userAccount);
        concept2.apply(toolBelt);
        CreateConcept concept3 = new CreateConcept("CircleConceptTwo", "CircleConceptThree", userAccount);
        concept3.apply(toolBelt);
        
        Concept concept = dao.findByName("CircleConceptOne");

        boolean test = changeParent.isCircular(toolBelt, concept);
        
        try {
            DeleteConcept dc = new DeleteConcept("CircleConceptOne", userAccount);
            dc.apply(toolBelt);
        }
        catch (RuntimeException rException) {
            System.out.println("Already deleted / doesn't exist.");
        }

        try {
            DeleteConcept dc = new DeleteConcept("CircleConceptTwo", userAccount);
            dc.apply(toolBelt);
        }
        catch (RuntimeException rException) {
            System.out.println("Already deleted / doesn't exist.");
        }

        try {
            DeleteConcept dc = new DeleteConcept("CircleConceptThree", userAccount);
            dc.apply(toolBelt);
        }
        catch (RuntimeException rException) {
            System.out.println("Already deleted / doesn't exist.");
        } 

        assertFalse(test);
    }

    @Test
    public void testIsCircularCircleExample() {
        /*

        Behavior
            X
        Concept One --\
            |         |
        Concept Two   |
            |         |
        Concept Three /
        
        */

        CreateConcept concept1 = new CreateConcept("behavior", "CircleConceptOne", userAccount);
        concept1.apply(toolBelt);
        CreateConcept concept2 = new CreateConcept("CircleConceptOne", "CircleConceptTwo", userAccount);
        concept2.apply(toolBelt);
        CreateConcept concept3 = new CreateConcept("CircleConceptTwo", "CircleConceptThree", userAccount);
        concept3.apply(toolBelt);

        ChangeParent changeParent = new ChangeParent("CircleConceptThree", "CircleConceptOne", userAccount);
        
        boolean test;
        
        try {
            test = changeParent.apply(toolBelt);
        }
        catch (StackOverflowError soe) {
            test = false;
        }

        try {
            DeleteConcept dc = new DeleteConcept("CircleConceptOne", userAccount);
            dc.apply(toolBelt);
        }
        catch (RuntimeException rException) {
            System.out.println("Already deleted / doesn't exist.");
        }

        try {
            DeleteConcept dc = new DeleteConcept("CircleConceptTwo", userAccount);
            dc.apply(toolBelt);
        }
        catch (RuntimeException rException) {
            System.out.println("Already deleted / doesn't exist.");
        }

        try {
            DeleteConcept dc = new DeleteConcept("CircleConceptThree", userAccount);
            dc.apply(toolBelt);
        }
        catch (RuntimeException rException) {
            System.out.println("Already deleted / doesn't exist.");
        } 

        assertFalse(test);
    }

    @Test
    public void testApply() {
        // Create new concept
        try {
            CreateConcept fn = new CreateConcept(ORIGINAL_PARENT, NEW_CONCEPT_NAME, userAccount);
            fn.apply(toolBelt);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        // Set the parent
        // Is set to behavior

        // Change the parent
        changeParent = new ChangeParent(NEW_PARENT, NEW_CONCEPT_NAME, userAccount);
        changeParent.apply(toolBelt);

        // Verify parent is changed
        Concept thisNewConcept = dao.findByName(NEW_CONCEPT_NAME);
        boolean isChanged;
        
        if (changeParent.getParentName(toolBelt, thisNewConcept).equals(NEW_PARENT)) {
            isChanged = true;
        } else {
            isChanged = false;
        }

        // TODO: Verify old parent doesn't have association

        // Delete concept
        try {
            DeleteConcept deleteConcept = new DeleteConcept(NEW_CONCEPT_NAME, userAccount);
            deleteConcept.apply(toolBelt);
        } catch (Exception e) {
            System.out.println(e + "\nConcept already cleaned up?");
        }
        
        assertTrue(isChanged);
    }
}
