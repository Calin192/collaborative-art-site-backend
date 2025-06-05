package org.example.domain;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    private String path;
    private Drawing drawing;
    private List<Tree> children;

    // Constructor fără parametri - obligatoriu pentru Jackson
    public Tree() {
        this.children = new ArrayList<>();
        this.drawing = null;
    }

    // Constructor cu parametru path, dacă vrei să-l păstrezi
    public Tree(String path) {
        this.path = path;
        this.drawing = null;
        this.children = new ArrayList<>();
    }

    // Getter și setter pentru path
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // Getter și setter pentru drawing
    public Drawing getDrawing() {
        return drawing;
    }

    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
    }

    // Getter și setter pentru children
    public List<Tree> getChildren() {
        return children;
    }

    public void setChildren(List<Tree> children) {
        this.children = children;
    }

    // Metoda addChild rămâne opțională
    public void addChild(Tree child) {
        children.add(child);
    }

    public void printTree(String indent) {
        System.out.println(indent + path);
        for (Tree child : children) {
            child.printTree(indent + "  ");
        }
    }
}
