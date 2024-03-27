package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.graphics.Color;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

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
        drawTexture(x, y, width, height, GLShaderList.TEXTURE, texture);
    }

    public static void drawViewLayers(float[][] vertices, int[] textures, float[] alphas){
        glEnable(GL_BLEND);
        glBlendFuncSeparate(GL_ONE, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        GLProgram program = GLShaderList.VIEW;

        program.bind();

        int vPosition = program.attribute(GLProgram.ATTRIBUTE_POSITION);
        int vTextureCoords = program.attribute(GLProgram.ATTRIBUTE_UV);
        int uTexture = program.uniform(GLProgram.UNIFORM_TEXTURE);
        int uAlpha = program.uniform("alpha");
        int uTime = program.uniform("time");

        glEnableVertexAttribArray(vPosition);
        glEnableVertexAttribArray(vTextureCoords);

        for (int i = 0; i < vertices.length; i++) {
            bufferRect.put(vertices[i]);
            bufferRect.position(0);

            glBindTexture(GL_TEXTURE_2D, textures[i]);
            glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, bufferRect);
            glVertexAttribPointer(vTextureCoords, 2, GL_FLOAT, false, 0, bufferUV);

            glUniform1i(uTexture, GL_TEXTURE_2D);
            glUniform1f(uAlpha, alphas[i]);
            glUniform1f(uTime, (System.currentTimeMillis() & 1000)/1000.0f);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            glDisable(GL_DEPTH_TEST);
        }

        program.unbind();
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public static void drawTexture(float x, float y, float width, float height, GLProgram program, GLTexture texture){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        prepareRect(x, y, width, height);
        //bufferColor.put(new float[]{ Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, Color.alpha(color)/255.0f  });
        program.bind();

        int vPosition = program.attribute(GLProgram.ATTRIBUTE_POSITION);
        int vTextureCoords = program.attribute(GLProgram.ATTRIBUTE_UV);
        int uTexture = program.uniform(GLProgram.UNIFORM_TEXTURE);

        //int vColor = program.uniform(GLProgram.ATTRIBUTE_COLOR);

        texture.bind();
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, bufferRect);
        glVertexAttribPointer(vTextureCoords, 2, GL_FLOAT, false, 0, bufferUV);

        glEnableVertexAttribArray(vPosition);
        glEnableVertexAttribArray(vTextureCoords);

        glUniform1i(uTexture, GL_TEXTURE_2D);
        //glUniform4fv(vColor, bufferColor);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        program.unbind();
        texture.unbind();
        glDisable(GL_DEPTH_TEST);
    }
}
