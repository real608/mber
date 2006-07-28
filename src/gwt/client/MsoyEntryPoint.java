//
// $Id$

package client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.google.gwt.user.client.ui.RootPanel;

import com.threerings.msoy.web.client.WebCreds;
import com.threerings.msoy.web.client.WebUserService;
import com.threerings.msoy.web.client.WebUserServiceAsync;

/**
 * Handles some standard services for a top-level MetaSOY web application
 * "entry point".
 */
public abstract class MsoyEntryPoint
    implements EntryPoint
{
    // from interface EntryPoint
    public void onModuleLoad ()
    {
        // get access to our service
        _usersvc = (WebUserServiceAsync)GWT.create(WebUserService.class);
        ServiceDefTarget target = (ServiceDefTarget)_usersvc;
        target.setServiceEntryPoint("/user");

        // create our standard logon panel which will provide access to our
        // credentials (TODO: have entry point manage creds?)
        RootPanel.get("logon").add(_logon = new LogonPanel(this, _usersvc));
    }

    /**
     * Called by our logon panel when the player logs on (or if we show up on
     * the page with valid credentials).
     */
    protected void didLogon (WebCreds creds)
    {
    }

    /**
     * Called by our logon panel if the player logs off.
     */
    protected void didLogoff ()
    {
    }

    protected WebUserServiceAsync _usersvc;
    protected LogonPanel _logon;
}
