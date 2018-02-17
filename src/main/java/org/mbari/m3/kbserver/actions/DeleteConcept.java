package org.mbari.m3.kbserver.actions;


import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.ui.ToolBelt;

/**
 * DeleteConcept
 */
public class DeleteConcept  {

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
        Concept parentConcept = concept.getParentConcept();
        if (parentConcept != null) {
            HistoryFactory historyFactory = toolBelt.getHistoryFactory();
            History history = historyFactory.delete(userAccount, concept);

            //this gave me an error
            //concept.removeConceptName(concept.getConceptName(this.name));

            //added this to see if removes concept(it didn't)
            parentConcept.removeChildConcept(concept);
            parentConcept.getConceptMetadata().addHistory(history);

            //added this to help fix error since this was present in create concept
            dao.persist(history);
        }
        else {
            ok = false;
        }

        dao.endTransaction();
        dao.close();
        return ok;

    }


    
}