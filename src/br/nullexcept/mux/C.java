package br.nullexcept.mux;

import br.nullexcept.mux.graphics.BitmapFactory;
import br.nullexcept.mux.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class C {
    public static int VIEW_COUNT = 0;
    public static long VG_CONTEXT = -1;
    public static long GLFW_CONTEXT = 0;
    public static BitmapFactory BITMAP_FACTORY;

    public static class Config {
        public static boolean SET_WINDOW_GL_HINT = true;
        public static final int[] WINDOW_GL_VERSION = new int[]{ 2, 0 };
    }

    public static class Flags {
        public static final boolean FULL_DRAW;
        public static final boolean DEBUG_OVERLAY;
        public static final boolean DISABLE_AUTO_REDRAW;

        static {
            ArrayList<String> flags = new ArrayList<>();
            if (System.getenv().containsKey("JMasterUiFlags")) {
                flags.addAll(Arrays.asList(System.getenv("JMasterUiFlags").toUpperCase().replaceAll(" ","").split(",")));
                Log.log("MasterUI", "Core flags: "+Arrays.toString(flags.toArray()));
            }

            FULL_DRAW = flags.contains("FULL_DRAW");
            DEBUG_OVERLAY = flags.contains("DEBUG_OVERLAY");
            DISABLE_AUTO_REDRAW = flags.contains("DISABLE_AUTO_REDRAW");
        }
    }
}
