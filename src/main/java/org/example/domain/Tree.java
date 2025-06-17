package org.example.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tree {
    private LocalDateTime createdAt;
    private String path;
    private Drawing drawing;
    private List<Tree> children;


    public Tree() {
        this.children = new ArrayList<>();
        this.drawing = null;
    }

    public Tree(String path) {
        this.createdAt = LocalDateTime.now();
        this.path = path;
        this.drawing = null;
        this.children = new ArrayList<>();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public Drawing getDrawing() {
        return drawing;
    }

    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
    }


    public List<Tree> getChildren() {
        return children;
    }

    public void setChildren(List<Tree> children) {
        this.children = children;
    }


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
