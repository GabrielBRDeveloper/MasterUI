package br.nullexcept.mux.lang.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XmlElement {
    private static final DocumentBuilderFactory factory;
    private final Node node;
    private final ArrayList<XmlElement> children = new ArrayList<>();
    private final HashMap<String, String> attributes = new HashMap<>();

    private XmlElement(Node node) {
        this.node = node;
        NodeList list = node.getChildNodes();
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Node child = list.item(i);
                if (!"#text".equals(child.getNodeName())) {
                    children.add(new XmlElement(list.item(i)));
                }
            }
        }
        NamedNodeMap map = node.getAttributes();
        if (map != null) {
            for (int i = 0; i < map.getLength(); i++) {
                Node attr = map.item(i);
                attributes.put(attr.getNodeName(), attr.getNodeValue());
            }
        }
    }

    public String value(){
        return node.getTextContent();
    }

    public String name() {
        return node.getNodeName();
    }

    public String[] attrNames() {
        return attributes.keySet().toArray(new String[0]);
    }

    public String attr(String name) {
        return attributes.get(name);
    }

    public int childCount(){
        return children.size();
    }

    public XmlElement childAt(int index) {
        return children.get(index);
    }

    public static XmlElement parse(InputStream input) {
        try {
            XmlElement element = new XmlElement(factory.newDocumentBuilder().parse(input).getFirstChild());
            input.close();
            return element;
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse xml", e);
        }
    }

    public boolean has(String name) {
        return attributes.containsKey(name);
    }

    public static XmlElement parse(String value) {
        ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
        return parse(input);
    }

    static {
        factory = DocumentBuilderFactory.newInstance();
    }

    public Map<String, String> attrs() {
        return new HashMap<>(attributes);
    }
}