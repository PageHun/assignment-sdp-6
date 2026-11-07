package com.filesystem;

public interface FileSystemElement {
    String getName();
    void accept(Visitor visitor);
}