package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import java.util.List;
import org.binaryinternals.commonlib.DefaultFileComponent;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.pdf.PDFStatics;
import org.binaryinternals.format.pdf.Texts;

/// Basic object analysis.
///
/// @author Amos Shi
public final class Analysis {

    private Analysis() {
    }

    /// Parse the next object, white-space or comment, and add it to
    /// `components`.
    ///
    /// @param stream The stream
    /// @param components The parsed components, the former 2 objects are
    /// merged into a [Reference]
    /// @return The parsed component, or `null` if the next byte does not
    /// start one; the byte is not consumed then
    /// @throws IOException Read failed
    public static FileComponent parseNextObject(final PosDataInputStream stream, final List<FileComponent> components) throws IOException {
        final int start = stream.getPos();
        final byte next = stream.readByte();

        final FileComponent comp = switch (next) {
            case PDFStatics.WhiteSpace.LF, PDFStatics.WhiteSpace.CR ->
                new DefaultFileComponent(start, 1, Texts.NewLine);
            case PDFStatics.WhiteSpace.SP ->
                new DefaultFileComponent(start, 1, Texts.Space);
            case PDFStatics.WhiteSpace.HT, PDFStatics.WhiteSpace.FF, PDFStatics.WhiteSpace.NUL ->
                new DefaultFileComponent(start, 1, Texts.WhiteSpace);
            case PDFStatics.DelimiterCharacter.PS -> {
                stream.backward(1);
                final String comment = stream.readASCIILine().line();
                stream.backward(stream.getPos() - start - comment.length());
                yield new DefaultFileComponent(start, comment.length(), "Comment: " + comment);
            }
            case PDFStatics.DelimiterCharacter.LP -> {                          //  '('  - Literal String
                stream.backward(1);
                yield new StringLiteral(stream);
            }
            case PDFStatics.DelimiterCharacter.LT -> {
                final boolean dictionary = stream.hasNext() && stream.readByte() == PDFStatics.DelimiterCharacter.LT;
                stream.backward(stream.getPos() - start);
                yield dictionary
                        ? new Dictionary(stream)                                //  '<<' - Dictionary
                        : new StringHexadecimal(stream);                        //  '<'  - Hexadecimal String
            }
            case PDFStatics.DelimiterCharacter.SO -> {                          //  '/' - Name
                stream.backward(1);
                yield new Name(stream);
            }
            case PDFStatics.DelimiterCharacter.LS -> {                          //  '[' - Array
                stream.backward(1);
                yield new Array(stream);
            }
            case Numeric.StartByte.SIGN_ADD, Numeric.StartByte.SIGN_MINUS, Numeric.StartByte.DECIMAL_POINT,
                 Numeric.StartByte.NUMBER_0, Numeric.StartByte.NUMBER_1, Numeric.StartByte.NUMBER_2,
                 Numeric.StartByte.NUMBER_3, Numeric.StartByte.NUMBER_4, Numeric.StartByte.NUMBER_5,
                 Numeric.StartByte.NUMBER_6, Numeric.StartByte.NUMBER_7, Numeric.StartByte.NUMBER_8,
                 Numeric.StartByte.NUMBER_9 -> {
                stream.backward(1);
                yield new Numeric(stream);
            }
            case Boolean.StartByte.TRUE, Boolean.StartByte.FALSE -> {           // 'true' 'false' - Boolean Object
                stream.backward(1);
                yield new Boolean(stream);
            }
            case Null.StartByte.NULL -> {                                       // 'null' - Null Object
                stream.backward(1);
                yield new Null(stream);
            }
            case Reference.SIGNATURE -> {                                       // '9 0 R' - Reference Indirect Object
                stream.backward(1);
                final Reference ref = new Reference(stream);
                mergeReferenceObjects(components, ref);
                yield ref;
            }
            default -> {                                                        // Unknown
                stream.backward(1);
                yield null;
            }
        };

        if (comp != null) {
            components.add(comp);
        }
        return comp;
    }

    /// Merge the former 2 objects before the [Reference] object 'R'.
    ///
    /// Example:
    ///
    /// ```
    /// 5 0 obj
    /// /Type /XObject
    /// /Subtype /Image
    /// /Filter 8 0 R
    /// ```
    ///
    /// When we found the `R`, we need to merge the former `8` and `0`
    /// together with the `R` object.
    private static void mergeReferenceObjects(final List<FileComponent> components, final Reference ref) {
        int objectCounter = 0;
        for (int i = components.size() - 1; i >= 0; i--) {
            if (components.get(i) instanceof BasicObject) {
                objectCounter++;
            }
            if (objectCounter == 2) {
                final List<FileComponent> former = components.subList(i, components.size());
                ref.formerComponents.addAll(former);
                former.clear();
                return;
            }
        }
    }
}
