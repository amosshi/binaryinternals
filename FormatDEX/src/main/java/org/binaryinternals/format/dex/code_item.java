/*
 * code_item.java    June 23, 2015, 06:20
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileComponent;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.ui.GenerateTreeNode;
import org.binaryinternals.commonlib.ui.Icons;

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
public class code_item extends FileComponent implements GenerateTreeNodeDexFile {

    public final Type_ushort registers_size;
    public final Type_ushort ins_size;
    public final Type_ushort outs_size;
    public final Type_ushort tries_size;
    public final Type_uint debug_info_off;

    public final Type_uint insns_size;
    public final Type_ushort[] insns;

    public final Type_ushort padding;
    public final try_item[] tries;
    public final encoded_catch_handler_list handlers;

    code_item(PosDataInputStreamDex stream) throws IOException, FileFormatException {
        super.startPos = stream.getPos();

        this.registers_size = stream.Dex_ushort();
        this.ins_size = stream.Dex_ushort();
        this.outs_size = stream.Dex_ushort();
        this.tries_size = stream.Dex_ushort();
        this.debug_info_off = stream.Dex_uint();
        // TODO Parse debug info

        this.insns_size = stream.Dex_uint();
        if (this.insns_size.value > 0) {
            DexFile.check_uint("code_item.insns_size", this.insns_size, stream.getPos());
            this.insns = new Type_ushort[(int) this.insns_size.value];
            for (int i = 0; i < this.insns_size.value; i++) {
                this.insns[i] = stream.Dex_ushort();
            }
        } else {
            this.insns = null;
        }

        // This element is only present if tries_size is non-zero and insns_size is odd.
        if (this.tries_size.value != 0 && ((this.insns_size.value & 1) == 1)) {
            this.padding = stream.Dex_ushort();
        } else {
            this.padding = null;
        }

        if (this.tries_size.value > 0) {
            this.tries = new try_item[this.tries_size.value];
            for (int i = 0; i < this.tries_size.value; i++) {
                this.tries[i] = new try_item(stream);
            }
            this.handlers = new encoded_catch_handler_list(stream);
        } else {
            this.tries = null;
            this.handlers = null;
        }


        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat dexFile) {
        int floatPos = super.startPos;
        addNode(parentNode, floatPos, Type_ushort.LENGTH, "registers_size", this.registers_size, "msg_code_item__registers_size", Icons.Size);
        floatPos += Type_ushort.LENGTH;
        addNode(parentNode, floatPos, Type_ushort.LENGTH, "ins_size", this.ins_size, "msg_code_item__ins_size", Icons.Size);
        floatPos += Type_ushort.LENGTH;
        addNode(parentNode, floatPos, Type_ushort.LENGTH, "outs_size", this.outs_size, "msg_code_item__outs_size", Icons.Size);
        floatPos += Type_ushort.LENGTH;
        addNode(parentNode, floatPos, Type_ushort.LENGTH, "tries_size", this.tries_size, "msg_code_item__tries_size", Icons.Size);
        floatPos += Type_ushort.LENGTH;
        addNode(parentNode, floatPos, Type_uint.LENGTH, "debug_info_off", this.debug_info_off, "msg_code_item__debug_info_off", Icons.Offset);
    }


    public static class try_item extends FileComponent implements GenerateTreeNode {

        public final Type_uint start_addr;
        public final Type_ushort insn_count;
        public final Type_ushort handler_off;

        try_item(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.start_addr = stream.Dex_uint();
            this.insn_count = stream.Dex_ushort();
            this.handler_off = stream.Dex_ushort();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode) {
            // TODO
        }
    }

    public static class encoded_catch_handler_list extends FileComponent implements GenerateTreeNode {

        public final Type_uleb128 size;
        public final encoded_catch_handler[] list;

        encoded_catch_handler_list(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.size = stream.Dex_uleb128();
            if (this.size.value > 0) {
                this.list = new encoded_catch_handler[this.size.value];
                for (int i = 0; i < this.size.value; i++) {
                    this.list[i] = new encoded_catch_handler(stream);
                }
            } else {
                this.list = null;
            }
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode) {
            // TODO
        }
    }

    public static class encoded_catch_handler extends FileComponent implements GenerateTreeNode {

        public final Type_sleb128 size;
        public final encoded_type_addr_pair[] handlers;
        public final Type_uleb128 catch_all_addr;

        encoded_catch_handler(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();

            this.size = stream.Dex_sleb128();
            final int sizeAbs = Math.abs(this.size.value);
            if (sizeAbs > 0) {
                this.handlers = new encoded_type_addr_pair[sizeAbs];
                for (int i = 0; i < sizeAbs; i++) {
                    this.handlers[i] = new encoded_type_addr_pair(stream);
                }
            } else {
                this.handlers = null;
            }

            if (this.size.value <= 0) {
                this.catch_all_addr = stream.Dex_uleb128();
            } else {
                this.catch_all_addr = null;
            }

            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode) {
            // TODO
        }
    }

    public static class encoded_type_addr_pair extends FileComponent implements GenerateTreeNode {

        public final Type_uleb128 type_idx;
        public final Type_uleb128 addr;

        encoded_type_addr_pair(PosDataInputStreamDex stream) throws IOException, FileFormatException {
            super.startPos = stream.getPos();
            this.type_idx = stream.Dex_uleb128();
            this.addr = stream.Dex_uleb128();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode) {
            throw new UnsupportedOperationException("Not supported yet 5.");
        }
    }
}
