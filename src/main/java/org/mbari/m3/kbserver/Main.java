package org.mbari.m3.kbserver;

import static spark.Spark.*;

import com.google.inject.spi.Toolable;

import org.mbari.m3.kbserver.Initializer;
import org.mbari.m3.kbserver.actions.CreateConcept;
import org.mbari.m3.kbserver.actions.DeleteConcept;
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
        //create concept
        CreateConcept fn = new CreateConcept("behavior", "dariotesting!", userAccount);
        fn.apply(toolBelt);
        return "Concept has been created!";
 
       });

	 delete("/deleteConcept", (request, response) -> {
    	
    	ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("brian");
        DeleteConcept fn = new DeleteConcept("dariotesting!", userAccount);
        fn.apply(toolBelt);
        return "Concept has been Deleted!";

	 });

	get("/hello", (req, res) -> "Hello World");
   	
	get("/team",(req,res) -> "testing"); 

	}
}
