//
// $Id$

package com.threerings.msoy.underwire.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.threerings.underwire.server.GameInfoProvider;
import com.threerings.underwire.web.data.Account;

import com.threerings.msoy.data.all.MemberName;
import com.threerings.msoy.server.persist.MemberRecord;
import com.threerings.msoy.server.persist.MemberRepository;
import com.threerings.msoy.server.persist.MemberWarningRecord;
import com.threerings.msoy.underwire.gwt.MsoyAccount;

/**
 * Provides game-specific info for Whirled.
 */
@Singleton
public class MsoyGameInfoProvider extends GameInfoProvider
{
    @Override // from GameInfoProvider
    public Map<String, List<String>> resolveGameNames (Set<String> names)
    {
        Map<String, List<String>> map = Maps.newHashMap();
        for (MemberName name : _memberRepo.loadMemberNames(names, TO_INT).values()) {
            map.put(Integer.toString(name.getId()), Lists.newArrayList(name.toString()));
        }
        return map;
    }

    @Override // from GameInfoProvider
    public String[] lookupAccountNames (String gameName)
    {
        List<Integer> memberIds =
            _memberRepo.findMembersByExactDisplayName(gameName, LOOKUP_LIMIT);
        ArrayList<String> names = new ArrayList<String>(memberIds.size());
        for (Integer memberId : memberIds) {
            names.add(memberId.toString());
        }
        return names.toArray(new String[names.size()]);
    }

    @Override // from GameInfoProvider
    public void populateAccount (Account account)
    {
        MemberRecord member = _memberRepo.loadMember(account.email);
        if (member != null) {
            account.firstSession = new Date(member.created.getTime());
            account.lastSession = new Date(member.lastSession.getTime());
            account.altName = member.permaName;
            MsoyAccount.SocialStatus status;
            if (member.isTroublemaker()) {
                status = MsoyAccount.SocialStatus.TROUBLEMAKER;
            } else if (member.isGreeter()) {
                status = MsoyAccount.SocialStatus.GREETER;
            } else if (member.isIPBanned()) {
                status = MsoyAccount.SocialStatus.IPBANNED;
            }
            else {
                status = MsoyAccount.SocialStatus.NORMAL;
            }
            ((MsoyAccount)account).status = status;
            MemberWarningRecord warning = _memberRepo.loadMemberWarningRecord(member.memberId);
            if (warning != null) {
                account.tempBan = warning.banExpires == null ?
                    null : new Date(warning.banExpires.getTime());
                account.warning = warning.warning;
            }
        }
    }

    // our dependencies
    @Inject protected MemberRepository _memberRepo;

    // maximum number of display names to return
    protected static final int LOOKUP_LIMIT = 50;

    protected static final Function<String,Integer> TO_INT = new Function<String,Integer>() {
        public Integer apply (String value) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException nfe) {
                return 0;
            }
        }
    };
}
