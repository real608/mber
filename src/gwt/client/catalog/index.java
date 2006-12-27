//
// $Id$

package client.catalog;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.threerings.msoy.item.web.Item;

import client.shell.MsoyEntryPoint;
import client.shell.ShellContext;

/**
 * Handles the MetaSOY inventory application.
 */
public class index extends MsoyEntryPoint
    implements HistoryListener
{
    /** Required to map this entry point to a page. */
    public static Creator getCreator ()
    {
        return new Creator() {
            public MsoyEntryPoint createEntryPoint () {
                return new index();
            }
        };
    }

    // from interface HistoryListener
    public void onHistoryChanged (String historyToken)
    {
        byte type = Item.AVATAR;
        try {
            if (historyToken != null) {
                type = Byte.parseByte(historyToken);
            }
        } catch (Exception e) {
            // whatever, just show the default
        }
        _catalog.selectType(type);
    }

    // @Override // from MsoyEntryPoint
    protected String getPageId ()
    {
        return "catalog";
    }

    // @Override // from MsoyEntryPoint
    protected ShellContext createContext ()
    {
        return _ctx = new CatalogContext();
    }

    // @Override from MsoyEntryPoint
    protected void onPageLoad ()
    {
        setContent(_catalog = new CatalogPanel(_ctx));
        History.addHistoryListener(this);
        onHistoryChanged(History.getToken());
    }

    protected CatalogContext _ctx;
    protected CatalogPanel _catalog;
}
