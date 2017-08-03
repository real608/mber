//
// $Id$

package com.threerings.msoy.server.persist;

import java.sql.Date;
import java.sql.Timestamp;

import com.samskivert.util.StringUtil;
import com.samskivert.util.Tuple;

import com.samskivert.depot.Key;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.StringFuncs;
import com.samskivert.depot.annotation.*;
import com.samskivert.depot.clause.OrderBy.Order;
import com.samskivert.depot.expression.ColumnExp;
import com.samskivert.depot.expression.SQLExpression;

@Entity
//@Table(name = "IPBlacklistRecords")
public class IPBlacklistRecord extends PersistentRecord
{
	public static enum Flag{
		BLACKLISTED (1 << 0),
		
		
		UNUSED (1 << 2);
		
		public int getBit () {
            return _bit;
        }

        Flag (int bit) {
            _bit = bit;
        }

        protected int _bit;
	}
	public static final Class<IPBlacklistRecord> _R = IPBlacklistRecord.class;
    public static final ColumnExp<Integer> MEMBER_ID = colexp(_R, "memberId");
    public static final ColumnExp<String> MEMBER_IP = colexp(_R, "memberIp");
	public static final ColumnExp<Integer> FLAGS = colexp(_R, "flags");
	
	public static final int SCHEMA_VERSION = 2;
	
	@Column(nullable=false)
	public int memberId;
	
	@Column(nullable=false)
	public String memberIp;
	
	@Column(nullable=true)
	public String blacklistReason;
	
	public int flags;
	
	public IPBlacklistRecord()
	{
		
	}
	
	public IPBlacklistRecord(String memberIP, int memberID)
	{
		this.memberIp = memberIP;
		this.memberId = memberID;
		
	}
	public boolean isBlacklisted ()
    {
        return isSet(flags, Flag.BLACKLISTED);
    }
	public void blacklist()
	{
		setFlag(Flag.BLACKLISTED, true);
	}
	public static boolean isSet (int flags, Flag flag)
    {
        return (flags & flag.getBit()) != 0;
    }
	
	public boolean isSet (Flag flag)
    {
        return isSet(flags, flag);
    }
	
	public void setFlag (Flag flag, boolean value)
    {
        flags = (value ? (flags | flag.getBit()) : (flags & ~flag.getBit()));
    }
	public boolean updateFlag (Flag flag, boolean value)
    {
        if (isSet(flag) == value) {
            return false;
        }
        setFlag(flag, value);
        return true;
    }

	
	@Override // from Object
    public String toString ()
    {
        return StringUtil.fieldsToString(this);
    }
	
    public static Key<IPBlacklistRecord> getKey (int memberID)
    {
        return newKey(_R, memberID);
    }

    /** Register the key fields in an order matching the getKey() factory. */
    static { registerKeyFields(MEMBER_ID); }
    // AUTO-GENERATED: METHODS END
}
