package org.binaryinternals.format.classfile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.classfile.ClassBuilder;
import java.lang.constant.ClassDesc;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;

/// Shared helpers for the class file parser tests.
final class TestSupport {

    private static List<Path> jdkClasses;

    private TestSupport() {
    }

    /// All `.class` files of the `java.base` module of the running JDK.
    static synchronized List<Path> jdkClasses() {
        if (jdkClasses == null) {
            final Path base = FileSystems.getFileSystem(URI.create("jrt:/")).getPath("/modules/java.base");
            try (Stream<Path> s = Files.walk(base)) {
                jdkClasses = s.filter(p -> p.toString().endsWith(".class")).sorted().toList();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return jdkClasses;
    }

    static byte[] read(final Path p) {
        try {
            return Files.readAllBytes(p);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static ClassFile parse(final byte[] bytes) throws IOException {
        return new ClassFile(bytes, "Test.class", "Test.class");
    }

    /// Builds a class file named `test.Sample` with the Class-File API.
    static byte[] build(final Consumer<ClassBuilder> handler) {
        return java.lang.classfile.ClassFile.of().build(ClassDesc.of("test.Sample"), handler);
    }

    /// Generates the tree of `cf`, and returns every node in depth-first order.
    static List<JTreeNodeFileComponent> treeNodes(final ClassFile cf) {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        cf.generateTreeNode(root);
        final List<JTreeNodeFileComponent> nodes = new ArrayList<>();
        for (Object node : Collections.list(root.depthFirstEnumeration())) {
            if (((DefaultMutableTreeNode) node).getUserObject() instanceof JTreeNodeFileComponent c) {
                nodes.add(c);
            }
        }
        return nodes;
    }

    /// Generates the tree of `cf`, and returns the text of every node.
    static List<String> treeTexts(final ClassFile cf) {
        return treeNodes(cf).stream().map(JTreeNodeFileComponent::getText).toList();
    }

    /// Generates the tree of `cf`, and returns the nodes whose byte range is
    /// not inside the byte range of their parent node.
    static List<String> nodesOutsideParent(final ClassFile cf) {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        cf.generateTreeNode(root);
        final List<String> outside = new ArrayList<>();
        for (Object o : Collections.list(root.depthFirstEnumeration())) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
            if (node.getUserObject() instanceof JTreeNodeFileComponent c && c.getLength() > 0
                    && node.getParent() instanceof DefaultMutableTreeNode parent
                    && parent.getUserObject() instanceof JTreeNodeFileComponent p && p.getLength() > 0
                    && (c.getStartPos() < p.getStartPos() || c.getLastPosPlus1() > p.getLastPosPlus1())) {
                outside.add("'%s' %d+%d is outside of '%s' %d+%d".formatted(
                        c.getText(), c.getStartPos(), c.getLength(), p.getText(), p.getStartPos(), p.getLength()));
            }
        }
        return outside;
    }

    /// Formats up to 10 failures, for an assertion message.
    static String summary(final List<String> failures) {
        return failures.size() + " failure(s), e.g.\n  " + String.join("\n  ", failures.subList(0, Math.min(10, failures.size())));
    }
}
