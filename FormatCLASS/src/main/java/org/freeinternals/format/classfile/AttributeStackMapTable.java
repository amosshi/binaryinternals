/*
 * AttributeStackMapTable.java    10:47 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * A variable-length attribute in the {@code attributes} table of a {@code Code}
 * attribute.
 *
 * @author Amos Shi
 * @see <a
 * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.4">
 * VM Spec: The StackMapTable Attribute
 * </a>
 */
public class AttributeStackMapTable extends AttributeInfo {

    /**
     * Gives the number of {@link StackMapFrame} entries in the {@link #entries}
     * table.
     */
    public transient final u2 number_of_entries;
    /**
     * Each entry in the {@link #entries} table describes one stack map frame of
     * the method. The order of the stack map frames in the entries table is
     * significant.
     */
    public transient final StackMapFrame[] entries;

    AttributeStackMapTable(
            final u2 nameIndex,
            final String type,
            final PosDataInputStream posDataInputStream)
            throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        this.number_of_entries = new u2(posDataInputStream);
        if (this.number_of_entries.value > 0) {
            this.entries = new StackMapFrame[this.number_of_entries.value];
            for (int i = 0; i < this.number_of_entries.value; i++) {
                this.entries[i] = new StackMapFrame(posDataInputStream);
            }
        } else {
            this.entries = null;
        }

        super.checkSize(posDataInputStream.getPos());
    }

    public final static class StackMapFrame extends FileComponent {

        public final u1 frame_type;
        public final SameLocals1StackItemFrame union_same_locals_1_stack_item_frame;
        public final SameLocals1StackItemFrameExtended union_same_locals_1_stack_item_frame_extended;
        public final ChopFrame union_chop_frame;
        public final SameFrameExtended union_same_frame_extended;
        public final AppendFrame union_append_frame;
        public final FullFrame union_full_frame;

        private StackMapFrame(final PosDataInputStream posDataInputStream)
                throws IOException {
            super.startPos = posDataInputStream.getPos();

            this.frame_type = new u1(posDataInputStream, true);
            if (FrameTypeEnum.SAME.inRange(this.frame_type.value)) {
                this.union_same_locals_1_stack_item_frame = null;
                this.union_same_locals_1_stack_item_frame_extended = null;
                this.union_chop_frame = null;
                this.union_same_frame_extended = null;
                this.union_append_frame = null;
                this.union_full_frame = null;
            } else if (FrameTypeEnum.SAME_LOCALS_1_STACK_ITEM.inRange(this.frame_type.value)) {
                this.union_same_locals_1_stack_item_frame = new SameLocals1StackItemFrame(posDataInputStream);
                this.union_same_locals_1_stack_item_frame_extended = null;
                this.union_chop_frame = null;
                this.union_same_frame_extended = null;
                this.union_append_frame = null;
                this.union_full_frame = null;
            } else if (FrameTypeEnum.SAME_LOCALS_1_STACK_ITEM_EXTENDED.inRange(this.frame_type.value)) {
                this.union_same_locals_1_stack_item_frame = null;
                this.union_same_locals_1_stack_item_frame_extended = new SameLocals1StackItemFrameExtended(posDataInputStream);
                this.union_chop_frame = null;
                this.union_same_frame_extended = null;
                this.union_append_frame = null;
                this.union_full_frame = null;
            } else if (FrameTypeEnum.CHOP.inRange(this.frame_type.value)) {
                this.union_same_locals_1_stack_item_frame = null;
                this.union_same_locals_1_stack_item_frame_extended = null;
                this.union_chop_frame = new ChopFrame(posDataInputStream);
                this.union_same_frame_extended = null;
                this.union_append_frame = null;
                this.union_full_frame = null;
            } else if (FrameTypeEnum.SAME_FRAME_EXTENDED.inRange(this.frame_type.value)) {
                this.union_same_locals_1_stack_item_frame = null;
                this.union_same_locals_1_stack_item_frame_extended = null;
                this.union_chop_frame = null;
                this.union_same_frame_extended = new SameFrameExtended(posDataInputStream);
                this.union_append_frame = null;
                this.union_full_frame = null;
            } else if (FrameTypeEnum.APPEND.inRange(this.frame_type.value)) {
                this.union_same_locals_1_stack_item_frame = null;
                this.union_same_locals_1_stack_item_frame_extended = null;
                this.union_chop_frame = null;
                this.union_same_frame_extended = null;
                this.union_append_frame = new AppendFrame(posDataInputStream, this.frame_type.value);
                this.union_full_frame = null;
            } else if (FrameTypeEnum.FULL_FRAME.inRange(this.frame_type.value)) {
                this.union_same_locals_1_stack_item_frame = null;
                this.union_same_locals_1_stack_item_frame_extended = null;
                this.union_chop_frame = null;
                this.union_same_frame_extended = null;
                this.union_append_frame = null;
                this.union_full_frame = new FullFrame(posDataInputStream);
            } else {
                this.union_same_locals_1_stack_item_frame = null;
                this.union_same_locals_1_stack_item_frame_extended = null;
                this.union_chop_frame = null;
                this.union_same_frame_extended = null;
                this.union_append_frame = null;
                this.union_full_frame = null;
            }

            super.length = posDataInputStream.getPos() - super.startPos;
        }

        public final class SameLocals1StackItemFrame extends FileComponent {

            public final VerificationTypeInfo[] stack = new VerificationTypeInfo[1];

            private SameLocals1StackItemFrame(final PosDataInputStream posDataInputStream)
                    throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.stack[0] = new VerificationTypeInfo(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final class SameLocals1StackItemFrameExtended extends FileComponent {

            public final u2 offset_delta;
            public final VerificationTypeInfo[] stack = new VerificationTypeInfo[1];

            private SameLocals1StackItemFrameExtended(final PosDataInputStream posDataInputStream)
                    throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset_delta = new u2(posDataInputStream);
                this.stack[0] = new VerificationTypeInfo(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final class ChopFrame extends FileComponent {

            public final u2 offset_delta;

            private ChopFrame(final PosDataInputStream posDataInputStream)
                    throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset_delta = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final class SameFrameExtended extends FileComponent {

            public final u2 offset_delta;

            private SameFrameExtended(final PosDataInputStream posDataInputStream)
                    throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset_delta = new u2(posDataInputStream);
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final class AppendFrame extends FileComponent {

            public final u2 offset_delta;
            public final VerificationTypeInfo[] locals;

            private AppendFrame(final PosDataInputStream posDataInputStream, short frameType)
                    throws IOException {
                super.startPos = posDataInputStream.getPos();
                this.offset_delta = new u2(posDataInputStream);
                int size = frameType - 251;
                this.locals = new VerificationTypeInfo[size];
                for (int i = 0; i < size; i++) {
                    this.locals[i] = new VerificationTypeInfo(posDataInputStream);
                }
                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public final class FullFrame extends FileComponent {

            public final u2 offset_delta;
            public final u2 number_of_locals;
            public final VerificationTypeInfo[] locals;
            public final u2 number_of_stack_items;
            public final VerificationTypeInfo[] stack;

            private FullFrame(final PosDataInputStream posDataInputStream)
                    throws IOException {
                super.startPos = posDataInputStream.getPos();

                this.offset_delta = new u2(posDataInputStream);
                this.number_of_locals = new u2(posDataInputStream);
                if (this.number_of_locals.value > 0) {
                    this.locals = new VerificationTypeInfo[this.number_of_locals.value];
                    for (int i = 0; i < this.number_of_locals.value; i++) {
                        this.locals[i] = new VerificationTypeInfo(posDataInputStream);
                    }
                } else {
                    this.locals = null;
                }

                this.number_of_stack_items = new u2(posDataInputStream);
                if (this.number_of_stack_items.value > 0) {
                    this.stack = new VerificationTypeInfo[this.number_of_stack_items.value];
                    for (int i = 0; i < this.number_of_stack_items.value; i++) {
                        this.stack[i] = new VerificationTypeInfo(posDataInputStream);
                    }
                } else {
                    this.stack = null;
                }

                super.length = posDataInputStream.getPos() - super.startPos;
            }
        }

        public enum FrameTypeEnum {

            SAME(0, 63, "same_frame"),
            SAME_LOCALS_1_STACK_ITEM(64, 127, "same_locals_1_stack_item_frame"),
            SAME_LOCALS_1_STACK_ITEM_EXTENDED(247, 247, "same_locals_1_stack_item_frame_extended"),
            CHOP(248, 250, "chop_frame"),
            SAME_FRAME_EXTENDED(251, 251, "same_frame_extended"),
            APPEND(252, 254, "append_frame"),
            FULL_FRAME(255, 255, "full_frame");

            public final short low;
            public final short high;
            public final String unionName;
            public static final String UNIONNAME_UNRECOGNIZED = "Unrecognized union name";

            FrameTypeEnum(int l, int h, String name) {
                this.low = (short) l;
                this.high = (short) h;
                this.unionName = name;
            }

            /**
             * Check if the <code>value</code> is in the value range or not.
             *
             * @param value The test value
             * @return   <code>true</code> if it is in range, else
             * <code>false</code>
             */
            public boolean inRange(short value) {
                return value >= this.low && value <= this.high;
            }

            /**
             * Get the corresponding union name of the <code>value</code>.
             *
             * @param value The {@link StackMapFrame#frame_type} value
             * @return The corresponding {@link #unionName} of
             * <code>value</code>
             */
            public static String getUnionName(short value) {
                String result = UNIONNAME_UNRECOGNIZED;
                for (FrameTypeEnum item : FrameTypeEnum.values()) {
                    if (item.inRange(value)) {
                        result = item.unionName;
                        break;
                    }
                }

                return result;
            }
        }

    }

    public static final class VerificationTypeInfo extends FileComponent {

        public final u1 tag;
        public final ObjectVariableInfo union_Object_variable_info;
        public final UninitializedVariableInfo union_Uninitialized_variable_info;

        private VerificationTypeInfo(final PosDataInputStream posDataInputStream)
                throws IOException {
            super.startPos = posDataInputStream.getPos();

            this.tag = new u1(posDataInputStream, true);
            if (this.tag.value == TagEnum.ITEM_Object.value) {
                this.union_Object_variable_info = new ObjectVariableInfo(posDataInputStream);
                this.union_Uninitialized_variable_info = null;
            } else if (this.tag.value == TagEnum.ITEM_Uninitialized.value) {
                this.union_Object_variable_info = null;
                this.union_Uninitialized_variable_info = new UninitializedVariableInfo(posDataInputStream);
            } else {
                this.union_Object_variable_info = null;
                this.union_Uninitialized_variable_info = null;
            }

            super.length = posDataInputStream.getPos() - super.startPos;
        }

        public final class ObjectVariableInfo {

            public final u2 cpool_index;

            ObjectVariableInfo(final PosDataInputStream posDataInputStream) throws IOException {
                this.cpool_index = new u2(posDataInputStream);
            }
        }

        public final class UninitializedVariableInfo {

            public u2 offset;

            UninitializedVariableInfo(final PosDataInputStream posDataInputStream) throws IOException {
                this.offset = new u2(posDataInputStream);
            }
        }

        public enum TagEnum {

            ITEM_Top(0),
            ITEM_Integer(1),
            ITEM_Float(2),
            ITEM_Double(3),
            ITEM_Long(4),
            ITEM_Null(5),
            ITEM_UninitializedThis(6),
            ITEM_Object(7),
            ITEM_Uninitialized(8);

            public final short value;
            public static final String TAGNAME_UNRECOGNIZED = "Unrecognized";

            TagEnum(int value) {
                this.value = (short) value;
            }

            /**
             * Get get enum name of the value <code>v</code>.
             *
             * @param v Value of the tag
             * @return The corresponding enum name
             */
            public static String getTagName(short v) {
                String result = TAGNAME_UNRECOGNIZED;
                for (TagEnum e : TagEnum.values()) {
                    if (e.value == v) {
                        result = e.name();
                        break;
                    }
                }
                return result;
            }
        }
    }

}
