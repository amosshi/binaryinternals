package org.binaryinternals.format.pdf.basicobj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.pdf.PDFStatics;
import org.binaryinternals.format.pdf.Texts;

/// PDF basic object Dictionary, see `PDF 32000-1:2008` `7.3.7`: Dictionary
/// Object.
///
/// @author Amos Shi
public final class Dictionary extends FileComponent implements BasicObject {

    private static final Logger LOGGER = Logger.getLogger(Dictionary.class.getName());

    /// Signature indicates the start of current object.
    public static final String SIGNATURE_START = "<<";
    /// Signature indicates the end of current object.
    public static final String SIGNATURE_END = ">>";
    /// Components of current object, including white-space.
    private final List<FileComponent> components = new ArrayList<>();
    /// Whether the closing `>>` is found.
    private final boolean closed;
    /// Dictionary entries, in the order of the file.
    ///
    /// The first element of each entry is the key and the second element is
    /// the value. The key shall be a name [Name]. The value may be any kind of
    /// object, including another dictionary. A dictionary entry whose value is
    /// null [Null] shall be treated the same as if the entry does not exist.
    ///
    /// Multiple entries in the same dictionary shall not have the same key.
    public final SequencedMap<String, BasicObject> DictionaryEntries;

    Dictionary(final PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.closed = this.parse(stream);
        super.length = stream.getPos() - super.startPos;
        this.DictionaryEntries = Collections.unmodifiableSequencedMap(this.organizeDictionary());
    }

    private boolean parse(final PosDataInputStream stream) throws IOException {
        stream.skipNBytes(SIGNATURE_START.length());

        while (stream.hasNext()) {
            if (Analysis.parseNextObject(stream, this.components) == null) {
                final byte next = stream.readByte();
                if (next == PDFStatics.DelimiterCharacter.GT
                        && stream.hasNext()
                        && stream.readByte() == PDFStatics.DelimiterCharacter.GT) {
                    return true;
                }
                LOGGER.log(Level.WARNING, () -> "Unexpected byte in Dictionary at %d: '%c'".formatted(
                        stream.getPos() - 1, (char) Byte.toUnsignedInt(next)));
            }
        }
        return false;
    }

    /// Pair the objects as key and value.
    private SequencedMap<String, BasicObject> organizeDictionary() {
        final SequencedMap<String, BasicObject> entries = new LinkedHashMap<>();
        Name key = null;
        for (final FileComponent comp : this.components) {
            if (!(comp instanceof BasicObject obj)) {
                continue;     // White-space
            }

            if (key != null) {
                entries.put(key.getName(), obj);
                key = null;
            } else if (obj instanceof Name name) {
                key = name;
            } else {
                LOGGER.log(Level.WARNING, () -> "Dictionary key at %d is not a Name: %s".formatted(obj.getStartPos(), obj));
            }
        }
        return entries;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final DefaultMutableTreeNode nodeDic = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Dictionary Object"));

        int pos = super.startPos;
        nodeDic.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                SIGNATURE_START.length(),
                Texts.Signature + SIGNATURE_START)));
        pos += SIGNATURE_START.length();

        final int contLen = super.length - SIGNATURE_START.length() - (this.closed ? SIGNATURE_END.length() : 0);
        final DefaultMutableTreeNode nodeContent = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                contLen,
                "Dictionary Content"));
        nodeDic.add(nodeContent);

        for (final FileComponent comp : this.components) {
            if (comp instanceof GenerateTreeNode node) {
                node.generateTreeNode(nodeContent);
            }
        }

        if (this.closed) {
            nodeDic.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos + contLen,
                    SIGNATURE_END.length(),
                    Texts.Signature + SIGNATURE_END)));
        }

        parentNode.add(nodeDic);
    }

    @Override
    public String toString() {
        return "Dictionary Object: Start Position = %d, Length = %d, Component Count = %d".formatted(
                super.startPos,
                super.length,
                this.components.size());
    }
}
