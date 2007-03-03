//
// $Id$

package com.threerings.msoy.game.data;

import com.threerings.presents.dobj.DSet;

import com.threerings.crowd.data.PlaceObject;

import com.threerings.parlor.data.Table;
import com.threerings.parlor.data.TableLobbyObject;

import com.threerings.msoy.item.web.Game;

/**
 * Represents a lobby for a particular game.
 */
public class LobbyObject extends PlaceObject
    implements TableLobbyObject
{
    // AUTO-GENERATED: FIELDS START
    /** The field name of the <code>game</code> field. */
    public static final String GAME = "game";

    /** The field name of the <code>tables</code> field. */
    public static final String TABLES = "tables";
    // AUTO-GENERATED: FIELDS END

    /** The game that we're matchmaking for. If the game is mutable, this may be updated while the
     * lobby is resolved. */
    public Game game;

    /** The tables. */
    public DSet<Table> tables = new DSet<Table>();

    // from TableLobbyObject
    public DSet getTables ()
    {
        return tables;
    }

    // AUTO-GENERATED: METHODS START
    /**
     * Requests that the <code>game</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    public void setGame (Game value)
    {
        Game ovalue = this.game;
        requestAttributeChange(
            GAME, value, ovalue);
        this.game = value;
    }

    /**
     * Requests that the specified entry be added to the
     * <code>tables</code> set. The set will not change until the event is
     * actually propagated through the system.
     */
    public void addToTables (Table elem)
    {
        requestEntryAdd(TABLES, tables, elem);
    }

    /**
     * Requests that the entry matching the supplied key be removed from
     * the <code>tables</code> set. The set will not change until the
     * event is actually propagated through the system.
     */
    public void removeFromTables (Comparable key)
    {
        requestEntryRemove(TABLES, tables, key);
    }

    /**
     * Requests that the specified entry be updated in the
     * <code>tables</code> set. The set will not change until the event is
     * actually propagated through the system.
     */
    public void updateTables (Table elem)
    {
        requestEntryUpdate(TABLES, tables, elem);
    }

    /**
     * Requests that the <code>tables</code> field be set to the
     * specified value. Generally one only adds, updates and removes
     * entries of a distributed set, but certain situations call for a
     * complete replacement of the set value. The local value will be
     * updated immediately and an event will be propagated through the
     * system to notify all listeners that the attribute did
     * change. Proxied copies of this object (on clients) will apply the
     * value change when they received the attribute changed notification.
     */
    public void setTables (DSet<com.threerings.parlor.data.Table> value)
    {
        requestAttributeChange(TABLES, value, this.tables);
        @SuppressWarnings("unchecked") DSet<com.threerings.parlor.data.Table> clone =
            (value == null) ? null : value.typedClone();
        this.tables = clone;
    }
    // AUTO-GENERATED: METHODS END
}
