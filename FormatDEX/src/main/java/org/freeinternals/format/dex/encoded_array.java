/*
 * encoded_array.java    Aug 22, 2021, 16:53
 *
 * Copyright 2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.ui.UITool;
import static org.freeinternals.format.dex.JTreeDexFile.addNode;

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

    public final Type_uleb128 size;
    public final encoded_value[] values;

    encoded_array(PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();
        this.size = stream.Dex_uleb128();
        if (this.size.value > 0) {
            this.values = new encoded_value[this.size.value];
            for (int i = 0; i < this.size.value; i++) {
                this.values[i] = new encoded_value(stream);

                // Dev phase Only
                if (i >= 1) {
                    break;
                }
            }
        } else {
            this.values = null;
        }

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        int floatPos = super.startPos;
        addNode(parentNode,
                floatPos,
                size.length,
                "class_annotations_off",
                this.size,
                "msg_encoded_array__size",
                UITool.icon4Size());
        floatPos += size.length;

        if (this.values != null) {
            DefaultMutableTreeNode valuesNode = addNode(parentNode,
                    floatPos,
                    this.length - this.size.length,
                    "values",
                    this.values.length,
                    "msg_encoded_array__values",
                    UITool.icon4Data()
            );

            for (int i = 0; i < this.values.length; i++) {
                encoded_value value = this.values[i];

                // Dev phase Only
                if (value == null) {
                    continue;
                }

                DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        value.getStartPos(),
                        value.getLength(),
                        String.format("%s[%d]", value.getClass().getSimpleName(), i),
                        UITool.icon4Data(),
                        MESSAGES.getString("msg_encoded_value")
                ));

                valuesNode.add(valueNode);
                value.generateTreeNode(valueNode, dexFile);
            }
        }
    }
}
