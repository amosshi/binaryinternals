/*
 * Elf64_Addr.java    Apr 10, 2020
 *
 * Copyright 2020, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.elf;

import java.math.BigInteger;

/**
 * 64-bit ELF base type: <code>Elf64_Addr</code>.
 *
 * <pre>
 *   typedef __u64    Elf64_Addr;
 *   typedef __u16    Elf64_Half;
 *   typedef __s16    Elf64_SHalf;
 *   typedef __u64    Elf64_Off;
 *   typedef __s32    Elf64_Sword;
 *   typedef __u32    Elf64_Word;
 *   typedef __u64    Elf64_Xword;
 *   typedef __s64    Elf64_Sxword;
 * </pre>
 *
 *
 * @author Amos Shi
 * @see
 * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
 */
public class Elf64_Addr {

    public static final int LENGTH = 8;
    
    public final BigInteger value;

    Elf64_Addr(BigInteger big) {
        this.value = big;
    }
}
