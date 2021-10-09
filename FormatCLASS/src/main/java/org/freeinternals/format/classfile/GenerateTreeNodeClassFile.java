/*
 * GenerateTreeNodeClassFile.java    September 07, 2019, 21:22
 *
 * Copyright 2019, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.format.classfile;

import java.util.Locale;
import java.util.ResourceBundle;
import org.freeinternals.commonlib.ui.GenerateTreeNodeFileFormat;

/**
 * Interface for generating tree node for {@link ClassFile}.
 *
 * @author Amos Shi
 */
public interface GenerateTreeNodeClassFile extends GenerateTreeNodeFileFormat {
    static final String FIELD_ACCESS_FLAGS = "access_flags";
    static final String FIELD_ATTRS = "attributes [%d]";
    static final String FIELD_ATTR_COUNT = "attributes_count";

    static final String TEXT_CPINDEX_PUREVALUE = "constant pool index = %d, %s";
    static final String TEXT_CPINDEX_VALUE = "constant pool index = %d, %s = %s";
    static final String TEXT_CP_PREFIX = "constant_pool[";
    static final String TEXT_FIELDS_PREFIX = "fields[";
    static final String TEXT_METHODS_PERFIX = "methods[";

    static final ResourceBundle MESSAGES = ResourceBundle.getBundle(GenerateTreeNodeClassFile.class.getPackageName() + ".MessagesBundle", Locale.ROOT);

    @Override
    default ResourceBundle getMessages() {
        return MESSAGES;
    }
}
