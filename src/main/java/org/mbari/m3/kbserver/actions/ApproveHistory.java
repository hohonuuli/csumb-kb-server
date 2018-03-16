package org.mbari.m3.kbserver.actions;

import java.util.Date;
import vars.DAO;
import vars.UserAccount;
import vars.knowledgebase.History;

/**
 * ApproveHistory
 */
public interface ApproveHistory extends CanDo 
{
    
    /**
     * Sets fields in history that are appropriate when approved. Should
     * be called within a transaction
     */
    default boolean approve(UserAccount userAccount, History history, DAO dao)
     {

        if (canDo(userAccount, history))
         {
            history.setProcessedDate(new Date());
            history.setProcessorName(userAccount.getUserName());
            history.setApproved(Boolean.TRUE);

            return true;
        }
        else 
        {
            final String msg = "Unable to approve the History [" + history + "]";
            RejectHistory rejected = new RejectHistory(){};

            try
            {
                rejected.reject(userAccount, history, dao);
                System.out.println(msg);
                return false;
            }
            catch(Exception e)
            {
                rejected.dropHistory(history,msg, dao);
                return false;
            }
        }
    }
}