//
// $Id$

package com.threerings.msoy.spam.server;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.util.IntSet;

import com.threerings.presents.annotation.BlockingThread;

import com.threerings.util.MessageBundle;

import com.threerings.msoy.data.all.DeploymentConfig;
import com.threerings.msoy.data.all.MediaDesc;

import com.threerings.msoy.person.gwt.FeedItemGenerator;
import com.threerings.msoy.person.gwt.FeedItemGenerator.Builder;
import com.threerings.msoy.person.gwt.FeedItemGenerator.Icon;
import com.threerings.msoy.person.gwt.FeedItemGenerator.Media;
import com.threerings.msoy.person.gwt.FeedItemGenerator.Messages;
import com.threerings.msoy.person.gwt.FeedItemGenerator.Plural;
import com.threerings.msoy.person.gwt.FeedMessageType.Category;
import com.threerings.msoy.person.gwt.FeedMessage;
import com.threerings.msoy.person.gwt.FeedMessageAggregator;
import com.threerings.msoy.person.gwt.FeedMessageType;
import com.threerings.msoy.person.gwt.MyWhirledData.FeedCategory;
import com.threerings.msoy.person.server.FeedLogic;
import com.threerings.msoy.server.ServerConfig;
import com.threerings.msoy.server.ServerMessages;
import com.threerings.msoy.server.persist.MemberRecord;
import com.threerings.msoy.server.persist.MemberRepository;
import com.threerings.msoy.server.util.MailSender;
import com.threerings.msoy.server.util.MailSender.Parameters;

import com.threerings.msoy.web.gwt.MarkupBuilder;
import com.threerings.msoy.web.gwt.Pages;

import static com.threerings.msoy.Log.log;

@Singleton @BlockingThread
public class SpamLogic
{
    /**
     * News feed category for the purposes of filling in a velocity template. NOTE: this class must
     * be public so velocity can reflect its methods and members.
     */
    public class EmailFeedCategory
    {
        /** The category. */
        public Category category;

        /** The items. */
        public List<EmailFeedItem> items;

        /**
         * Gets the name of this feed category. NOTE: this is called using reflection by velocity.
         */
        public String getCategoryName ()
        {
            return _dmsgs.get("feedCategory" + category.ordinal());
        }

        protected EmailFeedCategory (Category category, List<EmailFeedItem> items)
        {
            this.category = category;
            this.items = items;
        }
    }

    /**
     * News feed item for the purposes of filling in a velocity template. NOTE: this class must be
     * public so velocity can reflect its methods and members.
     */
    public class EmailFeedItem
    {
        /**
         * Gets the plain text representation of this feed item. NOTE: this is called using
         * reflection by velocity.
         */
        public String getPlainText ()
        {
            initContent();
            return _content;
        }

        /**
         * Gets the html text representation of this feed item. NOTE: this is called using
         * reflection by velocity.
         */
        public String getHTMLText ()
        {
            initContent();
            return _htmlContent;
        }

        protected EmailFeedItem (Generator generators[], int memberId, FeedMessage message)
        {
            _generators = generators;
            _memberId = memberId;
            _message = message;
        }

        protected void initContent ()
        {
            if (_content == null) {
                _generators[0].addMessage(_message);
                _generators[1].addMessage(_message);
                _content = builder(0).item;
                _htmlContent = builder(1).item;
            }
        }

        protected EmailItemBuilder builder (int ii)
        {
            return ((EmailItemBuilder)_generators[ii].getBuilder());
        }

        protected Generator _generators[];
        protected int _memberId;
        protected FeedMessage _message;
        protected String _content;
        protected String _htmlContent;
    }

    /**
     * Sends a feed email to the given member id. Returns true unless there were no feed items in
     * the user's feed.
     */
    public boolean sendFeedEmail (int memberId)
    {
        // lazy init message bundles for now
        initMessageBundles();

        // load up the data
        final MemberRecord mrec = _memberRepo.loadMember(memberId);
        IntSet friendIds = _memberRepo.loadFriendIds(mrec.memberId);
        List<FeedCategory> categories = _feedLogic.loadFeedCategories(
            mrec, friendIds, ITEMS_PER_CATEGORY, null);

        final Generator generators[] = {
            new Generator(memberId, new PlainTextBuilder(), _messages),
            new Generator(memberId, new HTMLBuilder(), _messages)};

        // convert to our accessible versions
        List<EmailFeedCategory> ecats = Lists.newArrayList();
        int total = 0;
        for (FeedCategory category : categories) {
            List<EmailFeedItem> eitems = Lists.transform(
                FeedMessageAggregator.aggregate(category.messages, false),
                new Function<FeedMessage, EmailFeedItem>() {;
                    public EmailFeedItem apply (FeedMessage fm) {
                        return new EmailFeedItem(generators, mrec.memberId, fm);
                    }
                });
            if (eitems.isEmpty()) {
                continue;
            }
            EmailFeedCategory ecat = new EmailFeedCategory(
                Category.values()[category.category], eitems);
            total += eitems.size();
            ecats.add(ecat);
        }

        // bail if we've got no items
        if (total == 0) {
            return false;
        }

        // fire off the email, the template will take care of looping over categories and items
        // TODO: we'll need more parameters here 
        Parameters params = new Parameters();
        params.set("feed", ecats);
        params.set("server_url", DeploymentConfig.serverURL);
        _mailSender.sendTemplateEmail(
            mrec.accountName, ServerConfig.getFromAddress(), MAIL_TEMPLATE, params);
        return true;
    }

    /**
     * Sets up the message bundles if they are not already.
     */
    protected void initMessageBundles ()
    {
        if (_pmsgs == null) {
            _pmsgs = _serverMsgs.getBundle("feed.PersonMessages");
        }
        if (_dmsgs == null) {
            _dmsgs = _serverMsgs.getBundle("feed.DynamicMessages");
        }
    }

    /**
     * Implements the generator components, just wraps a string.
     */
    protected static class StringWrapper
        implements Media, Icon
    {
        /** The text of the thing we wrap. */
        public String text;

        /**
         * Creates a new wrapper.
         */
        public StringWrapper (String text)
        {
            this.text = text;
        }

        // from Object
        public String toString ()
        {
            return text;
        }
    }

    /**
     * Base item builder for email that just records the text of the added feed item.
     */
    protected static abstract class EmailItemBuilder
        implements Builder
    {
        /**
         * The final item text just added (we only keep it long enough for the feed item to grab it.
         */
        public String item;

        // from Builder
        public void addIcon (Icon icon) {
            item = icon.toString();
        }

        // from Builder
        public void addMedia (Media media, String message) {
            _temp.setLength(0);
            _temp.append(media).append(message);
            item = _temp.toString();
        }

        // from Builder
        public void addMedia (Media[] medias, String message) {
            _temp.setLength(0);
            for (Media media : medias) {
                _temp.append(media);
            }
            _temp.append(message);
            item = _temp.toString();
        }

        // from Builder
        public void addText (String text) {
            item = text;
        }

        StringBuilder _temp = new StringBuilder();
    }

    /**
     * Implementation of an feed item builder that keeps a string buffer of the item so far.
     */
    protected static class HTMLBuilder extends EmailItemBuilder
    {
        // from Builder
        public Icon createGainedLevelIcon (String text) {
            return new StringWrapper(_html.reset().open("img",
                "src", "images/whirled/friend_gained_level.png").close().append(text).finish());
        }

        // from Builder
        public String createLink (String label, Pages page, String args) {
            return _html.reset().open("a", "href", link(page, args)).append(label).finish();
        }

        // from Builder
        public Media createMedia (MediaDesc md, Pages page, String args) {
            if (!md.isImage()) {
                // hmm
                return new StringWrapper(
                    _html.reset().open("a", "href", link(page, args)).finish());
            }
            return new StringWrapper(_html.reset().open("a", "href", link(page, args))
                .open("img", "src", md.getMediaPath()).finish());
        }

        protected static String link (Pages page, String args)
        {
            return Pages.makeLink(page, args).substring(1);
        }

        /** The buffer we append to for this item's html. */
        protected MarkupBuilder _html = new MarkupBuilder();
    }

    protected static class PlainTextBuilder extends EmailItemBuilder
    {
        // from Builder
        public Icon createGainedLevelIcon (String text) {
            return new StringWrapper(text);
        }

        // from Builder
        public String createLink (String label, Pages page, String args) {
            return label;
        }

        // from Builder
        public Media createMedia (MediaDesc md, Pages page, String args) {
            return new StringWrapper("");
        }
    }

    protected class Generator extends FeedItemGenerator
    {
        public Generator (int memberId, Builder builder, Messages messages)
        {
            super(memberId, true, builder, messages);
        }

        /**
         * Returns the builder we are using to generate the feed item.
         */
        public Builder getBuilder ()
        {
            return _builder;
        }
    }

    /** Messages instance that delegates to the bundles in our parent class. */
    protected Messages _messages = new Messages () {
        // from Messages
        public String typeName (String itemType) {
            return _dmsgs.get("itemType" + itemType);
        }

        // from Messages
        public String you () {
            return _pmsgs.get("feedProfileMemberYou");
        }

        // from Messages
        public String describeItem (String typeName, String itemName) {
            return _pmsgs.get("descCombine", typeName, itemName);
        }

        // from Messages
        public String badgeName (int code, String levelName) {
            String hexCode = Integer.toHexString(code);
            return _dmsgs.get("badge_" + hexCode, levelName);
        }

        // from Messages
        public String noGroupForMedal (String medalLink) {
            return _pmsgs.get("medalNoGroup", medalLink);
        }

        // from Messages
        public String medal (String medal, String group) {
            return _pmsgs.get("medal", medal, group);
        }

        // from Messages
        public String unknownMember () {
            return _pmsgs.get("feedProfileMemberUnknown");
        }

        // from Messages
        public String action (
            FeedMessageType type, String subject, String object, Plural plural) {
            switch (type) {
            case GLOBAL_ANNOUNCEMENT:
                return _pmsgs.get("globalAnnouncement", object);

            case FRIEND_ADDED_FRIEND:
                return _pmsgs.get("friendAddedFriend", subject, object);

            case FRIEND_UPDATED_ROOM:
                switch (plural) {
                case NONE:
                    return _pmsgs.get("friendUpdatedRoom", subject, object);
                case SUBJECT:
                    return _pmsgs.get("friendsUpdatedRoom", subject);
                case OBJECT:
                    return _pmsgs.get("friendUpdatedRooms", subject, object);
                }

            case FRIEND_WON_TROPHY:
                return plural == Plural.OBJECT ?
                    _pmsgs.get("friendWonTrophies", subject, object) :
                    _pmsgs.get("friendWonTrophy", subject, object);

            case FRIEND_LISTED_ITEM:
                return _pmsgs.get("friendListedItem", subject, object);

            case FRIEND_GAINED_LEVEL:
                return plural == Plural.SUBJECT ?
                    _pmsgs.get("friendsGainedLevel", subject) :
                    _pmsgs.get("friendGainedLevel", subject, object);

            case FRIEND_WON_BADGE:
                return plural == Plural.OBJECT ?
                    _pmsgs.get("friendWonBadges", subject, object) :
                    _pmsgs.get("friendWonBadge", subject, object);

            case FRIEND_WON_MEDAL:
                return _pmsgs.get("friendWonMedal", subject, object);

            case GROUP_ANNOUNCEMENT:
                return _pmsgs.get("groupAnnouncement", subject, object);

            case GROUP_UPDATED_ROOM:
                return _pmsgs.get("friendUpdatedRoom", subject, object);

            case SELF_ROOM_COMMENT:
                return _pmsgs.get("selfRoomComment", subject, object);

            case SELF_ITEM_COMMENT:
                return _pmsgs.get("selfItemComment", subject, object);

            case SELF_FORUM_REPLY:
                return _pmsgs.get("selfForumReply", subject, object);

            default:
                return "Unknown message type: " + subject + " did something to " + object + ".";
            }
        }

        // from Messages
        public String andCombine (String list, String item) {
            return _pmsgs.get("andCombine", list, item);
        }

        // from Messages
        public String briefLevelGain (String subject, String level) {
            return _pmsgs.get("colonCombine", subject, level);
        }

        // from Messages
        public String commaCombine (String list, String item) {
            return _pmsgs.get("commaCombine", list, item);
        }
    };

    // Message bundles
    // TODO: proper localization
    protected MessageBundle _pmsgs;
    protected MessageBundle _dmsgs;

    @Inject protected MemberRepository _memberRepo; 
    @Inject protected FeedLogic _feedLogic;
    @Inject protected MailSender _mailSender;
    @Inject protected ServerMessages _serverMsgs;

    protected static final int ITEMS_PER_CATEGORY = 50;
    protected static final String MAIL_TEMPLATE = "feed";
}
