package org.mbari.m3.kbserver;

import static spark.Spark.*;

/**
 * Main
 */
public class Main {

    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
        get("/foo", (req, res) -> "bar");
        get("/echo", (req, res) -> {
            return "Request:" + req + "\n\n" +
               "Response:" + res;
               
        });
    }
}