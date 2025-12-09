package core.agent.communication;

import jade.lang.acl.ACLMessage;

public class TranslatorMessageProtocol extends MessageProtocol {

    public TranslatorMessageProtocol() {
        super("Translator");
    }

    @Override
    public String createMessageBody(String msg) {
        if (Translator.isStyle(msg, MessageStyle.GENZ)) {
            return Translator.translate(msg, MessageStyle.GENZ, MessageStyle.FINNISH);
        } else if (Translator.isStyle(msg, MessageStyle.FINNISH) || Translator.isStyle(msg, MessageStyle.SANTA)) {
            return Translator.translate(msg, MessageStyle.FINNISH, MessageStyle.GENZ);
        } else {
            return msg;
        }
    }


    public ACLMessage createReplyMessage(ACLMessage received) {
        ACLMessage reply = received.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(createMessageBody(received.getContent()));
        return reply;
    }
}
