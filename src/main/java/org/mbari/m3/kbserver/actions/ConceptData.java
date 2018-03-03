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
	private String jsonString;


	public ConceptData(String name, ToolBelt toolBelt)
	{
		this.dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
		this.concept = dao.findByName(name);

        if (concept == null)
            throw new RuntimeException("Unable to find " + name);

        jsonString = "{\n\"name\": \""+name +"\",\n";
        
	}

	public String getMetadata()
	{
		getAlternatives();
		getMedia();
		return jsonString;
	}


	public void getAlternatives()
	{
		Set<ConceptName> names = concept.getConceptNames();
		jsonString += "\"alternatives\": [\n";
		int setSize = names.size();
		int i = 0;
		for (ConceptName s: names) 
		{
			if(i == setSize -1)
			{
				jsonString += "{\n\"Type\" : \"" + s.getNameType() + "\",\n";
				jsonString += "\"Name\" : \"" + s.getName() + "\"\n";
				jsonString += "}\n";
			}

			else
			{
				jsonString += "{\n\"Type\" : \"" + s.getNameType() + "\",\n";
				jsonString += "\"Name\" : \"" + s.getName() + "\"\n},\n";
			}

			i++;
		  //System.out.println("Value = " + s + "  Type: " + s.getNameType() + "Name: " + s.getName());
		}

		jsonString += "],";
		System.out.println(jsonString);

	}

	public void getMedia()
	{
		Collection<Media> media = concept.getConceptMetadata().getMedias();
		jsonString += "\n\"media\" : [";
		int setSize = media.size();
		int i = 0;

		for (Media s : media) 
		{
			if(i == setSize -1)
			{
				jsonString += "{\n\"url\" : \"" + s.getUrl() + "\",\n";
				jsonString += "\"caption\" : \"" + s.getCaption() + "\",\n";
				jsonString += "\"credit\" : \"" + s.getCredit() + "\",\n";
				jsonString += "\"type\" : \"" + s.getType() + "\",\n";
				jsonString += "\"isPrimary\" : \"" + s.isPrimary() + "\"\n";
				jsonString += "}\n";
			}

			else
			{
				jsonString += "{\n\"url\" : \"" + s.getUrl() + "\",\n";
				jsonString += "\"caption\" : \"" + s.getCaption() + "\",\n";
				jsonString += "\"credit\" : \"" + s.getCredit() + "\",\n";
				jsonString += "\"type\" : \"" + s.getType() + "\",\n";
				jsonString += "\"isPrimary\" : \"" + s.isPrimary() + "\",\n";
			}

			i++;

        }

        jsonString += "],";

        System.out.println(jsonString);
		//return jsonString;

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