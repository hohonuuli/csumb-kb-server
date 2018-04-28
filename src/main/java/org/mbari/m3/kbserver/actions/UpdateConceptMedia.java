package org.mbari.m3.kbserver.actions;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.History;
import vars.knowledgebase.Media;
import vars.knowledgebase.MediaTypes;
import org.mbari.m3.kbserver.actions.ApproveHistory;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;
import com.google.inject.Inject;
import java.util.Date;
import java.util.*;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class UpdateConceptMedia
{
	private Concept concept;
	private ConceptDAO dao;
	private Media media;
	private UserAccount userAccount;
	private ToolBelt toolBelt;
	private KnowledgebaseFactory knowledgebaseFactory;

	public UpdateConceptMedia( String name, ToolBelt toolBelt, UserAccount userAccount)
	{
		this.dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
		this.concept = dao.findByName(name);

        if (concept == null)
            throw new RuntimeException("Unable to find " + name);

        this.toolBelt = toolBelt;
        this.userAccount = userAccount;
        knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
        media = knowledgebaseFactory.newMedia();
	}

	public boolean apply(String previousUrl,String newUrl, String caption, String credit, String type, boolean primary)
	{
		//starting transaction and setting parameters for media object
        boolean ok = true;
        boolean urlFound = false;
        Media oldPrimaryMedia = null;
        Media oldMedia = null;
		dao.startTransaction();
  
        Collection<Media> medias = concept.getConceptMetadata().getMedias();
         //Media media = null;

         for (Media s : medias)
         {

            if(previousUrl.equals(s.getUrl()))
            {
                oldMedia = s;
                urlFound = true;
                break; 
            }
            
         }

         if(!urlFound)
            throw new RuntimeException("Unable to find media with url: " + previousUrl);
        


        // Check that the URL is valid
            URL verifyUrl = null;
            try {
                verifyUrl = new URL(newUrl);
                final InputStream in = verifyUrl.openStream();
                final int b = in.read();
                if (b == -1) {
                    throw new RuntimeException("Unable to read from " + verifyUrl.toExternalForm());
                    //EventBus.publish(StateLookup.TOPIC_WARNING, "Unable to read from " + verifyUrl.toExternalForm());
                }
                else {
                    //System.out.println(" url: " + verifyUrl.toExternalForm())
                    media.setUrl(verifyUrl.toExternalForm());
                }
            }
            catch (Exception e1) {
                dao.endTransaction();
                dao.close();
                throw new RuntimeException("Failed to open URL, the URL will not be updated.");
            }


		media.setCaption(caption);
		media.setCredit(credit);
		media.setPrimary(primary);

		//checking to see what kind of media it is
		switch (type.toLowerCase())
        {
            case "icon":
                media.setType(MediaTypes.ICON.toString());
                break;
            
            case "image":
                media.setType(MediaTypes.IMAGE.toString());
                break;

            case "video":
                media.setType(MediaTypes.VIDEO.toString());
                break;

            default:
                media.setType(MediaTypes.UNDEFINED.toString());
                break;
        }


         //applying media to concept
         //concept.getConceptMetadata().addMedia(media);
         //concept.setOriginator(userAccount.getUserName());

         //add to history the changes
         //History history = toolBelt.getHistoryFactory().add(userAccount, media);

        History history = knowledgebaseFactory.newHistory();
        history.setCreatorName(userAccount.getUserName());
        history.setCreationDate(new Date());
        history.setAction(History.ACTION_REPLACE);
        history.setField(History.FIELD_MEDIA);
        history.setOldValue(oldMedia.getUrl());
        history.setNewValue(media.getUrl());
        //History history = toolBelt.getHistoryFactory().newHistory(userAccount, "REPLACE", "Media", media.getUrl(), null);

         //saving changes of concept
        if(new ApproveHistory(){}.approve(userAccount, history, dao))
        {

            if (media.isPrimary()) {
                //final Concept concept = media.getConceptMetadata().getConcept();
                oldPrimaryMedia = concept.getConceptMetadata().getPrimaryMedia(MediaTypes.getType(media.getType()));

                if ((oldPrimaryMedia != null) && !oldPrimaryMedia.equals(media)) {
                    // log.info("You are adding a primary media of '" + media.getUrl() + "' to " +
                    //          concept.getPrimaryConceptName().getName() +
                    //          ". This concept contained a primary media of '" + oldPrimaryMedia.getUrl() +
                    //          "' which is now set to a secondary media");
                    oldPrimaryMedia.setPrimary(false);
                }
            }
            
            concept.getConceptMetadata().removeMedia(oldMedia);
            concept.getConceptMetadata().addMedia(media);


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