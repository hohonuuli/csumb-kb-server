package org.mbari.m3.kbserver.actions;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.History;
import org.mbari.m3.kbserver.actions.ApproveHistory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;


public class DeleteConceptName
{
    private String conceptName;
    private UserAccount userAccount;

    public DeleteConceptName(String conceptName, UserAccount userAccount) 
    {
      this.conceptName = conceptName;
      this.userAccount = userAccount;
    }


    public boolean apply(ToolBelt toolBelt)
    {
         boolean ok = true;
         ConceptNameDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptNameDAO();
         dao.startTransaction();

    //     //finding the concept
         ConceptName conceptName = dao.findByName(this.conceptName);

    //     //making sure the concept exists
         if (conceptName == null)
             throw new RuntimeException("Unable to find concept name" + this.conceptName);

        Concept concept =  conceptName.getConcept();//conceptDao.findByName(this.concepts);

        
          //replaceConceptName(UserAccount userAccount, ConceptName oldName, ConceptName newName) {
          History history = toolBelt.getHistoryFactory().delete(userAccount, conceptName);


        if(new ApproveHistory(){}.approve(userAccount, history, dao))
        {
            concept.getConceptMetadata().addHistory(history);
            dao.remove(conceptName);
            dao.persist(concept);
            dao.persist(history);
        }

        else
            ok = false;
        
        dao.endTransaction();
        dao.close();
        return ok;
    }

}