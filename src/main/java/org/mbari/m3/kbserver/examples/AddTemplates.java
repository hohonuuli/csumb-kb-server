package org.mbari.m3.kbserver.examples;

import com.google.inject.spi.Toolable;

import org.mbari.m3.kbserver.Initializer;
import org.mbari.m3.kbserver.actions.LinkTemplateUtil;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ui.ToolBelt;


/**
 * Add Concept Media
 */
public class AddTemplates {

    public static void main(String[] args) {
        example1();
    }

    private static void example1() {
        System.out.println(">>> Adding media to concept");
        ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("Dario");
        //ConceptMedia fn = new AddConceptMedia("dariomolina93", toolBelt, userAccount);
        
        LinkTemplateUtil fn = new LinkTemplateUtil("dariomolina12",toolBelt,"linkameTesting","linkValueTesting","self", userAccount);
        fn.addTemplate();
    }
    
}
