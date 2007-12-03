//
// $Id$

package com.threerings.msoy.item.server.persist;

import com.samskivert.jdbc.depot.PersistenceContext;
import com.samskivert.jdbc.depot.annotation.Entity;

import com.threerings.msoy.server.persist.TagRecord;
import com.threerings.msoy.server.persist.TagHistoryRecord;

/**
 * Manages the persistent store of {@link Pet} items.
 */
public class PetRepository extends ItemRepository<
    PetRecord,
    PetCloneRecord,
    PetCatalogRecord,
    PetRatingRecord>
{
    @Entity(name="PetTagRecord")
    public static class PetTagRecord extends TagRecord
    {
    }

    @Entity(name="PetTagHistoryRecord")
    public static class PetTagHistoryRecord extends TagHistoryRecord
    {
    }

    public PetRepository (PersistenceContext ctx)
    {
        super(ctx);
    }

    @Override
    protected Class<PetRecord> getItemClass ()
    {
        return PetRecord.class;
    }
    
    @Override
    protected Class<PetCatalogRecord> getCatalogClass ()
    {
        return PetCatalogRecord.class;
    }

    @Override
    protected Class<PetCloneRecord> getCloneClass ()
    {
        return PetCloneRecord.class;
    }
    
    @Override
    protected Class<PetRatingRecord> getRatingClass ()
    {
        return PetRatingRecord.class;
    }

    @Override
    protected TagRecord createTagRecord ()
    {
        return new PetTagRecord();
    }

    @Override
    protected TagHistoryRecord createTagHistoryRecord ()
    {
        return new PetTagHistoryRecord();
    }
}
