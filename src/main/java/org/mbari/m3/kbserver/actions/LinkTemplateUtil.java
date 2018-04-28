package org.mbari.m3.kbserver.actions;

import com.typesafe.config.ConfigException.Null;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.ui.ToolBelt;
import java.util.*;
import javax.tools.Tool;

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
		this.userAccount = userAccount;
		dao  = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
		this.concept = dao.findByName(concept);

        if (concept == null)
            throw new RuntimeException("Unable to find " + concept);
	}


	public boolean addTemplate()
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

        if(history == null)
        	System.out.println("history is null");

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

	public boolean updateTemplate(String oldLinkName)
	{
		dao.startTransaction();
		boolean ok = true;
		LinkTemplate oldTemplate = null;
		boolean templateFound = false;
		LinkTemplate newTemplate = toolBelt.getKnowledgebaseFactory().newLinkTemplate();

		Collection<LinkTemplate> linkTemplates = concept.getConceptMetadata().getLinkTemplates();

		for (LinkTemplate s : linkTemplates)
         {

            if(oldLinkName.equals(s.getLinkName()))
            {
                oldTemplate = s;
                templateFound = true;
                break; 
            }
            
         }

         if(!templateFound)
            throw new RuntimeException("Unable to find template with link name: " + oldLinkName);


        newTemplate.setLinkName(linkName);
        newTemplate.setLinkValue(linkValue);
        newTemplate.setToConcept(toConcept);

        History history = toolBelt.getHistoryFactory().replaceLinkTemplate(userAccount, oldTemplate, newTemplate);

        if(new ApproveHistory(){}.approve(userAccount, history, dao))
        {
        	concept.getConceptMetadata().removeLinkTemplate(oldTemplate);
        	concept.getConceptMetadata().addLinkTemplate(newTemplate);

            concept.getConceptMetadata().addHistory(history);
            dao.persist(history);
        }

        else
            ok = false;
        
        dao.endTransaction();
        dao.close();
        return ok;
	}

	public boolean deleteTemplate()
	{
		dao.startTransaction();
		boolean ok = true;
		boolean templateFound = false;
	
		LinkTemplate template = null;

		Collection<LinkTemplate> linkTemplates = concept.getConceptMetadata().getLinkTemplates();

		for (LinkTemplate s : linkTemplates)
         {
            if(linkName.equals(s.getLinkName()))
            {
                template = s;
                templateFound = true;
                break; 
            }
         }

         if(!templateFound)
            throw new RuntimeException("Cannot delete template. Unable to find template with link name: " + linkName);

         History history = toolBelt.getHistoryFactory().delete(userAccount, template);

         if(new ApproveHistory(){}.approve(userAccount, history, dao))
         {
        	concept.getConceptMetadata().removeLinkTemplate(template);
 			dao.remove(template);

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