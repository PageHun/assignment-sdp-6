package com.filesystem;

import java.util.*;

public class DirectoryNode implements FileSystemElement {
    private final String name;
    private final Map<String, FileSystemElement> children = new LinkedHashMap<>();
    private DirectoryNode parent;

    public DirectoryNode(String name) {
        if (name == null || name.contains("/")) {
            throw new IllegalArgumentException("Invalid directory name: " + name);
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public DirectoryNode getParent() {
        return parent;
    }

    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    public Map<String, FileSystemElement> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    public void addChild(FileSystemElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        if (element == this) {
            throw new IllegalArgumentException("Cannot add directory to itself (cycle prevention)");
        }

        DirectoryNode curr = this;
        while (curr != null) {
            if (curr == element) {
                throw new IllegalArgumentException("Cycle detected: cannot add an ancestor as a child");
            }
            curr = curr.getParent();
        }

        if (element instanceof FileNode fileNode) {
            if (fileNode.getParent() != null && fileNode.getParent() != this) {
                throw new IllegalArgumentException("File already belongs to another directory");
            }
            fileNode.setParent(this);
        } else if (element instanceof DirectoryNode dirNode) {
            if (dirNode.getParent() != null && dirNode.getParent() != this) {
                throw new IllegalArgumentException("Directory already belongs to another directory");
            }
            dirNode.setParent(this);
        }

        if (children.containsKey(element.getName())) {
            throw new IllegalArgumentException("An element with name '" + element.getName() + "' already exists in this directory");
        }

        children.put(element.getName(), element);
    }

    public void removeChild(String elementName) {
        FileSystemElement removed = children.remove(elementName);
        if (removed instanceof FileNode fn) {
            fn.setParent(null);
        } else if (removed instanceof DirectoryNode dn) {
            dn.setParent(null);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}