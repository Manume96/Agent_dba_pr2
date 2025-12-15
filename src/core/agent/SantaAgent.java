package core.agent;

import core.agent.communication.ContentKeyword;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import core.agent.communication.MessageProtocol;
import core.agent.communication.SantaMessageProtocol;
import core.logger.Logger;
import core.world.Position;
import java.util.Random;

public class SantaAgent extends Agent {
    
    private String secretCode;
    private Position position = new Position(0,0);
    MessageProtocol messageProtocol = new SantaMessageProtocol();
    
    @Override
    protected void setup() {
        secretCode = generateCode();
        Logger.info("Santa ready with code: " + secretCode);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = blockingReceive();
                if (msg != null) {
                    String senderName = (msg.getSender() != null) ? msg.getSender().getLocalName() : "unknown";
                    Logger.info("Santa received message from " + senderName + " | perf=" + msg.getPerformative()
                            + " | convId=" + msg.getConversationId() + " | content=" + msg.getContent());
                    handleMessage(msg);
                } else {
                    Logger.error("Something went wrong. No message received.");
                }
            }
        });
    }
    
    private String generateCode() {
        // Fecha de la muerte de Harambe: 2016-05-28 -> código determinista
        return "CODE-20160528";
    }
    
   private void handleMessage(ACLMessage msg) {
    ACLMessage reply = msg.createReply();

    switch (msg.getPerformative()) {
        case ACLMessage.PROPOSE -> handlePropose(msg, reply);
        case ACLMessage.QUERY_REF -> handleQueryRef(msg, reply);
        case ACLMessage.INFORM -> handleInform(msg, reply);
        default -> Logger.warn("Performative not handled: " + msg.getPerformative());
    }
}

    private void handlePropose(ACLMessage msg, ACLMessage reply) {
            Logger.info("Handling PROPOSE from " + msg.getSender().getLocalName() + " | content=" + msg.getContent());
            //boolean accept = new Random().nextDouble() < 0.8; //Descomentar para aleatoriedad
            boolean accept = true;
            if (accept) {
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContent(messageProtocol.createMessageBody(secretCode));
                Logger.info("ACCEPTED proposal. Sending secret code.");
            } else {
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                reply.setContent(messageProtocol.createMessageBody("UNLUCKY"));
                Logger.info("REJECTED proposal.");
            }
            Logger.debug("Sending reply to PROPOSE | perf=" + reply.getPerformative() + " | content=" + reply.getContent());
            send(reply);
    }

    private void handleQueryRef(ACLMessage msg, ACLMessage reply) {
        Logger.info("Handling QUERY_REF from " + msg.getSender().getLocalName() + " | requesting position");
        String pString =  "Position:(" + position.getX() + "," + position.getY() + ")";
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(messageProtocol.createMessageBody(pString));
        Logger.debug("Sending position reply | content=" + reply.getContent());
        send(reply);
        Logger.info("Position sent: (" + position.getX() + ", " + position.getY() + ") to " + msg.getSender().getLocalName());
    }

    private void handleInform(ACLMessage msg, ACLMessage reply) {
        Logger.info("Handling INFORM from " + msg.getSender().getLocalName() + " | content=" + msg.getContent());
        if (msg.getContent().contains(ContentKeyword.I_ARRIVED.getText())) {
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(ContentKeyword.HO_HO_HO.getText());
            Logger.debug("Replying HO_HO_HO to " + msg.getSender().getLocalName());
            send(reply);
            Logger.info("HoHoHo! Reply sent to " + msg.getSender().getLocalName());
        }
    }
}