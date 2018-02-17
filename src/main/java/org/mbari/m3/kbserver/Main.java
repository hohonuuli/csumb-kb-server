package org.mbari.m3.kbserver;

import static spark.Spark.*;

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
 * Main
 */
public class Main {

    public static void main(String[] args) {
      
 
	//create a new concept
	post("/createConcept", (request, response) -> {
 
         ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("brian");
        CreateConcept fn = new CreateConcept("behavior", "dariotesting123", userAccount);
        fn.apply(toolBelt);
        return "Concept has been created!";
 
       });

	// delete("/deleteConcept", (request, response) -> {
    

	// });

	get("/hello", (req, res) -> "Hello World");
   	
	get("/team",(req,res) -> "testing"); 

	}
}
