# Advanced File System - Visitor Pattern

A Java 17 Maven project implementing an in-memory hierarchical file system using the **Visitor behavioral pattern** with double dispatch.

## Features & Requirements

* **Object Model (`R1`)**: Common `FileSystemElement` interface implemented by `FileNode` (with a name and non-negative byte size) and `DirectoryNode` (holding named child elements with arbitrary nesting)[cite: 1].
* **Double Dispatch (`R2`)**: Elements implement `accept(Visitor)` and delegate to the matching overloaded `visit` method[cite: 1].
* **Total Size Visitor (`R3`)**: Computes the sum of file sizes for an entire subtree[cite: 1].
* **Search Visitor (`R4`)**: Filters files by name criterion and minimum-size threshold, returning deterministic root-relative paths separated by `/`[cite: 1, 4].
* **Statistics Visitor (`R5`, `R8`)**: Collects file count, directory count (including root), and maximum tree depth, implemented cleanly without changing existing element contracts[cite: 1].
* **Tree Integrity (`R6`)**: Prevents negative file sizes, duplicate sibling names, multiple parent ownership, and cycles[cite: 1].
* **Deterministic Traversal (`R7`)**: Visits each reachable node exactly once per operation[cite: 1].

---

## Project Structure

```text
file-system-visitor/
├── pom.xml
└── src
    ├── main
    │   └── java
    │       └── com
    │           └── example
    │               └── filesystem
    │                   ├── FileSystemElement.java
    │                   ├── FileNode.java
    │                   ├── DirectoryNode.java
    │                   ├── Visitor.java
    │                   ├── TotalSizeVisitor.java
    │                   ├── SearchVisitor.java
    │                   ├── StatisticsVisitor.java
    │                   └── Main.java
    └── test
        └── java
            └── com
                └── example
                    └── filesystem
                        └── FileSystemTest.java