package org.freeinternals.commonlib.ui.xmltree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import org.freeinternals.commonlib.ui.jtreetable.JTreeTable;
import org.freeinternals.commonlib.ui.jtreetable.TreeTableModel;

/**
 * There are some issues in JTreeTable from Sun's Article.
 * To fix them, I sub-classed it.
 * 
 * <p>
 * A little change may be done on formatting, annotation, java doc, etc. 
 * The class name is <code>XMLTreeTable</code> originally in the link bellow.
 * </p>
 *
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 * @see <a href="http://www.javalobby.org/java/forums/t19666.html">XML Viewer for Swing</a>
 */
public class XMLTreeTable extends JTreeTable {

    private static final long serialVersionUID = 4876543219876500005L;

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public XMLTreeTable(TreeTableModel treeTableModel) {
        super(treeTableModel);
        setShowGrid(true);
        setGridColor(new Color(234, 234, 234));
        setIntercellSpacing(new Dimension(1, 1));
        new TableColumnResizer(this);

        this.initTree();
    }

    private void initTree(){
        if (super.tree == null) {
            return;
        }

        // Set customized cell renderer
        super.tree.setCellRenderer(new XMLTreeTableCellRenderer());

        // Expand all
        int old = 0;
        int now = 0;
        do {
            old = this.tree.getRowCount();
            for (int i = 0; i < old; i++) {
                this.tree.expandRow(i);
            }
            now = this.tree.getRowCount();
        } while (now > old);
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            // If the modifiers are not 0 (or the left mouse button),
            // tree may try and toggle the selection, and table
            // will then try and toggle, resulting in the
            // selection remaining the same. To avoid this, we
            // only dispatch when the modifiers are 0 (or the left mouse
            // button).
            if (me.getModifiers() == 0
                    || me.getModifiers() == InputEvent.BUTTON1_MASK) {
                for (int counter = getColumnCount() - 1; counter >= 0;
                        counter--) {
                    if (getColumnClass(counter) == TreeTableModel.class) {
                        MouseEvent newME = new MouseEvent(tree, me.getID(),
                                me.getWhen(), me.getModifiers(),
                                me.getX() - getCellRect(0, counter, true).x,
                                me.getY(), me.getClickCount(),
                                me.isPopupTrigger());
                        tree.dispatchEvent(newME);
                        break;
                    }
                }
            }
            return false;
        }
        return super.editCellAt(row, column, e);
    }

    /**
     * Override to make the height of scroll match viewpost height if smaller.
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return getPreferredSize().height < getParent().getHeight();
    }

    /**
     * Mouse press intended for resize shouldn't change row/col/cell selection.
     * @param row
     * @param column
     */
    // 
    @Override
    public void changeSelection(int row, int column, boolean toggle, boolean extend) {
        if(getCursor()==TableColumnResizer.resizeCursor) {
            return;
        }
        super.changeSelection(row, column, toggle, extend);
    }

}
