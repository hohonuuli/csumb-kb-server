package org.mbari.m3.kbserver.actions;

import java.util.Date;
import vars.DAO;
import vars.UserAccount;
import vars.knowledgebase.History;

/**
 * ApproveHistory
 */
public interface ApproveHistory extends CanDo {
    
    /**
     * Sets fields in history that are appropriate when approved. Should
     * be called within a transaction
     */
    default void approve(UserAccount userAccount, History history, DAO dao) {

        if (canDo(userAccount, history)) {
            history.setProcessedDate(new Date());
            history.setProcessorName(userAccount.getUserName());
            history.setApproved(Boolean.TRUE);
        }
        else {
            final String msg = "Unable to approve the History [" + history + "]";

            // TODO Handle case where not apporved
        }
    }
}