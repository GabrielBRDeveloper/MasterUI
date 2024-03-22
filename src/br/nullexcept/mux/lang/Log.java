package br.nullexcept.mux.lang;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Log {
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
    public static void log(String tag, Object... values){
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("[%5s]: ", tag));
        int index = 1;
        for (Object obj: values){
            builder.append(toString(obj));

            if (index != values.length)
                builder.append(",");
            index++;
        }
        System.out.println(builder.toString());
    }
}
