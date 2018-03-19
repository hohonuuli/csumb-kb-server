package org.mbari.m3.kbserver.actions;


import java.util.Date;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.ui.ToolBelt;

/**
 * DeleteConcept
 */
public class DeleteConcept implements ApproveHistory {

    private final String name;
    private final UserAccount userAccount;

    public DeleteConcept(String name, UserAccount userAccount) {
        this.name = name;
        this.userAccount = userAccount;
    }

    public Boolean apply(ToolBelt toolBelt) {

        boolean ok = true;
        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();
        Concept concept = dao.findByName(name);

        if(concept == null)
            throw new RuntimeException("Unable to find " + name);

        Concept parentConcept = concept.getParentConcept();
        if (parentConcept != null) {
            HistoryFactory historyFactory = toolBelt.getHistoryFactory();
            History history = historyFactory.delete(userAccount, concept);

            if(new ApproveHistory(){}.approve(userAccount, history, dao))
            {
                parentConcept.getConceptMetadata().addHistory(history);
                dao.persist(history);
                dao.cascadeRemove(concept);
            }

            else
                ok = false;


            

        }
        else {
            ok = false;
        }

        dao.endTransaction();
        dao.close();
        return ok;

    }


    
}