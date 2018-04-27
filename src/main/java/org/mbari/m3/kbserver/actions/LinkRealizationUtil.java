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
        } 
        
        else {
            try {
                History history = toolBelt.getHistoryFactory().delete(userAccount, lr);
                
                if (new ApproveHistory(){}.approve(userAccount, history, dao)) {
                    // TODO: Verify this is correct
                    // This dao.merge() fixes things, but is it because the Link Realizations were added wrong?
                    lr = dao.merge(lr);

                    cm.removeLinkRealization(lr);
                    dao.remove(lr);
                    cm.addHistory(history);
                    dao.persist(history);
                    ok = true;
                }
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

    public boolean updateLinkRealization(ToolBelt toolBelt, String conceptName, String oldLinkName, String oldToConcept, String oldLinkValue, String newLinkName, String newToConcept, String newLinkValue) {
        KnowledgebaseFactory factory = toolBelt.getKnowledgebaseFactory();
        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();
        boolean status = false;
        
        // Get the old link realization, used for history tracking
        LinkRealization linkRealization = findLinkRealizationByName(toolBelt, conceptName, oldLinkName);

        // Didn't exist
        if (linkRealization == null) {
            return false;
        }

        try {
            // Create a copy of the old values to create a history
            LinkRealization oldValue = toolBelt.getKnowledgebaseFactory().newLinkRealization();

            // Copy data over
            oldValue.setLinkName(linkRealization.getLinkName());
            oldValue.setToConcept(linkRealization.getToConcept());
            oldValue.setLinkValue(linkRealization.getLinkValue());

            linkRealization = dao.find(linkRealization);
            linkRealization.setLinkName(newLinkName);
            linkRealization.setToConcept(newToConcept);
            linkRealization.setLinkValue(newLinkValue);

            History history = toolBelt.getHistoryFactory().replaceLinkRealization(userAccount, oldValue, linkRealization);

            if (new ApproveHistory(){}.approve(userAccount, history, dao)) {
                linkRealization.getConceptMetadata().addHistory(history);
                dao.persist(history);
                status = true;
            }
        }

        catch (Exception e) {
            System.err.println(e);
            status = false;
        }
        
        dao.endTransaction();
        dao.close();

        return true;
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

            LinkRealization lr = makeLinkRealization(toolBelt, linkName, toConcept, linkValue);
            History history = toolBelt.getHistoryFactory().add(userAccount, lr);

            if (new ApproveHistory(){}.approve(userAccount, history, dao)) {
                ConceptMetadata conceptMetadata = concept.getConceptMetadata();
                conceptMetadata.addLinkRealization(lr);
                conceptMetadata.addHistory(history);
                dao.persist(history);
                status = true;
            } else {
                status = false;
            }
            
            dao.endTransaction();
            dao.close();
        }

        catch (Exception e) {
            System.err.println(e);
            status = false;
        }

        return status;
    }
}
