package com.filesystem;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Demonstration of File System Visitor Pattern ===\n");

        DirectoryNode root = new DirectoryNode("root");

        DirectoryNode documents = new DirectoryNode("documents");
        documents.addChild(new FileNode("resume.pdf", 150));
        documents.addChild(new FileNode("notes.txt", 45));

        DirectoryNode media = new DirectoryNode("media");
        media.addChild(new FileNode("vacation.png", 2048));
        media.addChild(new FileNode("song.mp3", 5120));

        root.addChild(documents);
        root.addChild(media);
        root.addChild(new FileNode("config.json", 12));

        System.out.println("Structure created successfully.\n");

        System.out.println("--- 1. Total Size Operation ---");
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.println("Total size of the entire file system: " + sizeVisitor.getTotalSize() + " bytes");

        System.out.println("\n--- 2. Statistics Operation ---");
        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        System.out.println("File count: " + statsVisitor.getFileCount());
        System.out.println("Directory count: " + statsVisitor.getDirectoryCount());
        System.out.println("Max tree depth: " + statsVisitor.getMaxDepth());
        System.out.println("Maximum tree depth: " + statsVisitor.getMaxDepth());

        System.out.println("\n--- 3. Search Operation ---");
        SearchVisitor searchVisitor = new SearchVisitor("", 100);
        root.accept(searchVisitor);

        List<String> foundPaths = searchVisitor.getMatchingPaths();
        System.out.println("Files with size >= 100 bytes:");
        for (String path : foundPaths) {
            System.out.println(" -> /" + path);
        }

        System.out.println("\nSearching for files containing 'notes':");
        SearchVisitor nameSearchVisitor = new SearchVisitor("notes", 0);
        root.accept(nameSearchVisitor);
        for (String path : nameSearchVisitor.getMatchingPaths()) {
            System.out.println(" -> /" + path);
        }

        System.out.println("\n=== Demonstration Finished ===");
    }
}