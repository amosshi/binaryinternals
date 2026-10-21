/*
 * ParseService.java    October 20, 2026
 *
 * Copyright 2026, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.web;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PluginDescriptor;

/// Parse a file with the format plug-ins, and convert the result tree to
/// JSON.
///
/// @author Amos Shi
public final class ParseService {

    /// Allowed file name extension, also used as the temporary file suffix.
    private static final Pattern EXTENSION = Pattern.compile("[A-Za-z0-9]{1,16}");

    /// Find the {@link Icons} of an icon object, see {@link Icons#getIcon()}
    /// which returns the same object for each icon.
    private static final Map<Icon, Icons> ICON_NAMES = new IdentityHashMap<>();

    static {
        for (Icons icon : Icons.values()) {
            ICON_NAMES.put(icon.getIcon(), icon);
        }
    }

    private final List<PluginDescriptor> plugins;
    private final Path tempDir;

    /// The file format is not supported by any plug-in.
    public static final class UnsupportedFormatException extends Exception {

        private static final long serialVersionUID = 1L;

        UnsupportedFormatException(final String message) {
            super(message);
        }
    }

    /// Create the service with the plug-ins found by {@link ServiceLoader}.
    ///
    /// @param tempDir Folder for the temporary copy of the uploaded files
    public ParseService(final Path tempDir) {
        final List<PluginDescriptor> found = new ArrayList<>();
        ServiceLoader.load(PluginDescriptor.class).forEach(found::add);
        found.sort(Comparator.comparing(PluginDescriptor::getExtensionDescription));
        this.plugins = Collections.unmodifiableList(found);
        this.tempDir = tempDir;
    }

    /// Get the loaded plug-ins.
    ///
    /// @return The plug-ins
    public List<PluginDescriptor> getPlugins() {
        return this.plugins;
    }

    /// Get the supported formats as JSON:
    /// `[{"description": "...", "extensions": ["class"]}]`.
    ///
    /// @return JSON text
    public String formatsJson() {
        final StringBuilder sb = new StringBuilder("[");
        for (PluginDescriptor plugin : this.plugins) {
            if (sb.length() > 1) {
                sb.append(',');
            }
            sb.append("{\"description\":");
            Json.string(sb, plugin.getExtensionDescription()).append(",\"extensions\":[");
            final List<String> exts = plugin.getExtensions();
            for (int i = 0; i < exts.size(); i++) {
                Json.string(sb.append(i == 0 ? "" : ","), exts.get(i));
            }
            sb.append("]}");
        }
        return sb.append(']').toString();
    }

    /// Get the extension of a file name.
    ///
    /// @param fileName File name, for example `Foo.class`
    /// @return The extension, or empty when there is no valid extension
    static Optional<String> extensionOf(final String fileName) {
        final int dot = fileName.lastIndexOf('.');
        final String ext = dot < 0 ? "" : fileName.substring(dot + 1);
        return EXTENSION.matcher(ext).matches() ? Optional.of(ext) : Optional.empty();
    }

    /// Find the plug-in for a file name, by its extension.
    ///
    /// @param fileName File name, for example `Foo.class`
    /// @return The plug-in, or empty for an unsupported file
    public Optional<PluginDescriptor> findPlugin(final String fileName) {
        return extensionOf(fileName).flatMap(ext -> this.plugins.stream()
                .filter(p -> p.getExtensions() != null && p.getExtensions().stream().anyMatch(ext::equalsIgnoreCase))
                .findFirst());
    }

    /// Parse a file and get its tree as JSON.
    ///
    /// @param bytes File content
    /// @param fileName Original file name, its extension selects the plug-in
    /// @return JSON tree, see {@link #toJson(DefaultMutableTreeNode)}
    /// @throws UnsupportedFormatException No plug-in for the file
    /// @throws FileFormatException The file content is invalid
    /// @throws IOException Failed to write or read the temporary file
    /// @throws ReflectiveOperationException The plug-in cannot be created
    public String parse(final byte[] bytes, final String fileName)
            throws UnsupportedFormatException, FileFormatException, IOException, ReflectiveOperationException {
        final PluginDescriptor plugin = this.findPlugin(fileName)
                .orElseThrow(() -> new UnsupportedFormatException("Unsupported file type: " + fileName));
        final String ext = extensionOf(fileName).orElseThrow();

        // The plug-ins read from a file, keep the extension for them
        final Path tmp = Files.createTempFile(this.tempDir, "upload-", "." + ext);
        try {
            Files.write(tmp, bytes);
            final FileFormat format = newFileFormat(plugin, tmp);
            final DefaultMutableTreeNode root = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    0,
                    bytes.length,
                    fileName,
                    format.getIcon().orElse(null)));
            format.generateTreeNode(root);
            return toJson(root);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    private static FileFormat newFileFormat(final PluginDescriptor plugin, final Path file)
            throws FileFormatException, IOException, ReflectiveOperationException {
        try {
            return plugin.getFileFormatClass().getConstructor(Path.class).newInstance(file);
        } catch (InvocationTargetException ex) {
            // Surface what the plug-in actually threw
            switch (ex.getCause()) {
                case Error e -> throw e;
                case RuntimeException e -> throw e;
                case IOException e -> throw e;
                case FileFormatException e -> throw e;
                case null, default -> throw ex;
            }
        }
    }

    /// Convert a tree to JSON. Each node is
    /// `{"text": "...", "start": 0, "length": 4, "icon": "Magic", "description": "...", "children": [...]}`,
    /// where `icon`, `description` and `children` are left out when absent.
    ///
    /// @param root The tree root
    /// @return JSON text
    static String toJson(final DefaultMutableTreeNode root) {
        final StringBuilder sb = new StringBuilder(4096);
        appendNode(sb, root);
        return sb.toString();
    }

    private static void appendNode(final StringBuilder sb, final DefaultMutableTreeNode node) {
        sb.append('{');
        if (node.getUserObject() instanceof JTreeNodeFileComponent comp) {
            sb.append("\"text\":");
            Json.string(sb, comp.getText());
            sb.append(",\"start\":").append(comp.getStartPos());
            sb.append(",\"length\":").append(comp.getLength());
            final Icons icon = comp.getIcon() == null ? null : ICON_NAMES.get(comp.getIcon());
            if (icon != null) {
                sb.append(",\"icon\":");
                Json.string(sb, icon.name());
            }
            if (comp.getDescription() != null) {
                sb.append(",\"description\":");
                Json.string(sb, comp.getDescription());
            }
        } else {
            sb.append("\"text\":");
            Json.string(sb, String.valueOf(node.getUserObject()));
        }

        if (node.getChildCount() > 0) {
            sb.append(",\"children\":[");
            for (int i = 0; i < node.getChildCount(); i++) {
                if (i > 0) {
                    sb.append(',');
                }
                appendNode(sb, (DefaultMutableTreeNode) node.getChildAt(i));
            }
            sb.append(']');
        }
        sb.append('}');
    }
}
