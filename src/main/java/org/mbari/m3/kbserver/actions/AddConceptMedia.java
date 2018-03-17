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

public class AddConceptMedia
{
	private Concept concept;
	private ConceptDAO dao;
	private Media media;
	private UserAccount userAccount;
	private ToolBelt toolBelt;
	private KnowledgebaseFactory knowledgebaseFactory;

	public AddConceptMedia( String name, ToolBelt toolBelt, UserAccount userAccount)
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

	public void apply(String url, String caption, String credit, String type, boolean primary)
	{
		//starting transaction and setting parameters for media object
		dao.startTransaction();
		media.setUrl(url);
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
         concept.getConceptMetadata().addMedia(media);
         concept.setOriginator(userAccount.getUserName());

         //add to history the changes
         History history = toolBelt.getHistoryFactory().add(userAccount, media);


         //saving changes of concept
        if(new ApproveHistory(){}.approve(userAccount, history, dao))
        {
            dao.persist(concept);
            concept.getConceptMetadata().addHistory(history);
            dao.persist(history);
        }

         dao.endTransaction();
         dao.close();
         return;
	}

}