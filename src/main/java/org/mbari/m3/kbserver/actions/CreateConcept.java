package org.mbari.m3.kbserver.actions;

import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;

/**
 * CreateConcept
 */
public class CreateConcept implements ApproveHistory {

    private final String parentName;
    private final String name;
    private final UserAccount userAccount;

    public CreateConcept(String parentName, String name, UserAccount userAccount) {
        this.parentName = parentName;
        this.name = name;
        this.userAccount = userAccount;
    }

    public Concept apply(ToolBelt toolBelt) {

        ConceptDAO dao = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        dao.startTransaction();
        Concept parentConcept = dao.findByName(parentName);
        if (parentConcept == null) {
            throw new RuntimeException("Unable to find " + parentName);
        }
        Concept existingConcept = dao.findByName(name);
        if (existingConcept != null) {
            throw new RuntimeException(name + " already exists in the database");
        }
        KnowledgebaseFactory knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();
        Concept concept = knowledgebaseFactory.newConcept();
        History history = toolBelt.getHistoryFactory().add(userAccount, concept);
        if (canDo(userAccount, history)) {
            ConceptName conceptName = knowledgebaseFactory.newConceptName();
            conceptName.setName(name);
            conceptName.setNameType(ConceptNameTypes.PRIMARY.toString());
            concept.addConceptName(conceptName);
            concept.setOriginator(userAccount.getUserName());
            parentConcept.addChildConcept(concept);
            dao.persist(concept);
            
            parentConcept.getConceptMetadata().addHistory(history);
            approve(userAccount, history, dao);
            dao.persist(history);
        }
        dao.endTransaction();
        dao.close();
        return concept;
    }



}