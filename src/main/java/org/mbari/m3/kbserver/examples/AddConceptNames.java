package org.mbari.m3.kbserver.examples;

import com.google.inject.spi.Toolable;

import org.mbari.m3.kbserver.Initializer;
import org.mbari.m3.kbserver.actions.AddConceptName;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;


/**
 * CreateConcepts
 */
public class AddConceptNames {

    public static void main(String[] args) {
        example1();
    }

    private static void example1() {
        System.out.println(">>> Adding Conept name");
        ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("Dario");
        AddConceptName fn = new AddConceptName("DARIOTESTING1233", "dariotesting", userAccount,"synonym");
        fn.apply(toolBelt);

    }
    
}
