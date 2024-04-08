package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.lang.Function;
import br.nullexcept.mux.lang.Function2;
import br.nullexcept.mux.view.View;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

import static br.nullexcept.mux.hardware.GLES.*;

class GLTexel {
    private static final FloatBuffer bufferRect = BufferUtils.createFloatBuffer(2*4);
    private static final FloatBuffer bufferUV = BufferUtils.createFloatBuffer(2*4);
    private static final FloatBuffer bufferColor = BufferUtils.createFloatBuffer(4);
    private static final int[] VIEWPORT_BUFFER = new int[4];

    static {
        BufferUtils.zeroBuffer(bufferUV);
        bufferUV.put(new float[]{
                0, 0,
                1, 0,
                0, 1,
                1, 1
        });
        bufferUV.position(0);
    }

    private static void prepareColor(int color){
        bufferColor.put(new float[]{ Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, Color.alpha(color)/255.0f  });
        bufferColor.position(0);
    }

    private static void prepareRect(float x, float y, float width, float height){
        glGetIntegerv(GL_VIEWPORT,VIEWPORT_BUFFER);
        int vw = VIEWPORT_BUFFER[2]/2;
        int vh = VIEWPORT_BUFFER[3]/2;

        x = (x-vw)/vw;
        y = (vh-y-height)/vh;
        width /= vw;
        height /= vh;

        bufferRect.put(new float[]{
                x,       y,
                x+width, y,
                x,       y+height,
                x+width, y+height
        });
        bufferRect.position(0);
    }

    public static void drawTexture(float x, float y, float width, float height, GLTexture texture){
        drawTexture(x, y, width, height, GLShaderList.TEXTURE, texture.getTexture());
    }

    public static void drawViewLayers(float[][] vertices, int[] textures, View[] views){
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        GLProgram program = GLShaderList.VIEW;

        program.bind();

        int vPosition = program.attribute(GLProgram.ATTRIBUTE_POSITION);
        int vTextureCoords = program.attribute(GLProgram.ATTRIBUTE_UV);
        int uTexture = program.uniform(GLProgram.UNIFORM_TEXTURE);
        int uAlpha = program.uniform("alpha");
        int uTime = program.uniform("time");
        int uMode = program.uniform("mode");
        int uSize = program.uniform("size");
        int uElevation = program.uniform("elevation");
        int uShadowColor = program.uniform("shadowColor");

        glEnableVertexAttribArray(vPosition);
        glEnableVertexAttribArray(vTextureCoords);

        Function2<Integer, Integer> draw = (i, mode) -> {
            bufferRect.put(vertices[i]);
            bufferRect.position(0);

            glBindTexture(GL_TEXTURE_2D, textures[i]);
            glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, bufferRect);
            glVertexAttribPointer(vTextureCoords, 2, GL_FLOAT, false, 0, bufferUV);

            glUniform1i(uTexture, GL_TEXTURE_2D);
            glUniform1i(uMode, mode);
            glUniform1f(uElevation, views[i].getElevation());
            glUniform1f(uAlpha, views[i].getAlpha());
            glUniform4f(uShadowColor,
                    Color.red(views[i].getShadowColor()) / 255.0f,
                    Color.green(views[i].getShadowColor()) / 255.0f,
                    Color.blue(views[i].getShadowColor()) / 255.0f,
                    Color.alpha(views[i].getShadowColor()) / 255.0f
            );

            glUniform2f(uSize, views[i].getWidth(), views[i].getHeight());
            glUniform1f(uTime, (System.currentTimeMillis() & 1000)/1000.0f);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            glDisable(GL_DEPTH_TEST);
        };

        for (int i = 0; i < views.length; i++) {
            if (views[i].getElevation() > 0.001f) {
                draw.run(i, 1); // Draw shadow
            }
            draw.run(i, 0);
        }

        program.unbind();
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public static void drawTexture(float x, float y, float width, float height, GLProgram program, int texture){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        prepareRect(x, y, width, height);
        //bufferColor.put(new float[]{ Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, Color.alpha(color)/255.0f  });
        program.bind();

        int vPosition = program.attribute(GLProgram.ATTRIBUTE_POSITION);
        int vTextureCoords = program.attribute(GLProgram.ATTRIBUTE_UV);
        int uTexture = program.uniform(GLProgram.UNIFORM_TEXTURE);

        //int vColor = program.uniform(GLProgram.ATTRIBUTE_COLOR);

        glBindTexture(GL_TEXTURE_2D, texture);
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, bufferRect);
        glVertexAttribPointer(vTextureCoords, 2, GL_FLOAT, false, 0, bufferUV);

        glEnableVertexAttribArray(vPosition);
        glEnableVertexAttribArray(vTextureCoords);

        glUniform1i(uTexture, GL_TEXTURE_2D);
        //glUniform4fv(vColor, bufferColor);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        program.unbind();
        glBindTexture(GL_TEXTURE_2D,0);
        glDisable(GL_DEPTH_TEST);
    }
}
