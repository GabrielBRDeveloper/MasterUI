package br.nullexcept.mux.renderer.texel.surface;

import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.lang.Bindable;
import br.nullexcept.mux.lang.Disposable;

import static br.nullexcept.mux.hardware.GLES.*;

public class GLFramebuffer implements Disposable, Bindable {
    private final int width, height;
    private final GLTexture texture;
    private final int frameBuffer;
    public GLFramebuffer(int width, int height) {
        this.width = width;
        this.height = height;
        int[] buffer = new int[1];
        texture = new GLTexture(width,height, GL_RGBA);
        glGenFramebuffers(buffer);
        frameBuffer = buffer[0];

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

        buffer = new int[1];
        glGenRenderbuffers(buffer);
        glBindTexture(GL_TEXTURE_2D, texture.getTexture());

        glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH_COMPONENT,width,height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTexture(),0);
        glClear(GL_COLOR_BUFFER_BIT);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void clear(int color){
        bind();
        glClearColor(Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, Color.alpha(color)/255.0f);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        unbind();
    }

    public GLTexture getTexture() {
        return texture;
    }

    public int getFramebuffer(){
        return frameBuffer;
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);;
        glViewport(0,0,width,height);
    }

    @Override
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void dispose() {
        unbind();
        glDeleteFramebuffers(frameBuffer);
        texture.dispose();
    }
}
