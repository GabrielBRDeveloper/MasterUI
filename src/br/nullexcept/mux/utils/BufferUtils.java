package br.nullexcept.mux.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferUtils {
    public static final ByteBuffer allocateStream(InputStream input){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024 * 64];
            int len;
            while ((len = input.read(chunk)) != -1)
                out.write(chunk, 0, len);
            out.flush();
            out.close();
            input.close();
            ByteBuffer buffer = ByteBuffer.allocateDirect(out.size());
            buffer.put(out.toByteArray());
            buffer.flip();
            buffer.position(0);
            return buffer;
        } catch (Throwable e){
            throw new IllegalArgumentException(e);
        }
    }
}
