//
// $Id$

package com.threerings.msoy.room.client {

import mx.containers.HBox;
import mx.controls.Label;
import mx.controls.scrollClasses.ScrollBar;

import com.threerings.flex.CommandButton;
import com.threerings.flex.FlexUtil;

import com.threerings.msoy.client.Msgs;
import com.threerings.msoy.data.MsoyCodes;
import com.threerings.msoy.ui.MediaControls;

import com.threerings.msoy.item.data.all.Item;
import com.threerings.msoy.item.data.all.Audio;

import com.threerings.msoy.world.client.WorldContext;

import com.threerings.msoy.room.data.MemberInfo;
import com.threerings.msoy.room.data.RoomObject;

public class PlaylistRenderer extends HBox
{
    public var wctx :WorldContext;
    public var roomObj :RoomObject;

    override public function set data (value :Object) :void
    {
        super.data = value;
        if (value == null) {
            return;
        }

        var audio :Audio = Audio(value);
        var isPlayingNow :Boolean = (roomObj.currentSongId == audio.itemId);
        var isManager :Boolean = wctx.getMsoyController().canManagePlace();
        var canRemove :Boolean = isManager || (wctx.getMyId() == audio.ownerId);

        FlexUtil.setVisible(_playBtn, isManager);
        _playBtn.enabled = !isPlayingNow;
        _name.text = audio.name;
        if (audio.used != Item.UNUSED) {
            _name.toolTip = Msgs.WORLD.get("i.manager_music");
        } else {
            var info :MemberInfo = roomObj.getMemberInfo(audio.ownerId);
            _name.toolTip = Msgs.WORLD.get("i.visitor_music",
                (info != null) ? info.username : Msgs.WORLD.get("m.none"));
        }
        _name.setStyle("fontWeight", isPlayingNow ? "bold" : "normal");
        FlexUtil.setVisible(_removeBtn, canRemove);
        _removeBtn.enabled = canRemove;
    }

    override protected function createChildren () :void
    {
        super.createChildren();

        _playBtn = new CommandButton("\u25B6", doPlay);
        addChild(_playBtn);

        _name = FlexUtil.createLabel(null);
        _name.width = MediaControls.WIDTH - ScrollBar.THICKNESS - 70; // 70 pix for buttons/spacing
        addChild(_name);

        _removeBtn = new CommandButton(null, doRemove);
        _removeBtn.styleName = "closeButton";
        addChild(_removeBtn);
    }

    protected function doPlay () :void
    {
        roomObj.roomService.jumpToSong(wctx.getClient(), Audio(data).itemId,
            wctx.confirmListener(null, MsoyCodes.WORLD_MSGS, null, _playBtn));
    }

    protected function doRemove () :void
    {
        roomObj.roomService.modifyPlaylist(wctx.getClient(), Audio(data).itemId, false,
            wctx.confirmListener(null, MsoyCodes.WORLD_MSGS));
        _removeBtn.enabled = false;
    }

    protected var _playBtn :CommandButton;
    protected var _name :Label;
    protected var _removeBtn :CommandButton;
}
}