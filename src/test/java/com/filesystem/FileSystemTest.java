package com.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemTest {

    private DirectoryNode root;

    @BeforeEach
    void setUp() {
        root = new DirectoryNode("root");

        DirectoryNode docs = new DirectoryNode("documents");
        docs.addChild(new FileNode("resume.pdf", 150));
        docs.addChild(new FileNode("notes.txt", 50));

        DirectoryNode photos = new DirectoryNode("photos");
        photos.addChild(new FileNode("vacation.jpg", 2000));

        root.addChild(docs);
        root.addChild(photos);
        root.addChild(new FileNode("config.json", 10));
    }

    @Test
    void T1_DoubleDispatchAndVisitMethods() {
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        assertEquals(2210, sizeVisitor.getTotalSize());
    }

    @Test
    void T2_SizeAccumulationMultiLevel() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        assertEquals(2210, visitor.getTotalSize());
    }

    @Test
    void T3_SearchWithCombinedConditions() {
        SearchVisitor searchVisitor = new SearchVisitor("", 100);
        root.accept(searchVisitor);
        List<String> paths = searchVisitor.getMatchingPaths();

        assertEquals(2, paths.size());
        assertTrue(paths.contains("documents/resume.pdf"));
        assertTrue(paths.contains("photos/vacation.jpg"));
    }

    @Test
    void T4_SearchDeterministicRootRelativePaths() {
        SearchVisitor searchVisitor = new SearchVisitor("notes", 0);
        root.accept(searchVisitor);
        List<String> paths = searchVisitor.getMatchingPaths();

        assertEquals(1, paths.size());
        assertEquals("documents/notes.txt", paths.get(0));
    }

    @Test
    void T5_StatisticsCorrectNestingAndDepth() {
        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);

        assertEquals(4, statsVisitor.getFileCount()); // resume.pdf, notes.txt, vacation.jpg, config.json
        assertEquals(3, statsVisitor.getDirectoryCount()); // root, documents, photos
        assertEquals(2, statsVisitor.getMaxDepth()); // root (0) -> documents (1) -> resume.pdf (внутри documents, глубина ветки 2)
    }

    @Test
    void T6_EmptyStructuresAndNoMatch() {
        DirectoryNode emptyRoot = new DirectoryNode("empty");
        StatisticsVisitor stats = new StatisticsVisitor();
        emptyRoot.accept(stats);

        assertEquals(0, stats.getFileCount());
        assertEquals(1, stats.getDirectoryCount()); // Сам корень учитывается
        assertEquals(0, stats.getMaxDepth());

        SearchVisitor search = new SearchVisitor("nonexistent", 0);
        emptyRoot.accept(search);
        assertTrue(search.getMatchingPaths().isEmpty());
    }

    @Test
    void T7_RepeatExecutionsNoStateLeak() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        long firstRun = visitor.getTotalSize();

        TotalSizeVisitor visitorSecond = new TotalSizeVisitor();
        root.accept(visitorSecond);
        assertEquals(firstRun, visitorSecond.getTotalSize());
    }

    @Test
    void T8_InvalidMutationsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -5));

        DirectoryNode testDir = new DirectoryNode("test");
        testDir.addChild(new FileNode("file.txt", 10));
        assertThrows(IllegalArgumentException.class, () -> testDir.addChild(new FileNode("file.txt", 20)));

        FileNode sharedFile = new FileNode("shared.txt", 15);
        DirectoryNode dirA = new DirectoryNode("dirA");
        DirectoryNode dirB = new DirectoryNode("dirB");
        dirA.addChild(sharedFile);
        assertThrows(IllegalArgumentException.class, () -> dirB.addChild(sharedFile));

        DirectoryNode parent = new DirectoryNode("parent");
        DirectoryNode child = new DirectoryNode("child");
        parent.addChild(child);
        assertThrows(IllegalArgumentException.class, () -> child.addChild(parent));
    }
}