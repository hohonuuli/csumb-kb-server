
package org.mbari.m3.kbserver.actions;

import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.interfaces.*;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.*;
import vars.UserAccount;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;


public class JToken
{
	private String token;

	public JToken()
	{
		token = null;
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
	}


	public void verifyToken(String token, UserAccount user)
	{

		//String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc7yUa5MhvcP03nJPyCPzZtQcGEp-zWfOkEE";
		try 
		{
			boolean admin = (user.isAdministrator()) ? true : false;
		    Algorithm algorithm = Algorithm.HMAC256("secret");
		    JWTVerifier verifier = JWT.require(algorithm)
		    .withIssuer(user.getUserName())
		    .withClaim("isAdmin", admin)
		    .build(); //Reusable verifier instance
		    DecodedJWT jwt = verifier.verify(token);
		} 
		catch (UnsupportedEncodingException exception){
		    //UTF-8 encoding not supported
		    throw new AssertionError("UTF-8 is unknown");
		} 
		catch (JWTVerificationException e){
		    //Invalid signature/claims
			throw new JWTVerificationException("Invalid signature/claim", e);

		}

	}

}

