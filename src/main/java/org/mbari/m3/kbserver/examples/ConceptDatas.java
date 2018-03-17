package org.mbari.m3.kbserver.examples;

import com.google.inject.spi.Toolable;
import org.mbari.m3.kbserver.Initializer;
import org.mbari.m3.kbserver.actions.ConceptData;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.ToolBelt;

public class ConceptDatas
{

    public static void main(String[] args) {
        example1();
    }

    private static void example1() {
        System.out.println(">>>Concept data starting");
        ToolBelt toolBelt = Initializer.getToolBelt();
        
        ConceptData fn = new ConceptData("dariomolina12", toolBelt);
        //fn.getMetadata();
        //fn.getAlternatives();
        //fn.getMedia();
        //fn.getDescriptors();
        fn.getHistory();
    }

}