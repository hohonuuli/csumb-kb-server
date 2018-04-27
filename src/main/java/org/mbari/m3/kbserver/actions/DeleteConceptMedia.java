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
import com.google.inject.Inject;
import java.util.Date;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


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

        URL verifyUrl = null;

        try {
            verifyUrl = new URL(url);
           final InputStream in = verifyUrl.openStream();
           final int b = in.read();
                if (b == -1) {
                    throw new RuntimeException("Unable to read from " + verifyUrl.toExternalForm());
                    //EventBus.publish(StateLookup.TOPIC_WARNING, "Unable to read from " + verifyUrl.toExternalForm());
                }
            }
        catch (Exception e1) {
                dao.endTransaction();
                dao.close();
                throw new RuntimeException("Failed to open URL, media will not be created on concept.");
            }

         for (Media s : conceptMedias)
         {

            System.out.println("s-url: " + s.getUrl() + " length: " + s.getUrl().length());
            System.out.println("url: " + url + " length: " + url.length());

            if(s.getUrl().equals(verifyUrl.toExternalForm()))
            {
                media = s;
                urlFound = true;
                break;
            }
         }


         if(!urlFound)
            throw new RuntimeException("Unable to find media with url: " + url);

          //replaceConceptName(UserAccount userAccount, ConceptName oldName, ConceptName newName) {
          History history = toolBelt.getHistoryFactory().delete(userAccount, media);


        if(new ApproveHistory(){}.approve(userAccount, history, dao))
        {

            if(media.isPrimary())
            {
                for (Media s : conceptMedias)
                {
                    s.setPrimary(true);
                    break;
                }

            }
            concept.getConceptMetadata().removeMedia(media);
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