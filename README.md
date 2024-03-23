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

i = initial\
f = full\
n = need implement

- (i) XML UI
- (i) Events: Mouse, Keyboard
- (i) Activity
- (i) Multi window
- (i) Basic components.
- (i) Read bitmap.
- (n) Write bitmap.
- (i) Read drawables.
- (n) Clip based in path.
- (i) Themes
- (i) Attributes

Full project status: [WIP]