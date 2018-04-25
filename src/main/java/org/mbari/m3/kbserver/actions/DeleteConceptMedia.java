package org.mbari.m3.kbserver.actions;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.Media;
import vars.knowledgebase.History;
import org.mbari.m3.kbserver.actions.ApproveHistory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;
import java.util.*;


public class DeleteConceptMedia
{
    private String concept;
    private UserAccount userAccount;
    private String url;

    public DeleteConceptMedia(String concept, String url, UserAccount userAccount) 
    {
      this.concept = concept;
      this.userAccount = userAccount;
      this.url = url;
    }


    public boolean apply(ToolBelt toolBelt)
    {
         boolean ok = true;
         boolean urlFound = false;
         ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
         dao.startTransaction();

    //     //finding the concept
         Concept concept = dao.findByName(this.concept);

    //     //making sure the concept exists
         if (concept == null)
             throw new RuntimeException("Unable to find concept name" + this.concept);


         Collection<Media> conceptMedias = concept.getConceptMetadata().getMedias();
         Media media = null;

         for (Media s : conceptMedias)
         {

            if(url.equals(s.getUrl()))
            {
                media = s;
                break;
            }
         }

         if(!urlFound)
            throw new RuntimeException("Unable to find media with url: " + url);

          //replaceConceptName(UserAccount userAccount, ConceptName oldName, ConceptName newName) {
          History history = toolBelt.getHistoryFactory().delete(userAccount, media);


        if(new ApproveHistory(){}.approve(userAccount, history, dao))
        {
            concept.getConceptMetadata().removeMedia(media);
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