/*
 * encoded_value.java    Aug 22, 2021, 16:56
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;

/// @author Amos Shi
///
/// ```
/// java:S100 - Method names should comply with a naming convention --- We respect the name from DEX spec instead
/// java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
/// java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
/// ```
@SuppressWarnings({"java:S100", "java:S101", "java:S116", "java:S1104"})
public final class encoded_value extends FileComponent implements GenerateTreeNodeDexFile {

    public final Type_ubyte value_arg_and_type;

    /// low-order five (5) bits as value_type.
    public final int value_type;
    /// high-order three (3) bits as value_arg.
    public final int value_arg;

    /// The format of the value, from [#value_type].
    public final ValueFormat format;

    /// The value, its type depends on [#format].
    public final Value value;

    /// The `value` union of the [encoded_value] structure.
    public sealed interface Value {
    }

    public record ByteValue(Type_ubyte value) implements Value {
    }

    public record ShortValue(Type_short value) implements Value {
    }

    public record CharValue(Type_ushort value) implements Value {
    }

    public record IntValue(Type_int value) implements Value {
    }

    public record LongValue(Type_long value) implements Value {
    }

    public record FloatValue(float value) implements Value {
    }

    public record DoubleValue(double value) implements Value {
    }

    public record MethodTypeValue(Type_uint index) implements Value {
    }

    public record MethodHandleValue(Type_uint index) implements Value {
    }

    public record StringValue(Type_uint index) implements Value {
    }

    public record TypeValue(Type_uint index) implements Value {
    }

    public record FieldValue(Type_uint index) implements Value {
    }

    public record MethodValue(Type_uint index) implements Value {
    }

    public record EnumValue(Type_uint index) implements Value {
    }

    public record ArrayValue(encoded_array value) implements Value {
    }

    public record AnnotationValue(encoded_annotation value) implements Value {
    }

    public record NullValue() implements Value {
    }

    public record BooleanValue(boolean value) implements Value {
    }

    encoded_value(final PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        value_arg_and_type = stream.Dex_ubyte();

        // In the logic bellow we assume Java int type is still a 32-bit number
        // This should be truth in know future of Java programming language
        this.value_type = (value_arg_and_type.value & 0x0000001F);
        this.value_arg = (value_arg_and_type.value & 0x000000E0) >> 5;

        this.format = ValueFormat.of(this.value_type).orElseThrow(
                () -> new FileFormatException("Unrecognized value_type (%d) at location 0x%08X".formatted(this.value_type, stream.getPos())));

        this.value = switch (this.format) {
            case VALUE_BYTE -> new ByteValue(stream.Dex_ubyte());
            case VALUE_SHORT -> new ShortValue(this.read_short(stream, this.value_arg));
            case VALUE_CHAR -> new CharValue(this.read_char(stream, this.value_arg));
            case VALUE_INT -> new IntValue(this.read_int(stream, this.value_arg));
            case VALUE_LONG -> new LongValue(this.read_long(stream, this.value_arg));
            case VALUE_FLOAT -> new FloatValue(stream.readFloat(this.value_arg + 1));
            case VALUE_DOUBLE -> new DoubleValue(stream.readDouble(this.value_arg + 1));
            case VALUE_METHOD_TYPE -> new MethodTypeValue(this.read_uint(stream, this.value_arg));
            case VALUE_METHOD_HANDLE -> new MethodHandleValue(this.read_uint(stream, this.value_arg));
            case VALUE_STRING -> new StringValue(this.read_uint(stream, this.value_arg));
            case VALUE_TYPE -> new TypeValue(this.read_uint(stream, this.value_arg));
            case VALUE_FIELD -> new FieldValue(this.read_uint(stream, this.value_arg));
            case VALUE_METHOD -> new MethodValue(this.read_uint(stream, this.value_arg));
            case VALUE_ENUM -> new EnumValue(this.read_uint(stream, this.value_arg));
            case VALUE_ARRAY -> new ArrayValue(new encoded_array(stream));
            case VALUE_ANNOTATION -> new AnnotationValue(new encoded_annotation(stream));
            case VALUE_NULL -> new NullValue();
            case VALUE_BOOLEAN -> new BooleanValue(this.value_arg == 1);
        };

        super.length = stream.getPos() - super.startPos;
    }

    private Type_long read_long(final PosDataInputStreamDex stream, final int arg) throws IOException {
        return switch (arg) {
            case 0 -> new Type_long(stream.Dex_byte().value);
            case 1 -> new Type_long(stream.Dex_short().value);
            case 2 -> new Type_long(stream.Dex_int3().value);
            case 3 -> new Type_long(stream.Dex_int().value);
            case 4, 5, 6 -> stream.Dex_long(arg + 1);
            case 7 -> stream.Dex_long();
            default -> throw new FileFormatException("Unrecognized value_arg (%d) in %s for int at location 0x%08X".formatted(
                    arg,
                    this.getClass().getSimpleName(),
                    stream.getPos()));
        };
    }

    private Type_short read_short(final PosDataInputStreamDex stream, final int arg) throws IOException {
        return switch (arg) {
            case 0 -> new Type_short(stream.Dex_byte().value);
            case 1 -> stream.Dex_short();
            default -> throw new FileFormatException("Unrecognized value_arg (%d) for short in %s at location 0x%08X".formatted(
                    arg,
                    this.getClass().getSimpleName(),
                    stream.getPos()));
        };
    }

    private Type_ushort read_char(final PosDataInputStreamDex stream, final int arg) throws IOException {
        return switch (arg) {
            case 0 -> new Type_ushort(stream.Dex_ubyte().value);
            case 1 -> stream.Dex_ushort();
            default -> throw new FileFormatException("Unrecognized value_arg (%d) for char/ushort in %s at location 0x%08X".formatted(
                    arg,
                    this.getClass().getSimpleName(),
                    stream.getPos()));
        };
    }

    private Type_int read_int(final PosDataInputStreamDex stream, final int arg) throws IOException {
        return switch (arg) {
            case 0 -> new Type_int(stream.Dex_byte().value);
            case 1 -> new Type_int(stream.Dex_short().value);
            case 2 -> stream.Dex_int3();
            case 3 -> stream.Dex_int();
            default -> throw new FileFormatException("Unrecognized value_arg (%d) in %s for int at location 0x%08X".formatted(
                    arg,
                    this.getClass().getSimpleName(),
                    stream.getPos()));
        };
    }

    private Type_uint read_uint(final PosDataInputStreamDex stream, final int arg) throws IOException {
        return switch (arg) {
            case 0 -> new Type_uint(stream.Dex_ubyte().value);
            case 1 -> new Type_uint(stream.Dex_ushort().value);
            case 2 -> stream.Dex_uint3();
            case 3 -> stream.Dex_uint();
            default -> throw new FileFormatException("Unrecognized value_arg (%d) in %s for uint at location 0x%08X".formatted(
                    arg,
                    this.getClass().getSimpleName(),
                    stream.getPos()));
        };
    }

    @Override
    public String toString() {
        return "%s value_type=%d | %s value_arg=%d".formatted(
                this.getClass().getSimpleName(),
                this.value_type,
                this.format.name(),
                this.value_arg);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat fileFormat) {
        final DexFile dex = (DexFile) fileFormat;
        int floatPos = super.startPos;

        addNode(parentNode,
                floatPos,
                Type_ubyte.LENGTH,
                "value_arg + value_type",
                "0x%x | %s".formatted(this.value_arg_and_type.value, Integer.toBinaryString(this.value_arg_and_type.value)),
                "msg_encoded_value__value_type",
                Icons.Tag
        );
        addNode(parentNode,
                floatPos,
                Type_ubyte.LENGTH,
                "value_type",
                "%d | 0x%x - %s".formatted(this.value_type, this.value_type, this.format.name()),
                "msg_encoded_value__value_type",
                Icons.Tag
        );
        addNode(parentNode,
                floatPos,
                Type_ubyte.LENGTH,
                "value_arg",
                "decimal %d | binary %s".formatted(this.value_arg, Integer.toBinaryString(this.value_arg)),
                "msg_encoded_value__value_arg",
                Icons.Parameter
        );
        floatPos += Type_ubyte.LENGTH;

        final int size = this.value_arg + 1;
        switch (this.value) {
            case ByteValue(Type_ubyte v) -> addNode(parentNode, floatPos, Type_ubyte.LENGTH, "VALUE_BYTE", v, "msg_encoded_value__value_byte", Icons.Data);
            case ShortValue(Type_short v) -> addNode(parentNode, floatPos, size, "VALUE_SHORT", v, "msg_encoded_value__value_short", Icons.Data);
            case CharValue(Type_ushort v) -> addNode(parentNode, floatPos, size, "VALUE_CHAR", v, "msg_encoded_value__value_char", Icons.Data);
            case IntValue(Type_int v) -> addNode(parentNode, floatPos, size, "VALUE_INT", v, "msg_encoded_value__value_int", Icons.Data);
            case LongValue(Type_long v) -> addNode(parentNode, floatPos, size, "VALUE_LONG", v, "msg_encoded_value__value_long", Icons.Data);
            case FloatValue(float v) -> addNode(parentNode, floatPos, size, "VALUE_FLOAT", v, "msg_encoded_value__value_float", Icons.Data);
            case DoubleValue(double v) -> addNode(parentNode, floatPos, size, "VALUE_DOUBLE", v, "msg_encoded_value__value_double", Icons.Data);
            // TODO for value of the indexes: method type, method handle, type, field, method
            case MethodTypeValue(Type_uint i) -> addNode(parentNode, floatPos, size, "VALUE_METHOD_TYPE", i, "msg_encoded_value__value_method_type",
                    Icons.Data);
            case MethodHandleValue(Type_uint i) -> addNode(parentNode, floatPos, size, "VALUE_METHOD_HANDLE", i, "msg_encoded_value__value_method_handle",
                    Icons.Data);
            case StringValue(Type_uint i) -> addNode(parentNode,
                    floatPos,
                    size,
                    "VALUE_STRING",
                    FORMAT_STRING_STRING.formatted(i, dex.get_string_ids_string(i.intValue())),
                    "msg_encoded_value__value_string",
                    Icons.Data);
            case TypeValue(Type_uint i) -> addNode(parentNode, floatPos, size, "VALUE_TYPE", i, "msg_encoded_value__type", Icons.Data);
            case FieldValue(Type_uint i) -> addNode(parentNode, floatPos, size, "VALUE_FIELD", i, "msg_encoded_value__value_field", Icons.Data);
            case MethodValue(Type_uint i) -> addNode(parentNode, floatPos, size, "VALUE_METHOD", i, "msg_encoded_value__value_method", Icons.Data);
            case EnumValue(Type_uint i) -> {
                field_id_item fieldId = dex.field_ids[i.intValue()];
                addNode(parentNode,
                        floatPos,
                        size,
                        "VALUE_ENUM",
                        FORMAT_STRING_STRING.formatted(i, (fieldId == null) ? "null (should not happen)" : fieldId.toString(dex)),
                        "msg_encoded_value__value_enum",
                        Icons.Data);
            }
            case ArrayValue(encoded_array array) -> {
                DefaultMutableTreeNode arrayNode = addNode(parentNode,
                        floatPos, array.getLength(), "VALUE_ARRAY", array, "msg_encoded_value__value_array", Icons.Array);
                array.generateTreeNode(arrayNode, dex);
            }
            case AnnotationValue(encoded_annotation annotation) -> {
                DefaultMutableTreeNode annoNode = addNode(parentNode,
                        floatPos, annotation.getLength(), "VALUE_ANNOTATION", annotation, "msg_encoded_value__value_annotation", Icons.Annotations);
                annotation.generateTreeNode(annoNode, dex);
            }
            case NullValue() -> {
                // No value for null
            }
            case BooleanValue(boolean v) -> addNode(parentNode, super.startPos, Type_ubyte.LENGTH, "VALUE_BOOLEAN", v, "msg_encoded_value__value_boolean",
                    Icons.Data);
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
        VALUE_METHOD(0x1A, 3),
        VALUE_ENUM(0x1B, 3),
        VALUE_ARRAY(0x1C, 0),
        VALUE_ANNOTATION(0x1D, 0),
        VALUE_NULL(0x1E, 0),
        VALUE_BOOLEAN(0x1F, 1);

        /// Internal code of the value type.
        public final int code;

        /// Max allowed value for [encoded_value#value_arg]
        public final int arg_max;

        ValueFormat(int i, int m) {
            this.code = i;
            this.arg_max = m;
        }

        /// Get enum item based on [#code].
        ///
        /// @param value Value to search
        /// @return matched [ValueFormat] item, or empty for an unrecognized value
        public static Optional<ValueFormat> of(final int value) {
            return Arrays.stream(values()).filter(item -> item.code == value).findFirst();
        }
    }
}
