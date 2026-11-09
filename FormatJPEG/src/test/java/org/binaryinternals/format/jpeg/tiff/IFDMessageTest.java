package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeModel;
import java.lang.classfile.MethodModel;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Every message key used for the TIFF tags must exist in `IFDMessage.properties`, so a missing
/// entry or a typo fails the build instead of the viewer.
class IFDMessageTest {

    /// An [IFDTag] constant, which is also the name of the tag class, like `IFD_A409_Saturation`.
    private static final Pattern TAG_NAME = Pattern.compile("IFD_([0-9A-F]{4})_\\w+");

    /// Every tag class that calls [IFD#getDescription()] has its `IFD_<tag>_Description` key.
    ///
    /// The tag classes are found from the [IFDTag] constants, and the ones using the description
    /// are found in their bytecode, so new tags are checked without changing this test.
    @Test
    void descriptionKeyExistsForEveryTagUsingIt() throws IOException, ReflectiveOperationException {
        final List<String> missing = new ArrayList<>();
        int checked = 0;
        for (final Field field : IFDTag.class.getFields()) {
            final Matcher name = TAG_NAME.matcher(field.getName());
            final Optional<Class<?>> tagClass = name.matches() ? tagClass(field.getName()) : Optional.empty();
            if (tagClass.isEmpty() || !callsGetDescription(tagClass.get())) {
                continue;
            }

            final int tag = field.getInt(null);
            assertEquals(Integer.parseInt(name.group(1), 16), tag, "Tag value differs from its name: " + field.getName());
            final String key = IFDMessage.KEY_FORMAT_IFD_DESCRIPTION.formatted(tag);
            if (!MessageKeys.exists(IFDMessage::getString, key)) {
                missing.add(field.getName() + " -> " + key);
            }
            checked++;
        }

        assertTrue(checked > 0, "No tag class calling getDescription() was found");
        assertEquals(List.of(), missing, "Missing in IFDMessage.properties");
    }

    /// Every `KEY_` constant of [IFDMessage] exists in `IFDMessage.properties`.
    @Test
    void keyConstantsExist() throws IllegalAccessException {
        MessageKeys.assertConstantsExist(IFDMessage.class, IFDMessage::getString);
    }

    private static Optional<Class<?>> tagClass(final String name) {
        try {
            return Optional.of(Class.forName(IFDTag.class.getPackageName() + '.' + name));
        } catch (ClassNotFoundException _) {
            return Optional.empty();  // Tag without its own class, parsed by type only
        }
    }

    /// Whether any method of `clazz` calls `getDescription()`, read with the ClassFile API.
    private static boolean callsGetDescription(final Class<?> clazz) throws IOException {
        try (InputStream in = clazz.getResourceAsStream(clazz.getSimpleName() + ".class")) {
            if (in == null) {
                throw new IOException("Class file not found: " + clazz.getName());
            }
            return ClassFile.of().parse(in.readAllBytes()).methods().stream()
                    .map(MethodModel::code)
                    .flatMap(Optional::stream)
                    .flatMap(CodeModel::elementStream)
                    .anyMatch(e -> e instanceof InvokeInstruction call
                            && call.name().equalsString("getDescription")
                            && call.type().equalsString("()Ljava/lang/String;"));
        }
    }
}
