package core.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    public enum Level {
        INFO,
        WARN,
        ERROR,
        DEBUG
    }

private static String getCallerClassName() {
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    // Ahora saltamos 4 niveles: 0=getStackTrace, 1=getCallerClassName, 2=log, 3=info/debug/etc, 4=quien llamó realmente
    if (stack.length > 4) {
        return stack[4].getClassName();
    }
    return "UnknownClass";
}

private static String getCallerMethodName() {
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    if (stack.length > 4) {
        return stack[4].getMethodName();
    }
    return "UnknownMethod";
}


    public static void log(Level level, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String className = getCallerClassName();
        String methodName = getCallerMethodName();
        String line = "[" + timestamp + "] [" + level + "] [" + className + "] [" + methodName + "] " + message;
        String colored = applyColorForAgent(className, line);
        System.out.println(colored);
    }

    private static String applyColorForAgent(String className, String line) {
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m"; // Agent_DBA
        final String RED = "\u001B[31m"; // Santa
        final String BLUE = "\u001B[34m"; // Translator
        final String MAGENTA = "\u001B[35m"; // Rudolph / other

        if (className.contains("AgentBehaviour") || className.endsWith("Agent_dba")) {
            return GREEN + line + RESET;
        }
        if (className.contains("SantaAgent") || className.endsWith("SantaAgent")) {
            return RED + line + RESET;
        }
        if (className.contains("TranslatorAgent") || className.endsWith("TranslatorAgent")) {
            return BLUE + line + RESET;
        }
        if (className.contains("RudolphAgent") || className.endsWith("RudolphAgent")) {
            return MAGENTA + line + RESET;
        }
        return line;
    }

    // Métodos de conveniencia
    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void warn(String message) {
        log(Level.WARN, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void debug(String message) {
        log(Level.DEBUG, message);
    }
}
