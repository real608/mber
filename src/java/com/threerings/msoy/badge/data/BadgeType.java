//
// $Id$

package com.threerings.msoy.badge.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import com.samskivert.util.IntMap;
import com.samskivert.util.IntMaps;

import com.threerings.stats.Log;
import com.threerings.stats.data.StatSet;

import com.threerings.msoy.badge.data.all.Badge;
import com.threerings.msoy.badge.data.all.InProgressBadge;
import com.threerings.msoy.badge.data.all.EarnedBadge;
import com.threerings.msoy.badge.gwt.StampCategory;

import com.threerings.msoy.data.StatType;

/** Defines the various badge types. */
public enum BadgeType
{
    /* TODO - remove these testing badges
    SERVLET_BADGE(StampCategory.SOCIAL, StatType.ITEM_COMMENTS, new Level[] {
        new Level(1, 1000),
        new Level(3, 1000),
        new Level(5, 1000),
    }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.ITEM_COMMENTS);
        }
    },

    DOBJ_BADGE(StampCategory.SOCIAL, StatType.WHIRLEDS_VISITED, new Level[] {
        new Level(1, 1000),
        new Level(3, 1000),
        new Level(5, 1000),
    }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getSetStatSize(StatType.WHIRLEDS_VISITED);
        }

        @Override protected Collection<BadgeType> getUnlockRequirements () {
            return Collections.singleton(SERVLET_BADGE);
        }
    },*/

    // social badges
    FRIENDLY(StampCategory.SOCIAL, StatType.FRIENDS_MADE, new Level[] {
        new Level(1, 1000),
        new Level(5, 2000),
        new Level(15, 3000),
        new Level(50, 4000),
        new Level(150, 5000),
        new Level(500, 6000)
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.FRIENDS_MADE);
        }
    },

    IRONMAN(StampCategory.SOCIAL, StatType.CONSEC_DAILY_LOGINS, new Level[] {
        new Level(2, 1000),
        new Level(4, 2000),
        new Level(10, 3000),
        new Level(25, 4000),
        new Level(90, 5000),
        new Level(180, 6000)
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.CONSEC_DAILY_LOGINS);
        }

        @Override protected Collection<BadgeType> getUnlockRequirements () {
            return Collections.singleton(BadgeType.FRIENDLY);
        }
    },

    MAGNET(StampCategory.SOCIAL, StatType.INVITES_ACCEPTED, new Level[] {
        new Level(1, 1000),
        new Level(5, 2000),
        new Level(10, 3000),
        new Level(20, 4000),
        new Level(50, 5000),
        new Level(100, 6000)
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.INVITES_ACCEPTED);
        }

        @Override protected Collection<BadgeType> getUnlockRequirements () {
            return Collections.singleton(BadgeType.FRIENDLY);
        }
    },

    FIXTURE(StampCategory.SOCIAL, StatType.MINUTES_ACTIVE, new Level[] {
        new Level(3 * 60, 1000),
        new Level(24 * 60, 2000),
        new Level(48 * 60, 3000),
        new Level(96 * 60, 4000),
        new Level(200 * 60, 5000),
        new Level(500 * 60, 6000)
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.MINUTES_ACTIVE);
        }

        @Override protected Collection<BadgeType> getUnlockRequirements () {
            return Collections.singleton(BadgeType.FRIENDLY);
        }
    },

    // game badges
    GAMER(StampCategory.GAME, StatType.GAME_SESSIONS, new Level[] {
        new Level(1, 1000),
        new Level(5, 2000),
        new Level(25, 3000),
        new Level(100, 4000),
        new Level(500, 5000),
        new Level(2000, 6000)
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.GAME_SESSIONS);
        }
    },

    CONTENDER(StampCategory.GAME, StatType.MP_GAMES_WON, new Level[] {
        new Level(1, 1000),
        new Level(5, 2000),
        new Level(10, 3000),
        new Level(25, 4000),
        new Level(50, 5000),
        new Level(100, 6000)
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.MP_GAMES_WON);
        }

        @Override protected Collection<BadgeType> getUnlockRequirements () {
            return Collections.singleton(BadgeType.GAMER);
        }
    },

    COLLECTOR(StampCategory.GAME, StatType.TROPHIES_EARNED, new Level[] {
        new Level(1, 1000),
        new Level(5, 2000),
        new Level(10, 3000),
        new Level(25, 4000),
        new Level(50, 5000),
        new Level(100, 6000)
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.TROPHIES_EARNED);
        }

        @Override protected Collection<BadgeType> getUnlockRequirements () {
            return Collections.singleton(BadgeType.GAMER);
        }
    },

    // creation badges
    PROFESSIONAL(StampCategory.CREATION, StatType.COINS_EARNED_SELLING, new Level[] {
        new Level(10000, 1000),
        new Level(100000, 2000),
        new Level(500000, 3000),
        new Level(1000000, 4000),
        new Level(2000000, 5000),
        new Level(5000000, 6000)
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.COINS_EARNED_SELLING);
        }

        // PROFESSIONAL is unlocked once you have at least one other CREATION badge
        @Override public boolean isUnlocked (Collection<EarnedBadge> badges) {
            return (Iterables.find(badges, new Predicate<EarnedBadge>() {
                public boolean apply (EarnedBadge badge) {
                    return getType(badge.badgeCode).getCategory() == StampCategory.CREATION;
                }
            }) != null);
        }
    },

    // TODO: Character Designer, Furniture Builder, Landscape Painter, Professional, Popular

    // shopping badges
    SHOPPER(StampCategory.SHOPPING, StatType.ITEMS_PURCHASED, new Level[] {
        new Level(1, 1000),
        new Level(3, 2000),
        new Level(10, 3000),
        new Level(50, 4000),
        new Level(250, 5000),
        new Level(1000, 6000),
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.ITEMS_PURCHASED);
        }
    },

    JUDGE(StampCategory.SHOPPING, StatType.ITEMS_RATED, new Level[] {
        new Level(1, 1000),
        new Level(5, 2000),
        new Level(25, 3000),
        new Level(100, 4000),
        new Level(500, 5000),
        new Level(2000, 6000)
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.ITEMS_RATED);
        }

        @Override protected Collection<BadgeType> getUnlockRequirements () {
            return Collections.singleton(BadgeType.SHOPPER);
        }
    },

    OUTSPOKEN(StampCategory.SHOPPING, StatType.ITEM_COMMENTS, new Level[] {
        new Level(1, 1000),
        new Level(5, 2000),
        new Level(25, 3000),
        new Level(100, 4000),
        new Level(500, 5000),
        new Level(2000, 6000)
        }) {
        @Override protected int getAcquiredUnits (StatSet stats) {
            return stats.getIntStat(StatType.ITEM_COMMENTS);
        }

        @Override protected Collection<BadgeType> getUnlockRequirements () {
            return Collections.singleton(BadgeType.SHOPPER);
        }
    },

    // TODO: Shopper - needs an items purchased stat

    // Every member will have an InProgressBadgeRecord created for the HIDDEN badge, but they will
    // never earn it (to allow Passport to determine whether a player's initial set of
    // InProgressBadges needs to be created).
    HIDDEN(StampCategory.SOCIAL, 0) {
        @Override public boolean isHidden () {
            return true;
        }
    };

    // This can't be in InProgressBadge, because InProgressBadge is visible to GWT, and BadgeType
    // is incompatible with GWT.
    public static Predicate<InProgressBadge> IS_VISIBLE_BADGE = new Predicate<InProgressBadge>() {
        public boolean apply (InProgressBadge badge) {
            return !(getType(badge.badgeCode).isHidden());
        }
    };

    /** Function to get the badgeCode out of a Badge */
    public static Function<Badge, Integer> BADGE_TO_CODE = new Function<Badge, Integer>() {
        public Integer apply (Badge badge) {
            return badge.badgeCode;
        }
    };

    /** Encapsulates a stat requirement and coin reward for a single badge level. */
    public static class Level
    {
        public int requiredUnits;
        public int coinValue;

        public Level (int requiredUnits, int coinValue) {
            this.requiredUnits = requiredUnits;
            this.coinValue = coinValue;
        }
    }

    /**
     * A main method so that this class can be run on its own for Badge code discovery.
     */
    public static void main (String[] args)
    {
        // dump all of the known badge types and their code
        System.out.println("  Code   -   Badge\n--------------------");
        for (Map.Entry<Integer, BadgeType> entry : _codeToType.entrySet()) {
            System.out.println(Integer.toHexString(entry.getKey()) + " - " + entry.getValue());
        }
    }

    /**
     * Maps a {@link BadgeType}'s code back to a {@link BadgeType} instance.
     */
    public static BadgeType getType (int code)
    {
        return _codeToType.get(code);
    }

    /**
     * Returns a set of the badges that depend on the given stat.  When that stat is changed, the
     * party responsible for the change should call this method and find out what badges potentially
     * need updating.
     */
    public static Set<BadgeType> getDependantBadges (StatType stat)
    {
        Set<BadgeType> depends = (stat == null) ? null : _statDependencies.get(stat.code());
        return (depends == null) ? Collections.<BadgeType>emptySet() : depends;
    }

    /**
     * Certain badges are only unlocked once one or more other badges are earned.
     *
     * @return true if this badge has been unlocked, given the specified collection of EarnedBadges.
     */
    public boolean isUnlocked (Collection<EarnedBadge> badges)
    {
        return Sets.newHashSet(Iterables.transform(badges, BADGE_TO_TYPE)).containsAll(
            getUnlockRequirements());
    }

    /**
     * @return the number of levels this badge has.
     */
    public int getNumLevels ()
    {
        return _levels.length;
    }

    /**
     * @return the level data for the specified badge level, or null if the level is out of range.
     */
    public Level getLevel (int level)
    {
        return (level >= 0 && level < _levels.length ? _levels[level] : null);
    }

    /**
     * Returns the progress that the specified user has made on this badge.
     */
    public BadgeProgress getProgress (StatSet stats)
    {
        int highestLevel = -1;
        int requiredUnits = 0;
        int acquiredUnits = getAcquiredUnits(stats);
        if (_levels != null) {
            for (Level level : _levels) {
                if (acquiredUnits >= level.requiredUnits) {
                    highestLevel++;
                } else {
                    requiredUnits = level.requiredUnits;
                    break;
                }
            }
        }

        return new BadgeProgress(highestLevel, requiredUnits, acquiredUnits);
    }

    /**
     * @return the unique code for this badge type, which is a function of its name.
     */
    public final int getCode()
    {
        return _code;
    }

    /**
     * @return the relevant StatType associated with this badge, or null if the badge doesn't have
     * one. The badge system uses this information to update badges when their associated stats
     * are updated.
     */
    public StatType getRelevantStat ()
    {
        return _relevantStat;
    }

    /**
     * @return the Category this badge falls under.
     */
    public StampCategory getCategory ()
    {
        return _category;
    }

    /**
     * Overridden by badge types to indicate how many units of the stat that this badge tracks
     * (games played, friends made, etc) the user has acquired.
     */
    protected int getAcquiredUnits (StatSet stats)
    {
        return 0;
    }

    /**
     * Optionally overridden by badge types to indicate that players must earn a particular set
     * of badges before this badge becomes unlocked.
     */
    protected Collection<BadgeType> getUnlockRequirements ()
    {
        return Collections.emptyList();
    }

    /**
     * Overridden only by the HIDDEN BadgeType to indicate that the badge shouldn't be shown
     * to players.
     */
    public boolean isHidden ()
    {
        return false;
    }

    /** Constructs a new BadgeType. */
    BadgeType (StampCategory category, StatType relevantStat, Level[] levels)
    {
        _category = category;
        _relevantStat = relevantStat;
        _levels = levels;

        // ensure the badge has at least one level
        if (_levels == null || _levels.length == 0) {
            _levels = new Level[] { new Level(1, 0) };
        }
    }

    /** Constructs a new BadgeType with no relevant stat and a single level. */
    BadgeType (StampCategory category, int coinValue)
    {
        this(category, null, new Level[] { new Level(1, coinValue) });
    }

    /**
     * Create the hash<->BadgeType mapping for each BadgeType.
     * This is done in a static block because it's an error for an enum
     * to access its static members in its constructor.
     */
    static
    {
        _crc = new CRC32();

        for (BadgeType type : BadgeType.values()) {
            type._code = mapCodeForType(type);
            mapStatDependencies(type);
        }
    }

    protected static void mapStatDependencies (BadgeType type)
    {
        if (_statDependencies == null) {
            _statDependencies = IntMaps.newHashIntMap();
        }

        StatType stat = type.getRelevantStat();
        if (stat != null) {
            int code = stat.code();
            Set<BadgeType> dependantTypes = _statDependencies.get(code);
            if (dependantTypes == null) {
                _statDependencies.put(code, dependantTypes = Sets.newHashSet());
            }
            dependantTypes.add(type);
        }
    }

    protected static int mapCodeForType (BadgeType type)
    {
        // compute the CRC32 hash
        _crc.reset();
        _crc.update(type.name().getBytes());
        int code = (int) _crc.getValue();

        // store the hash in a map
        if (_codeToType == null) {
            _codeToType = IntMaps.newHashIntMap();
        }
        if (_codeToType.containsKey(code)) {
            Log.log.warning("Badge type collision! " + type + " and " + _codeToType.get(code) +
                " both map to '" + code + "'.");
        } else {
            _codeToType.put(code, type);
        }

        return code;
    }

    protected int _code;
    protected StampCategory _category;
    protected StatType _relevantStat;
    protected Level[] _levels;

    /** The table mapping stat codes to enumerated types. */
    protected static IntMap<BadgeType> _codeToType;

    /** The mapping of stats to the badges that depend on them. */
    protected static IntMap<Set<BadgeType>> _statDependencies;

    protected static CRC32 _crc;

    /** Helper function. */
    protected static final Function<Badge, BadgeType> BADGE_TO_TYPE =
        new Function<Badge, BadgeType>() {
        public BadgeType apply (Badge badge) {
            return BadgeType.getType(badge.badgeCode);
        }
    };
}
