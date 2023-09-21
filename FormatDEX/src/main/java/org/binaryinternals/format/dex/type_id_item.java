/*
 * type_id_item.java    June 23, 2015, 06:20
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
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
import org.binaryinternals.format.classfile.SignatureConvertor;

/**
 * Type identifiers list item.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S100 - Method names should comply with a naming convention --- We respect the name from DEX spec instead
 * java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S100", "java:S101", "java:S116", "java:S1104"})
public class type_id_item extends FileComponent implements GenerateTreeNodeDexFile {

    private static final Logger LOGGER = Logger.getLogger(type_id_item.class.getName());

    /**
     * Item size.
     *
     * @see map_list.TypeCodes#TYPE_TYPE_ID_ITEM
     */
    public static final int ITEM_SIZE = 0x04;

    /**
     * index into the string_ids list for the descriptor string of this type.
     * The string must conform to the syntax for TypeDescriptor, defined above.
     */
    public Type_uint descriptor_idx;

    /**
     * Local cache for the {@link #get_descriptor_jls(DexFile)}.
     */
    private SignatureConvertor.SignatureResult descriptor_jls = null;

    type_id_item(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.descriptor_idx = stream.Dex_uint();
        super.length = Type_uint.LENGTH;
    }
    
    /**
     * Get descriptor text.
     *
     * @param dexFile Current {@link DexFile}
     * @return Descriptor text
     */
    public String get_descriptor(DexFile dexFile) {
        return dexFile.get_string_ids_string(descriptor_idx.intValue());
    }

    /**
     * Get parsed descriptor name in Java Language Specification format.
     *
     * @param dexFile Current {@link DexFile}
     * @return Parsed descriptor name
     */
    public SignatureConvertor.SignatureResult get_descriptor_jls(DexFile dexFile) {
        if (this.descriptor_jls == null) {
            String text = this.get_descriptor(dexFile);
            if (text == null) {
                LOGGER.warning(String.format("The current item points to a null string: position = 0x%08X", this.startPos));
                this.descriptor_jls = null;
            } else {
                try {
                    this.descriptor_jls = SignatureConvertor.methodReturnTypeJLS(text);
                } catch (FileFormatException ex) {
                    // This should never happen
                    this.descriptor_jls = null;
                    LOGGER.log(Level.SEVERE, "The current type_id_item points to an invalid signature string", ex);
                }
            }
        }

        return this.descriptor_jls;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat dexFile) {
        addNode(parentNode,
                this.startPos,
                Type_uint.LENGTH,
                "descriptor_idx",
                String.format("%s - %s", descriptor_idx, this.get_descriptor((DexFile)dexFile)),
                "msg_type_id_item__descriptor_idx",
                Icons.Offset
        );
    }
}
