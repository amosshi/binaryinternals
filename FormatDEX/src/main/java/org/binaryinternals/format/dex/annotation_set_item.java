/*
 * annotation_set_item.java    June 23, 2015, 06:20
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import java.io.IOException;
import java.util.Arrays;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;

/// A set of annotations: the offsets of the annotations, sorted by type index.
///
/// @author Amos Shi
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
/// java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
/// ```
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public final class annotation_set_item extends FileComponent implements GenerateTreeNodeDexFile {

    /// Size of the set in entries.
    public final Type_uint size;
    /// Elements of the set.
    public final annotation_off_item[] entries;

    annotation_set_item(PosDataInputStreamDex stream, DexFile dexFile) throws IOException {
        super.startPos = stream.getPos();
        this.size = stream.Dex_uint();
        if (this.size.value > 0) {
            this.entries = new annotation_off_item[this.size.intValue()];
            for (int i = 0; i < this.size.value; i++) {
                this.entries[i] = new annotation_off_item(stream, dexFile);
            }
        } else {
            this.entries = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public String toString() {
        return FORMAT_STRING_STRING.formatted(this.getClass().getSimpleName(), this.size);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        final DexFile dexFile = (DexFile) format;
        int floatPos = super.startPos;
        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "size",
                this.size,
                "msg_annotation_set_item__size",
                Icons.Size
        );
        floatPos += Type_uint.LENGTH;

        if (this.entries == null) {
            return;
        }

        DefaultMutableTreeNode entriesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                floatPos,
                annotation_off_item.LENGTH * this.entries.length,
                "entries [%d]".formatted(this.entries.length),
                Icons.Data,
                MESSAGES.getString("msg_annotation_set_item__entries")
        ));
        parentNode.add(entriesNode);

        for (int i = 0; i < this.entries.length; i++) {
            // Since annotation_off_item has only 1 field, so we do not use child node
            annotation_off_item offItem = this.entries[i];
            DefaultMutableTreeNode offItemNode = addNode(entriesNode,
                    offItem.getStartPos(),
                    offItem.getLength(),
                    "%s[%d].annotation_off".formatted(annotation_off_item.class.getSimpleName(), i),
                    offItem.annotation_off,
                    "msg_annotation_off_item__annotation_off",
                    Icons.Offset
            );

            annotation_item item = (annotation_item) dexFile.data.get(offItem.annotation_off.value);
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    item.getStartPos(),
                    item.getLength(),
                    annotation_item.class.getSimpleName(),
                    Icons.Shortcut,
                    MESSAGES.getString("msg_annotation_item")
            ));
            offItemNode.add(itemNode);
            item.generateTreeNode(itemNode, dexFile);
        }
    }

    /// An element of the annotation set: the offset of an annotation.
    public static final class annotation_off_item extends FileComponent {

        /// Length of the item in bytes.
        public static final int LENGTH = Type_uint.LENGTH;
        /// Offset from the start of the file to an [annotation_item].
        public final Type_uint annotation_off;

        annotation_off_item(PosDataInputStreamDex stream, DexFile dexFile) throws IOException {
            super.startPos = stream.getPos();
            this.annotation_off = stream.Dex_uint();
            super.length = stream.getPos() - super.startPos;

            dexFile.parseData(this.annotation_off.value, (in, _) -> new annotation_item(in), stream);
        }
    }

    /// An annotation, with its visibility.
    public static final class annotation_item extends FileComponent implements GenerateTreeNodeDexFile {

        /// Intended visibility of this annotation, see [Visibility].
        public final Type_ubyte visibility;
        /// Encoded annotation contents.
        public final encoded_annotation annotation;

        annotation_item(PosDataInputStreamDex stream) throws IOException {
            super.startPos = stream.getPos();
            this.visibility = stream.Dex_ubyte();
            this.annotation = new encoded_annotation(stream);
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
            DexFile dexFile = (DexFile) format;
            int floatPos = super.startPos;
            addNode(parentNode,
                    floatPos,
                    Type_ubyte.LENGTH,
                    "visibility",
                    FORMAT_STRING_STRING.formatted(this.visibility, Visibility.toString(this.visibility.value)),
                    "msg_annotation_item__visibility",
                    Icons.AccessFlag
            );
            floatPos += Type_ubyte.LENGTH;

            DefaultMutableTreeNode annoNode = addNode(parentNode,
                    floatPos,
                    this.annotation.getLength(),
                    "annotation",
                    this.annotation.toString(dexFile),
                    "msg_annotation_item__annotation",
                    Icons.Annotations
            );
            this.annotation.generateTreeNode(annoNode, dexFile);
        }

        /// The visibility values of an annotation.
        public enum Visibility {
            /// Intended only to be visible at build time, e.g. during compilation of other code.
            VISIBILITY_BUILD(0x00, "msg_annotation_item__visibility_build"),
            /// Intended to visible at runtime.
            VISIBILITY_RUNTIME(0x01, "msg_annotation_item__visibility_runtime"),
            /// Intended to visible at runtime, but only to the underlying system, not to regular user code.
            VISIBILITY_SYSTEM(0x02, "msg_annotation_item__visibility_system");

            /// Value stored in the file.
            public final int value;
            /// Description of the visibility.
            public final String description;

            Visibility(int v, String d) {
                this.value = v;
                this.description = MESSAGES.getString(d);
            }

            @Override
            public String toString() {
                return "%d - %s".formatted(this.value, this.description);
            }

            /// Gets the text of a visibility value.
            ///
            /// @param v The visibility value
            /// @return The visibility text, e.g. `1 - Runtime`
            public static String toString(int v) {
                return Arrays.stream(values())
                        .filter(item -> item.value == v)
                        .findFirst()
                        .map(Visibility::toString)
                        .orElseGet(() -> "%d - %s".formatted(v, MESSAGES.getString("msg_common_unrecognized")));
            }
        }
    }
}
