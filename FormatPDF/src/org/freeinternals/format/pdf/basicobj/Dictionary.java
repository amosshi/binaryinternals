package org.freeinternals.format.pdf.basicobj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.util.DefaultFileComponent;
import org.freeinternals.format.pdf.PDFStatics;
import org.freeinternals.format.pdf.Texts;

/**
 * PDF basic object Dictionary, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.3.7</code>: Dictionary Object.
 *
 * @author Amos Shi
 */
public class Dictionary extends FileComponent implements GenerateTreeNode {

    /**
     * Signature indicates the start of current object.
     */
    public static final String SIGNATURE_START = "<<";
    /**
     * Signature indicates the end of current object.
     */
    public static final String SIGNATURE_END = ">>";
    /**
     * Component of current object.
     */
    private List<FileComponent> components = Collections.synchronizedList(new ArrayList<FileComponent>(31));
    /**
     * Dictionary entries. <p> The first element of each entry is the key and
     * the second element is the value. The key shall be a name {@link Name}.
     * The value may be any kind of object, including another dictionary. A
     * dictionary entry whose value is null {@link Null} shall be treated the
     * same as if the entry does not exist. </p> <p> The entries in a dictionary
     * represent an associative table and as such shall be unordered even though
     * an arbitrary order may be imposed upon them when written in a file. That
     * ordering shall be ignored. </p> <p> Multiple entries in the same
     * dictionary shall not have the same key. </p>
     */
    public final HashMap<String, FileComponent> DictionaryEntries = new HashMap<String, FileComponent>();

    Dictionary(PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.parse(stream);
        this.organizeDictionary();
        super.length = stream.getPos() - super.startPos;

        // Debug Output
        System.out.println(this.toString());
        Iterator it = this.DictionaryEntries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        } // End while
    }

    private void parse(PosDataInputStream stream) throws IOException {
        // The '<<' sign
        stream.skip(SIGNATURE_START.length());

        FileComponent comp;
        Analysis analysis = new Analysis();

        byte next1;
        byte next2;
        boolean stop = false;
        while (stream.hasNext()) {
            comp = analysis.ParseNextObject(stream, this.components);
            if (comp == null) {
                next1 = stream.readByte();
                switch (next1) {
                    // Dictionary Ends
                    case PDFStatics.DelimiterCharacter.GT:
                        next2 = stream.readByte();
                        if (next2 == PDFStatics.DelimiterCharacter.GT) {
                            // Stop current Dictionary Object
                            stop = true;
                            // System.out.println("==== PDF Dictionary -- Ends at " + stream.getPos());   // Deubg output
                        }
                        break;
                    default:
                        System.out.println(String.format("ERROR ======== Dictionary.parse() - Location = %d (%X), Value = %s",
                                stream.getPos(), stream.getPos(), String.valueOf((char) next1)));
                        break;
                } // switch
                if (stop == true) {
                    break;   // Quit current while-loop
                }
            }
        } // End While
    }

    private void organizeDictionary() {
        String name = null;
        int counter = 0;
        for (FileComponent comp : this.components) {
            if (comp instanceof DefaultFileComponent) {
                continue;
            }

            counter++;

            if (counter == 1) {
                if (comp instanceof Name) {
                    name = ((Name) comp).getName();
                } else {
                    System.out.println("==== ERROR!!! Incorrect Dictionary Content. " + this.toString());
                    name = null;
                }
            } else if (counter == 2 && name != null) {
                this.DictionaryEntries.put(name, comp);
            }

            if (counter >= 2) {
                counter = 0;
            }
        }
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Dictionary Object");
        DefaultMutableTreeNode nodeDic = new DefaultMutableTreeNode(nodeComp);

        int pos = super.startPos;
        nodeDic.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                SIGNATURE_START.length(),
                Texts.Signature + SIGNATURE_START)));
        pos += SIGNATURE_START.length();

        int contLen = super.length - SIGNATURE_START.length() - SIGNATURE_END.length();
        DefaultMutableTreeNode nodeContent;
        nodeDic.add(nodeContent = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                contLen,
                "Dictionary Content")));

        for (FileComponent comp : this.components) {
            if (comp instanceof GenerateTreeNode) {
                ((GenerateTreeNode) comp).generateTreeNode(nodeContent);
            }
        }

        pos += contLen;
        nodeDic.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                SIGNATURE_END.length(),
                Texts.Signature + SIGNATURE_END)));

        parentNode.add(nodeDic);
    }

    @Override
    public String toString() {
        return String.format("Dictionary Object: Start Position = %d, Length = %d, Component Count = %d",
                super.startPos,
                super.length,
                this.components.size());
    }
}
