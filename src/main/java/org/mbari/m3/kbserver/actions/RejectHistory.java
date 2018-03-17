package org.mbari.m3.kbserver.actions;

import java.util.Date;
import vars.DAO;
import vars.UserAccount;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.History;

/**
 * RejectHistory
 */
public interface RejectHistory extends CanDo 
{
    

    default void reject(final UserAccount userAccount, History history, DAO dao) {
        if (canDo(userAccount, history)) {
            history.setProcessedDate(new Date());
            history.setApproved(Boolean.FALSE);
            history.setProcessorName(userAccount.getUserName());
        }
    }

    /**
     * When an error occrues where the history is void. Get rid of the 
     * offending history. Should be called within a transction
     */
    default void dropHistory(History h, final String msg, DAO dao) {
        h = dao.find(h);
        final ConceptMetadata conceptMetadata = h.getConceptMetadata();
        conceptMetadata.removeHistory(h);
        dao.remove(h);
    }
}