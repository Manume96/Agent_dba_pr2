package core.agent.communication;

public enum MessageStyle {
    GENZ("Bro ", " En Plan"),
    FINNISH("Rakas Joulupukki ", " Kiitos"),
    SANTA("Hyvää joulua ", " Nähdään pian");

    private final String prefix;
    private final String suffix;

    MessageStyle(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}
