/*
 * AttributeStackMapTable.java    10:47 AM, April 28, 2014
 *
 * Copyright  2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import java.lang.classfile.attribute.StackMapFrameInfo.VerificationTypeInfo;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.GenerateTreeNodeClassFile;
import org.binaryinternals.format.classfile.u1;
import org.binaryinternals.format.classfile.u2;

/// A variable-length attribute in the `attributes` table of a `Code`
/// attribute.
///
/// @author Amos Shi
/// @since Java 6
/// @see <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.4">
/// VM Spec: The StackMapTable Attribute
/// </a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S101", "java:S116"})
public class StackMapTable_attribute extends attribute_info {

    private static final String FIELD_OFFSET_DELTA = "offset_delta";
    private static final String MSGKEY_OFFSET_DELTA= "msg_attr_stack_map_frame__all__offset_delta";
    private static final String MSGKEY_VTI = "msg_attr_verification_type_info";

    /// Gives the number of {@link stack_map_frame} entries in the
    /// {@link #entries} table.
    public final u2 number_of_entries;
    /// Each entry in the {@link #entries} table describes one stack map frame of
    /// the method. The order of the stack map frames in the entries table is
    /// significant.
    public final stack_map_frame[] entries;

    StackMapTable_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.number_of_entries = new u2(posDataInputStream);
        if (this.number_of_entries.value > 0) {
            this.entries = new stack_map_frame[this.number_of_entries.value];
            for (int i = 0; i < this.number_of_entries.value; i++) {
                this.entries[i] = new stack_map_frame(posDataInputStream);
            }
        } else {
            this.entries = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        this.addNode(parentNode,
                super.startPos + 6,
                2,
                "number_of_entries",
                this.number_of_entries.value,
                "msg_attr_StackMapTable__number_of_entries",
                Icons.Counter
        );

        if (this.number_of_entries.value > 0) {
            DefaultMutableTreeNode entriesNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.startPos + 8,
                    this.getLength() - 8,
                    "entries [%d]".formatted(this.number_of_entries.value),
                    MESSAGES.getString("msg_attr_StackMapTable__entries")
            ));
            parentNode.add(entriesNode);

            for (int i = 0; i < this.number_of_entries.value; i++) {
                DefaultMutableTreeNode entry = this.addNode(entriesNode,
                        this.entries[i].getStartPos(),
                        this.entries[i].getLength(),
                        "entry %d".formatted(i + 1),
                        this.entries[i].getFrameTypeName(),
                        this.entries[i].getFrameMessageKey(),
                        Icons.Stack
                );
                this.entries[i].generateTreeNode(entry, classFile);
            }
        }
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_StackMapTable";
    }

    public enum FrameTypeEnum {

        SAME(0, 63, "same_frame", "msg_attr_stack__same_frame"),
        SAME_LOCALS_1_STACK_ITEM(64, 127, "same_locals_1_stack_item_frame", "msg_attr_stack__same_locals_1_stack_item_frame"),
        /// Tags in the range [128-246] are reserved for future use.
        RESERVED(128, 246, "RESERVED", "msg_attr_stack__reserved"),
        SAME_LOCALS_1_STACK_ITEM_EXTENDED(247, 247, "same_locals_1_stack_item_frame_extended", "msg_attr_stack__same_locals_1_stack_item_frame_extended"),
        CHOP(248, 250, "chop_frame", "msg_attr_stack__chop_frame"),
        SAME_FRAME_EXTENDED(251, 251, "same_frame_extended", "msg_attr_stack__same_frame_extended"),
        APPEND(252, 254, "append_frame", "msg_attr_stack__append_frame"),
        FULL_FRAME(255, 255, "full_frame", "msg_attr_stack__full_frame");

        public final short low;
        public final short high;
        public final String unionName;
        public final String messageKey;

        /// The Java class representing to the frame.
        FrameTypeEnum(int l, int h, String name, String msgKey) {
            this.low = (short) l;
            this.high = (short) h;
            this.unionName = name;
            this.messageKey = msgKey;
        }

        /// Check if the `value` is in the value range or not.
        ///
        /// @param value The test value
        /// @return   `true` if it is in range, else `false`
        public boolean inRange(short value) {
            return value >= this.low && value <= this.high;
        }

        /// Get the corresponding enum item for `value`.
        ///
        /// @param value The {@link stack_map_frame#frame_type} value
        /// @return The corresponding {@link FrameTypeEnum} of `value`
        public static FrameTypeEnum valueOf(short value) {
            for (FrameTypeEnum item : FrameTypeEnum.values()) {
                if (item.inRange(value)) {
                    return item;
                }
            }

            throw new IllegalArgumentException("Unregnized enum value " + value + " for " + FrameTypeEnum.class.getName());
        }
    }

    public abstract static sealed class stack_frame extends FileComponent implements GenerateTreeNodeClassFile {
        protected stack_frame() {
        }

        /// Get the calculated `offset_delta`.
        public abstract int getOffsetDelta();
    }

    /// A stack map frame is represented by a discriminated union,
    /// `stack_map_frame`, which consists of a one-byte tag,
    /// indicating which item of the union is in use, followed by zero or more
    /// bytes, giving more information about the tag.
    ///
    /// ```
    /// union stack_map_frame {
    ///     same_frame;
    ///     same_locals_1_stack_item_frame;
    ///     same_locals_1_stack_item_frame_extended;
    ///     chop_frame;
    ///     same_frame_extended;
    ///     append_frame;
    ///     full_frame;
    /// }
    /// ```
    public static final class stack_map_frame extends FileComponent implements GenerateTreeNodeClassFile {
        public final u1 frame_type;

        /// The frame data after the {@link #frame_type}, or `null` for `same_frame`. It is one of:
        ///
        /// - {@link same_locals_1_stack_item_frame}
        /// - {@link same_locals_1_stack_item_frame_extended}
        /// - {@link chop_frame}
        /// - {@link same_frame_extended}
        /// - {@link append_frame}
        /// - {@link full_frame}
        public final stack_frame union_stack_frame;

        private stack_map_frame(final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
            super.startPos = posDataInputStream.getPos();

            this.frame_type = new u1(posDataInputStream);

            this.union_stack_frame = switch (FrameTypeEnum.valueOf(this.frame_type.value)) {
                case SAME, RESERVED -> null;
                case SAME_LOCALS_1_STACK_ITEM -> new same_locals_1_stack_item_frame(posDataInputStream);
                case SAME_LOCALS_1_STACK_ITEM_EXTENDED -> new same_locals_1_stack_item_frame_extended(posDataInputStream);
                case CHOP -> new chop_frame(posDataInputStream);
                case SAME_FRAME_EXTENDED -> new same_frame_extended(posDataInputStream);
                case APPEND -> new append_frame(posDataInputStream, this.frame_type.value);
                case FULL_FRAME -> new full_frame(posDataInputStream);
            };

            super.length = posDataInputStream.getPos() - super.startPos;
        }

        @SuppressWarnings("java:S3776") // java:S3776 - Cognitive Complexity of methods should not be too high --- No, it is not high
        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            this.addNode(parentNode,
                    this.getStartPos(),
                    u1.LENGTH,
                    "frame_type",
                    this.frame_type.value + " - " + this.getFrameTypeName(),
                    "msg_attr_stack_map_frame__frame_type",
                    Icons.Kind
            );

            if (this.union_stack_frame != null) {
                this.union_stack_frame.generateTreeNode(parentNode, fileFormat);
            }
        }

        /// Get message key for the corresponding {@link #union_stack_frame}.
        ///
        /// @return {@link #union_stack_frame} message key, or `null` if {@link #union_stack_frame} is null
        public String getFrameMessageKey () {
            return FrameTypeEnum.valueOf(this.frame_type.value).messageKey;
        }

        /// Get the union name of {@link #frame_type}.
        ///
        /// @return {@link #frame_type} union name
        public String getFrameTypeName() {
            return FrameTypeEnum.valueOf(this.frame_type.value).unionName;
        }

        /// Get the calculated `offset_delta`.
        public int getOffsetDelta() {
            final FrameTypeEnum type = FrameTypeEnum.valueOf(this.frame_type.value);
            return switch (type) {
                case SAME -> this.frame_type.value;
                case SAME_LOCALS_1_STACK_ITEM -> this.frame_type.value - 64;
                case SAME_LOCALS_1_STACK_ITEM_EXTENDED, CHOP, SAME_FRAME_EXTENDED, APPEND, FULL_FRAME -> this.union_stack_frame.getOffsetDelta();
                case RESERVED -> throw new UnsupportedOperationException("Unsupported offset_value for frame type: " + type.name());
            };
        }

        /// The frame type `same_locals_1_stack_item_frame` is
        /// represented by tags in the range `[64, 127]`. This frame
        /// type indicates that the frame has exactly the same local variables as
        /// the previous frame and that the operand stack has one entry.
        ///
        /// ```
        /// same_locals_1_stack_item_frame {
        ///     u1 frame_type = SAME_LOCALS_1_STACK_ITEM;   // 64-127
        ///     verification_type_info stack[1];
        /// }
        /// ```
        public static final class same_locals_1_stack_item_frame extends stack_frame {

            public final verification_type_info[] stack = new verification_type_info[1];

            same_locals_1_stack_item_frame(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.stack[0] = new verification_type_info(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                int startPosMoving = this.getStartPos();
                DefaultMutableTreeNode stacksNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.stack[0].getLength(),
                        "stack[1]"
                ));
                parentNode.add(stacksNode);

                DefaultMutableTreeNode stackNode = this.addNode(stacksNode,
                        this.stack[0].getStartPos(),
                        this.stack[0].getLength(),
                        "stack 1",
                        this.stack[0].getTagName(),
                        MSGKEY_VTI,
                        Icons.Verification
                );
                this.stack[0].generateTreeNode(stackNode, fileFormat);
            }

            @Override
            public int getOffsetDelta() {
                throw new UnsupportedOperationException("Not supported for " + this.getClass().getName());
            }
        }

        /// The frame type `same_locals_1_stack_item_frame_extended`
        /// is represented by the tag `247`. This frame type indicates
        /// that the frame has exactly the same local variables as the previous
        /// frame and that the operand stack has one entry.
        ///
        /// ```
        /// same_locals_1_stack_item_frame_extended {
        ///     u1 frame_type = SAME_LOCALS_1_STACK_ITEM_EXTENDED;   // 247
        ///     u2 offset_delta;
        ///     verification_type_info stack[1];
        /// }
        /// ```
        public static final class same_locals_1_stack_item_frame_extended extends stack_frame {

            public final u2 offset_delta;
            public final verification_type_info[] stack = new verification_type_info[1];

            same_locals_1_stack_item_frame_extended(final PosDataInputStream posDataInputStream)
                    throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset_delta = new u2(posDataInputStream);
                this.stack[0] = new verification_type_info(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                int startPosMoving = this.getStartPos();

                this.addNode(parentNode,
                        startPosMoving, u2.LENGTH,
                        FIELD_OFFSET_DELTA, this.offset_delta.value,
                        MSGKEY_OFFSET_DELTA, Icons.Offset
                );
                startPosMoving += u2.LENGTH;

                DefaultMutableTreeNode stacksNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        startPosMoving,
                        this.stack[0].getLength(),
                        "stack[1]"
                ));
                parentNode.add(stacksNode);

                DefaultMutableTreeNode stackNode = this.addNode(stacksNode,
                        this.stack[0].getStartPos(),
                        this.stack[0].getLength(),
                        "stack 1",
                        this.stack[0].getTagName(),
                        MSGKEY_VTI,
                        Icons.Verification
                );
                this.stack[0].generateTreeNode(stackNode, fileFormat);
            }

            @Override
            public int getOffsetDelta() {
                return this.offset_delta.value;
            }
        }

        /// The frame type `chop_frame` is represented by tags in the
        /// range `[248-250]`. This frame type indicates that the
        /// frame has the same local variables as the previous frame except that
        /// the last k local variables are absent, and that the operand stack is
        /// empty.
        ///
        /// ```
        /// chop_frame {
        ///     u1 frame_type = CHOP;   // 248-250
        ///     u2 offset_delta;
        /// }
        /// ```
        public static final class chop_frame extends stack_frame {

            public final u2 offset_delta;

            chop_frame(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset_delta = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                this.addNode(parentNode,
                        this.getStartPos(), u2.LENGTH,
                        FIELD_OFFSET_DELTA, this.offset_delta.value,
                        MSGKEY_OFFSET_DELTA, Icons.Offset
                );
            }

            @Override
            public int getOffsetDelta() {
                return this.offset_delta.value;
            }
        }

        /// The frame type `same_frame_extended` is represented by the
        /// tag `251`. This frame type indicates that the frame has
        /// exactly the same local variables as the previous frame and that the
        /// operand stack is empty.
        ///
        /// ```
        /// same_frame_extended {
        ///     u1 frame_type = SAME_FRAME_EXTENDED;   // 251
        ///     u2 offset_delta;
        /// }
        /// ```
        public static final class same_frame_extended extends stack_frame {

            public final u2 offset_delta;

            same_frame_extended(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset_delta = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                this.addNode(parentNode,
                        this.getStartPos(), u2.LENGTH,
                        FIELD_OFFSET_DELTA, this.offset_delta.value,
                        MSGKEY_OFFSET_DELTA, Icons.Offset
                );
            }

            @Override
            public int getOffsetDelta() {
                return this.offset_delta.value;
            }
        }

        /// The frame type `append_frame` is represented by tags in
        /// the range `[252-254]`. This frame type indicates that the
        /// frame has the same locals as the previous frame except that k
        /// additional locals are defined, and that the operand stack is empty.
        ///
        /// ```
        /// append_frame {
        ///     u1 frame_type = APPEND;   // 252-254
        ///     u2 offset_delta;
        ///     verification_type_info locals[frame_type - 251];
        /// }
        /// ```
        public static final class append_frame extends stack_frame {

            public final u2 offset_delta;
            public final verification_type_info[] locals;

            append_frame(final PosDataInputStream posDataInputStream, short frameType) throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset_delta = new u2(posDataInputStream);
                int size = frameType - 251;
                this.locals = new verification_type_info[size];
                for (int i = 0; i < size; i++) {
                    this.locals[i] = new verification_type_info(posDataInputStream);
                }
                super.length = posDataInputStream.getPos() - super.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                int startPosMoving = this.getStartPos();

                this.addNode(parentNode,
                        startPosMoving, u2.LENGTH,
                        FIELD_OFFSET_DELTA, this.offset_delta.value,
                        MSGKEY_OFFSET_DELTA, Icons.Offset
                );
                startPosMoving += u2.LENGTH;

                int sizeLocals = 0;
                if (this.locals.length > 0) {
                    for (verification_type_info local : this.locals) {
                        sizeLocals += local.getLength();
                    }
                    DefaultMutableTreeNode localsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            sizeLocals,
                            "locals [%d]".formatted(this.locals.length),
                            MESSAGES.getString("msg_attr_stack__append_frame__locals")
                    ));
                    parentNode.add(localsNode);

                    for (int i = 0; i < this.locals.length; i++) {
                        DefaultMutableTreeNode localNode = this.addNode(localsNode,
                                this.locals[i].getStartPos(),
                                this.locals[i].getLength(),
                                "local " + (i + 1),
                                this.locals[i].getTagName(),
                                MSGKEY_VTI,
                                Icons.Verification
                        );
                        this.locals[i].generateTreeNode(localNode, fileFormat);
                    }
                }
            }

            @Override
            public int getOffsetDelta() {
                return this.offset_delta.value;
            }
        }

        /// The frame type `full_frame` is represented by the tag
        /// `255`.
        ///
        /// ```
        /// full_frame {
        ///     u1 frame_type = FULL_FRAME;    // 255
        ///     u2 offset_delta;
        ///     u2 number_of_locals;
        ///     verification_type_info locals[number_of_locals];
        ///     u2 number_of_stack_items;
        ///     verification_type_info stack[number_of_stack_items];
        /// }
        /// ```
        public static final class full_frame extends stack_frame {

            public final u2 offset_delta;
            public final u2 number_of_locals;
            public final verification_type_info[] locals;
            public final u2 number_of_stack_items;
            public final verification_type_info[] stack;

            full_frame(final PosDataInputStream posDataInputStream) throws IOException {
                super.startPos = posDataInputStream.getPos();

                this.offset_delta = new u2(posDataInputStream);
                this.number_of_locals = new u2(posDataInputStream);
                if (this.number_of_locals.value > 0) {
                    this.locals = new verification_type_info[this.number_of_locals.value];
                    for (int i = 0; i < this.number_of_locals.value; i++) {
                        this.locals[i] = new verification_type_info(posDataInputStream);
                    }
                } else {
                    this.locals = null;
                }

                this.number_of_stack_items = new u2(posDataInputStream);
                if (this.number_of_stack_items.value > 0) {
                    this.stack = new verification_type_info[this.number_of_stack_items.value];
                    for (int i = 0; i < this.number_of_stack_items.value; i++) {
                        this.stack[i] = new verification_type_info(posDataInputStream);
                    }
                } else {
                    this.stack = null;
                }

                super.length = posDataInputStream.getPos() - super.startPos;
            }

            @Override
            public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
                int startPosMoving = this.getStartPos();

                this.addNode(parentNode,
                        startPosMoving, u2.LENGTH,
                        FIELD_OFFSET_DELTA, this.offset_delta.value,
                        MSGKEY_OFFSET_DELTA, Icons.Offset
                );
                startPosMoving += u2.LENGTH;

                this.addNode(parentNode,
                        startPosMoving, u2.LENGTH,
                        "number_of_locals", this.number_of_locals.value,
                        "msg_attr_stack__full_frame__number_of_locals", Icons.Counter
                );
                startPosMoving += u2.LENGTH;

                int sizeLocals = 0;
                if (this.number_of_locals.value > 0) {
                    for (int i = 0; i < this.number_of_locals.value; i++) {
                        sizeLocals += this.locals[i].getLength();
                    }
                    DefaultMutableTreeNode localsNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            sizeLocals,
                            "locals [%d]".formatted(this.number_of_locals.value),
                            MESSAGES.getString("msg_attr_stack__full_frame__locals")
                    ));
                    startPosMoving += sizeLocals;
                    parentNode.add(localsNode);

                    for (int i = 0; i < this.locals.length; i++) {
                        DefaultMutableTreeNode localNode = this.addNode(localsNode,
                                this.locals[i].getStartPos(),
                                this.locals[i].getLength(),
                                "local " + (i + 1),
                                this.locals[i].getTagName(),
                                MSGKEY_VTI,
                                Icons.Verification
                        );
                        this.locals[i].generateTreeNode(localNode, fileFormat);
                    }
                }

                this.addNode(parentNode,
                        startPosMoving, u2.LENGTH,
                        "number_of_stack_items", this.number_of_stack_items.value,
                        "msg_attr_stack__full_frame__number_of_stack_items", Icons.Counter
                );
                startPosMoving += u2.LENGTH;

                int sizeStack = 0;
                if (this.number_of_stack_items.value > 0) {
                    for (int i = 0; i < this.number_of_stack_items.value; i++) {
                        sizeStack += this.stack[i].getLength();
                    }

                    DefaultMutableTreeNode stacksNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            startPosMoving,
                            sizeStack,
                            "stack [%d]".formatted(this.number_of_stack_items.value),
                            MESSAGES.getString("msg_attr_stack__full_frame__stack")
                    ));
                    parentNode.add(stacksNode);

                    for (int i = 0; i < this.stack.length; i++) {
                        DefaultMutableTreeNode stackNode = this.addNode(stacksNode,
                                this.stack[i].getStartPos(),
                                this.stack[i].getLength(),
                                "stack " + (i + 1),
                                this.stack[i].getTagName(),
                                MSGKEY_VTI,
                                Icons.Verification
                        );
                        this.stack[i].generateTreeNode(stackNode, fileFormat);
                    }
                }
            }

            @Override
            public int getOffsetDelta() {
                return this.offset_delta.value;
            }
        }
    }

    public static final class verification_type_info extends FileComponent implements GenerateTreeNodeClassFile {

        public final u1 tag;
        /// The `*_variable_info` item of the union, determined by
        /// the {@link #tag}.
        public final VariableInfo variable_info;

        private verification_type_info(final PosDataInputStream posDataInputStream)
                throws IOException {
            super.startPos = posDataInputStream.getPos();

            this.tag = new u1(posDataInputStream);
            this.variable_info = switch (TagEnum.of(this.tag.value).orElse(null)) {
                case ITEM_Object -> new Object_variable_info(new u2(posDataInputStream));
                case ITEM_Uninitialized -> new Uninitialized_variable_info(new u2(posDataInputStream));
                case null, default -> new Simple_variable_info();
            };

            super.length = posDataInputStream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
            ClassFile classFile = (ClassFile) fileFormat;
            final int unionPos = this.getStartPos() + 1;

            this.addNode(parentNode,
                    this.getStartPos(),
                    1,
                    "tag",
                    this.tag.value + " - " + this.getTagName(),
                    "msg_attr_verification_type_info__tag",
                    Icons.Tag
            );

            switch (this.variable_info) {
                case Object_variable_info(u2 cpoolIndex) -> this.addNode(parentNode,
                        unionPos,
                        u2.LENGTH,
                        "cpool_index",
                        cpoolIndex.value + " - " + classFile.getCPDescription(cpoolIndex.value),
                        "msg_attr_verification_type_info__cpool_index",
                        Icons.Index
                );
                case Uninitialized_variable_info(u2 offset) -> this.addNode(parentNode,
                        unionPos,
                        u2.LENGTH,
                        "offset",
                        offset.value,
                        "msg_attr_verification_type_info__offset",
                        Icons.Offset
                );
                case Simple_variable_info() -> {
                    // Only the tag
                }
            }
        }

        /// Get name of {@link #tag}.
        ///
        /// @return {@link #tag} name
        public String getTagName() {
            return verification_type_info.TagEnum.getTagName(this.tag.value);
        }

        /// The `*_variable_info` union of a
        /// {@link verification_type_info}.
        public sealed interface VariableInfo permits Simple_variable_info, Object_variable_info, Uninitialized_variable_info {
        }

        /// The variable info which has the {@link #tag} only, for example
        /// `Top_variable_info` and `Integer_variable_info`.
        public record Simple_variable_info() implements VariableInfo {
        }

        /// The `Object_variable_info` item.
        ///
        /// @param cpool_index Index of a `CONSTANT_Class_info` in the
        /// constant pool
        public record Object_variable_info(u2 cpool_index) implements VariableInfo {
        }

        /// The `Uninitialized_variable_info` item.
        ///
        /// @param offset Offset of the `new` instruction that created the
        /// object
        public record Uninitialized_variable_info(u2 offset) implements VariableInfo {
        }

        // java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
        @SuppressWarnings("java:S115")
        public enum TagEnum {

            ITEM_Top(VerificationTypeInfo.ITEM_TOP),
            ITEM_Integer(VerificationTypeInfo.ITEM_INTEGER),
            ITEM_Float(VerificationTypeInfo.ITEM_FLOAT),
            ITEM_Double(VerificationTypeInfo.ITEM_DOUBLE),
            ITEM_Long(VerificationTypeInfo.ITEM_LONG),
            ITEM_Null(VerificationTypeInfo.ITEM_NULL),
            ITEM_UninitializedThis(VerificationTypeInfo.ITEM_UNINITIALIZED_THIS),
            ITEM_Object(VerificationTypeInfo.ITEM_OBJECT),
            ITEM_Uninitialized(VerificationTypeInfo.ITEM_UNINITIALIZED);

            public final short value;
            public static final String TAGNAME_UNRECOGNIZED = "Unrecognized";

            TagEnum(int value) {
                this.value = (short) value;
            }

            /// Get get enum name of the value `v`.
            ///
            /// @param v Value of the tag
            /// @return The corresponding enum name
            public static String getTagName(short v) {
                return of(v).map(TagEnum::name).orElse(TAGNAME_UNRECOGNIZED);
            }

            /// Get the enum of the value `v`.
            ///
            /// @param v Value of the tag
            /// @return The corresponding enum, or empty if it is not recognized
            public static Optional<TagEnum> of(short v) {
                return Arrays.stream(values()).filter(e -> e.value == v).findFirst();
            }
        }
    }

}
