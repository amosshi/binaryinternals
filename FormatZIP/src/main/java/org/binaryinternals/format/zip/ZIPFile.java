/*
 * ZIPFile.java Apr 19, 2011, 07:58
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.zip;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/// ZIP archive file, see
/// [APPNOTE.TXT](https://pkware.cachefly.net/webdocs/casestudies/APPNOTE.TXT).
///
/// The archive is located from the end: the end of central directory record,
/// the optional Zip64 end of central directory locator and record, then the
/// central directory, which points to each local file header.
///
/// @author Amos Shi
public final class ZIPFile extends FileFormat implements GenerateTreeNode {

    static final ResourceBundle MESSAGES = ResourceBundle.getBundle(ZIPFile.class.getPackageName() + ".MessagesBundle", Locale.ROOT);

    /// Minimal size for a valid zip file.
    public static final int ZIPFILE_MIN_LENGTH = EndOfCentralDirectoryRecord.MIN_LENGTH;
    /// Central file header signature of File header in Central directory
    /// structure.
    static final byte[] CENTRAL_FILE_HEADER = {(byte) 0x50, (byte) 0x4B, (byte) 0x01, (byte) 0x02};
    /// Local file header signature in Local file header.
    static final byte[] LOCAL_FILE_HEADER = {(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04};
    /// Header signature of Digital signature in Central directory structure.
    static final byte[] DIGITAL_SIG_HEADER = {(byte) 0x50, (byte) 0x4B, (byte) 0x05, (byte) 0x05};
    /// End of central dir signature in End of central directory record.
    static final byte[] CENTRAL_END = {(byte) 0x50, (byte) 0x4B, (byte) 0x05, (byte) 0x06};
    /// Signature in Zip64 end of central directory record.
    static final byte[] CENTRAL_ZIP64_RECORD = {(byte) 0x50, (byte) 0x4B, (byte) 0x06, (byte) 0x06};
    /// Signature in Zip64 end of central directory locator.
    static final byte[] CENTRAL_ZIP64_LOCATOR = {(byte) 0x50, (byte) 0x4B, (byte) 0x06, (byte) 0x07};
    /// Archive extra data signature in Archive extra data record.
    static final byte[] ARCHIVE_EXTRA_DATA = {(byte) 0x50, (byte) 0x4B, (byte) 0x06, (byte) 0x08};
    /// Optional signature of the data descriptor.
    static final byte[] DATA_DESCRIPTOR = {(byte) 0x50, (byte) 0x4B, (byte) 0x07, (byte) 0x08};

    public ZIPFile(final Path file) throws IOException {
        super(file);

        if (this.fileByteArray.length < ZIPFILE_MIN_LENGTH) {
            throw new FileFormatException(
                    "The file length (%d) is less than the minimal allowed size.".formatted(this.fileByteArray.length));
        }

        this.parse();
    }

    private void parse() throws IOException {
        final PosDataInputStream stream = new PosDataInputStream(new PosByteArrayInputStream(super.fileByteArray));

        // End of central directory record
        stream.skipToEnd();
        final int posEnd = stream.backwardTo(CENTRAL_END);
        if (posEnd < 0) {
            throw new FileFormatException("This is not a valid zip file since cannot find 'end of central directory record'.");
        }
        final EndOfCentralDirectoryRecord end = new EndOfCentralDirectoryRecord(stream);
        this.addFileComponent(end);

        long entryCount = end.EntryTotalNumber;
        long centralDirectorySize = end.CentralDirectorySize;
        long centralDirectoryOffset = end.CentralDirectoryOffset;
        int centralDirectoryEnd = posEnd;

        // Zip64 end of central directory locator and record
        final int posLocator = posEnd - Zip64EndOfCentralDirectoryLocator.LENGTH;
        if (this.hasSignature(posLocator, CENTRAL_ZIP64_LOCATOR)) {
            stream.flyTo(posLocator);
            final Zip64EndOfCentralDirectoryLocator locator = new Zip64EndOfCentralDirectoryLocator(stream);
            this.addFileComponent(locator);

            // The offset in the locator is wrong when data is prepended to the archive,
            // then the record is expected right before the locator.
            long posRecord = locator.Zip64EndOffset;
            if (!this.hasSignature(posRecord, CENTRAL_ZIP64_RECORD)) {
                posRecord = -1;
                for (int candidate = posLocator - Zip64EndOfCentralDirectoryRecord.MIN_LENGTH; candidate >= 0; candidate--) {
                    if (this.hasSignature(candidate, CENTRAL_ZIP64_RECORD)) {
                        stream.flyTo(candidate + CENTRAL_ZIP64_RECORD.length);
                        final long recordSize = stream.readLongInLittleEndian();
                        if (recordSize == (long) posLocator - candidate - 12) {
                            posRecord = candidate;
                            break;
                        }
                    }
                }
            }
            if (this.hasSignature(posRecord, CENTRAL_ZIP64_RECORD)) {
                stream.flyTo((int) posRecord);
                final Zip64EndOfCentralDirectoryRecord record = new Zip64EndOfCentralDirectoryRecord(stream);
                this.addFileComponent(record);

                centralDirectoryEnd = record.getStartPos();
                if (end.isZip64()) {
                    entryCount = record.EntryTotalNumber;
                    centralDirectorySize = record.CentralDirectorySize;
                    centralDirectoryOffset = record.CentralDirectoryOffset;
                }
            }
        }
        if (end.isZip64() && centralDirectoryEnd == posEnd) {
            throw new FileFormatException("This is not a valid zip file since cannot find 'zip64 end of central directory record'.");
        }

        // If no entry, nothing to do
        if (entryCount < 1) {
            return;
        }
        if (entryCount > centralDirectorySize / CentralDirectoryStructure.FileHeader.MIN_LENGTH) {
            throw new FileFormatException(
                    "The entry count (%d) does not fit the size of the central directory (%d).".formatted(entryCount, centralDirectorySize));
        }

        // Bytes before the archive, like the launcher script of an executable jar,
        // shift all the offsets in the archive.
        final long prefixLength = centralDirectoryEnd - centralDirectorySize - centralDirectoryOffset;
        if (centralDirectoryOffset < 0 || prefixLength < 0) {
            throw new FileFormatException(
                    "The central directory (offset 0x%08X, size %d) does not fit before the end of central directory record at 0x%08X."
                            .formatted(centralDirectoryOffset, centralDirectorySize, centralDirectoryEnd));
        }

        // File header of central directory structure
        stream.flyTo((int) (prefixLength + centralDirectoryOffset));
        final CentralDirectoryStructure[] cds = new CentralDirectoryStructure[(int) entryCount];
        for (int i = 0; i < cds.length; i++) {
            cds[i] = new CentralDirectoryStructure(stream);
            this.addFileComponent(cds[i]);
        }

        // Local file header
        for (final CentralDirectoryStructure entry : cds) {
            final long posLocal = prefixLength + entry.header.RelativeOffsetOfLocalHeaderValue;
            if (posLocal < 0 || posLocal >= centralDirectoryEnd) {
                throw new FileFormatException("The local file header offset 0x%08X of '%s' is outside the archive.".formatted(
                        posLocal, entry.header.FileNameValue));
            }
            stream.flyTo((int) posLocal);
            this.addFileComponent(new LocalFileHeader(stream, entry.header));
        }
    }

    /// Whether `signature` is at `pos` of the file.
    private boolean hasSignature(final long pos, final byte[] signature) {
        return pos >= 0
                && pos <= this.fileByteArray.length - signature.length
                && Arrays.equals(this.fileByteArray, (int) pos, (int) pos + signature.length, signature, 0, signature.length);
    }

    @Override
    public String getContentTabName() {
        return "ZIP File";
    }

    @Override
    public Optional<Icons> getIcon() {
        return Optional.of(Icons.ZIP);
    }

    @Override
    public void generateTreeNode(final DefaultMutableTreeNode root) {
        int lastPos = 0;

        for (final FileComponent value : super.components.values()) {
            // Fill the gap first
            final int gap = value.getStartPos() - lastPos;
            if (gap > 0) {
                generateTreeNodeGap(root, lastPos, gap);
            }

            // Generate the tree nodes
            if (value instanceof GenerateTreeNode node) {
                node.generateTreeNode(root);
            }
            final int endPos = switch (value) {
                case LocalFileHeader local -> local.getEndPos();
                default -> value.getStartPos() + value.getLength();
            };
            lastPos = Math.max(lastPos, endPos);
        }

        if (lastPos < this.fileByteArray.length) {
            generateTreeNodeGap(root, lastPos, this.fileByteArray.length - lastPos);
        }
    }
}
