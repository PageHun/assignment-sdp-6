package com.filesystem;

import java.util.*;

public class SearchVisitor implements Visitor {
    private final String nameCriterion;
    private final long minSizeThreshold;
    private final List<String> matchingPaths = new ArrayList<>();
    private final Deque<String> pathStack = new ArrayDeque<>();
    private boolean isRoot = true;

    public SearchVisitor(String nameCriterion, long minSizeThreshold) {
        if (nameCriterion == null) {
            throw new IllegalArgumentException("Name criterion cannot be null");
        }
        if (minSizeThreshold < 0) {
            throw new IllegalArgumentException("Min size threshold cannot be negative");
        }
        this.nameCriterion = nameCriterion;
        this.minSizeThreshold = minSizeThreshold;
    }

    @Override
    public void visit(FileNode file) {
        boolean nameMatches = nameCriterion.isEmpty() || file.getName().contains(nameCriterion);
        boolean sizeMatches = file.getSize() >= minSizeThreshold;

        if (nameMatches && sizeMatches) {
            List<String> segments = new ArrayList<>(pathStack);
            segments.add(file.getName());
            matchingPaths.add(String.join("/", segments));
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        boolean currentIsRoot = isRoot;
        if (isRoot) {
            isRoot = false;
        } else {
            pathStack.addLast(directory.getName());
        }

        for (FileSystemElement child : directory.getChildren().values()) {
            child.accept(this);
        }

        if (!currentIsRoot) {
            pathStack.removeLast();
        }
    }

    public List<String> getMatchingPaths() {
        List<String> sorted = new ArrayList<>(matchingPaths);
        Collections.sort(sorted);
        return sorted;
    }
}