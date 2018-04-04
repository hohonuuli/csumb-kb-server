package org.mbari.m3.kbserver.actions;

import com.typesafe.config.ConfigException.Null;

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

    public boolean isCircular(Concept thisConcept) {
        return false;
    }

    // TODO: Get better identifyer for top of Phylogenic tree
    // Currently getting name "object", could be used somewhere else.
    public boolean isChildOfRootConcept(ToolBelt toolBelt, Concept concept) {
        if (getParentName(toolBelt, concept).equals("object")) {
            return true;
        } else {
            return false;
        }
    }

    public String getParentName(ToolBelt toolBelt, Concept concept) {
        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();

        // Get the parent
        if (concept.getParentConcept() == null) {
            return "ChangeParent: Found object, parent was null";
        }

        return concept.getParentConcept().getPrimaryConceptName().getName();
    }

    public boolean apply(ToolBelt toolBelt) throws NullPointerException {
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

        // Verifying deletion

        // Check if Concept is child of root node
        if (this.isChildOfRootConcept(toolBelt, existingConcept)) {
            System.out.println("Concept is direct child of root (object)");
            return false;
        }

        // TODO: Check if Concept has a circular refernce
        //

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
        return true;
    }
}
