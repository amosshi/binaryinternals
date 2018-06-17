package org.freeinternals.format.pdf.basicobj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.pdf.Texts;

/**
 * Reference of PDF Indirect Object, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.3.10</code>: Indirect Object.
 *
 * @author Amos Shi
 */
public final class Reference extends FileComponent implements GenerateTreeNode {

    static final byte SIGNATURE = 'R';
    public final byte Value;
    /**
     * Component of current object.
     */
    protected List<FileComponent> formerComponents = Collections.synchronizedList(new ArrayList<FileComponent>(7));

    Reference(PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.Value = stream.readByte();
        this.length = stream.getPos() - super.startPos;

        // System.out.println(this.toString());   // Deubg output
    }

    /**
     * Length of the former objects {@link #formerComponents}.
     */
    public int getFormerLength() {
        int len = 0;
        if (this.formerComponents.size() > 0) {
            for (FileComponent comp : this.formerComponents) {
                len += comp.getLength();
            }
        }
        return len;
    }

    /**
     * Need to call current method to get the object length, instead of using
     * the {@link #length} field because the reference object total length would
     * include the former 2 objects.
     */
    @Override
    public int getLength() {
        return this.getFormerLength() + this.length;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos - this.getFormerLength(),
                this.getLength(),
                "Refence to Indirect Object");
        DefaultMutableTreeNode nodeRef = new DefaultMutableTreeNode(nodeComp);

        for (FileComponent comp : this.formerComponents) {
            if (comp instanceof GenerateTreeNode) {
                ((GenerateTreeNode) comp).generateTreeNode(nodeRef);
            }
        }

        nodeRef.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                1,
                String.valueOf((char) this.Value))));

        parentNode.add(nodeRef);
    }

    @Override
    public String toString() {
        return String.format("Refence to Indirect Object: Start Position = %d, Length = %d, Raw Value = '%s'",
                super.startPos,
                super.length,
                (char) this.Value);
    }
}
