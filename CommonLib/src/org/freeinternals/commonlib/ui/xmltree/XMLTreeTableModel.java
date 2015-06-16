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
 * @see <a href="http://www.javalobby.org/java/forums/t19666.html">XML Viewer for Swing</a>
 */
public class XMLTreeTableModel implements TreeTableModel {

    private Node node;

    public XMLTreeTableModel(InputSource is) 
            throws ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException {
        this(DOMUtil.createDocument(is).getDocumentElement());
    }

    public XMLTreeTableModel(Node node) {
        this.node = node;
    }

    public Object getRoot() {
        return node;
    }

    public Object getChild(Object parent, int index) {
        Node n = (Node) parent;
        NamedNodeMap attrs = n.getAttributes();
        int attrCount = attrs != null ? attrs.getLength() : 0;
        if (index < attrCount) {
            return attrs.item(index);
        }
        NodeList children = n.getChildNodes();
        return children.item(index - attrCount);
    }

    public int getChildCount(Object parent) {
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

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    public int getIndexOfChild(Object parent, Object child) {
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

    public void addTreeModelListener(TreeModelListener listener) {
        // not editable
    }

    public void removeTreeModelListener(TreeModelListener listener) {
        // not editable
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        // not editable
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int column) {
        return column == 0 ? "Node" : "Value";
    }

    public Class getColumnClass(int column) {
        return column == 0 ? TreeTableModel.class : String.class;
    }

    public Object getValueAt(Object node, int column) {
        if (column == 0) {
            return node;
        } else {
            Node n = (Node) node;
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

    public boolean isCellEditable(Object node, int column) {
        return false;
    }

    public void setValueAt(Object aValue, Object node, int column) {
        // not editable
    }
}
