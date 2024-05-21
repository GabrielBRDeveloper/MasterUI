package org.lwjgl.nanovg;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opengles.GLES;
import org.lwjgl.system.FunctionProvider;
import org.lwjgl.system.MemoryUtil;

/**
 * Fix native image build reflection from {org.lwjgl.opengles.GLES},
 * replacing that for direct access to OpenGLES
 */

public class NanoVGGLConfig {
    static void configGL(long pointersAddress) {
        config(pointersAddress, getFunctionProvider("org.lwjgl.opengl.GL"));
    }

    static void configGLES(long pointersAddress) {
        config(pointersAddress, getFunctionProvider(null));
    }

    private static FunctionProvider getFunctionProvider(String none) {
        return GLES.getFunctionProvider();
    }

    private static void config(long pointersAddress, FunctionProvider fp) {
        String[] functions = new String[]{"glActiveTexture", "glAttachShader", "glBindAttribLocation", "glBindBuffer", "glBindBufferRange", "glBindFramebuffer", "glBindRenderbuffer", "glBindTexture", "glBindVertexArray", "glBlendFunc", "glBlendFuncSeparate", "glBufferData", "glCheckFramebufferStatus", "glColorMask", "glCompileShader", "glCreateProgram", "glCreateShader", "glCullFace", "glDeleteBuffers", "glDeleteFramebuffers", "glDeleteProgram", "glDeleteRenderbuffers", "glDeleteShader", "glDeleteTextures", "glDeleteVertexArrays", "glDetachShader", "glDisable", "glDisableVertexAttribArray", "glDrawArrays", "glEnable", "glEnableVertexAttribArray", "glFinish", "glFlush", "glFramebufferRenderbuffer", "glFramebufferTexture2D", "glFrontFace", "glGenBuffers", "glGenFramebuffers", "glGenRenderbuffers", "glGenTextures", "glGenVertexArrays", "glGenerateMipmap", "glGetError", "glGetIntegerv", "glGetProgramiv", "glGetProgramInfoLog", "glGetShaderiv", "glGetShaderInfoLog", "glGetUniformBlockIndex", "glGetUniformLocation", "glLinkProgram", "glPixelStorei", "glRenderbufferStorage", "glShaderSource", "glStencilFunc", "glStencilMask", "glStencilOp", "glStencilOpSeparate", "glTexImage2D", "glTexParameteri", "glTexSubImage2D", "glUniform1i", "glUniform2fv", "glUniform4fv", "glUniformBlockBinding", "glUseProgram", "glVertexAttribPointer"};
        PointerBuffer pointers = MemoryUtil.memPointerBuffer(pointersAddress, functions.length);

        for(int i = 0; i < functions.length; ++i) {
            pointers.put(i, fp.getFunctionAddress(functions[i]));
        }
    }
}
