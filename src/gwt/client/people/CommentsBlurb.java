//
// $Id$

package client.people;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Command;

import com.threerings.gwt.ui.WidgetUtil;
import com.threerings.gwt.util.ExpanderResult;

import com.threerings.msoy.comment.data.all.CommentType;
import com.threerings.msoy.person.gwt.FeedMessage;
import com.threerings.msoy.profile.gwt.ProfileService;
import com.threerings.msoy.profile.gwt.ProfileServiceAsync;
import com.threerings.msoy.web.gwt.Activity;

import client.comment.CommentsPanel;
import client.person.FeedMessagePanel;
import client.person.FeedUtil;
import client.shell.CShell;
import client.shell.ShellMessages;
import client.ui.MsoyUI;
import client.ui.PromptPopup;
import client.util.ClickCallback;

/**
 * Displays a comment wall on a member's profile.
 */
public class CommentsBlurb extends Blurb
{
    @Override // from Blurb
    public void init (ProfileService.ProfileResult pdata)
    {
        super.init(pdata);

        setHeader(_msgs.commentsTitle(), new Image("/images/me/icon_social.png"));
        setContent(_wall = new WallPanel(pdata.name.getId()));
        _wall.expand();
    }

    protected class WallPanel extends CommentsPanel
    {
        public WallPanel (int memberId) {
            super(CommentType.PROFILE_WALL, memberId, true);
            addStyleName("Wall");
        }

        @Override
        protected void addControls ()
        {
            super.addControls();
            if (CShell.getMemberId() != _name.getId()) {
                _commentControls.add(WidgetUtil.makeShim(7, 1));
                Button pokeButton = new Button(_msgs.poke());
                new PokeClickCallback(pokeButton, _msgs.pokeConfirm(_name.toString()));
                _commentControls.add(pokeButton);
            }

        //allow friend enabling of profile comments: we can check if the member posting is the owner of the wall by looking at the profile URL id 
        if (CShell.getMemberId() == _name.getId()) {
	     _commentControls.add(WidgetUtil.makeShim(7, 1));
        Button _enableFriendComments = new Button("Commenting Privileges");    

        if(true){
        _enableFriendComments.addClickHandler(new PromptPopup("Would you like to make your profile comments for friends only?", enableFriendsComments()) );
        } else if(false){
        _enableFriendComments.addClickHandler(new PromptPopup("Would you like to make your profile comments for the public?", enablePublicComments()) );
        }     
        _commentControls.add(_enableFriendComments);
        } //end the friend enabling of profile comments code

    }

    public Command enableFriendsComments()
    {
    	return null;
    }
    
    public Command enablePublicComments()
    {
    	return null;
    }
        @Override
        protected Widget createElement (Activity activity)
        {
            if (activity instanceof FeedMessage) {
                FeedMessage message = (FeedMessage) activity;
                String possessive = (_name.getId() == CShell.getMemberId()) ?
                    _msgs.your() : _msgs.their(MsoyUI.escapeHTML(_name.toString()));
                return new FeedMessagePanel(message, true, possessive);
            }
            return super.createElement(activity);
        }

        @Override
        protected void fetchElements (AsyncCallback<ExpanderResult<Activity>> callback)
        {
            _profilesvc.loadActivity(_name.getId(), _earliest, 20, callback);
        }

        @Override
        public void addElements (List<Activity> result, boolean append)
        {
            List<Activity> aggregated = Lists.newArrayList();
            _earliest = FeedUtil.aggregate(result, aggregated);
            super.addElements(aggregated, append);
        }
    }

    protected class PokeClickCallback extends ClickCallback<FeedMessage>
    {
        protected PokeClickCallback (Button button, String confirmMessage)
        {
            super(button, confirmMessage);
        }

        @Override
        protected boolean callService ()
        {
            _profilesvc.poke(_name.getId(), this);
            return true;
        }

        @Override
        protected boolean gotResult (FeedMessage message)
        {
            _wall.addElements(Collections.singletonList((Activity) message), false);
            return false;
        }
    }

    protected WallPanel _wall;

    protected static final PeopleMessages _msgs = GWT.create(PeopleMessages.class);
    protected static final ShellMessages _cmsgs = GWT.create(ShellMessages.class);
    protected static final ProfileServiceAsync _profilesvc = GWT.create(ProfileService.class);
}
