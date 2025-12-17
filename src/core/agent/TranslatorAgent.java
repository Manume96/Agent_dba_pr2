package core.agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import core.agent.communication.MessageProtocol;
import core.agent.communication.TranslatorMessageProtocol;
import core.logger.Logger;
import jade.lang.acl.MessageTemplate;
import core.agent.communication.AgentName;
import core.agent.communication.ConversationId;

public class TranslatorAgent extends Agent {

    private final MessageProtocol messageProtocol = new TranslatorMessageProtocol();

    @Override
    protected void setup() {
        Logger.info("Translator ready!");
        addBehaviour(new TranslationBehaviour());
    }

    private class TranslationBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate template = MessageTemplate.and(
                    MessageTemplate.and(
                            MessageTemplate.MatchConversationId(ConversationId.TRANSLATION.getId()),
                            MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
                    ),
                    MessageTemplate.MatchSender(AgentName.AGENT.toAID())
            );
            ACLMessage msg = blockingReceive(template);
            if (msg != null) {
                Logger.info("Translator received: " + msg.getContent());
                ACLMessage reply = ((TranslatorMessageProtocol) messageProtocol).createReplyMessage(msg);
                send(reply);

                Logger.info("Translation sent: " + reply.getContent());
            } else {
                block();
            }
        }
    }

    @Override
    protected void takeDown() {
        Logger.info("Translator terminating.");
    }
}
