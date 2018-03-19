package org.mbari.m3.kbserver.actions;

import vars.knowledgebase.ui.ToolBelt;
import vars.MiscDAOFactory;
import vars.UserAccountDAO;
import vars.UserAccount;

public class AddUserAccount
{
	private ToolBelt toolBelt;
	private UserAccountDAO userDao;
	private UserAccount userAccount;

	public AddUserAccount(ToolBelt toolBelt)
	{
		this.toolBelt = toolBelt;
		userDao = toolBelt.getMiscDAOFactory().newUserAccountDAO();
		userAccount = toolBelt.getMiscFactory().newUserAccount();
	}

	public void apply(String userName, String firstName, String lastName, String password, String role, String email, String affiliation)
	{
		userDao.startTransaction();
		UserAccount user_account = userDao.findByUserName(userName);

		if (user_account != null) 
            throw new RuntimeException("User account already exists with user name: " + userName);
        

        userAccount.setUserName(userName);
        userAccount.setFirstName(firstName);
        userAccount.setLastName(lastName);
        userAccount.setPassword(password);
        userAccount.setRole(role);
        userAccount.setEmail(email);
        userAccount.setAffiliation(affiliation);

        System.out.println("Is administrator: " + userAccount.isAdministrator());

        userDao.persist(userAccount);
        userDao.endTransaction();
        userDao.close();
        return;
	}

}