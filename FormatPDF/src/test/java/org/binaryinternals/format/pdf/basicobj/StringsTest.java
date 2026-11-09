package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringsTest {

    private static PosDataInputStream stream(final String text) {
        return new PosDataInputStream(new PosByteArrayInputStream(text.getBytes(StandardCharsets.ISO_8859_1)));
    }

    @Test
    void literalStringKeepsNestedParenthesesAndDecodesEscapes() throws IOException {
        final StringLiteral s = new StringLiteral(stream("(a (b) \\(c\\) \\\\ \\101\\0533 \\n\\t\\q end) tail"));
        assertEquals("a (b) (c) \\ A+3 \n\tq end", s.getString());
        assertEquals("(a (b) \\(c\\) \\\\ \\101\\0533 \\n\\t\\q end)".length(), s.getLength());
    }

    @Test
    void literalStringEndOfLines() {
        assertEquals("a\nb\nc", StringLiteral.unescape("a\r\nb\rc"));
        assertEquals("ab", StringLiteral.unescape("a\\\r\nb"));
        assertEquals("ab", StringLiteral.unescape("a\\\nb"));
    }

    @Test
    void hexadecimalStringKeepsHighBytesAndPadsOddDigits() throws IOException {
        assertEquals("Helloé", new StringHexadecimal(stream("<48656C 6C6FE9>")).getString());
        assertEquals("p", new StringHexadecimal(stream("<7>")).getString());
    }

    @Test
    void nameDecodesNumberSignCodes() {
        assertEquals("Name With#Hash", Name.decode("Name#20With#23Hash"));
        assertEquals("A#", Name.decode("A#"));
        assertEquals("A#zz", Name.decode("A#zz"));
    }

    @Test
    void dictionaryEntriesArePairedInOrder() throws IOException {
        final Dictionary d = new Dictionary(stream("<<\t/Type /Page /Parent 2 0 R /Scale .5 /Flag true /None null>>"));
        assertEquals(List.of("Type", "Parent", "Scale", "Flag", "None"),
                new ArrayList<>(d.DictionaryEntries.keySet()));
        final Reference parent = (Reference) d.DictionaryEntries.get("Parent");
        assertEquals("2 0 R".length(), parent.getLength());
        assertEquals("<<\t/Type /Page /Parent ".length(), parent.getStartPos());
    }
}
