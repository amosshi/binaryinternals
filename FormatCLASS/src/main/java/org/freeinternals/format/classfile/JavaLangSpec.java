/*
 * JavaLang.java    17:18 PM, May 19, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

/**
 * Java Language specification constants.
 *
 * @see <a href="http://docs.oracle.com/javase/specs/index.html">
 * The Java Language Specification
 * </a>
 * @author Amos Shi
 */
public class JavaLangSpec {

    /**
     * 50 character sequences, formed from ASCII letters, are reserved for use
     * as keywords and cannot be used as identifiers.
     *
     * <p>
     * The keywords <code>const</code> and <code>goto</code> are reserved, even
     * though they are not currently used. This may allow a Java compiler to
     * produce better error messages if these C++ keywords incorrectly appear in
     * programs.
     * </p>
     *
     * @see <a
     * href="http://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.9">
     * Keywords
     * </a>
     */
    public static class Keyword {

        public static final String kw_abstract = "abstract";
        public static final String kw_assert = "assert";
        public static final String kw_boolean = "boolean";
        public static final String kw_break = "break";
        public static final String kw_byte = "byte";
        public static final String kw_case = "case";
        public static final String kw_catch = "catch";
        public static final String kw_char = "char";
        public static final String kw_class = "class";
        public static final String kw_const = "const";
        public static final String kw_continue = "continue";
        public static final String kw_default = "default";
        public static final String kw_do = "do";
        public static final String kw_double = "double";
        public static final String kw_else = "else";
        public static final String kw_enum = "enum";
        public static final String kw_extends = "extends";
        public static final String kw_final = "final";
        public static final String kw_finally = "finally";
        public static final String kw_float = "float";
        public static final String kw_for = "for";
        public static final String kw_if = "if";
        public static final String kw_goto = "goto";
        public static final String kw_implements = "implements";
        public static final String kw_import = "import";
        public static final String kw_instanceof = "instanceof";
        public static final String kw_int = "int";
        public static final String kw_interface = "interface";
        public static final String kw_long = "long";
        public static final String kw_native = "native";
        public static final String kw_new = "new";
        public static final String kw_package = "package";
        public static final String kw_private = "private";
        public static final String kw_protected = "protected";
        public static final String kw_public = "public";
        public static final String kw_return = "return";
        public static final String kw_short = "short";
        public static final String kw_static = "static";
        public static final String kw_super = "super";
        public static final String kw_switch = "switch";
        public static final String kw_synchronized = "synchronized";
        public static final String kw_this = "this";
        public static final String kw_throw = "throw";
        public static final String kw_throws = "throws";
        public static final String kw_transient = "transient";
        public static final String kw_try = "try";
        public static final String kw_void = "void";
        public static final String kw_volatile = "volatile";
        public static final String kw_while = "while";
    }

}
