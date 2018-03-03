package org.mbari.m3.kbserver;

import static spark.Spark.*;

import com.google.inject.spi.Toolable;
import com.google.gson.Gson;
import org.mbari.m3.kbserver.Initializer;
import org.mbari.m3.kbserver.actions.CreateConcept;
import org.mbari.m3.kbserver.actions.DeleteConcept;
import org.mbari.m3.kbserver.actions.AddConceptName;
import org.mbari.m3.kbserver.actions.ConceptData;
//import org.mbari.m3.kbserver.actions.JsonTransformer;
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


// get("/hello", "application/json", (request, response) -> {
//     return new MyMessage("Hello World");
// }, new JsonTransformer());

  //Gson gson = new Gson();
  get("/getMetadata/:name", (request, response) -> {

    ConceptData data = new ConceptData(request.params(":name"), Initializer.getToolBelt());
    String[] names = {"james","dwight","tomas","meya"};
    response.type("application/json");

    String ls = "[";

    for(int i = 0; i < names.length; i++)
    {
      if( i == names.length - 1)
        ls += '\"'+ names[i] +"\"";

      else
        ls += '\"'+ names[i] +"\",";

    }

    ls += ']';

    return "{\"names\" : " + ls + "}";

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
            return "{\"message\":\"concept not created\", \"code\": \"401\"}";
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

        //boolean f = (String.class.isInstance(request.queryParams("conceptName"))) ? true : false;


        //String s = request.queryParams("conceptName") + " and " + request.queryParams("type");


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
            return "{\"message\":\"concept name not created\", \"code\": \"401\"}";
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
