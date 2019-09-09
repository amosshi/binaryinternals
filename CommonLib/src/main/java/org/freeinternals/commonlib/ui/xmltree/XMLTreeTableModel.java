package org.freeinternals.commonlib.ui.xmltree;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.freeinternals.commonlib.ui.jtreetable.TreeTableModel;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * A little change may be done on formatting, annotation, java doc, etc.
 * </p>
 *
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 * @see <a href="http://www.javalobby.org/java/forums/t19666.html">XML Viewer
 * for Swing</a>
 */
public final class XMLTreeTableModel implements TreeTableModel {

    /**
     * DOM Node.
     */
    private Node node;

    /**
     * Constructor.
     *
     * @param is Input Source
     * @throws ParserConfigurationException Error
     * @throws SAXException Error
     * @throws TransformerConfigurationException Error
     * @throws TransformerException Error
     */
    public XMLTreeTableModel(final InputSource is) throws ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException {
        this(DOMUtil.createDocument(is).getDocumentElement());
    }

    /**
     * Constructor.
     *
     * @param n Value for {@link #node}
     */
    public XMLTreeTableModel(final Node n) {
        this.node = n;
    }

    @Override
    public Object getRoot() {
        return node;
    }

    @Override
    public Object getChild(final Object parent, final int index) {
        Node n = (Node) parent;
        NamedNodeMap attrs = n.getAttributes();
        int attrCount = attrs != null ? attrs.getLength() : 0;
        if (index < attrCount) {
            return attrs.item(index);
        }
        NodeList children = n.getChildNodes();
        return children.item(index - attrCount);
    }

    @Override
    public int getChildCount(final Object parent) {
        Node n = (Node) parent;
        NamedNodeMap attrs = n.getAttributes();
        int attrCount = attrs != null ? attrs.getLength() : 0;
        NodeList children = n.getChildNodes();
        int childCount = children != null ? children.getLength() : 0;
        if (childCount == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
            return attrCount;
        } else {
            return attrCount + childCount;
        }
    }

    @Override
    public boolean isLeaf(final Object nodeObject) {
        return getChildCount(nodeObject) == 0;
    }

    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
        Node n = (Node) parent;
        Node childNode = (Node) child;

        NamedNodeMap attrs = n.getAttributes();
        int attrCount = attrs != null ? attrs.getLength() : 0;
        if (childNode.getNodeType() == Node.ATTRIBUTE_NODE) {
            for (int i = 0; i < attrCount; i++) {
                if (attrs.item(i) == child) {
                    return i;
                }
            }
        } else {
            NodeList children = n.getChildNodes();
            int childCount = children != null ? children.getLength() : 0;
            for (int i = 0; i < childCount; i++) {
                if (children.item(i) == child) {
                    return attrCount + i;
                }
            }
        }
        throw new RuntimeException("this should never happen!");
    }

    @Override
    public void addTreeModelListener(final TreeModelListener listener) {
        // not editable
    }

    @Override
    public void removeTreeModelListener(final TreeModelListener listener) {
        // not editable
    }

    @Override
    public void valueForPathChanged(final TreePath path, final Object newValue) {
        // not editable
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(final int column) {
        return column == 0 ? "Node" : "Value";
    }

    @Override
    public Class getColumnClass(final int column) {
        return column == 0 ? TreeTableModel.class : String.class;
    }

    @Override
    public Object getValueAt(final Object nodeObject, final int column) {
        if (column == 0) {
            return nodeObject;
        } else {
            Node n = (Node) nodeObject;
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                NodeList children = n.getChildNodes();
                int childCount = children != null ? children.getLength() : 0;
                if (childCount == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
                    return children.item(0).getNodeValue();
                }
            }
            return n.getNodeValue();
        }
    }

    @Override
    public boolean isCellEditable(final Object nodeObject, final int column) {
        return false;
    }

    @Override
    public void setValueAt(final Object aValue, final Object nodeObject, final int column) {
        // not editable
    }
}
