/*
 * ProtoIdItem.java    June 23, 2015, 06:20
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.ui.UITool;
import static org.freeinternals.format.dex.JTreeDexFile.addNode;

/**
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
public class proto_id_item extends FileComponent implements GenerateTreeNodeDexFile {

    /**
     * Item Size In Bytes.
     *
     * @see map_list.TypeCodes#TYPE_PROTO_ID_ITEM
     */
    public static final int ITEM_SIZE = 0x0c;

    /**
     * index into the string_ids list for the short-form descriptor string of
     * this prototype. The string must conform to the syntax for
     * ShortyDescriptor, defined above, and must correspond to the return type
     * and parameters of this item.
     */
    public Type_uint shorty_idx;
    private String shorty = null;

    /**
     * index into the type_ids list for the return type of this prototype.
     */
    public Type_uint return_type_idx;
    private String return_type = null;
    private String return_type_jls = null;

    /**
     * offset from the start of the file to the list of parameter types for this
     * prototype, or 0 if this prototype has no parameters. This offset, if
     * non-zero, should be in the data section, and the data there should be in
     * the format specified by "type_list" below. Additionally, there should be
     * no reference to the type void in the list.
     */
    public final Type_uint parameters_off;

    proto_id_item(final PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();
        this.shorty_idx = stream.Dex_uint();
        this.return_type_idx = stream.Dex_uint();
        this.parameters_off = stream.Dex_uint();
        super.length = stream.getPos() - super.startPos;
    }

    /**
     * Get {@link #shorty_idx} text.
     *
     * @param dexFile Current {@link DexFile}
     * @return shorty text
     * @see #shorty_idx
     */
    public String get_shorty(final DexFile dexFile) {
        if (this.shorty == null) {
            this.shorty = dexFile.get_string_ids_string(shorty_idx.intValue());
        }
        return this.shorty;
    }

    /**
     * Get {@link #return_type_idx} text.
     *
     * @param dexFile Current {@link DexFile}
     * @return {@link #return_type_idx} text
     * @see #return_type_idx
     */
    public String get_return_type(final DexFile dexFile) {
        if (this.return_type == null) {
            this.return_type = dexFile.get_type_ids_string(return_type_idx.intValue());
        }
        return this.return_type;
    }

    /**
     * Get {@link #return_type_idx} text in Java Language Specification format.
     *
     * @param dexFile Current {@link DexFile}
     * @return {@link #return_type_idx} text in JSL format
     * @see #return_type_idx
     */
    public String get_return_type_jls(final DexFile dexFile) {
        if (this.return_type_jls == null) {
            this.return_type_jls = dexFile.type_ids[return_type_idx.intValue()].get_descriptor_jls(dexFile).toString();
        }
        return this.return_type_jls;
    }

    /**
     * Get the corresponding {@link type_list} for {@link #parameters_off}.
     *
     * @param dexFile Current {@link DexFile}
     * @return {@link type_list} for {@link #parameters_off}, or null if no
     * parameters
     */
    public type_list get_parameters(final DexFile dexFile) {
        if (this.parameters_off.value == 0) {
            return null;
        } else {
            return (type_list) dexFile.data.get(this.parameters_off.value);
        }
    }

    /**
     * String format of current {@link proto_id_item}.
     *
     * @param dexFile Current {@link DexFile}
     * @return String format of a {@link proto_id_item}
     */
    public String toString(DexFile dexFile) {
        type_list params = this.get_parameters(dexFile);
        return String.format("%s - %s (%s)",
                this.get_shorty(dexFile),
                this.get_return_type_jls(dexFile),
                (params == null) ? "" : params.toString(dexFile)
        );
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, DexFile dexFile) {
        int floatPos = super.startPos;

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "shorty_idx",
                String.format(FORMAT_STRING_STRING, this.shorty_idx, this.get_shorty(dexFile)),
                "msg_proto_id_item__shorty_idx",
                UITool.icon4Index());
        floatPos += Type_uint.LENGTH;

        addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "return_type_idx",
                String.format(FORMAT_STRING_STRING, this.return_type_idx, this.get_return_type_jls(dexFile)),
                "msg_proto_id_item__return_type_idx",
                UITool.icon4Return());
        floatPos += Type_uint.LENGTH;

        String param = (this.parameters_off.value == 0) ? "no parameter" : String.format("%d paramters", this.get_parameters(dexFile).size.value);
        DefaultMutableTreeNode parametersNode = addNode(parentNode,
                floatPos,
                Type_uint.LENGTH,
                "parameters_off",
                String.format(FORMAT_STRING_STRING, this.parameters_off, param),
                "msg_proto_id_item__parameters_off",
                UITool.icon4Offset());

        type_list list = this.get_parameters(dexFile);
        if (list != null) {
            DefaultMutableTreeNode listNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    list.getStartPos(),
                    list.getLength(),
                    "type_list",
                    UITool.icon4Shortcut(),
                    MESSAGES.getString("msg_type_list")
            ));
            parametersNode.add(listNode);
            list.generateTreeNode(listNode, dexFile);
        }
    }
}
