package org.mbari.m3.kbserver;

import org.mbari.net.URLUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.gson.InitializeKnowledgebaseApp;
import vars.knowledgebase.KnowledgebaseDAOFactory;

import java.io.File;
import java.net.URL;

/**
 * @author Brian Schlining
 * @since 2018-03-29T15:11:00
 */
public class DatabaseLoader {

    private static final Logger log = LoggerFactory.getLogger(DatabaseLoader.class);
    private static volatile boolean alreadyLoaded = false;

    /**
     * Only the first call to this is executed. Subsequent calls are ignored.
     * We do this because the app that loads the db will exit the JVM if
     * the database is not empty.
     *
     * @return true if the database loaded. False if some error occurred
     */
    public static boolean load() {
        if (alreadyLoaded) {
            log.info("Knowledgebase already loaded. Ignoring request.");
            return true;
        }
        else {
            log.info("Begin knowledgebase initialization");
            try {
                URL url = DatabaseLoader.class.getResource("/kb/kb-dump.json.zip");
                File file = URLUtilities.toFile(url);
                KnowledgebaseDAOFactory daoFactory = Initializer.getToolBelt().getKnowledgebaseDAOFactory();
                InitializeKnowledgebaseApp.run(file, daoFactory);
                log.info("Loaded knowledgebase from {}", file.getAbsolutePath());
                alreadyLoaded = true;
                return true;
            } catch (Exception e) {
                log.warn("An error occurred while loading knowledgebase from cached data.", e);
                return false;
            }
        }
    }


}
