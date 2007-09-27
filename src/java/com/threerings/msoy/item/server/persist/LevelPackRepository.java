//
// $Id$

package com.threerings.msoy.item.server.persist;

import com.samskivert.io.PersistenceException;
import com.samskivert.jdbc.depot.EntityMigration;
import com.samskivert.jdbc.depot.PersistenceContext;
import com.samskivert.jdbc.depot.annotation.Entity;

import com.threerings.msoy.server.persist.TagHistoryRecord;
import com.threerings.msoy.server.persist.TagRecord;

import static com.threerings.msoy.Log.log;

/**
 * Manages the persistent store of {@link LevelPackRecord} items.
 */
public class LevelPackRepository extends ItemRepository<
    LevelPackRecord,
    LevelPackCloneRecord,
    LevelPackCatalogRecord,
    LevelPackRatingRecord>
{
    @Entity(name="LevelPackTagRecord")
    public static class LevelPackTagRecord extends TagRecord
    {
    }

    @Entity(name="LevelPackTagHistoryRecord")
    public static class LevelPackTagHistoryRecord extends TagHistoryRecord
    {
    }

    public LevelPackRepository (PersistenceContext ctx)
    {
        super(ctx);
        _ctx.registerMigration(getItemClass(), new EntityMigration.Drop(14002, "levelMediaHash"));
        _ctx.registerMigration(getItemClass(), new EntityMigration.Drop(14002, "levelMimeType"));
    }

    @Override
    protected Class<LevelPackRecord> getItemClass ()
    {
        return LevelPackRecord.class;
    }

    @Override
    protected Class<LevelPackCatalogRecord> getCatalogClass ()
    {
        return LevelPackCatalogRecord.class;
    }

    @Override
    protected Class<LevelPackCloneRecord> getCloneClass ()
    {
        return LevelPackCloneRecord.class;
    }

    @Override
    protected Class<LevelPackRatingRecord> getRatingClass ()
    {
        return LevelPackRatingRecord.class;
    }

    @Override
    protected TagRecord createTagRecord ()
    {
        return new LevelPackTagRecord();
    }

    @Override
    protected TagHistoryRecord createTagHistoryRecord ()
    {
        return new LevelPackTagHistoryRecord();
    }
}
