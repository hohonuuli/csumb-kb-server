package org.mbari.m3.kbserver.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.tools.Tool;
import com.typesafe.config.ConfigException.Null;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Media;
import vars.knowledgebase.ui.ToolBelt;

public class LinkTemplateUtil
{
	private String linkName;
	private String linkValue;
	private String toConcept;
	private Concept concept;
	private ConceptDAO dao;
	private UserAccount userAccount;
	private ToolBelt toolBelt;

	public LinkTemplateUtil(String concept,ToolBelt toolBelt, String linkName, String linkValue, String toConcept, UserAccount userAccount)
	{
		this.linkValue = linkValue;
		this.linkName = linkName;
		this.toConcept = toConcept;
		this.toolBelt = toolBelt;
		dao  = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
		this.concept = dao.findByName(concept);

        if (concept == null)
            throw new RuntimeException("Unable to find " + concept);
	}


	public void addTemplate()
	{
		dao.startTransaction();
		boolean ok = true;


		LinkTemplate template = toolBelt.getKnowledgebaseFactory().newLinkTemplate();


		Collection<LinkTemplate> linkTemplates = concept.getConceptMetadata().getLinkTemplates();

		for (LinkTemplate s : linkTemplates) 
		{
            if (linkName.equals(s.getLinkName())) 
            	throw new RuntimeException("Concept already has a link template with name: " + linkName);
        }


        template.setLinkName(linkName);
        template.setLinkValue(linkValue);
        template.setToConcept(toConcept);

        History history = toolBelt.getHistoryFactory().add(userAccount, template);

        if(new ApproveHistory(){}.approve(userAccount, history, dao))
        {
        	concept.getConceptMetadata().addLinkTemplate(template);
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