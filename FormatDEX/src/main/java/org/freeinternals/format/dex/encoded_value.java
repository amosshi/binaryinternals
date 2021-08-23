/*
 * encoded_value.java    Aug 22, 2021, 16:56
 *
 * Copyright 2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.UITool;
import static org.freeinternals.format.dex.GenerateTreeNodeDexFile.FORMAT_STRING_STRING;
import static org.freeinternals.format.dex.JTreeDexFile.addNode;

/**
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public class encoded_value extends FileComponent implements GenerateTreeNodeDexFile {

    /**
     * low-order five (5) bits as value_type.
     */
    public final int value_type;
    /**
     * high-order three (3) bits as value_arg.
     */
    public final int value_arg;

    public final Type_ubyte union_value_byte;

    encoded_value(PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        Type_ubyte valueArgType = stream.Dex_ubyte();

        // In the logic bellow we assume Java int type is still a 32-bit number
        // This should be truth in know future of Java programming language
        this.value_type = (valueArgType.value & 0x0000001F);
        this.value_arg = (valueArgType.value & 0x000000E0) >> 5;

        ValueFormat format = ValueFormat.valueOf(this.value_type);
        if (format == null) {
            throw new FileFormatException(String.format("Unrecognized value_type (%d) at location 0x%08X", this.value_type, stream.getPos()));
        }

        if (format == ValueFormat.VALUE_BYTE) {
            this.union_value_byte = stream.Dex_ubyte();
        } else {
            this.union_value_byte = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    @SuppressWarnings("java:S2259")
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile arg1) {
        int floatPos = super.startPos;

        ValueFormat format = ValueFormat.valueOf(this.value_type);
        addNode(parentNode,
                floatPos,
                Type_ubyte.LENGTH,
                "value_type",
                String.format(FORMAT_STRING_STRING, this.value_type, format.name()),
                "msg_encoded_value__value_type",
                UITool.icon4Data() // TODO to be changed
        );
        addNode(parentNode,
                floatPos,
                Type_ubyte.LENGTH,
                "value_arg",
                this.value_arg,
                "msg_encoded_value__value_type",
                UITool.icon4Data() // TODO to be changed
        );
        floatPos += Type_ubyte.LENGTH;

        if (this.union_value_byte != null) {
            addNode(parentNode,
                    floatPos,
                    Type_ubyte.LENGTH,
                    "value_arg",
                    this.value_arg,
                    "msg_encoded_value__value_byte",
                    UITool.icon4Data()
            );

        }

    }

    public enum ValueFormat {
        VALUE_BYTE(0x00, 0),
        VALUE_SHORT(0x02, 1),
        VALUE_CHAR(0x03, 1),
        VALUE_INT(0x04, 3),
        VALUE_LONG(0x06, 7),
        VALUE_FLOAT(0x10, 3),
        VALUE_DOUBLE(0x11, 7),
        VALUE_METHOD_TYPE(0x15, 3),
        VALUE_METHOD_HANDLE(0x16, 3),
        VALUE_STRING(0x17, 3),
        VALUE_TYPE(0x18, 3),
        VALUE_FIELD(0x19, 3),
        VALUE_METHOD(0x1a, 3),
        VALUE_ENUM(0x1b, 3),
        VALUE_ARRAY(0x1c, 0),
        VALUE_ANNOTATION(0x1d, 0),
        VALUE_NULL(0x1e, 0),
        VALUE_BOOLEAN(0x1f, 1);

        /**
         * Internal code of the value type.
         */
        public final int code;

        /**
         * Max allowed value for {@link encoded_value#value_arg}
         */
        public final int arg_max;

        private ValueFormat(int i, int m) {
            this.code = i;
            this.arg_max = m;
        }

        /**
         * Get enum item based on {@link #code}.
         *
         * @param value Value to search
         * @return matched {@link ValueFormat} item, else <code>null</code>
         */
        public static ValueFormat valueOf(int value) {
            for (ValueFormat item : values()) {
                if (item.code == value) {
                    return item;
                }
            }
            return null;
        }
    }
}
