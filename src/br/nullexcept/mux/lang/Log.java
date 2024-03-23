package br.nullexcept.mux.lang;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Log {
    private static final long start = System.currentTimeMillis();

    private static final String time(){
        long time = (System.currentTimeMillis() - start)/1000;
        return String.format("%02d:%02d", (time/60), time % 60);
    }

    private static String toString(Object obj){
        if (obj == null){
            return "~null";
        } else if (obj instanceof Throwable){
            try {
                ByteArrayOutputStream o = new ByteArrayOutputStream();
                PrintStream stream = new PrintStream(o);
                ((Throwable) obj).printStackTrace(stream);
                stream.flush();
                stream.close();
                o.flush();
                o.close();
                return o.toString("UTF-8");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return String.valueOf(obj);
    }


    private synchronized static void print(PrintStream out, Level level, String tag, Object... values) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s %5s %5s ", time(), level.name(), tag));
        int index = 1;
        for (Object obj: values){
            builder.append(toString(obj));

            if (index != values.length)
                builder.append(",");
            index++;
        }
        out.println(builder.toString());
        out.flush();
    }

    public static void error(String tag, Object... values){
        print(System.err,Level.ERROR, tag, values);
    }

    public static void log(String tag, Object... values){
        print(System.out, Level.LOG, tag, values);
    }

    private enum Level {
        ERROR,
        LOG,
        WARN
    }
}
