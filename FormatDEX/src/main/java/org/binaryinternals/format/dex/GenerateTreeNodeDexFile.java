/*
 * GenerateTreeNodeDexFile.java    August 18, 2021, 19:13
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import java.util.Locale;
import java.util.ResourceBundle;
import org.binaryinternals.commonlib.ui.GenerateTreeNodeFileFormat;

/**
 * Interface for generating tree node for {@link DexFile}.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S115 - Constant name does not follow naming conventions --- We respect the DEX spec name instead
 * </pre>
 */
@SuppressWarnings({"java:S115"})
public interface GenerateTreeNodeDexFile extends GenerateTreeNodeFileFormat {
    static final ResourceBundle MESSAGES = ResourceBundle.getBundle(JTreeDexFile.class.getPackageName() + ".MessagesBundle", Locale.ROOT);
    static final String msg_annotation_set_item = "msg_annotation_set_item";

    @Override
    default ResourceBundle getMessages() {
        return MESSAGES;
    }
}
