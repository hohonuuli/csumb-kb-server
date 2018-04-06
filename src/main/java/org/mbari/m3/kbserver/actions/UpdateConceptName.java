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


public class UpdateConceptName
{
	private final String newConceptName;
    private final String oldConceptName;
    private final UserAccount userAccount;
    private final String typeOfName;
    private final String concepts;

    public UpdateConceptName(String newConceptName, String oldConceptName, UserAccount userAccount, String type) 
    {
        this.newConceptName = newConceptName;
        this.oldConceptName = oldConceptName;
        this.userAccount = userAccount;
        this.typeOfName = type;
        //this.concepts = concept;
    }




    public boolean apply(ToolBelt toolBelt)
    {
         boolean ok = true;
         //ConceptDAO conceptDao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
         ConceptNameDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptNameDAO();
         dao.startTransaction();

    //     //finding the concept
         ConceptName oldConceptName = dao.findByName(this.oldConceptName);

    //     //making sure the concept exists
         if (oldConceptName == null)
             throw new RuntimeException("Unable to find concept name" + this.oldConceptName);

        Concept concept =  oldConceptName.getConcept();//conceptDao.findByName(this.concepts);

        KnowledgebaseFactory knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
        
        ConceptName newConceptName = knowledgebaseFactory.newConceptName();

         //setting other name to the concept
         newConceptName.setName(this.newConceptName);

         //checking to see what kind of name it is and assigning it
         switch (this.typeOfName.toLowerCase())
        {
            case "common":
                newConceptName.setNameType(ConceptNameTypes.COMMON.toString());
                break;
            
            case "synonym":
                newConceptName.setNameType(ConceptNameTypes.SYNONYM.toString());
                break;

            case "former":
                newConceptName.setNameType(ConceptNameTypes.FORMER.toString());
                break;

            default:
                newConceptName.setNameType(ConceptNameTypes.ALTERNATE.toString());
                break;

        }
        
          //replaceConceptName(UserAccount userAccount, ConceptName oldName, ConceptName newName) {
          History history = toolBelt.getHistoryFactory().replaceConceptName(userAccount, oldConceptName,newConceptName);


        if(new ApproveHistory(){}.approve(userAccount, history, dao))
        {

            //adding the concept name to concept
            // concept.addConceptName(newConceptName);

            // //setting who made the change
            // concept.setOriginator(userAccount.getUserName());
            concept.removeConceptName(oldConceptName);
            concept.addConceptName(newConceptName);
            
            concept.getConceptMetadata().addHistory(history);
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