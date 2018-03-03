package org.mbari.m3.kbserver.actions;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.Media;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;
import java.util.*;

public class ConceptData
{
	private Concept concept;
	private ConceptDAO dao;


	public ConceptData(String name, ToolBelt toolBelt)
	{
		this.dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
		this.concept = dao.findByName(name);

        if (concept == null)
            throw new RuntimeException("Unable to find " + name);
        
	}


	public void getAlternatives()
	{
		Set<ConceptName> names = concept.getConceptNames();

		for (ConceptName s: names) 
		{
		  System.out.println("Value = " + s + "  Type: " + s.getNameType() + "Name: " + s.getName());
		}

	}

	public void getMedia()
	{
		Collection<Media> media = concept.getConceptMetadata().getMedias();

		for (Media s : media) 
		{
        System.out.println("url: " + s.getUrl() + '\n' +
        					"caption: " + s.getCaption() + '\n' + 
        					"credit: " + s.getCredit() + '\n' +
        					"type: " + s.getType() + '\n' +
        					"isPrimary: " + s.isPrimary() + "\n\n");
        }
	}

	public void getDescriptors()
	{
		Collection<LinkRealization> links = concept.getConceptMetadata().getLinkRealizations();

		for (LinkRealization s: links) 
		{
		  System.out.println("Value = " + s + '\n' +
		  					"linkName: " + s.getLinkName() + '\n' +
		  					"toConcept: " + s.getToConcept() + '\n' +
		  					"linkValue: " + s.getLinkValue()+'\n');
		}
	}


}