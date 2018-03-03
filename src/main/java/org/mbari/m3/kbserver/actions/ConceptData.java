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
		getDescriptors();
		return jsonString;
	}


	private void getAlternatives()
	{
		Set<ConceptName> names = concept.getConceptNames();
		jsonString += "\"alternatives\": [\n";
		int setSize = names.size();
		int i = 0;
		for (ConceptName s: names) 
		{
			if(i == setSize -1)
			{
				jsonString += "\t{\n\t  \"Type\" : \"" + s.getNameType() + "\",\n";
				jsonString += "\t  \"Name\" : \"" + s.getName() + "\"\n";
				jsonString += "\t}\n";
			}

			else
			{
				jsonString += "\t{\n\t  \"Type\" : \"" + s.getNameType() + "\",\n";
				jsonString += "\t  \"Name\" : \"" + s.getName() + "\"\n\t},\n";
			}

			i++;
		  //System.out.println("Value = " + s + "  Type: " + s.getNameType() + "Name: " + s.getName());
		}

		jsonString += "],";
		System.out.println(jsonString);

	}

	private void getMedia()
	{
		Collection<Media> media = concept.getConceptMetadata().getMedias();
		jsonString += "\n\"media\" : [";
		int setSize = media.size();
		int i = 0;

		for (Media s : media) 
		{
			if(i == setSize -1)
			{
				jsonString += "\n\t{\n\t  \"url\" : \"" + s.getUrl() + "\",\n";
				jsonString += "\t  \"caption\" : \"" + s.getCaption() + "\",\n";
				jsonString += "\t  \"credit\" : \"" + s.getCredit() + "\",\n";
				jsonString += "\t  \"type\" : \"" + s.getType() + "\",\n";
				jsonString += "\t  \"isPrimary\" : \"" + s.isPrimary() + "\"";
				jsonString += "\n\t}\n";
			}

			else
			{
				jsonString += "\n\t{\n\t  \"url\" : \"" + s.getUrl() + "\",\n";
				jsonString += "\t  \"caption\" : \"" + s.getCaption() + "\",\n";
				jsonString += "\t  \"credit\" : \"" + s.getCredit() + "\",\n";
				jsonString += "\t  \"type\" : \"" + s.getType() + "\",\n";
				jsonString += "\t  \"isPrimary\" : \"" + s.isPrimary() + "\"\n\t},";
			}

			i++;

        }

        jsonString += "],";

        System.out.println(jsonString);
		//return jsonString;

	}

	private void getDescriptors()
	{
		Collection<LinkRealization> links = concept.getConceptMetadata().getLinkRealizations();

		jsonString += "\n\"descriptors\" : [";
		int setSize = links.size();
		int i = 0;

		for (LinkRealization s: links) 
		{
			if(i == setSize -1)
			{

				jsonString += "\n\t{\n\t  \"linkName\" : \"" + s.getLinkName() + "\",\n";
				jsonString += "\t  \"toConcept\" : \"" + s.getToConcept() + "\",\n";
				jsonString += "\t  \"linkValue\" : \"" + s.getLinkValue() + "\"";
				jsonString += "\n\t}\n";
				
			}

			else
			{
				jsonString += "\n\t{\n\t  \"linkName\" : \"" + s.getLinkName() + "\",\n";
				jsonString += "\t  \"toConcept\" : \"" + s.getToConcept() + "\",\n";
				jsonString += "\t  \"linkValue\" : \"" + s.getLinkValue() + "\"\n\t},";

			}

			i++;
		}

		jsonString += "]\n}";
	}


}