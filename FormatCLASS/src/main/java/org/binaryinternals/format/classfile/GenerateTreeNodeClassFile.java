/*
 * GenerateTreeNodeClassFile.java    September 07, 2019, 21:22
 *
 * Copyright 2019, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.classfile;

import java.util.Locale;
import java.util.ResourceBundle;
import org.binaryinternals.commonlib.GenerateTreeNodeFileFormat;

/// Interface for generating tree node for [ClassFile].
///
/// @author Amos Shi
public interface GenerateTreeNodeClassFile extends GenerateTreeNodeFileFormat {
    /// Field name of `access_flags`.
    String FIELD_ACCESS_FLAGS = "access_flags";
    /// Format string of the `attributes` field name, with the attribute count.
    String FIELD_ATTRS = "attributes [%d]";
    /// Field name of `attributes_count`.
    String FIELD_ATTR_COUNT = "attributes_count";

    /// Format string of a constant pool index and its value.
    String TEXT_CPINDEX_PUREVALUE = "constant pool index = %d, %s";
    /// Format string of a constant pool index, a value name and the value.
    String TEXT_CPINDEX_VALUE = "constant pool index = %d, %s = %s";
    /// Text prefix of a constant pool entry.
    String TEXT_CP_PREFIX = "constant_pool[";
    /// Text prefix of a field.
    String TEXT_FIELDS_PREFIX = "fields[";
    /// Text prefix of a method.
    String TEXT_METHODS_PERFIX = "methods[";

    /// Messages of the class file tree nodes.
    ResourceBundle MESSAGES = ResourceBundle.getBundle(GenerateTreeNodeClassFile.class.getPackageName() + ".MessagesBundle", Locale.ROOT);

    @Override
    default ResourceBundle getMessages() {
        return MESSAGES;
    }
}
