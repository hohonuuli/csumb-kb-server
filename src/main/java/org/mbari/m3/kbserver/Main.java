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
import vars.MiscDAOFactory;
import org.mbari.m3.kbserver.actions.AddUserAccount;
import org.mbari.m3.kbserver.actions.ChangeParent;
import org.mbari.m3.kbserver.actions.JToken;
import vars.UserAccountDAO;
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



  post("/userLogin/:username", (request, response) -> {
    
    if(request.queryParams("password") == null || request.params(":username") == null)
      return "{\"message\":\"username and/or password were not included in endpoint\",\"code\": \"401\"}";

    

     JToken jtoken = new JToken();

     UserAccount userAccount = findUser(request.params(":username"));
     response.type("application/json");

    //return "{\"message\":\"token successfully created.\",\"jwt\": " + jtoken.createToken(userAccount) + ",\"code\": \"201\"}";

    try
    {
      if(userAccount.authenticate(request.queryParams("password")))
        return "{\"message\":\"token successfully created.\",\"jwt\": \"" + jtoken.createToken(userAccount) + "\",\"code\": \"201\"}";
      
      return "{\"message\":\"username and/or password are incorrect\",\"code\": \"401\"}";
    }
    
    catch(Exception e)
    {
      return "{\"message\" : \"" + e.getMessage() +"\", \"code\" : \"401\"}";
    }

  });

  post("/changeParent", (request, response) -> {
    ToolBelt toolBelt = Initializer.getToolBelt();

    if (request.queryParams("userName") == null) {
      return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";
    }
    
    if (request.queryParams("jwt") == null) {
      return "jwt is: " + request.queryParams("jwt");
    }

    if (request.queryParams("newParent") == null) {
      return "{\"message\":\"newParent was not provided in endpoint\",\"code\": \"401\"}";
    }

    if (request.queryParams("concept") == null) {
      return "{\"message\":\"concept was not provided in endpoint\",\"code\": \"401\"}";
    }
    
    UserAccount userAccount = findUser(request.queryParams("userName"));

    if (userAccount == null) {
      return "{\"message\":\"username not found\",\"code\": \"401\"}";
    }

    try {
      // JToken  jtoken = new JToken();
      // jtoken.verifyToken(request.queryParams("jwt"), userAccount);
      
      ChangeParent fn = new ChangeParent(request.queryParams("newParent"), request.queryParams("concept"), userAccount);
      response.type("application/json");
      
      if (fn.apply(toolBelt)) {
        return "{\"message\":\"Concept parent has been updated!\",\"code\": \"201\"}";
      }

      else {
        return "{\"message\":\"Concept parent was NOT updated.\",\"code\": \"401\"}";
      }
    }

    catch (Exception e) {
        return "{\"message\":\"" + e.getMessage() + "\", \"code\": \"401\"}";
    }
  });



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
        // UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        // userAccount.setRole("Admin");
        // userAccount.setUserName("brian");

         if(request.queryParams("userName") == null)
            return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";

         if(request.queryParams("jwt") == null)
            return "jwt is: " + request.queryParams("jwt");
            //return "{\"message\":\"jwt token was not provided in endpoint\",\"code\": \"401\"}";
         

         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";

          try
        {
          JToken  jtoken = new JToken();

          jtoken.verifyToken(request.queryParams("jwt"), userAccount);



        //create concept
        CreateConcept fn = new CreateConcept("behavior", request.params(":name"), userAccount);
        response.type("application/json");

        //checking to see if concept can be created and return json
        
        if(fn.apply(toolBelt))
           return "{\"message\":\"Concept has been created!\",\"code\": \"201\"}";

        else
           return "{\"message\":\"Concept was not created! User is not admin.\",\"code\": \"401\"}";

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
        // UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        // userAccount.setRole("Admin");
        // userAccount.setUserName("brian");

        if(request.queryParams("userName") == null)
            return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";


         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";

        response.type("application/json");


        //checking to see if concept can be created and return json
        try
        {
            AddConceptMedia fn = new AddConceptMedia(request.params(":name"), toolBelt, userAccount);
            if(fn.apply(request.queryParams("url"), request.queryParams("caption"),request.queryParams("credit"),request.queryParams("type"),Boolean.valueOf(request.queryParams("primary"))))
            {
              String s = "{\"message\":\"media added to concept\",\"code\": \"201\",";
              s += "\"type\":\""+ request.queryParams("type")+"\"}";
              return s;
            } 
            else
            {
              String s = "{\"message\":\"media not added to concept. User is not admin.\",\"code\": \"401\",";
              s += "\"type\":\""+ request.queryParams("type")+"\"}";
              return s;
            }


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
        // UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        // userAccount.setRole("Admin");
        // userAccount.setUserName("brian");

         if(request.queryParams("userName") == null)
            return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";


         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";

        //create concept name
        AddConceptName fn = new AddConceptName(request.queryParams("conceptName"), request.params(":conceptApplyTo"), userAccount, request.queryParams("type") );
        response.type("application/json");


        //checking to see if concept can be created and return json
        try
        {
            // fn.apply(toolBelt);

            // String s = "{\"message\":\"concept name added\",\"code\": \"201\",";

            // s += "\"conceptName\":\""+ request.queryParams("conceptName")+"\",";
            // s += "\"type\":\""+ request.queryParams("type")+"\"}";
            // return s;

          if(fn.apply(toolBelt))
            {
              String s = "{\"message\":\"concept name added\",\"code\": \"201\",";
              s += "\"conceptName\":\""+ request.queryParams("conceptName")+"\",";
              s += "\"type\":\""+ request.queryParams("type")+"\"}";
              return s;
            } 
            else
            {
              String s = "{\"message\":\"concept name not added! User is not admin.\",\"code\": \"401\",";
              s += "\"conceptName\":\""+ request.queryParams("conceptName")+"\",";
              s += "\"type\":\""+ request.queryParams("type")+"\"}";
              return s;
            }

        }
        catch (Exception e)
        {
            return "{\"message\":\""+e.getMessage() +"\", \"code\": \"401\"}";
        }

       });

        post("/addUserAccount/:userName", (request, response) -> {

         ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        // UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        // userAccount.setRole("Admin");
        // userAccount.setUserName("brian");

         if(request.params(":userName") == null)
            return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";

         else if(request.queryParams("firstName") == null)
            return "{\"message\":\"first name was not provided in endpoint\",\"code\": \"401\"}";

         else if(request.queryParams("lastName") == null)
            return "{\"message\":\"last name was not provided in endpoint\",\"code\": \"401\"}";
         
         else if(request.queryParams("password") == null)
            return "{\"message\":\"password was not provided in endpoint\",\"code\": \"401\"}";

          else if(request.queryParams("role") == null)
            return "{\"message\":\"role name was not provided in endpoint\",\"code\": \"401\"}";

          else if(request.queryParams("email") == null)
            return "{\"message\":\"email was not provided in endpoint\",\"code\": \"401\"}";

          else if(request.queryParams("affiliation") == null)
            return "{\"message\":\"first name was not provided in endpoint\",\"code\": \"401\"}";

         
         AddUserAccount fn = new AddUserAccount(toolBelt);
         response.type("application/json");
        //String, userName, String firstName, String lastName,
        //String password, String role, String email, String affiliation)

         try
        {
            fn.apply(request.params(":userName"),request.queryParams("firstName"),request.queryParams("lasttName"),
              request.queryParams("password"),request.queryParams("role"),request.queryParams("email"), request.queryParams("affiliation"));

            String s = "{\"message\":\"user account with user name: '"+request.params(":userName")+"' was created.\",\"code\": \"201\"}";
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
           // UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
           // userAccount.setRole("Admin");
           // userAccount.setUserName("brian");

          if(request.queryParams("userName") == null)
            return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";


         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";

           //create concept
           DeleteConcept fn = new DeleteConcept(request.params(":name"), userAccount);

           response.type("application/json");

           //checking to see if concept can be created and return json
           try
           {
               if(fn.apply(toolBelt))
                  return "{\"message\":\"Concept has been Deleted!\",\"code\": \"201\"}";

               else
                  return "{\"message\":\"Concept was not deleted! User is not admin.\",\"code\": \"401\"}";

           }
           catch (Exception e)
           {
               return "{\"message\":\""+e.getMessage() +"\", \"code\": \"401\"}";
           }
       });
	}

  public static UserAccount findUser(String userName)
  {
      ToolBelt toolBelt = Initializer.getToolBelt();
      UserAccountDAO userDao = toolBelt.getMiscDAOFactory().newUserAccountDAO();
      return userDao.findByUserName(userName);

  }
}
