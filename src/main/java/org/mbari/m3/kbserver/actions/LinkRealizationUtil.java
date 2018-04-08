package org.mbari.m3.kbserver.actions;

import java.util.Collection;

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

public class LinkRealizationUtil {
    private UserAccount userAccount;

    public LinkRealizationUtil(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getLinkRealizations(ToolBelt toolBelt, String conceptName) {
        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();

        Collection<LinkRealization> linkRealizations;

        try {
            Concept concept = dao.findByName(conceptName);
            dao.close();

            if (concept == null) {
                System.out.println("concept null");
            }
        
            linkRealizations = concept.getConceptMetadata().getLinkRealizations();
            return linkRealizations.toString();
        }

        catch (Exception e) {
            System.err.println(e + " Issue with finding Concept Link Realizations.");
            return "Error: LinkRealizations";
        }
    }

    private static long randomNumber(long min, long max) {
        long range = max - min;
        long value = (long) (Math.random() * range + min);
        return value;
    }

    public LinkRealization makeLinkRealization(ToolBelt toolBelt, String linkName, String toConcept) {
        KnowledgebaseFactory factory = toolBelt.getKnowledgebaseFactory();
        LinkRealization lr = factory.newLinkRealization();
        lr.setLinkName(linkName);
        lr.setLinkValue(randomNumber(0, 9999) + "");
        lr.setToConcept(toConcept);
        return lr;
    }

    public boolean addLinkRealizations(ToolBelt toolBelt, String conceptName, String linkName, String toConcept) {
        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();

        try {
            ConceptMetadata conceptMetadata = dao.findByName(conceptName).getConceptMetadata();
            conceptMetadata.addLinkRealization(makeLinkRealization(toolBelt, linkName, toConcept));

            // TODO: Doesn't actually add anything.
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
