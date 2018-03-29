package org.mbari.m3.kbserver.examples;

// //import org.mbari.m3.kbserver.jjwt.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;


public class JWT
{
	public static void main()
	{


	// We need a signing key, so we'll create one just for this example. Usually
	// the key would be read from your application configuration instead.
	Key key = MacProvider.generateKey();

	String compactJws = Jwts.builder()
	  .setSubject("Joe")
	  .signWith(SignatureAlgorithm.HS512, key)
	  .compact();
		  
		  
	}
}