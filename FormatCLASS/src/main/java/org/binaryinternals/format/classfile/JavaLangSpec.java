/*
 * JavaLangSpec.java    17:18 PM, May 19, 2014
 *
 * Copyright  2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.classfile;

/// Java Language specification constants.
///
/// @see <a href="http://docs.oracle.com/javase/specs/index.html">
/// The Java Language Specification
/// </a>
/// @author Amos Shi
public class JavaLangSpec {

    private JavaLangSpec() {
    }

    /// 51 character sequences, formed from ASCII characters, are reserved for
    /// use as keywords and cannot be used as identifiers.
    ///
    ///
    /// The keywords `const` and `goto` are reserved, even
    /// though they are not currently used. This may allow a Java compiler to
    /// produce better error messages if these C++ keywords incorrectly appear in
    /// programs.
    ///
    ///
    /// @see <a href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html#jls-3.9">
    /// Keywords
    /// </a>
    public enum Keyword {

        // ReservedKeyword

        /// Keyword `abstract`.
        ABSTRACT("abstract"),
        /// Keyword `assert`.
        ASSERT("assert"),
        /// Keyword `boolean`.
        BOOLEAN("boolean"),
        /// Keyword `break`.
        BREAK("break"),
        /// Keyword `byte`.
        BYTE("byte"),
        /// Keyword `case`.
        CASE("case"),
        /// Keyword `catch`.
        CATCH("catch"),
        /// Keyword `char`.
        CHAR("char"),
        /// Keyword `class`.
        CLASS("class"),
        /// Keyword `const`, reserved but not used.
        CONST("const"),
        /// Keyword `continue`.
        CONTINUE("continue"),
        /// Keyword `default`.
        DEFAULT("default"),
        /// Keyword `do`.
        DO("do"),
        /// Keyword `double`.
        DOUBLE("double"),
        /// Keyword `else`.
        ELSE("else"),
        /// Keyword `enum`.
        ENUM("enum"),
        /// Keyword `extends`.
        EXTENDS("extends"),
        /// Keyword `final`.
        FINAL("final"),
        /// Keyword `finally`.
        FINALLY("finally"),
        /// Keyword `float`.
        FLOAT("float"),
        /// Keyword `for`.
        FOR("for"),
        /// Keyword `if`.
        IF("if"),
        /// Keyword `goto`, reserved but not used.
        GOTO("goto"),
        /// Keyword `implements`.
        IMPLEMENTS("implements"),
        /// Keyword `import`.
        IMPORT("import"),
        /// Keyword `instanceof`.
        INSTANCEOF("instanceof"),
        /// Keyword `int`.
        INT("int"),
        /// Keyword `interface`.
        INTERFACE("interface"),
        /// Keyword `long`.
        LONG("long"),
        /// Keyword `native`.
        NATIVE("native"),
        /// Keyword `new`.
        NEW("new"),
        /// Keyword `package`.
        PACKAGE("package"),
        /// Keyword `private`.
        PRIVATE("private"),
        /// Keyword `protected`.
        PROTECTED("protected"),
        /// Keyword `public`.
        PUBLIC("public"),
        /// Keyword `return`.
        RETURN("return"),
        /// Keyword `short`.
        SHORT("short"),
        /// Keyword `static`.
        STATIC("static"),
        /// Keyword `strictfp`.
        STRICTFP("strictfp"),
        /// Keyword `super`.
        SUPER("super"),
        /// Keyword `switch`.
        SWITCH("switch"),
        /// Keyword `synchronized`.
        SYNCHRONIZED("synchronized"),
        /// Keyword `this`.
        THIS("this"),
        /// Keyword `throw`.
        THROW("throw"),
        /// Keyword `throws`.
        THROWS("throws"),
        /// Keyword `transient`.
        TRANSIENT("transient"),
        /// Keyword `try`.
        TRY("try"),
        /// Keyword `void`.
        VOID("void"),
        /// Keyword `volatile`.
        VOLATILE("volatile"),
        /// Keyword `while`.
        WHILE("while"),
        /// Keyword `_`, the underscore.
        UNDERSCORE("_");

        /// The keyword text.
        public final String text;

        Keyword(String k) {
            this.text = k;
        }
    }

    /// 17 character sequences, also formed from ASCII characters, may be
    /// interpreted as keywords or as other tokens, depending on the context in
    /// which they appear.
    ///
    /// @see <a href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html#jls-3.9">
    /// Keywords
    /// </a>
    public enum ContextualKeyword {

        /// Contextual keyword `exports`.
        EXPORTS("exports"),
        /// Contextual keyword `module`.
        MODULE("module"),
        /// Contextual keyword `non-sealed`.
        NON_SEALED("non-sealed"),
        /// Contextual keyword `open`.
        OPEN("open"),
        /// Contextual keyword `opens`.
        OPENS("opens"),
        /// Contextual keyword `permits`.
        PERMITS("permits"),
        /// Contextual keyword `provides`.
        PROVIDES("provides"),
        /// Contextual keyword `record`.
        RECORD("record"),
        /// Contextual keyword `requires`.
        REQUIRES("requires"),
        /// Contextual keyword `sealed`.
        SEALED("sealed"),
        /// Contextual keyword `to`.
        TO("to"),
        /// Contextual keyword `transitive`.
        TRANSITIVE("transitive"),
        /// Contextual keyword `uses`.
        USES("uses"),
        /// Contextual keyword `var`.
        VAR("var"),
        /// Contextual keyword `when`.
        WHEN("when"),
        /// Contextual keyword `with`.
        WITH("with"),
        /// Contextual keyword `yield`.
        YIELD("yield");

        /// The keyword text.
        public final String keyword;

        ContextualKeyword(String k) {
            this.keyword = k;
        }
    }
}
