/*
 * JavaLangSpec.java    17:18 PM, May 19, 2014
 *
 * Copyright  2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

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
     * 51 character sequences, formed from ASCII characters, are reserved for
     * use as keywords and cannot be used as identifiers.
     *
     * <p>
     * The keywords <code>const</code> and <code>goto</code> are reserved, even
     * though they are not currently used. This may allow a Java compiler to
     * produce better error messages if these C++ keywords incorrectly appear in
     * programs.
     * </p>
     *
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html#jls-3.9">
     * Keywords
     * </a>
     */
    public enum Keyword {

        // ReservedKeyword

        ABSTRACT("abstract"),
        ASSERT("assert"),
        BOOLEAN("boolean"),
        BREAK("break"),
        BYTE("byte"),
        CASE("case"),
        CATCH("catch"),
        CHAR("char"),
        CLASS("class"),
        CONST("const"),
        CONTINUE("continue"),
        DEFAULT("default"),
        DO("do"),
        DOUBLE("double"),
        ELSE("else"),
        ENUM("enum"),
        EXTENDS("extends"),
        FINAL("final"),
        FINALLY("finally"),
        FLOAT("float"),
        FOR("for"),
        IF("if"),
        GOTO("goto"),
        IMPLEMENTS("implements"),
        IMPORT("import"),
        INSTANCEOF("instanceof"),
        INT("int"),
        INTERFACE("interface"),
        LONG("long"),
        NATIVE("native"),
        NEW("new"),
        PACKAGE("package"),
        PRIVATE("private"),
        PROTECTED("protected"),
        PUBLIC("public"),
        RETURN("return"),
        SHORT("short"),
        STATIC("static"),
        STRICTFP("strictfp"),
        SUPER("super"),
        SWITCH("switch"),
        SYNCHRONIZED("synchronized"),
        THIS("this"),
        THROW("throw"),
        THROWS("throws"),
        TRANSIENT("transient"),
        TRY("try"),
        VOID("void"),
        VOLATILE("volatile"),
        WHILE("while"),
        UNDERSCORE("_");

        public final String text;

        private Keyword(String k) {
            this.text = k;
        }
    }

    /**
     * 17 character sequences, also formed from ASCII characters, may be
     * interpreted as keywords or as other tokens, depending on the context in
     * which they appear.
     *
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html#jls-3.9">
     * Keywords
     * </a>
     */
    public enum ContextualKeyword {

        EXPORTS("exports"),
        MODULE("module"),
        NON_SEALED("non-sealed"),
        OPEN("open"),
        OPENS("opens"),
        PERMITS("permits"),
        PROVIDES("provides"),
        RECORD("record"),
        REQUIRES("requires"),
        SEALED("sealed"),
        TO("to"),
        TRANSITIVE("transitive"),
        USES("uses"),
        VAR("var"),
        WHEN("when"),
        WITH("with"),
        YIELD("yield");

        public final String keyword;

        private ContextualKeyword(String k) {
            this.keyword = k;
        }
    }
}
