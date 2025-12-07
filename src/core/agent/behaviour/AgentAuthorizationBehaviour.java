package core.agent.behaviour;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import core.agent.communication.MessageProtocol;


public class AgentAuthorizationBehaviour extends Behaviour {
    
    private enum State {
        REQUEST_TRANSLATION,
        WAIT_TRANSLATION,
        SEND_PROPOSAL,
        WAIT_SANTA_RESPONSE,
        REQUEST_RESPONSE_TRANSLATION,
        WAIT_RESPONSE_TRANSLATION,
        FINISHED
    }
    
    private State currentState = State.REQUEST_TRANSLATION;
    private String secretCode = null;
    private boolean done = false;
    private ACLMessage lastSentMessage;
    
    @Override
    public void action() {
        switch (currentState) {
            case REQUEST_TRANSLATION:
                requestTranslation();
                break;
            case WAIT_TRANSLATION:
                waitForTranslation();
                break;
            case SEND_PROPOSAL:
                sendProposalToSanta();
                break;
            case WAIT_SANTA_RESPONSE:
                waitForSantaResponse();
                break;
            case REQUEST_RESPONSE_TRANSLATION:
                requestResponseTranslation();
                break;
            case WAIT_RESPONSE_TRANSLATION:
                waitForResponseTranslation();
                break;
        }
    }
    
    private void requestTranslation() {
        System.out.println("\n ======PHASE 1 Starting ========");
        
        String genZMessage = MessageProtocol.createGenZMessage(MessageProtocol.WANT_TO_HELP);
        
        ACLMessage msg = MessageProtocol.createMessage(
            ACLMessage.REQUEST,
            MessageProtocol.AGENT_TRANSLATOR,
            MessageProtocol.createTranslationRequest(genZMessage),
            MessageProtocol.CONV_AUTHORIZATION
        );
        
        myAgent.send(msg);
        System.out.println("Agent to Translator: " + genZMessage);
        currentState = State.WAIT_TRANSLATION;
    }
    
    private void waitForTranslation() {
        ACLMessage msg = myAgent.receive();
        
        if (msg != null && msg.getConversationId().equals(MessageProtocol.CONV_AUTHORIZATION)) {
            String translated = MessageProtocol.extractTranslatedText(msg.getContent());
            System.out.println("Translation: " + translated);
            
            lastSentMessage = new ACLMessage(ACLMessage.PROPOSE);
            lastSentMessage.setContent(translated);
            currentState = State.SEND_PROPOSAL;
        } else {
            block();
        }
    }
    
    private void sendProposalToSanta() {
        ACLMessage msg = MessageProtocol.createMessage(
            ACLMessage.PROPOSE,
            MessageProtocol.AGENT_SANTA,
            lastSentMessage.getContent(),
            MessageProtocol.CONV_AUTHORIZATION
        );
        
        myAgent.send(msg);
        System.out.println("Agent to Santa: proposal");
        currentState = State.WAIT_SANTA_RESPONSE;
    }
    
    private void waitForSantaResponse() {
        ACLMessage msg = myAgent.receive();
        
        if (msg != null && msg.getConversationId().equals(MessageProtocol.CONV_AUTHORIZATION)) {
            lastSentMessage = msg;
            currentState = State.REQUEST_RESPONSE_TRANSLATION;
        } else {
            block();
        }
    }
    
    private void requestResponseTranslation() {
        ACLMessage msg = MessageProtocol.createMessage(
            ACLMessage.REQUEST,
            MessageProtocol.AGENT_TRANSLATOR,
            MessageProtocol.createTranslationRequest(lastSentMessage.getContent()),
            MessageProtocol.CONV_AUTHORIZATION
        );
        
        myAgent.send(msg);
        System.out.println("Agent: Request translation");
        currentState = State.WAIT_RESPONSE_TRANSLATION;
    }
    
    private void waitForResponseTranslation() {
        ACLMessage msg = myAgent.receive();
        
        if (msg != null && msg.getConversationId().equals(MessageProtocol.CONV_AUTHORIZATION)) {
            String translated = MessageProtocol.extractTranslatedText(msg.getContent());
            System.out.println("Translation: " + translated);
            
            if (lastSentMessage.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                secretCode = MessageProtocol.extractCode(translated);
                System.out.println("AUTHORIZED! Code: " + secretCode);
                
            } else {
                System.out.println("REJECTED!");
                myAgent.doDelete();
            }
            
            currentState = State.FINISHED;
            done = true;
        } else {
            block();
        }
    }
    
    @Override
    public boolean done() {
        return done;
    }
    
    public String getSecretCode() {
        return secretCode;
    }
}