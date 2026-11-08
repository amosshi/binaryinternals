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
    String FIELD_ACCESS_FLAGS = "access_flags";
    String FIELD_ATTRS = "attributes [%d]";
    String FIELD_ATTR_COUNT = "attributes_count";

    String TEXT_CPINDEX_PUREVALUE = "constant pool index = %d, %s";
    String TEXT_CPINDEX_VALUE = "constant pool index = %d, %s = %s";
    String TEXT_CP_PREFIX = "constant_pool[";
    String TEXT_FIELDS_PREFIX = "fields[";
    String TEXT_METHODS_PERFIX = "methods[";

    ResourceBundle MESSAGES = ResourceBundle.getBundle(GenerateTreeNodeClassFile.class.getPackageName() + ".MessagesBundle", Locale.ROOT);

    @Override
    default ResourceBundle getMessages() {
        return MESSAGES;
    }
}
