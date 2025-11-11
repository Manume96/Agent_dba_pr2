package agent_dba_pr2.logger;

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
        System.out.println("[" + timestamp + "] [" + level + "] [" + className + "] [" + methodName + "] " + message);
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
