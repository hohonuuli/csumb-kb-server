package org.mbari.m3.kbserver.examples;

import com.google.inject.spi.Toolable;

import org.mbari.m3.kbserver.Initializer;
import org.mbari.m3.kbserver.actions.UpdateConceptMedia;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ui.ToolBelt;


/**
 * Add Concept Media
 */
public class UpdateConceptMedias {

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
        
        UpdateConceptMedia fn = new UpdateConceptMedia("dariomolina12", toolBelt, userAccount);
        fn.apply("https://www.facebook.com","https://www.google.com/","testing-caption","testing-credit","image",true);
    }
    
}
