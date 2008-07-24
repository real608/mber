package client.me;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import client.shell.Application;
import client.shell.Page;
import client.util.MsoyUI;

/**
 * Displays a summary of what Whirled is aimed at people who like to create things.
 * Whirleds, rooms, avatars, furniture and decors highlighted, no mention of games.
 */
public class CreatorsPanel extends SimplePanel
{
    public CreatorsPanel ()
    {
        setStyleName("creatorsPanel");
        AbsolutePanel content = new AbsolutePanel();
        content.setStyleName("Content");
        setWidget(content);
        
        PushButton getStartedButton = new PushButton();
        getStartedButton.setStyleName("GetStartedButton");
        getStartedButton.addClickListener(Application.createLinkListener(Page.ACCOUNT, "create"));
        content.add(getStartedButton, 342, 381);
        
        content.add(MsoyUI.createActionImage("/images/landing/creators_info.jpg", 
            Application.createLinkListener(Page.ACCOUNT, "create")), 15, 504);
                
        content.add(new LandingCopyright(), 0, 1085);
    }
}
