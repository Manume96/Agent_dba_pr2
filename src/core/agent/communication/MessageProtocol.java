/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.agent.communication;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * FIPA communication protocol
 */

public class MessageProtocol {
    
    // Agent names
    public static final String AGENT_AGENT = "Agent";
    public static final String AGENT_SANTA = "Santa";
    public static final String AGENT_TRANSLATOR = "Translator";
    public static final String AGENT_RUDOLPH = "Rudolph";
    
    // Conversation IDs
    public static final String CONV_AUTHORIZATION = "authorization";  
    public static final String CONV_MISSION = "mission";
    
    // Message Content Keywords
    public static final String WANT_TO_HELP = "I want to help";
    public static final String NOT_TRUSTWORTHY = "Not trustworthy";
    public static final String TRANSLATE_REQUEST = "Translate: ";
    public static final String TRANSLATION_RESULT = "Translation: ";
    public static final String WHERE_IS_REINDEER = "Where is next reindeer?";
    public static final String ALL_FOUND = "ALL_FOUND";
    public static final String FOUND_REINDEER = "Found reindeer";
    public static final String WHERE_ARE_YOU = "Where are you?";
    public static final String I_ARRIVED = "I arrived";
    public static final String HO_HO_HO = "HoHoHo!";
    
    // Reindeer names
    public static final String[] REINDEER_NAMES = {
        "Dasher", "Dancer", "Vixen", "Prancer", 
        "Cupid", "Comet", "Blitzen", "Donner"
    };
    
    
    /**
     * Creates messages with prefix and sufix
     */
    
    public static String createGenZMessage(String content) {
        return Translator.GENZ_PREFIX  + content + Translator.GENZ_SUFFIX ;
    }
    
    public static String createSantaResponse(String content) {
        return Translator.SANTA_PREFIX  + content + Translator.SANTA_SUFFIX ;
    }    
        

    /**
     * Extracts the code from a message
     * Input: "Code:92639"
     */
    public static String extractCode(String message) {
        if (message.contains("Code:")) {
            int start = message.indexOf("Code:") + 5;
            int end = message.indexOf(" ", start);
            if (end == -1) end = message.length();
            return message.substring(start, end).trim();
        }
        return null;
    }
    
    /**
     * Creates a message with the code
     */
    public static String createMessageWithCode(String code) {
        return "Code:" + code;
    }
    
    /**
     * Parses a position from a message
     * Input: "Position:(10,15)"
     * Output: {10, 15}
     */
    public static int[] extractPosition(String message) {
        if (message.contains("Position:")) {
            String posStr = message.substring(
                message.indexOf("(") + 1, 
                message.indexOf(")")
            );
            String[] coords = posStr.split(",");
            return new int[]{
                Integer.parseInt(coords[0].trim()), 
                Integer.parseInt(coords[1].trim())
            };
        }
        return null;
    }
    
    /**
     * Creates a position message
     */
    public static String createPositionMessage(int x, int y) {
        return "Position:(" + x + "," + y + ")";
    }
    
    /**
     * Create a standard ACLMessage
     */
    public static ACLMessage createMessage(int performative, String receiverName, 
                                          String content, String conversationId) {
        ACLMessage msg = new ACLMessage(performative);
        msg.addReceiver(new AID(receiverName, AID.ISLOCALNAME));
        msg.setContent(content);
        msg.setConversationId(conversationId);
        msg.setLanguage("English");
        msg.setOntology("christmas");
        return msg;
    }
    
    /**
     * Creates a translation request message
     */
    public static String createTranslationRequest(String textToTranslate) {
        return TRANSLATE_REQUEST + textToTranslate;
    }
    
    /**
     * Extracts the text from a translation request
     * Input: "Translate: Bro I want to help En Plan"
     * Output: "Bro I want to help En Plan"
     */
    public static String extractTranslationText(String message) {
        if (message.startsWith(TRANSLATE_REQUEST)) {
            return message.substring(TRANSLATE_REQUEST.length()).trim();
        }
        return message;
    }
    
    /**
     * Creates a translation result message
     */
    public static String createTranslationResult(String translatedText) {
        return TRANSLATION_RESULT + translatedText;
    }
    
    /**
     * Extracts the translated text from a translation result
     * Input: "Translation: Rakas Joulupukki I want to help Kiitos"
     * Output: "Rakas Joulupukki I want to help Kiitos"
     */
    public static String extractTranslatedText(String message) {
        if (message.startsWith(TRANSLATION_RESULT)) {
            return message.substring(TRANSLATION_RESULT.length()).trim();
        }
        return message;
    }
    
}