
package org.mbari.m3.kbserver.actions;

import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.interfaces.*;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.*;
import vars.UserAccount;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;
import java.util.Date;
import java.util.Random;
import org.apache.commons.lang3.time.DateUtils;




public class JToken
{
	private static JWTVerifier verifier;
	private static JToken instance = null;

	private JToken()
	{
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

			Date targetTime = new Date(); 
			targetTime = DateUtils.addMinutes(targetTime, (60 * 8));
			boolean admin = (user.isAdministrator()) ? true : false;
			Algorithm algorithm = Algorithm.HMAC256("secret");
	    	String token = JWT.create()
        		.withIssuer(user.getUserName())
        		.withClaim("isAdmin", admin)
        		.withExpiresAt(targetTime)
        		.sign(algorithm);

        	return token;
		} 
		catch (UnsupportedEncodingException e)
		{
	    //UTF-8 encoding not supported
			throw new AssertionError("UTF-8 is unknown");
		}
	 	catch (JWTCreationException e)
	 	{
	 		throw new JWTCreationException("Invalid signing configuration creating jwt", e);
	    //Invalid Signing configuration / Couldn't convert Claims.
		}

	}


	public static  DecodedJWT verifyToken(String token, UserAccount user)
	{
		try 
		{
		   boolean admin = (user.isAdministrator()) ? true : false;
		   Algorithm algorithm = Algorithm.HMAC256("secret");
		   verifier = JWT.require(algorithm)
		   .withIssuer(user.getUserName())
           .withClaim("isAdmin", admin)
		   .build();
		   return verifier.verify(token);
		   
		} 
		catch (UnsupportedEncodingException e)
		{
	    //UTF-8 encoding not supported
			throw new AssertionError("UTF-8 is unknown");
		}
		catch (JWTVerificationException e){
		    //Invalid signature/claims
			throw new JWTVerificationException("Invalid signature/claim while verifying token", e);

		}

	}


}

