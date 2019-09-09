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
public final class XMLTreeTableCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 4876543219876500005L;
    /** Color for element. */
    private final Color elementColor = Color.BLUE;
    /** Color for attribute. */
    private final Color attributeColor = Color.GREEN;

    /**
     * Constructor.
     */
    public XMLTreeTableCellRenderer() {
        this.setOpenIcon(null);
        this.setClosedIcon(null);
        this.setLeafIcon(null);
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        Object newValue;
        Node node = (Node) value;
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                newValue = '<' + node.getNodeName() + '>';
                break;
            case Node.ATTRIBUTE_NODE:
                newValue = '@' + node.getNodeName();
                break;
            case Node.TEXT_NODE:
                newValue = "# text";
                break;
            case Node.COMMENT_NODE:
                newValue = "# comment";
                break;
            case Node.DOCUMENT_TYPE_NODE:
                DocumentType dtype = (DocumentType) node;
                newValue = "# doctype";
                break;
            default:
                newValue = node.getNodeName();
        }
        super.getTreeCellRendererComponent(tree, newValue, sel, expanded, leaf, row, hasFocus);
        if (!selected) {
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    setForeground(elementColor);
                    break;
                case Node.ATTRIBUTE_NODE:
                    setForeground(attributeColor);
                    break;
                default:
                    break;
            }
        }
        return this;
    }
}
