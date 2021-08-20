/*
 * GenerateTreeNodeDexFile.java    August 18, 2021, 19:13
 *
 * Copyright 2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.freeinternals.format.dex;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Interface for generating tree node for {@link DexFile}.
 *
 * @author Amos Shi
 */
public interface GenerateTreeNodeDexFile {
    static final ResourceBundle MESSAGES = ResourceBundle.getBundle(JTreeDexFile.class.getPackageName() + ".MessagesBundle", Locale.ROOT);
    static final String FORMAT_STRING_STRING = "%s - %s";

    void generateTreeNode(final DefaultMutableTreeNode parentNode, DexFile dexFile);

}
