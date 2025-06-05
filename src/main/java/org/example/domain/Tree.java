package org.example.domain;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    private String path;
    private Drawing drawing;
    private List<Tree> children;

    public Tree(String path) {
        this.path = path;
        this.drawing = null; // Initialize drawing as null
        this.children = new ArrayList<>();
    }

    public void addChild(Tree child) {
        children.add(child);
    }

    public String getPath() {
        return path;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public Drawing getDrawing() {
        return drawing;
    }
    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
    }

    public void printTree(String indent) {
        System.out.println(indent + path);
        for (Tree child : children) {
            child.printTree(indent + "  ");
        }
    }
}
