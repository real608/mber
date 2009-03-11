//
// $Id$

package com.threerings.msoy.game.client {

import com.threerings.util.Name;

import com.threerings.presents.client.Client;
import com.threerings.presents.dobj.DObjectManager;

import com.threerings.crowd.chat.client.ChatDirector;
import com.threerings.crowd.chat.client.ChatFilter;
import com.threerings.crowd.client.LocationDirector;
import com.threerings.crowd.client.OccupantDirector;
import com.threerings.crowd.client.PlaceView;

import com.threerings.parlor.client.ParlorDirector;

import com.threerings.msoy.client.MsoyContext;
import com.threerings.msoy.client.Prefs;
import com.threerings.msoy.data.MsoyCodes;
import com.threerings.msoy.data.WorldCredentials;

import com.threerings.msoy.world.client.WorldContext;

import com.threerings.msoy.game.data.GameCredentials;
import com.threerings.msoy.game.data.PlayerObject;

/**
 * Provides context for games running in the World client via a liaison.
 */
public class LiaisonGameContext
    implements GameContext
{
    public function LiaisonGameContext (wctx :WorldContext)
    {
        _wctx = wctx;

        var gcreds :GameCredentials = new GameCredentials(null);

        // inherit our visitor id from our world creds
        var wcreds :WorldCredentials = (wctx.getClient().getCredentials() as WorldCredentials);
        gcreds.visitorId = wcreds.visitorId;

        // if we have a session token in our world credentials use that
        if (wcreds.sessionToken != null) {
            gcreds.sessionToken = wcreds.sessionToken;

        // otherwise if we're a permaguest, use that username
        } else if (Prefs.getPermaguestUsername() != null) {
            gcreds.setUsername(new Name(Prefs.getPermaguestUsername()));
        }
        // otherwise we're a brand new guest so we leave everything else blank

        _client = new Client(gcreds);
        _client.addServiceGroup(MsoyCodes.GAME_GROUP);

        // create our directors
        _locDtr = new GameLocationDirector(this);
        _chatDtr = new GameChatDirector(this);
        _parDtr = new ParlorDirector(this);
        // use all the same chat filters for games
        for each (var filter :ChatFilter in wctx.getMsoyChatDirector().getChatFilters()) {
            _chatDtr.addChatFilter(filter);
        }
    }
    
    // from PresentsContext
    public function getClient () :Client
    {
        return _client;
    }

    // from PresentsContext
    public function getDObjectManager () :DObjectManager
    {
        return _client.getDObjectManager();
    }

    // from CrowdContext
    public function getLocationDirector () :LocationDirector
    {
        return _locDtr;
    }

    // from CrowdContext
    public function getOccupantDirector () :OccupantDirector
    {
        return null; // NOT USED
    }

    // from CrowdContext
    public function getChatDirector () :ChatDirector
    {
        return _chatDtr;
    }

    // from CrowdContext
    public function setPlaceView (view :PlaceView) :void
    {
        _wctx.setPlaceView(view);
    }

    // from CrowdContext
    public function clearPlaceView (view :PlaceView) :void
    {
        _wctx.clearPlaceView(view);
    }

    // from ParlorContext
    public function getParlorDirector () :ParlorDirector
    {
        return _parDtr;
    }

    // from GameContext
    public function getMsoyContext () :MsoyContext
    {
        return _wctx;
    }

    // from GameContext
    public function getPlayerObject () :PlayerObject
    {
        return (_client.getClientObject() as PlayerObject);
    }

    // from GameContext
    public function backToWhirled (showLobby :Boolean) :void
    {
        _wctx.getGameDirector().backToWhirled(showLobby);
    }

    // from GameContext 
    public function showGameLobby () :void
    {
        _wctx.getGameDirector().displayCurrentLobby();
    }
    
    // from GameContext
    public function showGameInstructions () :void
    {
        _wctx.getGameDirector().viewGameInstructions();
    }

    // from GameContext
    public function showGameShop (itemType :int, catalogId :int = 0) :void
    {
        _wctx.getGameDirector().viewGameShop(itemType, catalogId);
    }

    // from GameContext
    public function showInvitePage (defmsg :String, token :String = "", roomId :int = 0) :void
    {
        _wctx.getGameDirector().viewInvitePage(defmsg, token, roomId);
    }

    // from GameContext
    public function showTrophies () :void
    {
        _wctx.getGameDirector().viewGameTrophies();
    }

    // from GameContext
    public function getOnlineFriends () :Array
    {
        return _wctx.getMemberObject().getSortedOnlineFriends();
    }
    
    // from GameContext
    public function getInviteToken () :String
    {
    	return _wctx.getGameDirector().getInviteToken();
    }
    
    // from GameContext
    public function getInviterMemberId () :int
    {
    	return _wctx.getGameDirector().getInviterMemberId();
    }

    protected var _wctx :WorldContext;
    protected var _client :Client;
    protected var _locDtr :LocationDirector;
    protected var _chatDtr :ChatDirector;
    protected var _parDtr :ParlorDirector;
}
}

import flash.events.TimerEvent;
import flash.utils.Timer;
import flash.utils.getTimer;

import com.threerings.crowd.client.LocationDirector;
import com.threerings.crowd.data.PlaceConfig;
import com.threerings.crowd.util.CrowdContext;

import com.threerings.msoy.client.Preloader;

import com.threerings.msoy.game.client.GameContext;

class GameLocationDirector extends LocationDirector
{
    public function GameLocationDirector (ctx :CrowdContext)
    {
        super(ctx);
    }

    override public function didMoveTo (placeId :int, config :PlaceConfig) :void
    {
        // if we're in the embed client and it has been less than 5 seconds since the preloader
        // started, delay this move so that we continue to display our Whirled splash ad for the
        // minimum desired amount of time
        var elapsed :int = getTimer() - Preloader.preloaderStart;
        if ((_ctx as GameContext).getMsoyContext().getMsoyClient().isEmbedded() &&
            (elapsed < MIN_EMBED_SPLASH_TIME)) {
            // TODO: there must be an easier way to do this
            var timer :Timer = new Timer(MIN_EMBED_SPLASH_TIME - elapsed, 1);
            timer.addEventListener(TimerEvent.TIMER, function () :void {
                didMoveTo(placeId, config); // can't use super here, yay actionscript!
            });
            timer.start();
        } else {
            super.didMoveTo(placeId, config);
        }
    }

    /** We require that our splash screen show for at least this many millis in the embed client. */
    protected static const MIN_EMBED_SPLASH_TIME :int = 5000;
}
