package org.mbari.m3.kbserver.examples;

import org.mbari.m3.kbserver.actions.AddUserAccount;
import vars.knowledgebase.ui.ToolBelt;
import org.mbari.m3.kbserver.Initializer;

public class AddUserAccounts
{
	public static void main(String[] args)
	{
		ToolBelt toolBelt = Initializer.getToolBelt();
		AddUserAccount user = new AddUserAccount(toolBelt);
		user.apply("dario12345","Dario","Molina","password","adminis","dar@hello.com","affiliation");

		//String, userName, String firstName, String lastName,
	 	//String password, String role, String email, String affiliation)
	}
}