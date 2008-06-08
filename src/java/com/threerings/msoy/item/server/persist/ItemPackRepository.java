//
// $Id$

package com.threerings.msoy.item.server.persist;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.jdbc.depot.PersistenceContext;
import com.samskivert.jdbc.depot.annotation.Entity;

import com.threerings.msoy.server.persist.TagHistoryRecord;
import com.threerings.msoy.server.persist.TagRecord;

/**
 * Manages the persistent store of {@link ItemPackRecord} items.
 */
@Singleton
public class ItemPackRepository extends ItemRepository<
    ItemPackRecord,
    ItemPackCloneRecord,
    ItemPackCatalogRecord,
    ItemPackRatingRecord>
{
    @Entity(name="ItemPackTagRecord")
    public static class ItemPackTagRecord extends TagRecord
    {
    }

    @Entity(name="ItemPackTagHistoryRecord")
    public static class ItemPackTagHistoryRecord extends TagHistoryRecord
    {
    }

    @Inject public ItemPackRepository (PersistenceContext ctx)
    {
        super(ctx);
    }

    @Override
    protected Class<ItemPackRecord> getItemClass ()
    {
        return ItemPackRecord.class;
    }

    @Override
    protected Class<ItemPackCatalogRecord> getCatalogClass ()
    {
        return ItemPackCatalogRecord.class;
    }

    @Override
    protected Class<ItemPackCloneRecord> getCloneClass ()
    {
        return ItemPackCloneRecord.class;
    }

    @Override
    protected Class<ItemPackRatingRecord> getRatingClass ()
    {
        return ItemPackRatingRecord.class;
    }

    @Override
    protected TagRecord createTagRecord ()
    {
        return new ItemPackTagRecord();
    }

    @Override
    protected TagHistoryRecord createTagHistoryRecord ()
    {
        return new ItemPackTagHistoryRecord();
    }
}
