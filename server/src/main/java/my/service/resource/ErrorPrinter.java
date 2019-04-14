package my.service.resource;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorPrinter {
    public static String getStackTraceString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}
