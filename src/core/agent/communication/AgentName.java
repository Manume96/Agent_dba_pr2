package core.agent.communication;

public enum AgentName {
    AGENT("Agent"),
    SANTA("Santa"),
    TRANSLATOR("Translator"),
    RUDOLPH("Rudolph");

    private final String localName;

    AgentName(String localName) {
        this.localName = localName;
    }

    public String local() {
        return localName;
    }

    public jade.core.AID toAID() {
        return new jade.core.AID(localName, jade.core.AID.ISLOCALNAME);
    }
}
