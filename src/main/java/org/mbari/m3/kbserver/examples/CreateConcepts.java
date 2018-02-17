package org.mbari.m3.kbserver.examples;

import com.google.inject.spi.Toolable;

import org.mbari.m3.kbserver.Initializer;
import org.mbari.m3.kbserver.actions.CreateConcept;
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
public class CreateConcepts {

    public static void main(String[] args) {
        example1();
    }

    private static void example1() {
        System.out.println(">>> Creating a concept");
        ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("brian");
        CreateConcept fn = new CreateConcept("behavior", "Dario", userAccount);
        fn.apply(toolBelt);

    }
    
}
