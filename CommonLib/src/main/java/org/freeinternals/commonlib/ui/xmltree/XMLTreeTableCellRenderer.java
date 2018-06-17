package org.freeinternals.commonlib.ui.xmltree;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

/**
 * 
 * <p>
 * A little change may be done on formatting, annotation, java doc, etc.
 * </p>
 *
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 * @see javax.swing.plaf.basic.BasicTableHeaderUI.MouseInputHandler
 * @see <a href="http://www.javalobby.org/java/forums/t19666.html">XML Viewer for Swing</a>
 */
public class XMLTreeTableCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 4876543219876500005L;
    Color elementColor = new Color(0, 0, 128);
    Color attributeColor = new Color(0, 128, 0);

    public XMLTreeTableCellRenderer() {
        this.setOpenIcon(null);
        this.setClosedIcon(null);
        this.setLeafIcon(null);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Node node = (Node) value;
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                value = '<' + node.getNodeName() + '>';
                break;
            case Node.ATTRIBUTE_NODE:
                value = '@' + node.getNodeName();
                break;
            case Node.TEXT_NODE:
                value = "# text";
                break;
            case Node.COMMENT_NODE:
                value = "# comment";
                break;
            case Node.DOCUMENT_TYPE_NODE:
                DocumentType dtype = (DocumentType) node;
                value = "# doctype";
                break;
            default:
                value = node.getNodeName();
        }
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (!selected) {
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    setForeground(elementColor);
                    break;
                case Node.ATTRIBUTE_NODE:
                    setForeground(attributeColor);
                    break;
            }
        }
        return this;
    }
}
