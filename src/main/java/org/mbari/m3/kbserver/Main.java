package org.mbari.m3.kbserver;

import static spark.Spark.*;

import com.google.inject.spi.Toolable;

import org.mbari.m3.kbserver.Initializer;
import org.mbari.m3.kbserver.actions.CreateConcept;
import org.mbari.m3.kbserver.actions.DeleteConcept;
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
 * Main
 */
public class Main {

    public static void main(String[] args) {


	//create a new concept
	post("/createConcept/:name", (request, response) -> {

         ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("brian");

        //create concept
        CreateConcept fn = new CreateConcept("behavior", request.params(":name"), userAccount);
        response.type("application/json");

        //checking to see if concept can be created and return json
        try
        {
            fn.apply(toolBelt);
            return "{\"message\":\"concept created\",\"code\": \"201\"}";


        }
        catch (Exception e)
        {
            return "{\"message\":\"concept not created\", \"code\": \"401\"}";
        }

       });

       delete("/deleteConcept/:name", (request, response) -> {

           ToolBelt toolBelt = Initializer.getToolBelt();
           // Need user. Normally we would look this up
           UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
           userAccount.setRole("Admin");
           userAccount.setUserName("brian");

           //create concept
           DeleteConcept fn = new DeleteConcept(request.params(":name"), userAccount);

           response.type("application/json");

           //checking to see if concept can be created and return json
           try
           {
               fn.apply(toolBelt);
               return "{\"message\":\"Concept has been Deleted!\",\"code\": \"201\"}";


           }
           catch (Exception e)
           {
               return "{\"message\":\"concept not deleted\", \"code\": \"401\"}";
           }
       });


	get("/hello", (req, res) -> "Hello World");

	get("/team",(req,res) -> "testing");

	}
}
