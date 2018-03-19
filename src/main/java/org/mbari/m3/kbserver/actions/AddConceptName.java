package org.mbari.m3.kbserver.actions;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import org.mbari.m3.kbserver.actions.ApproveHistory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;


public class AddConceptName
{
	private final String newConceptName;
    private final String conceptApplyTo;
    private final UserAccount userAccount;
    private final String typeOfName;

    public AddConceptName(String newConceptName, String conceptApplyTo, UserAccount userAccount, String type) 
    {
        this.newConceptName = newConceptName;
        this.conceptApplyTo = conceptApplyTo;
        this.userAccount = userAccount;
        this.typeOfName = type;
    }


    public boolean apply(ToolBelt toolBelt)
    {
        boolean ok = true;
        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();

        //finding the concept
        Concept concept = dao.findByName(this.conceptApplyTo);

        //making sure the concept exists
        if (concept == null)
            throw new RuntimeException("Unable to find " + this.conceptApplyTo);

        KnowledgebaseFactory knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
        
        ConceptName conceptName = knowledgebaseFactory.newConceptName();

        //setting other name to the concept
        conceptName.setName(this.newConceptName);

        //checking to see what kind of name it is and assigning it
        switch (this.typeOfName.toLowerCase())
        {
            case "common":
                conceptName.setNameType(ConceptNameTypes.COMMON.toString());
                break;
            
            case "synonym":
                conceptName.setNameType(ConceptNameTypes.SYNONYM.toString());
                break;

            case "former":
                conceptName.setNameType(ConceptNameTypes.FORMER.toString());
                break;

            default:
                conceptName.setNameType(ConceptNameTypes.ALTERNATE.toString());
                break;

        }
        
        //adding the concept name to concept
        concept.addConceptName(conceptName);

        //setting who made the change
        concept.setOriginator(userAccount.getUserName());

        //newHistory(UserAccount userAccount, String action, String fieldName, String oldValue, String newValue)

         History history = toolBelt.getHistoryFactory().add(userAccount, conceptName);


        if(new ApproveHistory(){}.approve(userAccount, history, dao))
        {
            dao.persist(concept);
            concept.getConceptMetadata().addHistory(history);
            dao.persist(history);
        }

        else
            ok = false;
        
        dao.endTransaction();
        dao.close();
        return ok;
    }

}