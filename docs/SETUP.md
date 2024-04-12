# Bootstrap

Import .jar with library and natives. \
Create folder structure on your project:
>    assets/\
>    res/

**assets**: Folder for save all assets from your project.\
**res:** Folder for save all resources a like texts, layouts, icons, themes, etc.

**IMPORTANT:** Ouput folders need follow that structure too.

# Create a Hello World

You can create a hello world:

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

# Create a UI with XML.

For create complex UIs you can use XMLs, first you need create your layout file `res/layout/example.xml`, that is a example of a simple Hello world layout:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<LinearLayout
    width="match_parent"
    height="match_parent"
    background="?colorSurface"
    orientation="horizontal">
  <TextView
    width="match_parent"
    height="match_parent"
    text="Hello world"/>
</LinearLayout>
```

For import that layout, you can use ``setContentView(layoutId)`` in your activity.

```java

@Override
public void onCreate() {
    super.onCreate();
    setContentView("example");
}

```

### Change widgets from a xml layout.

For access and edit nodes created from xml layout, you can use method: ``findViewById('NODE_ID')``, and set tag id in your xml code, example:

```xml
  <TextView
    id="NODE_ID"
    width="match_parent"
    height="match_parent"
    text="Hello world"/>
```