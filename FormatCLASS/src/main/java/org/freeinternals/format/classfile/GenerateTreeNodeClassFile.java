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

    ResourceBundle MESSAGES = ResourceBundle.getBundle(GenerateTreeNodeClassFile.class.getPackageName() + ".MessagesBundle", Locale.ROOT);

    @Override
    default ResourceBundle getMessages() {
        return MESSAGES;
    }
}
