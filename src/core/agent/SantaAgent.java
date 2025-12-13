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
    private Position position = new Position(10,10);
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
                    handleMessage(msg);
                } else {
                    Logger.error("Something went wrong. No message received.");
                }
            }
        });
    }
    
    private String generateCode() {
        Random rand = new Random();
        return "CODE-" + (10000 + rand.nextInt(90000));
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
        if (new Random().nextDouble() < 0.8) {
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            reply.setContent(messageProtocol.createMessageBody(secretCode));
            Logger.info("ACCEPTED");
        } else {
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            reply.setContent(messageProtocol.createMessageBody("UNLUCKY"));
            Logger.info("REJECTED");
        }
        send(reply);
    }

    private void handleQueryRef(ACLMessage msg, ACLMessage reply) {
        String pString =  "Position:(" + position.getX() + "," + position.getY() + ")";
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(messageProtocol.createMessageBody(pString));
        send(reply);
        Logger.info("Position sent: (" + position.getX() + ", " + position.getY() + ")");
    }

    private void handleInform(ACLMessage msg, ACLMessage reply) {
        if (msg.getContent().contains(ContentKeyword.I_ARRIVED.getText())) {
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(ContentKeyword.HO_HO_HO.getText());
            send(reply);
            Logger.info("HoHoHo!");
        }
    }
}