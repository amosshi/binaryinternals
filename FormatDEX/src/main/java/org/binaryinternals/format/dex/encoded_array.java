/*
 * encoded_array.java    Aug 22, 2021, 16:53
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

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
public class encoded_array extends FileComponent implements GenerateTreeNodeDexFile {

    private static final Logger LOGGER = Logger.getLogger(encoded_array.class.getName());
    public final Type_uleb128 size;
    public final encoded_value[] values;

    encoded_array(PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        this.size = stream.Dex_uleb128();
        if (this.size.value > 0) {
            this.values = new encoded_value[this.size.value];
            for (int i = 0; i < this.size.value; i++) {
                try {
                    this.values[i] = new encoded_value(stream);
                } catch (FileFormatException e) {
                    // This should never happen
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        } else {
            this.values = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public String toString() {
        return String.format(FORMAT_STRING_STRING, this.getClass().getSimpleName(), size.toString());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat dexFile) {
        int floatPos = super.startPos;
        addNode(parentNode,
                floatPos,
                size.length,
                "size",
                this.size,
                "msg_encoded_array__size",
                Icons.Size);
        floatPos += size.length;

        if (this.values != null) {
            DefaultMutableTreeNode valuesNode = addNode(parentNode,
                    floatPos,
                    this.length - this.size.length,
                    "values",
                    this.values.length,
                    "msg_encoded_array__values",
                    Icons.Data
            );

            for (int i = 0; i < this.values.length; i++) {
                encoded_value value = this.values[i];
                if (value != null) {
                    DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            value.getStartPos(),
                            value.getLength(),
                            String.format("%s[%d] %s", value.getClass().getSimpleName(), i, value.toString()),
                            Icons.Data,
                            MESSAGES.getString("msg_encoded_value")
                    ));

                    valuesNode.add(valueNode);
                    value.generateTreeNode(valueNode, dexFile);
                } else {
                    // This should never happen
                    LOGGER.log(Level.SEVERE, "{0} at 0x{1} : value[{2}] is null", new Object[]{this.getClass().getSimpleName(), Integer.toHexString(this.getStartPos()), i});
                }
            }
        }
    }
}
