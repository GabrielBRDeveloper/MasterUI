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
# User requeriments

- OpenGLES 2.0
- Java 8

# Status

🟥 **Need implement**\
🟨 **Initial support**\
🟩 **Full implemented**

### Components

- 🟩 AbsoluteLayout
- 🟩 LinearLayout
- 🟩 Button
- 🟩 TextView
- 🟨 EditText
- 🟨 ImageView

### Interface

- 🟨 XML UI
- 🟨 Themes
- 🟨 Multi window
- 🟨 Attributes
- 🟨 Read drawables.
- 🟥 Clip based in path.

### General

- 🟩 Looper
- 🟨 Activity
- 🟨 Read bitmap.
- 🟥 Write bitmap.

### Events

- 🟨 Keyboard
- 🟩 Mouse
- 🟥 Joystick

Full project status: [WIP]