/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agent_dba_pr2.agents;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import agent_dba_pr2.communication.MessageProtocol;
import java.util.Random;


public class SantaAgent extends Agent {
    
    private String secretCode;
    
    @Override
    protected void setup() {
        secretCode = generateCode();
        System.out.println("Santa ready");
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    handleMessage(msg);
                } else {
                    block();
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
            case ACLMessage.PROPOSE:
                // Authorization
                if (new Random().nextDouble() < 0.8) {
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    reply.setContent(MessageProtocol.createSantaResponse(MessageProtocol.createMessageWithCode(secretCode)));
                    System.out.println("ACCEPTED");
                } else {
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    reply.setContent(MessageProtocol.createSantaResponse(MessageProtocol.NOT_TRUSTWORTHY));
                    System.out.println("REJECTED");
                }
                send(reply);
                break;
                
            case ACLMessage.QUERY_REF:
                // Position request
                int x = 10;
                int y = 10;
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(MessageProtocol.createSantaResponse(
                    MessageProtocol.createPositionMessage(x, y)));
                send(reply);
                System.out.println("Position sent: (" + x + ", " + y + ")");
                break;
                
            case ACLMessage.INFORM:
                // Final arrival
                if (msg.getContent().contains(MessageProtocol.I_ARRIVED)) {
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(MessageProtocol.HO_HO_HO);
                    send(reply);
                    System.out.println("HoHoHo!");
                }
                break;
        }
    }
}