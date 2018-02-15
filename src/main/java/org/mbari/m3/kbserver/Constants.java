package org.mbari.m3.kbserver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import vars.jpa.InjectorModule;

/**
 * Constants
 */
public class Constants {

    public static Config CONFIG = ConfigFactory.load();

    public static String ENVIRONMENT = CONFIG.getString("database.environment");

    private static Injector injector = Guice.createInjector(new InjectorModule());
    
    
}