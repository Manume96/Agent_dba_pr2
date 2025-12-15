package core.agent.communication;

public enum ContentKeyword {
    WANT_TO_HELP("I want to help"),
    NOT_TRUSTWORTHY("Not trustworthy"),
    TRANSLATE_REQUEST("Translate: "),
    TRANSLATION_RESULT("Translation: "),
    WHERE_IS_REINDEER("Where is next reindeer?"),
    ALL_FOUND("ALL_FOUND"),
    FOUND_REINDEER("Found reindeer"),
    WHERE_ARE_YOU("Where are you?"),
    I_ARRIVED("I arrived"),
    HO_HO_HO("HoHoHo!");

    private final String text;

    ContentKeyword(String text) { this.text = text; }
    public String getText() { return text; }
}
