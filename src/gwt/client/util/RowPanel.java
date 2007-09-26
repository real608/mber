//
// $Id$

package client.util;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

import com.threerings.gwt.ui.WidgetUtil;

/**
 * Works around browser's bullshit inability to put fucking spacing between cells in CSS without
 * also putting it around the outer edge of the whole table. Yay!
 */
public class RowPanel extends FlexTable
{
    public RowPanel ()
    {
        setCellPadding(0);
        setCellSpacing(0);
    }

    // @Override // from Panel
    public void add (Widget child)
    {
        int col = (getRowCount() > 0) ? getCellCount(0) : 0;
        setWidget(0, col, child);
        getFlexCellFormatter().setStyleName(0, col, "rowPanelCell");
    }
}
