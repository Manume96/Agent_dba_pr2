package core.agent.communication;

public class Translator {

    public static String translate(String message, MessageStyle from, MessageStyle to) {
        String core = extractCore(message, from);
        return to.getPrefix() + core + to.getSuffix();
    }

    private static String extractCore(String message, MessageStyle style) {
        String result = message.trim();
        if (result.startsWith(style.getPrefix())) {
            result = result.substring(style.getPrefix().length());
        }
        if (result.endsWith(style.getSuffix())) {
            result = result.substring(0, result.length() - style.getSuffix().length());
        }
        return result.trim();
    }

    public static boolean isStyle(String message, MessageStyle style) {
        return message.startsWith(style.getPrefix()) && message.endsWith(style.getSuffix());
    }
}
