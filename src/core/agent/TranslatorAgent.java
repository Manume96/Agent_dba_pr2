package core.agent;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import core.agent.communication.MessageProtocol;
import core.agent.communication.TranslatorMessageProtocol;
import core.logger.Logger;

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
            ACLMessage msg = blockingReceive();
            if (msg != null) {
                Logger.info("\nTranslator received: " + msg.getContent());
                ACLMessage reply = ((TranslatorMessageProtocol) messageProtocol).createReplyMessage(msg);
                send(reply);

                Logger.info("Translation sent: " + reply.getContent() + "\n");
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
