/*
 * JavaLangSpec.java    17:18 PM, May 19, 2014
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
     * href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-3.9">
     * Keywords
     * </a>
     */
    public enum Keyword {

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
        WHILE("while");

        public final String text;

        private Keyword(String k) {
            this.text = k;
        }
    }

    /**
     * The identifiers <code>var</code> and <code>yield</code> are restricted
     * identifiers because they are not allowed in some contexts.
     *
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-3.8">
     * Keywords
     * </a>
     */
    public enum RestrictedIdentifier {

        PERMITS("permits"),
        RECORD("record"),
        SEALED("sealed"),
        VAR("var"),
        YIELD("yield");

        public final String identifier;

        private RestrictedIdentifier(String id) {
            this.identifier = id;
        }
    }

    /**
     * A further ten character sequences are restricted keywords: open, module,
     * requires, transitive, exports, opens, to, uses, provides, and with.
     *
     * These character sequences are tokenized as keywords solely where they
     * appear as terminals in the ModuleDeclaration, ModuleDirective, and
     * RequiresModifier productions.
     *
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-3.9">
     * Keywords
     * </a>
     */
    public enum RestrictedKeyword {

        EXPORTS("exports"),
        MODULE("module"),
        NON_SEALED("non-sealed"),
        OPEN("open"),
        OPENS("opens"),
        PROVIDES("provides"),
        RECORD("record"),
        REQUIRES("requires"),
        SEALED("sealed"),
        TO("to"),
        TRANSITIVE("transitive"),
        USES("uses"),
        VAR("var"),
        WITH("with"),
        YIELD("yield");

        public final String keyword;

        private RestrictedKeyword(String k) {
            this.keyword = k;
        }
    }
}
