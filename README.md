![Build linux (x86)](https://github.com/GabrielBRDeveloper/MasterUI/actions/workflows/build-linux.yml/badge.svg)

# Master UI

Java library for make a beautiful UI, code design is based in android ui.
That project uses OpenGLES 2.0 for render, so that is compatible with many actual devices.

# Usage example

```java

public class Example extends Activity {
    public static void main(String[] args) {
        Application.initialize(Example::new);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        View view = new View(this);
        setTitle("Hello world!");
        view.setBackground(new ColorDrawable(Color.RED));
        setContentView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Application.stop();
    }
}
```

# Previews

![Example](https://i.imgur.com/xCUdAGS.png)

# User requirements

- OpenGLES 2.0
- Java 11 (Need some native fixes in nanovg, to work in java 8)

# Status

🟥 **Need implement**\
🟨 **Initial support**\
🟩 **Full implemented**

### Components

- 🟩 AbsoluteLayout
- 🟩 LinearLayout
- 🟩 Button
- 🟩 Switch
- 🟩 TextView
- 🟨 ScrollView
- 🟨 EditText
- 🟨 ImageView

### Interface

- 🟨 XML UI
- 🟨 Themes
- 🟨 Multi window
- 🟨 Menus
- 🟩 Text/Localization
- 🟩 Attributes
- 🟩 Read drawables.
- 🟩 Scalable UI.
- 🟩 Path (A like SVG) Drawables
- 🟥 Clip based in path.

### General

- 🟩 Looper
- 🟨 Activity
- 🟨 Read bitmap.
- 🟥 Services.
- 🟥 Write bitmap.

### Events

- 🟩 Keyboard
- 🟩 Mouse
- 🟥 Joystick

Full project status: [WIP]

### Licenses | About

LWJGL: https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md \
NanoVG: https://github.com/memononen/nanovg/blob/master/LICENSE.txt \
OpenGLES: https://www.khronos.org/opengles/ \
Material icons: https://github.com/google/material-design-icons