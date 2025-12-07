/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.agent.communication;

/**
 * Translates messages between Gen Z format and Finnish format (between agent and santa)
 */


public class Translator {
    
    // Gen Z 
    public static final String GENZ_PREFIX = "Bro ";
    public static final String GENZ_SUFFIX = " En Plan";
    
    // Translation into Finnish of messages sent
    public static final String FINNISH_PREFIX = "Rakas Joulupukki ";
    public static final String FINNISH_SUFFIX = " Kiitos";
    
    // Santa's response 
    public static final String SANTA_PREFIX = "Hyvää joulua ";
    public static final String SANTA_SUFFIX = " Nähdään pian";
    
    /**
     * Translates a Gen Z message to Finnish 
     */
    public static String genZToFinnish(String message) {
        String core = extractCore(message, GENZ_PREFIX, GENZ_SUFFIX);
        return FINNISH_PREFIX + core + FINNISH_SUFFIX;
    }
    
    /**
     * Translates a Santa'message to Gen Z 
     */
    public static String finnishToGenZ(String message) {
        String core = extractCore(message, SANTA_PREFIX, SANTA_SUFFIX);
        return GENZ_PREFIX + core + GENZ_SUFFIX;
    }
    
    /**
     * Extracts the core content from a message
     */
    private static String extractCore(String message, String prefix, String suffix) {
        String result = message.trim();
        
        if (result.startsWith(prefix)) {
            result = result.substring(prefix.length());
        }
        
        if (result.endsWith(suffix)) {
            result = result.substring(0, result.length() - suffix.length());
        }
        
        return result.trim();
    }
    
    public static boolean isGenZ(String message) {
        return message.startsWith(GENZ_PREFIX) && message.endsWith(GENZ_SUFFIX);
    }
    

    public static boolean isFinnish(String message) {
        return message.startsWith(FINNISH_PREFIX) || message.startsWith(SANTA_PREFIX);
    }

}