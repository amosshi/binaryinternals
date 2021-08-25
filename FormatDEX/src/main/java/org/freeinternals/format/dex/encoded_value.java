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
import static org.freeinternals.format.dex.JTreeDexFile.addNode;

/**
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S100 - Method names should comply with a naming convention --- We respect the name from DEX spec instead
 * java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S100", "java:S101", "java:S116", "java:S1104"})
public class encoded_value extends FileComponent implements GenerateTreeNodeDexFile {

    public final Type_ubyte value_arg_and_type;

    /**
     * low-order five (5) bits as value_type.
     */
    public final int value_type;
    /**
     * high-order three (3) bits as value_arg.
     */
    public final int value_arg;

    private Type_ubyte union_value_byte;
    private Type_short union_value_short;
    private Type_ushort union_value_char;
    private Type_int union_value_int;
    private Type_long union_value_long;
    private Float union_value_float;
    private Double union_value_double;
    private Type_uint union_value_method_type;
    private Type_uint union_value_method_handle;
    private Type_uint union_value_string;
    private Type_uint union_value_type;
    private Type_uint union_value_field;
    private Type_uint union_value_method;
    private Type_uint union_value_enum;
    private encoded_array union_value_array;
    private encoded_annotation union_value_annotation;
    private Boolean union_value_bollean;

    encoded_value(final PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        value_arg_and_type  = stream.Dex_ubyte();

        // In the logic bellow we assume Java int type is still a 32-bit number
        // This should be truth in know future of Java programming language
        this.value_type = (value_arg_and_type.value & 0x0000001F);
        this.value_arg = (value_arg_and_type.value & 0x000000E0) >> 5;

        ValueFormat format = ValueFormat.valueOf(this.value_type);
        if (format == null) {
            throw new FileFormatException(String.format("Unrecognized value_type (%d) at location 0x%08X", this.value_type, stream.getPos()));
        }

        switch (format) {
            case VALUE_BYTE:
                this.union_value_byte = stream.Dex_ubyte();
                break;
            case VALUE_SHORT:
                this.union_value_short = this.read_short(stream, this.value_arg);
                break;
            case VALUE_CHAR:
                this.union_value_char = this.read_char(stream, this.value_arg);
                break;
            case VALUE_INT:
                this.union_value_int = this.read_int(stream, this.value_arg);
                break;
            case VALUE_LONG:
                // TODO FIX
                if (this.value_arg != 7) {
                    System.out.println(this.getClass().getSimpleName() + " VALUE_LONG value_arg is not 7 but " + this.value_arg + " - to be parsed");
                }
                this.union_value_long = stream.Dex_long();
                break;
            case VALUE_FLOAT:
                // TODO FIX
                if (this.value_arg != 3) {
                    System.out.println(this.getClass().getSimpleName() + " VALUE_FLOAT value_arg is not 3 but " + this.value_arg + " - to be parsed");
                }
                this.union_value_float = stream.readFloat();
                break;
            case VALUE_DOUBLE:
                // TODO FIX
                if (this.value_arg != 7) {
                    System.out.println(this.getClass().getSimpleName() + " VALUE_DOUBLE value_arg is not 7 but " + this.value_arg + " - to be parsed");
                }
                this.union_value_double = stream.readDouble();
                break;
            case VALUE_METHOD_TYPE:
                this.union_value_method_type = this.read_uint(stream, this.value_arg);
                break;
            case VALUE_METHOD_HANDLE:
                this.union_value_method_handle = this.read_uint(stream, this.value_arg);
                break;
            case VALUE_STRING:
                this.union_value_string = this.read_uint(stream, this.value_arg);
                break;
            case VALUE_TYPE:
                this.union_value_type = this.read_uint(stream, this.value_arg);
                break;
            case VALUE_FIELD:
                this.union_value_field = this.read_uint(stream, this.value_arg);
                break;
            case VALUE_METHOD:
                this.union_value_method = this.read_uint(stream, this.value_arg);
                break;
            case VALUE_ENUM:
                this.union_value_enum = this.read_uint(stream, this.value_arg);
                break;
            case VALUE_ARRAY:
                this.union_value_array = new encoded_array(stream);
                break;
            case VALUE_ANNOTATION:
                this.union_value_annotation = new encoded_annotation(stream);
                break;
            case VALUE_BOOLEAN:
                this.union_value_bollean = (this.value_arg == 1);
                break;
            case VALUE_NULL:
            default:
                this.union_value_byte = null;
                this.union_value_short = null;
                this.union_value_char = null;
                this.union_value_int = null;
                this.union_value_long = null;
                this.union_value_float = null;
                this.union_value_double = null;
                this.union_value_method_type = null;
                this.union_value_method_handle = null;
                this.union_value_string = null;
                this.union_value_type = null;
                this.union_value_field = null;
                this.union_value_method = null;
                this.union_value_enum = null;
                this.union_value_array = null;
                this.union_value_annotation = null;
                this.union_value_bollean = null;
                break;
        }

        super.length = stream.getPos() - super.startPos;
    }

    public Type_ubyte get_value_byte() {
        return this.union_value_byte;
    }

    public Type_short get_value_short() {
        return this.union_value_short;
    }
    public Type_ushort get_value_char() {
        return this.union_value_char;
    }
    public Type_int get_value_int() {
        return this.union_value_int;
    }
    public Type_long get_value_long() {
        return this.union_value_long;
    }
    public Float get_value_float() {
        return this.union_value_float;
    }

    public Double get_value_double() {
        return this.union_value_double;
    }
    public Type_uint get_value_method_type() {
        return this.union_value_method_type;
    }
    public Type_uint get_value_method_handle() {
        return this.union_value_method_handle;
    }
    public Type_uint get_value_string() {
        return this.union_value_string;
    }
    public Type_uint get_value_type() {
        return this.union_value_type;
    }
    public Type_uint get_value_field() {
        return this.union_value_field;
    }
    public Type_uint get_value_method() {
        return this.union_value_method;
    }
    public Type_uint get_value_enum() {
        return this.union_value_enum;
    }
    public encoded_array get_value_array() {
        return this.union_value_array;
    }
    public encoded_annotation get_value_annotation() {
        return this.union_value_annotation;
    }
    public Boolean get_value_bollean() {
        return this.union_value_bollean;
    }

    private Type_short read_short(final PosDataInputStreamDex stream, final int arg) throws FileFormatException, IOException{
        Type_short result = null;
        switch (arg) {
            case 0:
                result = new Type_short(stream.Dex_byte().value);
                break;
            case 1:
                result = stream.Dex_short();
                break;
            default:
                throw new FileFormatException(String.format("Unrecognized value_arg (%d) for short in %s at location 0x%08X",
                        arg,
                        this.getClass().getSimpleName(),
                        stream.getPos())
                );
        }

        return result;
    }

    private Type_ushort read_char(final PosDataInputStreamDex stream, final int arg) throws FileFormatException, IOException{
        Type_ushort result = null;
        switch (arg) {
            case 0:
                result = new Type_ushort(stream.Dex_ubyte().value);
                break;
            case 1:
                result = stream.Dex_ushort();
                break;
            default:
                throw new FileFormatException(String.format("Unrecognized value_arg (%d) for char/ushort in %s at location 0x%08X",
                        arg,
                        this.getClass().getSimpleName(),
                        stream.getPos())
                );
        }

        return result;

    }

    private Type_int read_int(final PosDataInputStreamDex stream, final int arg) throws FileFormatException, IOException{
        Type_int result = null;
        switch (arg) {
            case 0:
                result = new Type_int(stream.Dex_byte().value);
                break;
            case 1:
                result = new Type_int(stream.Dex_short().value);
                break;
            case 2:
                // todo
                System.out.println(this.getClass().getSimpleName() + " Unhandled case encountered: triple bytes int");
                result = null;
                break;
            case 3:
                result = stream.Dex_int();
                break;
            default:
                throw new FileFormatException(String.format("Unrecognized value_arg (%d) in %s for int at location 0x%08X",
                        arg,
                        this.getClass().getSimpleName(),
                        stream.getPos())
                );
        }

        return result;
    }

    private Type_uint read_uint(final PosDataInputStreamDex stream, final int arg) throws FileFormatException, IOException{
        Type_uint result = null;
        switch (arg) {
            case 0:
                result = new Type_uint(stream.Dex_ubyte().value);
                break;
            case 1:
                result = new Type_uint(stream.Dex_ushort().value);
                break;
            case 2:
                // todo
                System.out.println(this.getClass().getSimpleName() + " Unhandled case encountered: triple bytes uint");
                result = null;
                break;
            case 3:
                result = stream.Dex_uint();
                break;
            default:
                throw new FileFormatException(String.format("Unrecognized value_arg (%d) in %s for uint at location 0x%08X",
                        arg,
                        this.getClass().getSimpleName(),
                        stream.getPos())
                );
        }

        return result;
    }

    @Override
    @SuppressWarnings({"java:S2259"})
    public String toString() {
        return String.format("%s value_type=%d | %s value_arg=%d",
                this.getClass().getSimpleName(),
                this.value_type,
                ValueFormat.valueOf(this.value_type).name(),
                this.value_arg);
    }

    @Override
    @SuppressWarnings({"java:S2259", "java:S3776"})
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dex) {
        int floatPos = super.startPos;

        ValueFormat format = ValueFormat.valueOf(this.value_type);
        addNode(parentNode,
                floatPos,
                Type_ubyte.LENGTH,
                "value_arg + value_type",
                String.format("0x%s | %s", Integer.toHexString(this.value_arg_and_type.value), Integer.toBinaryString(this.value_arg_and_type.value)),
                "msg_encoded_value__value_type",
                UITool.icon4Tag()
        );
        addNode(parentNode,
                floatPos,
                Type_ubyte.LENGTH,
                "value_type",
                String.format("%d | 0x%s - %s", this.value_type, Integer.toHexString(this.value_type), format.name()),
                "msg_encoded_value__value_type",
                UITool.icon4Tag()
        );
        addNode(parentNode,
                floatPos,
                Type_ubyte.LENGTH,
                "value_arg",
                String.format("decimal %d | binary %s", this.value_arg, Integer.toBinaryString(this.value_arg)),
                "msg_encoded_value__value_arg",
                UITool.icon4Parameter()
        );
        floatPos += Type_ubyte.LENGTH;

        if (this.union_value_byte != null) {
            addNode(parentNode, floatPos, Type_ubyte.LENGTH, "VALUE_BYTE", this.union_value_byte, "msg_encoded_value__value_byte", UITool.icon4Data());
        } else if (this.union_value_short != null) {
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_SHORT", this.union_value_short, "msg_encoded_value__value_short", UITool.icon4Data());
        } else if (this.union_value_char != null) {
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_CHAR", this.union_value_char, "msg_encoded_value__value_char", UITool.icon4Data());
        } else if (this.union_value_int != null) {
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_INT", this.union_value_int, "msg_encoded_value__value_int", UITool.icon4Data());
        } else if (this.union_value_long != null) {
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_LONG", this.union_value_long, "msg_encoded_value__value_long", UITool.icon4Data());
        } else if (this.union_value_float != null) {
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_FLOAT", this.union_value_float, "msg_encoded_value__value_float", UITool.icon4Data());
        } else if (this.union_value_double != null) {
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_DOUBLE", this.union_value_double, "msg_encoded_value__value_double", UITool.icon4Data());
        } else if (this.union_value_method_type != null) {
            // TODO for value
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_METHOD_TYPE", this.union_value_method_type, "msg_encoded_value__value_method_type", UITool.icon4Data());
        } else if (this.union_value_method_handle != null) {
            // TODO for value
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_METHOD_HANDLE", this.union_value_method_handle, "msg_encoded_value__value_method_handle", UITool.icon4Data());
        } else if (this.union_value_string != null) {
            addNode(parentNode,
                    floatPos,
                    this.value_arg + 1,
                    "VALUE_STRING",
                    String.format(FORMAT_STRING_STRING, this.union_value_string, dex.get_string_ids_string(this.union_value_string.intValue())),
                    "msg_encoded_value__value_string",
                    UITool.icon4Data());
        } else if (this.union_value_type != null) {
            // TODO for value
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_TYPE", this.union_value_type, "msg_encoded_value__type", UITool.icon4Data());
        } else if (this.union_value_field != null) {
            // TODO for value
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_FIELD", this.union_value_field, "msg_encoded_value__value_field", UITool.icon4Data());
        } else if (this.union_value_method != null) {
            // TODO for value
            addNode(parentNode, floatPos, this.value_arg + 1, "VALUE_METHOD", this.union_value_method, "msg_encoded_value__value_method", UITool.icon4Data());
        } else if (this.union_value_enum != null) {
            field_id_item fieldId = dex.field_ids[this.union_value_enum.intValue()];
            addNode(parentNode,
                    floatPos,
                    this.value_arg + 1,
                    "VALUE_ENUM",
                    String.format(FORMAT_STRING_STRING, this.union_value_enum, (fieldId == null) ? "null (should not happen)" : fieldId.toString(dex)),
                    "msg_encoded_value__value_enum",
                    UITool.icon4Data());
        } else if (this.union_value_array != null) {
            DefaultMutableTreeNode arrayNode = addNode(parentNode,
                    floatPos, this.union_value_array.getLength(), "VALUE_ARRAY", this.union_value_array, "msg_encoded_value__value_array", UITool.icon4Array());
            this.union_value_array.generateTreeNode(arrayNode, dex);
        } else if (this.union_value_annotation != null) {
            DefaultMutableTreeNode annoNode = addNode(parentNode,
                    floatPos, this.union_value_annotation.getLength(), "VALUE_ANNOTATION", this.union_value_annotation, "msg_encoded_value__value_annotation", UITool.icon4Annotations());
            this.union_value_annotation.generateTreeNode(annoNode, dex);
        } else if (this.union_value_bollean != null) {
            addNode(parentNode, super.startPos, Type_ubyte.LENGTH, "VALUE_BOOLEAN", this.union_value_bollean, "msg_encoded_value__value_boolean", UITool.icon4Data());
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
