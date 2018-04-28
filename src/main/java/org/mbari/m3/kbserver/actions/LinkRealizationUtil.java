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

public class LinkRealizationUtil {
    private UserAccount userAccount;

    public LinkRealizationUtil(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Collection<LinkRealization> getLinkRealizations(ToolBelt toolBelt, String conceptName) {
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
            return linkRealizations;
        }

        catch (Exception e) {
            System.err.println(e + " Issue with finding Concept Link Realizations.");
            return null;
        }
    }

    public LinkRealization findLinkRealizationByName(ToolBelt toolBelt, String conceptName, String linkName) {
        Collection<LinkRealization> linkRealizations = getLinkRealizations(toolBelt, conceptName);
        for (LinkRealization lr : linkRealizations) {
            if (linkName.equals(lr.getLinkName())) {
                return lr;
            }
        }
        
        return null;
    }

    public boolean doesLinkRealizationExist(ToolBelt toolBelt, String conceptName, String linkName) {
        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();

        Concept concept = dao.findByName(conceptName);

        if (getLinkRealizations(toolBelt, conceptName) == null) {
            return false;
        }

        try {
            Collection<LinkRealization> linkRealizations = getLinkRealizations(toolBelt, conceptName);
            for (LinkRealization lr : linkRealizations) {
                if (linkName.equals(lr.getLinkName())) {
                    return true;
                }
            }

            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean deleteLinkRealization(ToolBelt toolBelt, String conceptName, String linkName) {
        LinkRealization lr = findLinkRealizationByName(toolBelt, conceptName, linkName);

        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();

        ConceptMetadata cm = dao.findByName(conceptName).getConceptMetadata();
        boolean ok = false;
        if (lr == null) {
            return false;
        } else {
            try {
                // TODO: Not actually deleting anything..
                
                //History history = toolBelt.getHistoryFactory().add(userAccount, cm.getConcept());
                //dao.persist(history);
                // dao.endTransaction();
                // dao.close();

                History history = toolBelt.getHistoryFactory().delete(userAccount, lr);


            if(new ApproveHistory(){}.approve(userAccount, history, dao))
            {
                cm.removeLinkRealization(lr);
                dao.persist(cm);
                //concept.getConceptMetadata().removeMedia(media);
                cm.addHistory(history);
                //dao.persist(concept);
                dao.persist(history);
                ok = true;
            }

            else
                ok = false;

                }
                catch (Exception e) {
                    System.err.println(e);
                    ok = false;
                }


                dao.endTransaction();
                dao.close();
                return ok;

            }
    }

    public LinkRealization makeLinkRealization(ToolBelt toolBelt, String linkName, String toConcept, String linkValue) {
        KnowledgebaseFactory factory = toolBelt.getKnowledgebaseFactory();
        LinkRealization lr = factory.newLinkRealization();
        lr.setLinkName(linkName);
        lr.setLinkValue(linkValue);
        lr.setToConcept(toConcept);
        return lr;
    }

    public boolean addLinkRealizations(ToolBelt toolBelt, String conceptName, String linkName, String toConcept, String linkValue) {
        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();

        boolean status;

        try {
            Concept concept = dao.findByName(conceptName);

            if (doesLinkRealizationExist(toolBelt, conceptName, linkName)) {
                return false;
            }

            ConceptMetadata conceptMetadata = concept.getConceptMetadata();
            conceptMetadata.addLinkRealization(makeLinkRealization(toolBelt, linkName, toConcept, linkValue));

            History history = toolBelt.getHistoryFactory().add(userAccount, concept);

            dao.persist(history);
            dao.endTransaction();
            dao.close();

            status = true;
        }
        catch (Exception e) {
            status = false;
        }

        return status;
    }
}
