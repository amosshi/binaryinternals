/*
 * JRowViewer.java    September 12, 2007, 2:28 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui.binviewer;

import org.freeinternals.commonlib.ui.HTMLKit;
import java.awt.Component;
import javax.swing.JTextPane;
import org.freeinternals.commonlib.ui.JBinaryViewer;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class JRowViewer extends JTextPane {

    private static final long serialVersionUID = 4876543219876500000L;
    public static final int WIDTH_VALUE = 110;

    public JRowViewer() {
        super();
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setEditable(false);
        this.setBorder(null);
        this.setContentType("text/html");
    }

    public void setData(final int rowStart, final int rowCount, final int rowMax) {

        // Update contents
        this.setText(null);
        if (rowCount <= 0) {
            return;
        }
        if (rowStart >= rowMax) {
            return;
        }

        StringBuilder sb = new StringBuilder(1024);
        sb.append(HTMLKit.Start());

        int rowValue = rowStart * JBinaryViewer.ROW_ITEM_MAX;
        for (int i = 0; i < rowCount; i++) {
            if ((rowStart + i) < rowMax) {
                sb.append(HTMLKit.Span(String.format("%08Xh\n", rowValue)));
                sb.append(HTMLKit.NewLine());
                rowValue += JBinaryViewer.ROW_ITEM_MAX;
            }
        }

        sb.append(HTMLKit.End());
        this.setText(sb.toString());
    }
}
