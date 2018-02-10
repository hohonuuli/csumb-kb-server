package org.mbari.m3.kbserver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import vars.knowledgebase.ui.ToolBelt;

/**
 * Initializer
 */
public class Initializer {

    private static Config config;
    private static Injector injector;
    private static ToolBelt toolBelt;

    /**
     * First looks for the file `~/.vars/vars-kb.conf` and, if found,
     * loads that file and uses the default configs as fallbacks. Otherwise
     * used the usual `reference.conf`/`application.conf` combination for
     * typesafe's config library.
     * @return
     */
    public static Config getConfig() {
        if (config == null) {
            config =  ConfigFactory.load();
        }
        return config;
    }

    public static Injector getInjector() {
        if (injector == null) {
            String moduleName = getConfig().getString("app.injector.module.class");
            try {
                Class<?> clazz = Class.forName(moduleName);
                // TODO in java 9 use clazz.getDeclaredConstructor().newInstance()
                // You'll have to find one where constructor.getParameterCount == 0
                Module module = (Module) clazz.newInstance();
                injector = Guice.createInjector(module);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create dependency injector", e);
            }
        }
        return injector;
    }

    public static ToolBelt getToolBelt() {
        if (toolBelt == null) {
            toolBelt = getInjector().getInstance(ToolBelt.class);
        }
        return toolBelt;
    }
}