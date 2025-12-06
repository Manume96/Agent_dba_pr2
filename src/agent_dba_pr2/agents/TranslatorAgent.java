/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agent_dba_pr2.agents;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import agent_dba_pr2.communication.Translator;
import agent_dba_pr2.communication.MessageProtocol;



public class TranslatorAgent extends Agent {
    
    @Override
    protected void setup() {
        System.out.println("Translator ready!");
        addBehaviour(new TranslationBehaviour());
    }
    
    private class TranslationBehaviour extends CyclicBehaviour {
        
        @Override
        public void action() {
            ACLMessage msg = receive();
            
            if (msg != null) {
                System.out.println("\nTranslator received: " + msg.getContent());
                
                String textToTranslate = MessageProtocol.extractTranslationText(msg.getContent());
                String translated;
                
                if (Translator.isGenZ(textToTranslate)) {
                    translated = Translator.genZToFinnish(textToTranslate);
                    System.out.println("Translation Gen Z -> Finnish");
                    
                } else if (Translator.isFinnish(textToTranslate)) {
                    translated = Translator.finnishToGenZ(textToTranslate);
                    System.out.println("Translation Finnish -> Gen Z");
                    
                } else {
                    translated = textToTranslate;
                }
                
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(MessageProtocol.createTranslationResult(translated));
                send(reply);
                
                System.out.println("Translation sent\n");
                
            } else {
                block();
            }
        }
    }
    
    @Override
    protected void takeDown() {
        System.out.println("Translator terminating.");
    }
}