package org.mbari.m3.kbserver;

import static spark.Spark.*;

import com.google.inject.spi.Toolable;
import com.google.gson.Gson;
import org.mbari.m3.kbserver.Initializer;
import org.mbari.m3.kbserver.actions.CreateConcept;
import org.mbari.m3.kbserver.actions.DeleteConcept;
import org.mbari.m3.kbserver.actions.AddConceptName;
import org.mbari.m3.kbserver.actions.UpdateConceptName;
import org.mbari.m3.kbserver.actions.DeleteConceptName;
import org.mbari.m3.kbserver.actions.AddConceptMedia;
import org.mbari.m3.kbserver.actions.UpdateConceptMedia;
import org.mbari.m3.kbserver.actions.ConceptData;
import vars.MiscDAOFactory;
import org.mbari.m3.kbserver.actions.AddUserAccount;
import org.mbari.m3.kbserver.actions.DeleteConceptMedia;
import org.mbari.m3.kbserver.actions.ChangeParent;
import org.mbari.m3.kbserver.actions.JToken;
import org.mbari.m3.kbserver.actions.LinkRealizationUtil;
import org.mbari.m3.kbserver.actions.LinkTemplateUtil;
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

    

     JToken jtoken = JToken.getInstance();

     UserAccount userAccount = findUser(request.params(":username"));

     if(userAccount == null)
        return "{\"message\":\" username does not exist\",\"code\": \"401\"}";

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

      JToken.verifyToken(request.queryParams("jwt"), userAccount);
      
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
            return "{\"message\":\"jwt not provided in endpoint\",\"code\": \"401\"}"; 

          if(request.queryParams("parent") == null)
            return "{\"message\":\"parent concept was not provided in endpoint\",\"code\": \"401\"}";


            //return "{\"message\":\"jwt token was not provided in endpoint\",\"code\": \"401\"}";
         

         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";

          try
        {
          //JToken  jtoken = JToken.getInstance();

          JToken.verifyToken(request.queryParams("jwt"),userAccount);



        //create concept
        CreateConcept fn = new CreateConcept(request.queryParams("parent"), request.params(":name"), userAccount);
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

        if(request.queryParams("type") == null)
            return "{\"message\":\"type was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("url") == null)
            return "{\"message\":\"url was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("credit") == null)
            return "{\"message\":\"credit was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("caption") == null)
            return "{\"message\":\"caption was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("primary") == null)
            return "{\"message\":\"primary was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("jwt") == null)
            return "{\"message\":\"jwt not provided in endpoint\",\"code\": \"401\"}"; 

         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";

        response.type("application/json");


        //checking to see if concept can be created and return json
        try
        {
            //JToken  jtoken = new JToken();

            JToken.verifyToken(request.queryParams("jwt"),userAccount);


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


post("/updateConceptMedia/:name",(request,response) -> {

        ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        // UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        // userAccount.setRole("Admin");
        // userAccount.setUserName("brian");

        if(request.queryParams("userName") == null)
            return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("type") == null)
            return "{\"message\":\"type was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("previousUrl") == null)
            return "{\"message\":\"previousUrl was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("newUrl") == null)
            return "{\"message\":\"newUrl was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("credit") == null)
            return "{\"message\":\"credit was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("caption") == null)
            return "{\"message\":\"caption was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("primary") == null)
            return "{\"message\":\"primay was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("jwt") == null)
            return "{\"message\":\"jwt not provided in endpoint\",\"code\": \"401\"}"; 

         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";

        response.type("application/json");


        //checking to see if concept can be created and return json
        try
        {
            //JToken  jtoken = new JToken();

            JToken.verifyToken(request.queryParams("jwt"),userAccount);


            UpdateConceptMedia fn = new UpdateConceptMedia(request.params(":name"), toolBelt, userAccount);
            if(fn.apply(request.queryParams("previousUrl"),request.queryParams("newUrl"), request.queryParams("caption"),request.queryParams("credit"),request.queryParams("type"),Boolean.valueOf(request.queryParams("primary"))))
            {
              String s = "{\"message\":\"media updated on concept\",\"code\": \"201\",";
              s += "\"type\":\""+ request.queryParams("type")+"\"}";
              return s;
            } 
            else
            {
              String s = "{\"message\":\"media not updated onto concept. User is not admin.\",\"code\": \"401\",";
              s += "\"type\":\""+ request.queryParams("type")+"\"}";
              return s;
            }


        }
        catch (Exception e)
        {
            return "{\"message\":\""+e.getMessage() +" catch exception update\", \"code\": \"401\"}";
        }

    });






    delete("/deleteConceptMedia/:name",(request,response) -> {

        ToolBelt toolBelt = Initializer.getToolBelt();
        // Need user. Normally we would look this up
        // UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
        // userAccount.setRole("Admin");
        // userAccount.setUserName("brian");

        if(request.queryParams("userName") == null)
            return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";

        if(request.queryParams("jwt") == null)
            return "{\"message\":\"jwt not provided in endpoint\",\"code\": \"401\"}"; 

          if(request.queryParams("url") == null)
            return "{\"message\":\"url not provided in endpoint\",\"code\": \"401\"}"; 

         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";

        response.type("application/json");


        //checking to see if concept can be created and return json
        try
        {
            //JToken  jtoken = new JToken();

            JToken.verifyToken(request.queryParams("jwt"),userAccount);


            DeleteConceptMedia fn = new DeleteConceptMedia(request.params(":name"),request.queryParams("url"), userAccount);
            if(fn.apply(toolBelt))
            {
              String s = "{\"message\":\"media deleted\",\"code\": \"201\",";
              s += "\"type\":\""+ request.queryParams("type")+"\"}";
              return s;
            } 
            else
            {
              String s = "{\"message\":\"media not deleted. User is not admin.\",\"code\": \"401\",";
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

         if(request.params(":conceptApplyTo") == null)
            return "{\"message\":\"concept to apply to was not provided in endpoint\",\"code\": \"401\"}";

         if(request.queryParams("conceptName") == null)
            return "{\"message\":\"conceptName was not provided in endpoint\",\"code\": \"401\"}";

         if(request.queryParams("userName") == null)
            return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";

          if(request.queryParams("type") == null)
            return "{\"message\":\"type was not provided in endpoint\",\"code\": \"401\"}";

         if(request.queryParams("jwt") == null)
            return "{\"message\":\"jwt not provided in endpoint\",\"code\": \"401\"}"; 
        
         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";

    
        //specifying response type
        response.type("application/json");

        try
        {

          //JToken  jtoken = new JToken();

          JToken.verifyToken(request.queryParams("jwt"),userAccount);

          AddConceptName fn = new AddConceptName(request.queryParams("conceptName"), request.params(":conceptApplyTo"), userAccount, request.queryParams("type") );

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


      post("/updateConceptName/:oldConceptName", (request, response) -> {

          ToolBelt toolBelt = Initializer.getToolBelt();
              // Need user. Normally we would look this up
              // UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
              // userAccount.setRole("Admin");
              // userAccount.setUserName("brian");

          // if(request.params(":concept") == null)
          //   return "{\"message\":\"concept to apply to was not provided in endpoint\",\"code\": \"401\"}";

         if(request.queryParams("newConceptName") == null)
            return "{\"message\":\"conceptName was not provided in endpoint\",\"code\": \"401\"}";

          if(request.params("oldConceptName") == null)
            return "{\"message\":\"oldConceptName was not provided in endpoint\",\"code\": \"401\"}";

         if(request.queryParams("userName") == null)
            return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";

          if(request.queryParams("type") == null)
            return "{\"message\":\"type was not provided in endpoint\",\"code\": \"401\"}";

         if(request.queryParams("jwt") == null)
            return "{\"message\":\"jwt not provided in endpoint\",\"code\": \"401\"}"; 
              
          UserAccount userAccount = findUser(request.queryParams("userName"));


               if(userAccount == null)
                  return "{\"message\":\"username not found\",\"code\": \"401\"}";

          
              //specifying response type
              response.type("application/json");

              try
              {

                //JToken  jtoken = new JToken();

                JToken.verifyToken(request.queryParams("jwt"),userAccount);

                //public UpdateConceptName(String newConceptName, String conceptApplyTo, UserAccount userAccount, String type) 
                UpdateConceptName fn = new UpdateConceptName(request.queryParams("newConceptName"), request.params(":oldConceptName"), userAccount, request.queryParams("type"));

                if(fn.apply(toolBelt))
                  {
                    String s = "{\"message\":\"concept name was updated!\",\"code\": \"201\",";
                    s += "\"newConceptName\":\""+ request.queryParams("newConceptName")+"\",";
                    s += "\"oldConceptName\":\""+ request.params(":oldConceptName")+"\",";
                    s += "\"type\":\""+ request.queryParams("type")+"\",";
                    s += "\"code\": \"201\"}";
                    return s;
                  } 
                  else
                  {
                    String s = "{\"message\":\"concept name not updated! User is not admin.\",\"code\": \"401\",";
                    s += "\"newConceptName\":\""+ request.queryParams("newConceptName")+"\",";
                    s += "\"oldConceptName\":\""+ request.params(":oldConceptName")+"\",";
                    s += "\"type\":\""+ request.queryParams("type")+"\",";
                    s += "\"code\": \"401\"}";
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

          if(request.queryParams("jwt") == null)
            return "{\"message\":\"jwt not provided in endpoint\",\"code\": \"401\"}"; 

         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";


           response.type("application/json");

           //checking to see if concept can be created and return json
           try
           {

                //JToken  jtoken = new JToken();

               JToken.verifyToken(request.queryParams("jwt"),userAccount);      

               DeleteConcept fn = new DeleteConcept(request.params(":name"), userAccount); 

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

      delete("/deleteConceptName/:name", (request, response) -> {

           ToolBelt toolBelt = Initializer.getToolBelt();
           // Need user. Normally we would look this up
           // UserAccount userAccount = toolBelt.getMiscFactory().newUserAccount();
           // userAccount.setRole("Admin");
           // userAccount.setUserName("brian");

           if(request.params(":name") == null)
            return "{\"message\":\"concept name was not provided in endpoint\",\"code\": \"401\"}";


          if(request.queryParams("userName") == null)
            return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";

          if(request.queryParams("jwt") == null)
            return "{\"message\":\"jwt not provided in endpoint\",\"code\": \"401\"}"; 

         UserAccount userAccount = findUser(request.queryParams("userName"));


         if(userAccount == null)
            return "{\"message\":\"username not found\",\"code\": \"401\"}";


           response.type("application/json");

           //checking to see if concept can be created and return json
           try
           {

                //JToken  jtoken = new JToken();

               JToken.verifyToken(request.queryParams("jwt"),userAccount);      

               DeleteConceptName fn = new DeleteConceptName(request.params(":name"), userAccount); 

               if(fn.apply(toolBelt))
                  return "{\"message\":\"Concept name has been Deleted!\",\"code\": \"201\"}";

               else
                  return "{\"message\":\"Concept name was not deleted! User is not admin.\",\"code\": \"401\"}";

           }
           catch (Exception e)
           {
               return "{\"message\":\""+e.getMessage() +"\", \"code\": \"401\"}";
           }
       });
      
       // Link realization

    post("/addLinkRealization/:name", (request, response) -> {
      ToolBelt toolBelt = Initializer.getToolBelt();

      if (request.queryParams("userName") == null) {
        return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";
      }
      
      if (request.queryParams("jwt") == null) {
        return "jwt is: " + request.queryParams("jwt");
      }

      if (request.params(":name") == null) {
        return "{\"message\":\"concept name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("toConcept") == null) {
        return "{\"message\":\"toConcept was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("linkName") == null) {
        return "{\"message\":\"linkName name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("linkValue") == null) {
        return "{\"message\":\"linkValue name was not provided in endpoint\",\"code\": \"401\"}";
      }
    
      UserAccount userAccount = findUser(request.queryParams("userName"));

      if (userAccount == null) {
        return "{\"message\":\"username not found\",\"code\": \"401\"}";
      }

      try {
        JToken.verifyToken(request.queryParams("jwt"), userAccount);
        
        LinkRealizationUtil linkRealizationUtil = new LinkRealizationUtil(userAccount);
        response.type("application/json");

        if (linkRealizationUtil.addLinkRealizations(toolBelt, request.params(":name"), request.queryParams("linkName"), request.queryParams("toConcept"), request.queryParams("linkValue"))) {
          return "{\"message\":\"successly added link realization\", \"code\": \"200\"}";
        } 
        
        else {
          return "{\"message\":\"Issue with adding link realization, linkName may already exist.\", \"code\": \"401\"}";
        }
      }
  
      catch (Exception e) {
          return "{\"message\":\"" + e.getMessage() + "\", \"code\": \"401\"}";
      }
    });

    delete("/deleteLinkRealization/:name", (request, response) -> {
      ToolBelt toolBelt = Initializer.getToolBelt();

      if (request.queryParams("userName") == null) {
        return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";
      }
      
      if (request.queryParams("jwt") == null) {
        return "jwt is: " + request.queryParams("jwt");
      }

      if (request.params(":name") == null) {
        return "{\"message\":\"concept name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("linkName") == null) {
        return "{\"message\":\"linkName name was not provided in endpoint\",\"code\": \"401\"}";
      }
    
      UserAccount userAccount = findUser(request.queryParams("userName"));

      if (userAccount == null) {
        return "{\"message\":\"username not found\",\"code\": \"401\"}";
      }

      try {
        JToken.verifyToken(request.queryParams("jwt"), userAccount);
        
        LinkRealizationUtil linkRealizationUtil = new LinkRealizationUtil(userAccount);
        response.type("application/json");

        if (linkRealizationUtil.deleteLinkRealization(toolBelt, request.params(":name"), request.queryParams("linkName"))) {
          return "{\"message\":\"successly deleted link realization\", \"code\": \"200\"}";
        } 
        
        else {
          return "{\"message\":\"Issue with deleting link realization\", \"code\": \"401\"}";
        }
      }
  
      catch (Exception e) {
          return "{\"message\":\"" + e.getMessage() + "\", \"code\": \"401\"}";
      }
    });

    post("/updateLinkRealization/:name", (request, response) -> {

      ToolBelt toolBelt = Initializer.getToolBelt();

      if (request.queryParams("userName") == null) {
        return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";
      }
      
      if (request.queryParams("jwt") == null) {
        return "jwt is: " + request.queryParams("jwt");
      }

      if (request.params(":name") == null) {
        return "{\"message\":\"concept name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("toConcept") == null) {
        return "{\"message\":\"toConcept was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("linkName") == null) {
        return "{\"message\":\"linkName name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("linkValue") == null) {
        return "{\"message\":\"linkValue name was not provided in endpoint\",\"code\": \"401\"}";
      }
    
      UserAccount userAccount = findUser(request.queryParams("userName"));

      if (userAccount == null) {
        return "{\"message\":\"username not found\",\"code\": \"401\"}";
      }

      try {
        JToken.verifyToken(request.queryParams("jwt"), userAccount);
        
        LinkRealizationUtil linkRealizationUtil = new LinkRealizationUtil(userAccount);
        response.type("application/json");

        if (!linkRealizationUtil.doesLinkRealizationExist(toolBelt, request.params(":name"), request.queryParams("linkName"))) {
          return "{\"message\":\"successly updated link realization\", \"code\": \"200\"}";
        } 
        
        else {
          return "{\"message\":\"Issue with adding link realization\", \"code\": \"401\"}";
        }
      }
  
      catch (Exception e) {
          return "{\"message\":\"" + e.getMessage() + "\", \"code\": \"401\"}";
      }
    });



    post("/addLinkTemplate/:name", (request, response) -> {
      ToolBelt toolBelt = Initializer.getToolBelt();

      if (request.queryParams("userName") == null) {
        return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";
      }
      
      if (request.queryParams("jwt") == null) {
        return "jwt is: " + request.queryParams("jwt");
      }

      if (request.params(":name") == null) {
        return "{\"message\":\"concept name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("toConcept") == null) {
        return "{\"message\":\"toConcept was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("linkName") == null) {
        return "{\"message\":\"linkName name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("linkValue") == null) {
        return "{\"message\":\"linkValue name was not provided in endpoint\",\"code\": \"401\"}";
      }
    
      UserAccount userAccount = findUser(request.queryParams("userName"));

      if (userAccount == null) {
        return "{\"message\":\"username not found\",\"code\": \"401\"}";
      }

      try {
        JToken.verifyToken(request.queryParams("jwt"), userAccount);
        //String concept,ToolBelt toolBelt, String linkName, String linkValue, String toConcept, UserAccount userAccount
        LinkTemplateUtil fn = new LinkTemplateUtil(request.params(":name"),toolBelt,request.queryParams("linkName"),request.queryParams("linkValue"),request.queryParams("toConcept"), userAccount);
        response.type("application/json");

        if (fn.addTemplate())
          return "{\"message\":\"successly added template\", \"code\": \"200\"}";
        
        else
          return "{\"message\":\"Cannot add template, user is not admin.\", \"code\": \"401\"}";
      }
  
      catch (Exception e) {
          return "{\"message\":\"" + e.getMessage() + "\", \"code\": \"401\"}";
      }
    });



    post("/updateLinkRealization", (request, response) -> {
      ToolBelt toolBelt = Initializer.getToolBelt();

      if (request.queryParams("userName") == null) {
        return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";
      }
      
      if (request.queryParams("jwt") == null) {
        return "jwt is: " + request.queryParams("jwt");
      }

      if (request.queryParams("concept") == null) {
        return "{\"message\":\"concept name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("oldToConcept") == null) {
        return "{\"message\":\"oldToConcept was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("oldLinkName") == null) {
        return "{\"message\":\"oldLinkName name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("oldLinkValue") == null) {
        return "{\"message\":\"oldLinkValue name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("newToConcept") == null) {
        return "{\"message\":\"newToConcept was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("newLinkName") == null) {
        return "{\"message\":\"newLinkName name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("newLinkValue") == null) {
        return "{\"message\":\"newLinkValue name was not provided in endpoint\",\"code\": \"401\"}";
      }
    
      UserAccount userAccount = findUser(request.queryParams("userName"));

      if (userAccount == null) {
        return "{\"message\":\"username not found\",\"code\": \"401\"}";
      }

      try {
        JToken.verifyToken(request.queryParams("jwt"), userAccount);
        
        LinkRealizationUtil linkRealizationUtil = new LinkRealizationUtil(userAccount);
        response.type("application/json");

        if (linkRealizationUtil.updateLinkRealization(toolBelt, request.queryParams("concept"), request.queryParams("oldLinkName"), request.queryParams("oldToConcept"), request.queryParams("oldLinkValue"), request.queryParams("newLinkName"), request.queryParams("newToConcept"), request.queryParams("newLinkValue"))) {
          return "{\"message\":\"successly updated link realization\", \"code\": \"200\"}";
        } 
        
        else {
          return "{\"message\":\"Issue with adding link realization\", \"code\": \"401\"}";
        }
      }
  
      catch (Exception e) {
          return "{\"message\":\"" + e.getMessage() + "\", \"code\": \"401\"}";
      }
    });


    delete("/deleteLinkTemplate/:name", (request, response) -> {
      ToolBelt toolBelt = Initializer.getToolBelt();

      if (request.queryParams("userName") == null) {
        return "{\"message\":\"username was not provided in endpoint\",\"code\": \"401\"}";
      }
      
      if (request.queryParams("jwt") == null) {
        return "jwt is: " + request.queryParams("jwt");
      }

      if (request.params(":name") == null) {
        return "{\"message\":\"concept name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("toConcept") == null) {
        return "{\"message\":\"toConcept was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("linkName") == null) {
        return "{\"message\":\"linkName name was not provided in endpoint\",\"code\": \"401\"}";
      }

      if (request.queryParams("linkValue") == null) {
        return "{\"message\":\"linkValue name was not provided in endpoint\",\"code\": \"401\"}";
      }
    
      UserAccount userAccount = findUser(request.queryParams("userName"));

      if (userAccount == null) {
        return "{\"message\":\"username not found\",\"code\": \"401\"}";
      }

      try {
        JToken.verifyToken(request.queryParams("jwt"), userAccount);
        //String concept,ToolBelt toolBelt, String linkName, String linkValue, String toConcept, UserAccount userAccount
        LinkTemplateUtil fn = new LinkTemplateUtil(request.params(":name"),toolBelt,request.queryParams("linkName"),request.queryParams("linkValue"),request.queryParams("toConcept"), userAccount);
        response.type("application/json");

        if (fn.deleteTemplate())
          return "{\"message\":\"successly deleted template\", \"code\": \"200\"}";
        
        else
          return "{\"message\":\"Cannot delete template, user is not admin.\", \"code\": \"401\"}";
      }
  
      catch (Exception e) {
          return "{\"message\":\"" + e.getMessage() + "\", \"code\": \"401\"}";
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
