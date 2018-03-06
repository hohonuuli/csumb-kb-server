package org.mbari.m3.kbserver;

import static spark.Spark.*;

import com.google.inject.spi.Toolable;
import com.google.gson.Gson;
import org.mbari.m3.kbserver.Initializer;
import org.mbari.m3.kbserver.actions.CreateConcept;
import org.mbari.m3.kbserver.actions.DeleteConcept;
import org.mbari.m3.kbserver.actions.AddConceptName;
import org.mbari.m3.kbserver.actions.AddConceptMedia;
import org.mbari.m3.kbserver.actions.ConceptData;
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

    options("/*", 
         (request, response) -> {

          String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
          if (accessControlRequestHeaders != null) 
          {
              response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
          }

          String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
          if (accessControlRequestMethod != null)
          {
              response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
          }
          
          return "OK";
        });

  before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

  //getting information about concept(metadata)
  get("/getMetadata/:name", (request, response) -> {

    try
    {
      ConceptData data = new ConceptData(request.params(":name"), Initializer.getToolBelt());
      response.type("application/json");

      return data.getMetadata();
    }
    catch(Exception e)
    {
      return "{\"message\" : \"" + e.getMessage() +"\", \"code\" : \"401\"}";
    }

  });



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
            return "{\"message\":\""+ e.getMessage() + "\", \"code\": \"401\"}";
        }

       });

    //localhost:4567/addConceptMedia/dariotesting?type=image&url=https://jsonformatter.curiousconcept.com/&credit=testing-concept&caption=testing-caption&primary=false
    post("/addConceptMedia/:name",(request,response) -> {

        ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("brian");

        response.type("application/json");


        //checking to see if concept can be created and return json
        try
        {
            AddConceptMedia fn = new AddConceptMedia(request.params(":name"), toolBelt, userAccount);
            fn.apply(request.queryParams("url"), request.queryParams("caption"),request.queryParams("credit"),request.queryParams("type"),Boolean.valueOf(request.queryParams("primary"))); 


            String s = "{\"message\":\"media added to concept\",\"code\": \"201\",";
            s += "\"type\":\""+ request.queryParams("type")+"\"}";
            return s;

        }
        catch (Exception e)
        {
            return "{\"message\":\""+e.getMessage() +"\", \"code\": \"401\"}";
        }

    });

    //add synonym to a concept

     //********************!!!!IMPORTANT!!!!!!!!!********************

      //on the http endpoint, you need to add the type and concept name.
      //Example:
      //localhost:4567/addConceptName/dariomolina93?type=common&conceptName=dariotesting12333

    //**********************************************************
    post("/addConceptName/:conceptApplyTo", (request, response) -> {

         ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        userAccount.setRole("Admin");
        userAccount.setUserName("brian");

        //create concept name
        AddConceptName fn = new AddConceptName(request.queryParams("conceptName"), request.params(":conceptApplyTo"), userAccount, request.queryParams("type") );
        response.type("application/json");


        //checking to see if concept can be created and return json
        try
        {
            fn.apply(toolBelt);

            String s = "{\"message\":\"concept name added\",\"code\": \"201\",";

            s += "\"conceptName\":\""+ request.queryParams("conceptName")+"\",";
            s += "\"type\":\""+ request.queryParams("type")+"\"}";
            return s;

        }
        catch (Exception e)
        {
            return "{\"message\":\""+e.getMessage() +"\", \"code\": \"401\"}";
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
               return "{\"message\":\""+e.getMessage() +"\", \"code\": \"401\"}";
           }
       });


	get("/hello", (req, res) -> "Hello World");

	get("/team",(req,res) -> "testing");

	}
}
