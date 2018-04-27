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

	public LinkTemplateUtil(String concept,ToolBelt toolBelt, String linkName, String linkValue, String toConcept, UserAccount userAccount)
	{
		this.linkValue = linkValue;
		this.linkName = linkName;
		this.toConcept = toConcept;
		dao  = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
		this.concept = dao.findByName(name);

        if (concept == null)
            throw new RuntimeException("Unable to find " + name);


	}


	public addTemplate()
	{

	}


}