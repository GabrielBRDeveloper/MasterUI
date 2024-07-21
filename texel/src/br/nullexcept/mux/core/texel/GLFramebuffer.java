package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.lang.Bindable;
import br.nullexcept.mux.lang.Disposable;

import static br.nullexcept.mux.hardware.GLES.*;

class GLFramebuffer implements Disposable, Bindable {
    private int width, height;
    private final GLTexture texture;
    private final int renderBuffer;
    private final int frameBuffer;


    public GLFramebuffer(int width, int height) {
        this.width = width;
        this.height = height;
        int[] buffer = new int[1];
        texture = new GLTexture(width,height, GL_RGBA);
        glGenFramebuffers(buffer);
        frameBuffer = buffer[0];
        glGenRenderbuffers(buffer);
        renderBuffer = buffer[0];

        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER,GL_STENCIL_INDEX8, width, height);

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glBindTexture(GL_TEXTURE_2D, texture.getTexture());
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTexture(),0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);

        glClear(GL_COLOR_BUFFER_BIT);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void resize(int width, int height){
        texture.recreate(width, height, null);
        this.width = width;
        this.height = height;

        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_STENCIL_INDEX8, width, height);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void clear(int color){
        glClearColor(Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, Color.alpha(color)/255.0f);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
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
