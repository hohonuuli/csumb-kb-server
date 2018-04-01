
package org.mbari.m3.kbserver.examples;

import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.interfaces.*;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.*;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class JToken
{
	public static void main(String[] args)
	{

		try 
		{
	    	Algorithm algorithm = Algorithm.HMAC256("secret");
	    	String token = JWT.create()
        		.withIssuer("auth0")
        		.sign(algorithm);


	      System.out.println("Token is: " + token);
		} 
		catch (UnsupportedEncodingException e)
		{
	    //UTF-8 encoding not supported
			throw new AssertionError("UTF-8 is unknown");
		}
	 // 	catch (JWTCreationException e)
	 // 	{
	 // 		e = new JWTCreationException("Invalid signing configuration",throw new AssertionError("error!!"));
	 //    //Invalid Signing configuration / Couldn't convert Claims.
		// }


	}
}