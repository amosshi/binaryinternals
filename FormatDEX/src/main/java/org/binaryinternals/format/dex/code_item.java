/*
 * code_item.java    June 23, 2015, 06:20
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.Icons;

/// @author Amos Shi
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
/// java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
/// ```
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public final class code_item extends FileComponent implements GenerateTreeNodeDexFile {

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

    code_item(PosDataInputStreamDex stream) throws IOException {
        super.startPos = stream.getPos();

        this.registers_size = stream.Dex_ushort();
        this.ins_size = stream.Dex_ushort();
        this.outs_size = stream.Dex_ushort();
        this.tries_size = stream.Dex_ushort();
        this.debug_info_off = stream.Dex_uint();

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
        floatPos += Type_uint.LENGTH;
        addNode(parentNode, floatPos, Type_uint.LENGTH, "insns_size", this.insns_size, "msg_code_item__insns_size", Icons.Size);
        floatPos += Type_uint.LENGTH;

        if (this.insns != null) {
            final int insnsLength = this.insns.length * Type_ushort.LENGTH;
            addNode(parentNode, floatPos, insnsLength, "insns", "%d code units".formatted(this.insns.length), "msg_code_item__insns", Icons.Data);
            floatPos += insnsLength;
        }
        if (this.padding != null) {
            addNode(parentNode, floatPos, Type_ushort.LENGTH, "padding", this.padding, "msg_code_item__padding", Icons.Empty);
            floatPos += Type_ushort.LENGTH;
        }
        if (this.tries != null) {
            final DefaultMutableTreeNode triesNode = addNode(parentNode, floatPos, this.tries.length * try_item.LENGTH,
                    "tries", "[%d]".formatted(this.tries.length), "msg_code_item__tries", Icons.Array);
            for (int i = 0; i < this.tries.length; i++) {
                final try_item item = this.tries[i];
                final DefaultMutableTreeNode itemNode = addNode(triesNode, item.getStartPos(), item.getLength(),
                        "try_item[%d]".formatted(i), item.handler_off, null, Icons.Exception);
                item.generateTreeNode(itemNode, dexFile);
            }
        }
        if (this.handlers != null) {
            final DefaultMutableTreeNode handlersNode = addNode(parentNode, this.handlers.getStartPos(), this.handlers.getLength(),
                    "handlers", "[%d]".formatted(this.handlers.size.value), "msg_code_item__handlers", Icons.Array);
            this.handlers.generateTreeNode(handlersNode, dexFile);
        }
    }

    public static final class try_item extends FileComponent implements GenerateTreeNodeDexFile {

        /// Length of a `try_item`: `uint` + `ushort` + `ushort`.
        static final int LENGTH = Type_uint.LENGTH + Type_ushort.LENGTH + Type_ushort.LENGTH;

        public final Type_uint start_addr;
        public final Type_ushort insn_count;
        public final Type_ushort handler_off;

        try_item(PosDataInputStreamDex stream) throws IOException {
            super.startPos = stream.getPos();
            this.start_addr = stream.Dex_uint();
            this.insn_count = stream.Dex_ushort();
            this.handler_off = stream.Dex_ushort();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat dexFile) {
            int floatPos = super.startPos;
            addNode(parentNode, floatPos, Type_uint.LENGTH, "start_addr", this.start_addr, "msg_try_item__start_addr", Icons.Offset);
            floatPos += Type_uint.LENGTH;
            addNode(parentNode, floatPos, Type_ushort.LENGTH, "insn_count", this.insn_count, "msg_try_item__insn_count", Icons.Size);
            floatPos += Type_ushort.LENGTH;
            addNode(parentNode, floatPos, Type_ushort.LENGTH, "handler_off", this.handler_off, "msg_try_item__handler_off", Icons.Offset);
        }
    }

    public static final class encoded_catch_handler_list extends FileComponent implements GenerateTreeNodeDexFile {

        public final Type_uleb128 size;
        public final encoded_catch_handler[] list;

        encoded_catch_handler_list(PosDataInputStreamDex stream) throws IOException {
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
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat dexFile) {
            addNode(parentNode, super.startPos, this.size.length, "size", this.size, "msg_encoded_catch_handler_list__size", Icons.Size);
            if (this.list != null) {
                for (int i = 0; i < this.list.length; i++) {
                    final encoded_catch_handler handler = this.list[i];
                    final DefaultMutableTreeNode node = addNode(parentNode, handler.getStartPos(), handler.getLength(),
                            "encoded_catch_handler[%d]".formatted(i), "offset %d".formatted(handler.getStartPos() - super.startPos),
                            "msg_encoded_catch_handler_list__list", Icons.Exception);
                    handler.generateTreeNode(node, dexFile);
                }
            }
        }
    }

    public static final class encoded_catch_handler extends FileComponent implements GenerateTreeNodeDexFile {

        public final Type_sleb128 size;
        public final encoded_type_addr_pair[] handlers;
        public final Type_uleb128 catch_all_addr;

        encoded_catch_handler(PosDataInputStreamDex stream) throws IOException {
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
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat dexFile) {
            int floatPos = super.startPos;
            addNode(parentNode, floatPos, this.size.length, "size", this.size, "msg_encoded_catch_handler__size", Icons.Size);
            floatPos += this.size.length;
            if (this.handlers != null) {
                for (int i = 0; i < this.handlers.length; i++) {
                    final encoded_type_addr_pair pair = this.handlers[i];
                    final DefaultMutableTreeNode node = addNode(parentNode, pair.getStartPos(), pair.getLength(),
                            "handlers[%d]".formatted(i), pair.addr, "msg_encoded_catch_handler__handlers", Icons.Exception);
                    pair.generateTreeNode(node, dexFile);
                    floatPos += pair.getLength();
                }
            }
            if (this.catch_all_addr != null) {
                addNode(parentNode, floatPos, this.catch_all_addr.length, "catch_all_addr", this.catch_all_addr,
                        "msg_encoded_catch_handler__catch_all_addr", Icons.Offset);
            }
        }
    }

    public static final class encoded_type_addr_pair extends FileComponent implements GenerateTreeNodeDexFile {

        public final Type_uleb128 type_idx;
        public final Type_uleb128 addr;

        encoded_type_addr_pair(PosDataInputStreamDex stream) throws IOException {
            super.startPos = stream.getPos();
            this.type_idx = stream.Dex_uleb128();
            this.addr = stream.Dex_uleb128();
            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat dexFile) {
            final DexFile dex = (DexFile) dexFile;
            addNode(parentNode, super.startPos, this.type_idx.length, "type_idx",
                    FORMAT_STRING_STRING.formatted(this.type_idx, dex.get_type_ids_string(this.type_idx.value)),
                    "msg_encoded_type_addr_pair__type_idx", Icons.Class);
            addNode(parentNode, super.startPos + this.type_idx.length, this.addr.length, "addr", this.addr,
                    "msg_encoded_type_addr_pair__addr", Icons.Offset);
        }
    }
}
