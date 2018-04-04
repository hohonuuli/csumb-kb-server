
package org.mbari.m3.kbserver.actions;

import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.interfaces.*;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.*;
import vars.UserAccount;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;


// Java program implementing Singleton class
// with method name as that of class
// Java program implementing Singleton class
// with getInstance() method
// class Singleton
// {
//     // static variable single_instance of type Singleton
//     private static Singleton single_instance = null;
 
//     // variable of type String
//     public String s;
 
//     // private constructor restricted to this class itself
//     private Singleton()
//     {
//         s = "Hello I am a string part of Singleton class";
//     }
 
//     // static method to create instance of Singleton class
//     public static Singleton getInstance()
//     {
//         if (single_instance == null)
//             single_instance = new Singleton();
 
//         return single_instance;
//     }
// }


public class JToken
{
	private String token;
	private static JWTVerifier verifier;
	private static JToken instance = null;

	private JToken()
	{
		token = null;
		verifier = null;
	}

	public static JToken getInstance()
	{

		if(instance == null)
			instance = new JToken();

		return instance;
	}

	public String createToken(UserAccount user)
	{

		try 
		{

			boolean admin = (user.isAdministrator()) ? true : false;
	    	Algorithm algorithm = Algorithm.HMAC256("secret");
	    	token = JWT.create()
        		.withIssuer(user.getUserName())
        		.withClaim("isAdmin", admin)
        		.sign(algorithm);


		    verifier = JWT.require(algorithm)
		    .withIssuer(user.getUserName())
		    .withClaim("isAdmin", admin)
		    .acceptExpiresAt(60) // should expire in 120 sec
		    .build(); //Reusable verifier instance

		    this.token = token;

	      //System.out.println("Token is: " + token);
        	return token;
		} 
		catch (UnsupportedEncodingException e)
		{
	    //UTF-8 encoding not supported
			throw new AssertionError("UTF-8 is unknown");
		}
	 	catch (JWTCreationException e)
	 	{
	 		throw new JWTCreationException("Invalid signing configuration", e);
	    //Invalid Signing configuration / Couldn't convert Claims.
		}
		catch (JWTVerificationException e){
		    //Invalid signature/claims
			throw new JWTVerificationException("Invalid signature/claim", e);
		}
	}


	public static void verifyToken(String token)
	{

		//String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc7yUa5MhvcP03nJPyCPzZtQcGEp-zWfOkEE";

		try 
		{

		   verifier.verify(token);
		} 
		catch (JWTVerificationException e){
		    //Invalid signature/claims
			throw new JWTVerificationException("Invalid signature/claim", e);

		}

	}

}

