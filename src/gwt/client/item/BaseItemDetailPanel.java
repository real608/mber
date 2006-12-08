//
// $Id$

package client.item;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.threerings.msoy.item.web.Item;
import com.threerings.msoy.item.web.ItemDetail;
import com.threerings.msoy.web.client.WebContext;

/**
 * Defines the base item detail panel from which we derive an inventory item detail and a catalog
 * item detail.
 */
public class BaseItemDetailPanel extends VerticalPanel
{
    protected BaseItemDetailPanel (WebContext ctx, Item item)
    {
        setStyleName("itemDetailPanel");
        _ctx = ctx;
        _item = item;

        // create our user interface
        HorizontalPanel title = new HorizontalPanel();
        title.setVerticalAlignment(ALIGN_BOTTOM);
        title.setStyleName("itemDetailTitle");
        title.add(_name = new Label(item.getDescription()));
        _name.setStyleName("itemDetailName");
        title.add(_creator = new Label(""));
        _creator.setStyleName("itemDetailCreator");
        // TODO: add a close box
        add(title);

        FlexTable middle = new FlexTable();
        middle.setStyleName("itemDetailContent");
        // a place for the item's preview visualization
        Widget preview = ItemUtil.createMediaView(item.getPreviewMedia(), false);
        middle.setWidget(0, 0, preview);
        middle.getFlexCellFormatter().setStyleName(0, 0, "itemDetailPreview");
        middle.getFlexCellFormatter().setRowSpan(0, 0, 2);
        // a place for details
        middle.setWidget(0, 1, _details = new VerticalPanel());
        middle.getFlexCellFormatter().setVerticalAlignment(0, 1, ALIGN_TOP);
        _details.setStyleName("itemDetailDetails");
        // a place for controls
        middle.setWidget(1, 0, _controls = new VerticalPanel());
        middle.getFlexCellFormatter().setVerticalAlignment(1, 0, ALIGN_BOTTOM);
        _controls.setStyleName("itemDetailControls");
        // allow derived classes to add their own nefarious bits
        createInterface(_details, _controls);
        add(middle);

        // TODO: add tag stuff

        // load up the item details
        _ctx.itemsvc.loadItemDetail(_ctx.creds, _item.getIdent(), new AsyncCallback() {
            public void onSuccess (Object result) {
                gotDetail(_detail = (ItemDetail)result);
            }
            public void onFailure (Throwable caught) {
                // TODO: translate, unhack
                _description.setText("Failed to load item details: " + caught);
            }
        });
    }

    protected void createInterface (VerticalPanel details, VerticalPanel controls)
    {
        details.add(_description = new Label("..."));
    }

    protected void gotDetail (ItemDetail detail)
    {
        _creator.setText("by " + detail.creator.toString());
    }

    protected WebContext _ctx;
    protected Item _item;
    protected ItemDetail _detail;

    protected VerticalPanel _details, _controls;
    protected Label _name, _creator, _description;
}
