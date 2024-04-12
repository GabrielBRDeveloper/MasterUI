![Build linux (x86)](https://github.com/GabrielBRDeveloper/MasterUI/actions/workflows/build-linux.yml/badge.svg)

# Master UI

Java library for make a beautiful UI, based in android code design. \
That project uses OpenGLES 2.0 for render, so that is compatible with many actual devices.

>**[Use Guide](docs/SETUP.md)** \
>**[API Status](docs/STATUS.md)**

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
}
```

# Previews

![Example](https://i.imgur.com/xCUdAGS.png)

# User requirements

- OpenGLES 2.0
- Java 11 (Need some native fixes in nanovg, to work in java 8)


### Licenses

LWJGL: https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md \
NanoVG: https://github.com/memononen/nanovg/blob/master/LICENSE.txt \
OpenGLES: https://www.khronos.org/opengles/ \
Material icons: https://github.com/google/material-design-icons