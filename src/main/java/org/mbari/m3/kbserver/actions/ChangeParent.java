package org.mbari.m3.kbserver.actions;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;

/**
 * CreateConcept
 */
public class ChangeParent {

    private final String newParentName;
    private final String name;
    private final UserAccount userAccount;

    public ChangeParent(String newParentName, String name, UserAccount userAccount) {
        this.newParentName = newParentName;
        this.name = name;
        this.userAccount = userAccount;
    }

    public Concept apply(ToolBelt toolBelt) {
        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();

        Concept newParentConcept = dao.findByName(newParentName);
        if (newParentConcept == null) {
            throw new RuntimeException("Unable to find " + newParentName);
        }

        Concept existingConcept = dao.findByName(name);
        if (existingConcept == null) {
            throw new RuntimeException(name + " does not exist in the database");
        }


        Concept currentParentConcept = dao.findByName(existingConcept.getPrimaryConceptName().getName());
        if (currentParentConcept == null) {
            throw new RuntimeException("Unable to find " + existingConcept.getPrimaryConceptName().getName());
        }

        // PARENT.removeChildConcept(existingConcept);
        currentParentConcept.removeChildConcept(existingConcept);

        // NEW_PARENT.addChildConcept(existingConcept);
        newParentConcept.addChildConcept(existingConcept);

        dao.persist(existingConcept);
        History history = toolBelt.getHistoryFactory().add(userAccount, existingConcept);
        newParentConcept.getConceptMetadata().addHistory(history);
        existingConcept.getParentConcept().getConceptMetadata().addHistory(history);
        dao.persist(history);
        dao.endTransaction();
        dao.close();
        return existingConcept;
    }
}
