//
// $Id$

package com.threerings.msoy.game.client {

import mx.collections.ArrayCollection;

import mx.containers.VBox;
import mx.controls.ButtonBar;

import mx.core.ClassFactory;

import com.threerings.util.ArrayUtil;

import com.threerings.crowd.client.PlaceView;

import com.threerings.crowd.data.PlaceObject;

import com.threerings.flex.CommandButton;

import com.threerings.parlor.client.SeatednessObserver;
import com.threerings.parlor.client.TableDirector;
import com.threerings.parlor.client.TableObserver;

import com.threerings.parlor.data.Table;

import com.threerings.msoy.client.Msgs;
import com.threerings.msoy.client.MsoyContext;

import com.threerings.msoy.chat.client.ChatContainer;

import com.threerings.msoy.ui.MsoyList;

import com.threerings.msoy.game.data.LobbyObject;

/**
 * A panel that displays pending table games.
 */
public class LobbyPanel extends VBox
    implements PlaceView, TableObserver, SeatednessObserver
{
    /** Our log. */
    private const log :Log = Log.getLog(LobbyPanel);

    /** The lobby controller. */
    public var controller :LobbyController;

    /** The create-a-table button. */
    public var createBtn :CommandButton;

    /**
     * Create a new LobbyPanel.
     */
    public function LobbyPanel (ctx :MsoyContext, ctrl :LobbyController)
    {
        _ctx = ctx;
        controller = ctrl;
    }

    // from PlaceView
    public function willEnterPlace (plobj :PlaceObject) :void
    {
        // add all preexisting tables
        var lobbyObj :LobbyObject = (plobj as LobbyObject);
        for each (var table :Table in lobbyObj.tables.toArray()) {
            tableAdded(table);
        }
    }

    // from PlaceView
    public function didLeavePlace (plobj :PlaceObject) :void
    {
        // clear all the tables
        _tables.removeAll();
    }

    // from TableObserver
    public function tableAdded (table :Table) :void
    {
        _tables.addItem(table);
    }

    // from TableObserver
    public function tableUpdated (table :Table) :void
    {
        var idx :int = ArrayUtil.indexOf(_tables.source, table);
        if (idx >= 0) {
            _tables.setItemAt(table, idx);

        } else {
            log.warning("Never found table to update: " + table);
        }
    }

    // from TableObserver
    public function tableRemoved (tableId :int) :void
    {
        for (var ii :int = 0; ii < _tables.length; ii++) {
            var table :Table = (_tables.getItemAt(ii) as Table);
            if (table.tableId == tableId) {
                _tables.removeItemAt(ii);
                return;
            }
        }

        log.warning("Never found table to remove: " + tableId);
    }

    // from SeatednessObserver
    public function seatednessDidChange (isSeated :Boolean) :void
    {
        _isSeated = isSeated;
        createBtn.enabled = !isSeated;

        // wacky: I think I'd need to refresh the list or something
        // but apparently everything gets re-rendered when any element
        // changes, so I don't. If this turns out to be not true, then
        // here we'll want to do _tables.refresh() or, if we save
        // the list we can do list.updateList().
    }

    /**
     * Are we seated at a table?
     */
    public function isSeated () :Boolean
    {
        return _isSeated;
    }

    override protected function createChildren () :void
    {
        super.createChildren();

        var list :MsoyList = new MsoyList(_ctx);
        list.variableRowHeight = true;
        list.percentHeight = 100;
        list.percentWidth = 100;
        addChild(list);

        var factory :ClassFactory = new ClassFactory(TableRenderer);
        factory.properties = { ctx: _ctx, panel: this };
        list.itemRenderer = factory;
        list.dataProvider = _tables;

        createBtn = new CommandButton(LobbyController.CREATE_TABLE);
        createBtn.label = Msgs.GAME.get("b.create");

        var butbar :ButtonBar = new ButtonBar();
        butbar.addChild(createBtn);
        addChild(butbar);

        // and a chat box
        var chatbox :ChatContainer = new ChatContainer(_ctx);
        chatbox.percentWidth = 100;
        chatbox.percentHeight = 100;
        addChild(chatbox);
    }

    /** Buy one get one free. */
    protected var _ctx :MsoyContext;

    /** Are we seated? */
    protected var _isSeated :Boolean;

    /** The currently displayed tables. */
    protected var _tables :ArrayCollection = new ArrayCollection();
}
}
