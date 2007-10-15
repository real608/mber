//
// $Id$

package client.game;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.threerings.msoy.item.data.all.MediaDesc;
import com.threerings.msoy.web.data.PlayerRating;

import client.shell.Application;
import client.shell.Page;
import client.util.MediaUtil;
import client.util.MsoyUI;

/**
 * Displays top-rankings for a particular game.
 */
public class TopRankingPanel extends FlexTable
{
    public TopRankingPanel (int gameId, boolean onlyMyFriends)
    {
        _gameId = gameId;
        _onlyMyFriends = onlyMyFriends;

        setStyleName("topRankingPanel");
        setCellSpacing(0);
        setCellPadding(0);
        setText(0, 0, "Loading ratings...");
    }

    // @Override // from UIObject
    public void setVisible (boolean visible)
    {
        super.setVisible(visible);
        if (!visible || _gameId == 0) {
            return;
        }

        // it's possible to have this tab shown and be a guest; so we avoid freakoutage
        if (_onlyMyFriends && (CGame.getMemberId() == 0)) {
            setText(0, 0, "Log in to see your rankings.");
            break;
        }

        CGame.gamesvc.loadTopRanked(CGame.ident, _gameId, _onlyMyFriends, new AsyncCallback() {
            public void onSuccess (Object result) {
                gotRankings((PlayerRating[][])result);
            }
            public void onFailure (Throwable caught) {
                CGame.log("getTopRanked failed", caught);
                setText(0, 0, CGame.serverError(caught));
            }
        });
        _gameId = 0; // note that we've asked for our data
    }

    protected void gotRankings (PlayerRating[][] results)
    {
        int col = 0;
        if (results[0].length > 0) {
            displayRankings("Single Player", col, results[0]);
            col++;
        }
        if (results[1].length > 0) {
            displayRankings("Multiplayer", col, results[1]);
            col++;
        }
        if (col == 0) {
            setText(0, 0, _onlyMyFriends ? "You and your friends have no rankings in this game." :
                    "No one is ranked in this game.");
        } else {
            int row = getRowCount();
            setText(row, 0, _onlyMyFriends ? "Top ranked players among you and your friends." :
                    "Top ranked players in all the Whirled.");
            getFlexCellFormatter().setStyleName(row, 0, "tipLabel");
            getFlexCellFormatter().setHorizontalAlignment(row, 0, HasAlignment.ALIGN_CENTER);
            getFlexCellFormatter().setColSpan(row, 0, 2);
        }
    }

    protected void displayRankings (String header, int col, PlayerRating[] results)
    {
        setText(0, col, header);
        getFlexCellFormatter().setStyleName(0, col, "Header");
        for (int ii = 0; ii < results.length; ii++) {
            setWidget(1 + ii*2, col, new PlayerRatingPanel(results[ii]));
        }
    }

    protected static class PlayerRatingPanel extends FlexTable
        implements ClickListener
    {
        public PlayerRatingPanel (PlayerRating rating) {
            _rating = rating;

            int width = 2*MediaDesc.DIMENSIONS[MediaDesc.HALF_THUMBNAIL_SIZE]/3;
            int height = 2*MediaDesc.DIMENSIONS[MediaDesc.HALF_THUMBNAIL_SIZE+1]/3;
            Widget photo = MediaUtil.createMediaView(rating.photo, width, height);
            if (photo instanceof Image) {
                ((Image) photo).addClickListener(this);
                photo.setStyleName("actionLabel");
            }
            setWidget(0, 0, photo);
            getFlexCellFormatter().setRowSpan(0, 0, 2);
            getFlexCellFormatter().setWidth(0, 0, width + "px");
            getFlexCellFormatter().setHeight(0, 0, height + "px");
            getFlexCellFormatter().setHorizontalAlignment(0, 0, HasAlignment.ALIGN_CENTER);
            setWidget(0, 1, MsoyUI.createActionLabel(rating.name.toString(), this));
            setWidget(1, 0, new RatingLabel(rating.rating));
        }

        public void onClick (Widget sender) {
            Application.go(Page.PROFILE, "" + _rating.name.getMemberId());
        }

        protected PlayerRating _rating;
    }

    protected int _gameId;
    protected boolean _onlyMyFriends;
}
