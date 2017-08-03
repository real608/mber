//
// $Id$

package com.threerings.msoy.server.persist;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.util.Calendars;

import com.samskivert.depot.DepotRepository;
import com.samskivert.depot.Exps;
import com.samskivert.depot.Key;
import com.samskivert.depot.Ops;
import com.samskivert.depot.PersistenceContext;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.SchemaMigration;
import com.samskivert.depot.clause.QueryClause;
import com.samskivert.depot.clause.Where;
import com.samskivert.depot.expression.ColumnExp;
import com.samskivert.depot.expression.SQLExpression;

import com.threerings.presents.annotation.BlockingThread;

import com.threerings.msoy.item.data.all.MsoyItemType;

import static com.threerings.msoy.Log.log;

import com.threerings.msoy.server.persist.IPBlacklistRecord;

@Singleton @BlockingThread
public class IPBlacklistRepository extends DepotRepository
{
	@Inject public IPBlacklistRepository (final PersistenceContext ctx)
    {
        super(ctx);
    }
	
	public void insertIPRecord (IPBlacklistRecord iprec)
	{
		insert(iprec);
	}
	public List<IPBlacklistRecord> loadRecords (int mId)
    {
        List<QueryClause> clauses = Lists.newArrayList(
            new Where(IPBlacklistRecord.MEMBER_ID, mId));
		
        return findAll(IPBlacklistRecord.class, clauses);
    }
	public void blacklistAllByMemberID(int mId)
	{
		List <IPBlacklistRecord> _IPRecs = loadRecords(mId);
		if(_IPRecs.size() > 0)
		{
			for (IPBlacklistRecord a : _IPRecs) {
				
					a.blacklist();
					String s;
					Process p;
					try {
						p = Runtime.getRuntime().exec("sudo ufw deny from" + a.memberIp + "to any");
						p.destroy();
					} catch (Exception e) {}
			}
			
		}
	}
	public IPBlacklistRecord loadRecord(int mId, String memberIp)
	{
		IPBlacklistRecord _IPRec = null;
		List <IPBlacklistRecord> _IPRecs = loadRecords(mId);
		if(_IPRecs.size() > 0)
		{
			for (IPBlacklistRecord a : _IPRecs) {
				if (a.memberIp.equals(memberIp)) {
					_IPRec = a;
					break;
				}else{
					
				}
			}
			
		}else{
			return null;
		}
		return _IPRec;
	}
	@Override // from DepotRepository
    protected void getManagedRecords (final Set<Class<? extends PersistentRecord>> classes)
    {
        classes.add(IPBlacklistRecord.class);
    }
}
