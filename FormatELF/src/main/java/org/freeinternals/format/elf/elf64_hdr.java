/*
 * elf64_hdr.java    Apr 10, 2020
 *
 * Copyright 2020, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.elf;

/**
 * ELF header for 64-bit system. In this class definition, we respect the name
 * defined in Linux source code.
 *
 * <pre>
 *   typedef struct elf64_hdr
 * </pre>
 *
 * @author Amos Shi
 *
 * @see
 * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
 */
public class elf64_hdr {

    //Elf64_Half e_type;
    //Elf64_Half e_machine;
    //Elf64_Word e_version;
    /**
     * Entry point virtual address
     */
    //Elf64_Addr e_entry;
    /**
     * Program header table file offset
     */
    //Elf64_Off e_phoff;
    /**
     * Section header table file offset
     */
    //Elf64_Off e_shoff;
    //Elf64_Word e_flags;
    //Elf64_Half e_ehsize;
    //Elf64_Half e_phentsize;
    //Elf64_Half e_phnum;
    //Elf64_Half e_shentsize;
    //Elf64_Half e_shnum;
    //Elf64_Half e_shstrndx;

}
