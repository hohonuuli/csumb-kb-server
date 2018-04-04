
package org.mbari.m3.kbserver.examples;

import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.interfaces.*;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.*;
import vars.UserAccountDAO;
import vars.UserAccount;
import vars.knowledgebase.ui.ToolBelt;
import org.mbari.m3.kbserver.Initializer;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;
import org.mbari.m3.kbserver.actions.JToken;

public class JTokens
{
	public static void main(String[] args)
	{

		UserAccount userAccount = findUser("testffing");

		//JToken jtoken = new JToken();
		//String token = jtoken.createToken(userAccount);

		System.out.println("Value: ");


		//jtoken.verifyToken(token);
	}

	public static UserAccount findUser(String userName)
  	{
      ToolBelt toolBelt = Initializer.getToolBelt();
      UserAccountDAO userDao = toolBelt.getMiscDAOFactory().newUserAccountDAO();
      return userDao.findByUserName(userName);

  	}
}