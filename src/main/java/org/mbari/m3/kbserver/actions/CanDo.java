package org.mbari.m3.kbserver.actions;

import vars.UserAccount;
import vars.knowledgebase.History;

/**
 * CanDo
 */
public interface CanDo {

    default boolean canDo(final UserAccount userAccount, final History history) {
        return userAccount != null && userAccount.isAdministrator() && history != null;
    }
}