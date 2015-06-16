package org.freeinternals.format.pdf.basicobj;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.pdf.PDFStatics;
import org.freeinternals.format.pdf.Texts;

/**
 * PDF basic object Name, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.3.5</code>: Name Objects.
 *
 * @author Amos Shi
 */
public final class Name extends FileComponent implements GenerateTreeNode {

    /**
     * Name text in
     * <code>Raw</code> format.
     */
    private final String RawName;

    Name(PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        stream.skip(1);
        this.RawName = stream.readASCIIUntil(
                PDFStatics.WhiteSpace.SP,
                PDFStatics.WhiteSpace.LF,
                PDFStatics.WhiteSpace.CR,
                PDFStatics.DelimiterCharacter.LP,
                PDFStatics.DelimiterCharacter.RP,
                PDFStatics.DelimiterCharacter.LT,
                PDFStatics.DelimiterCharacter.GT,
                PDFStatics.DelimiterCharacter.LS,
                PDFStatics.DelimiterCharacter.RS,
                PDFStatics.DelimiterCharacter.LC,
                PDFStatics.DelimiterCharacter.RC,
                PDFStatics.DelimiterCharacter.SO,
                PDFStatics.DelimiterCharacter.PS);
        stream.backward(1);
        this.length = stream.getPos() - super.startPos; // The length includes 3 parts: '/' RawNameText ' '(Space at the End)

        // System.out.println(this.toString());   // Deubg output
    }

    public String getName() {
        // TODO - Change to the .getName() - Issue: No test case until now.
        //        The following if-clause is used to search for such case
        if (this.RawName.contains("#")) {
            System.out.println(this.toString());
        }
        return this.RawName;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Name Object: " + this.getName());
        DefaultMutableTreeNode nodeName = new DefaultMutableTreeNode(nodeComp);

        int pos = super.startPos;
        nodeName.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.Signature + (char) PDFStatics.DelimiterCharacter.SO)));
        pos += 1;

        int len = super.length - 1;
        nodeName.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                len,
                "Name = " + this.RawName)));

        parentNode.add(nodeName);
    }

    @Override
    public String toString() {
        return String.format("Name Object: Start Position = %d, Length = %d, Raw Name = '%s'",
                super.startPos,
                super.length,
                this.RawName);
    }
}
