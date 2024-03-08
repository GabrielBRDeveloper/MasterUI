package br.nullexcept.mux.renderer.texel.surface;

import br.nullexcept.mux.lang.Bindable;
import br.nullexcept.mux.lang.Disposable;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengles.GLES20.*;

public class GLTexture implements Bindable, Disposable {
    private final int format;

    private int[] texture = new int[1];
    private int width, height;

    public GLTexture(int width, int height){
        this(width,height, GL_RGBA);
    }

    public GLTexture(int width, int height, int format){
        this.width = width;
        this.height = height;
        this.format = format;
        glGenTextures(texture);
        glBindTexture(GL_TEXTURE_2D, texture[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height,0, format, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
    }

    public void clear(int color){
        bind(GL_TEXTURE_2D);
        ByteBuffer buffer = BufferUtils.createByteBuffer(width*height);
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height,0, format, GL_UNSIGNED_BYTE, buffer);
        glBindTexture(GL_TEXTURE_2D,0);
        //MemoryUtil.memFree(buffer);
    }

    public void recreate(int width, int height, ByteBuffer buffer){
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height,0, format, GL_UNSIGNED_BYTE, buffer);
        unbind();
    }

    public int getTexture(){
        return texture[0];
    }

    public void setPixels(int x, int y, int width, int height, ByteBuffer pixels){
        bind(GL_TEXTURE_2D);
        glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width, height, format, GL_UNSIGNED_BYTE, pixels);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bind(int index){
        glBindTexture(index, texture[0]);
    }

    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texture[0]);
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D,0);
    }

    public void dispose(){
        glDeleteTextures(texture);
    }

}
