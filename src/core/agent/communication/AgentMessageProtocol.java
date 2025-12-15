package core.agent.communication;

public class AgentMessageProtocol extends MessageProtocol {

    public AgentMessageProtocol() {
        super("Agente DBA");
      
    }

    @Override
    public String createMessageBody(String msg) {
        String body = MessageStyle.GENZ.getPrefix() + msg + MessageStyle.GENZ.getSuffix();
        return body;
    }
}
