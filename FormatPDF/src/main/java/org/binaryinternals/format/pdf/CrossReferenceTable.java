package org.binaryinternals.format.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.commonlib.PosDataInputStream.ASCIILine;

/// PDF Cross-Reference Table, see `PDF 32000-1:2008` `7.5.4`: Cross-Reference
/// Table.
///
/// @author Amos Shi
public final class CrossReferenceTable extends FileComponent implements GenerateTreeNode {

    static final String SIGNATURE = "xref";
    /// The subsections of the table.
    public final List<Subsection> Subsections;
    /// The first line current object.
    public final ASCIILine HeaderLine;

    CrossReferenceTable(final PosDataInputStream stream, final ASCIILine line) throws IOException {
        super.startPos = stream.getPos() - line.length();
        this.HeaderLine = line;

        final List<Subsection> subsections = new ArrayList<>();
        while (stream.hasNext()) {
            final ASCIILine next = stream.readASCIILine();
            if (next.line().strip().startsWith(Trailer.SIGNATURE)) {
                stream.backward(next.length());
                break;
            }
            if (!next.line().isBlank()) {
                subsections.add(new Subsection(stream, next));
            }
        }
        this.Subsections = List.copyOf(subsections);

        super.length = stream.getPos() - super.startPos;
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
        final JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Cross Reference Table");
        nodeComp.setDescription(Texts.getString(Texts.PDF_CROSS_REFERENCE_TABLE));
        final DefaultMutableTreeNode nodeCRT = new DefaultMutableTreeNode(nodeComp);
        parentNode.add(nodeCRT);

        int pos = this.startPos;
        nodeCRT.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.HeaderLine.line().length(),
                Texts.Signature + SIGNATURE)));
        pos += this.HeaderLine.line().length();
        nodeCRT.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                this.HeaderLine.newLineLength(),
                Texts.NewLine)));

        for (final Subsection subsection : this.Subsections) {
            subsection.generateTreeNode(nodeCRT);
        }
    }

    /// A subsection of the Cross-Reference Table: a header line with the first object number and the number of entries, followed by the entries.
    public static final class Subsection extends FileComponent implements GenerateTreeNode {

        /// First object number.
        public final int FirstObjectNumber;
        /// Number of entries.
        public final int NumberOfEntries;
        /// The first line of the Subsection.
        public final ASCIILine HeaderLine;
        /// Cross reference entries list.
        public final List<Entry> Entries;

        Subsection(final PosDataInputStream stream, final ASCIILine line) throws IOException {
            super.startPos = stream.getPos() - line.length();
            this.HeaderLine = line;

            final String[] header = line.line().strip().split("\\s+");
            try {
                if (header.length < 2) {
                    throw new NumberFormatException("2 numbers expected");
                }
                this.FirstObjectNumber = Integer.parseInt(header[0]);
                this.NumberOfEntries = Integer.parseInt(header[1]);
            } catch (NumberFormatException ex) {
                throw new FileFormatException(
                        "This is not a valid Cross Reference Table Subsection header. Position [%d], text [%s].".formatted(
                                super.startPos,
                                line),
                        ex);
            }

            final List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < this.NumberOfEntries; i++) {
                entries.add(new Entry(stream));
            }
            this.Entries = List.copyOf(entries);

            super.length = stream.getPos() - super.startPos;
        }

        @Override
        public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
            int pos = this.startPos;
            final DefaultMutableTreeNode nodeSS = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    super.length,
                    "Subsection: From %d Length %d".formatted(this.FirstObjectNumber, this.NumberOfEntries)));
            parentNode.add(nodeSS);

            nodeSS.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    this.HeaderLine.line().length(),
                    "Header '%s'".formatted(this.HeaderLine))));
            pos += this.HeaderLine.line().length();
            nodeSS.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    this.HeaderLine.newLineLength(),
                    Texts.NewLine)));

            for (final Entry entry : this.Entries) {
                entry.generateTreeNode(nodeSS);
            }
        }
    }

    /// One cross-reference entry:
    ///
    /// ```
    /// nnnnnnnnnn ggggg n eol
    /// ```
    ///
    /// The `eol` shall be 2 characters, but some writers use 1, which is
    /// accepted too.
    public static final class Entry extends FileComponent implements GenerateTreeNode {

        /// Length of an entry in bytes, end-of-line marker included.
        public static final int LENGTH = 20;
        /// Length of the byte offset field of an entry.
        public static final int OFFSET_LENGTH = 10;
        /// Length of the generation number field of an entry.
        public static final int GENERATIONNUMBER_LENGTH = 5;
        /// Entry type of an in-use object.
        public static final char TYPE_IN_USE = 'n';
        /// Entry type of a free object.
        public static final char TYPE_FREE = 'f';
        /// Length of the end-of-line marker.
        private static final int EOL_LENGTH_MAX = 2;
        /// Byte offset of the object for an in-use entry, or the object number of the next free object for a free entry.
        public final int Offset;
        /// Generation number of the object.
        public final int GenerationNumber;
        /// Entry type, [#TYPE_IN_USE] or [#TYPE_FREE].
        public final char Type;

        Entry(final PosDataInputStream stream) throws IOException {
            super.startPos = stream.getPos();

            try {
                this.Offset = Integer.parseInt(stream.readASCII(OFFSET_LENGTH).strip());
                stream.skipNBytes(1);
                this.GenerationNumber = Integer.parseInt(stream.readASCII(GENERATIONNUMBER_LENGTH).strip());
            } catch (NumberFormatException ex) {
                throw new FileFormatException("Invalid Cross Reference Table entry at %d.".formatted(super.startPos), ex);
            }
            stream.skipNBytes(1);
            this.Type = (char) stream.readByte();

            // SP CR, SP LF, CR LF, or a single CR / LF of a non-conforming writer
            final byte[] buf = stream.getBuf();
            int eol = 0;
            while (eol < EOL_LENGTH_MAX) {
                final int i = stream.getPos() - stream.getOffset();
                if (i >= buf.length || (buf[i] != ' ' && buf[i] != '\r' && buf[i] != '\n')) {
                    break;
                }
                stream.skipNBytes(1);
                eol++;
            }
            super.length = stream.getPos() - super.startPos;
        }

        /// Get meaning of the [#Type] field.
        ///
        /// @return The meaning
        public String getTypeMeaning() {
            return switch (this.Type) {
                case TYPE_IN_USE -> "In-Use (n)";
                case TYPE_FREE -> "Free (f)";
                default -> "ERROR!!! Un-recognized";
            };
        }

        @Override
        public void generateTreeNode(final DefaultMutableTreeNode parentNode) {
            int pos = this.startPos;
            final DefaultMutableTreeNode nodeEntry = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    super.length,
                    "Entry %010d - %s".formatted(this.Offset, this.getTypeMeaning())));
            parentNode.add(nodeEntry);

            nodeEntry.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    OFFSET_LENGTH,
                    "Offset: %d".formatted(this.Offset))));
            pos += OFFSET_LENGTH;
            nodeEntry.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    1,
                    "Separator")));
            pos += 1;
            nodeEntry.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    GENERATIONNUMBER_LENGTH,
                    "Generation Number: %d".formatted(this.GenerationNumber))));
            pos += GENERATIONNUMBER_LENGTH;
            nodeEntry.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    pos,
                    1,
                    "Separator")));
            pos += 1;
            final JTreeNodeFileComponent compType = new JTreeNodeFileComponent(
                    pos,
                    1,
                    "Type - " + this.Type);
            compType.setDescription(Texts.getString(Texts.PDF_CROSS_REFERENCE_TABLE_KEY));
            nodeEntry.add(new DefaultMutableTreeNode(compType));
            pos += 1;
            if (pos < this.startPos + super.length) {
                nodeEntry.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        pos,
                        this.startPos + super.length - pos,
                        "End of Line")));
            }
        }
    }
}
