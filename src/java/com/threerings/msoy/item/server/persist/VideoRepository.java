//
// $Id$

package com.threerings.msoy.item.server.persist;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.jdbc.depot.PersistenceContext;
import com.samskivert.jdbc.depot.annotation.Entity;

import com.threerings.msoy.server.persist.TagRecord;
import com.threerings.msoy.server.persist.TagHistoryRecord;

/**
 * Manages the persistent store of {@link VideoRecord} items.
 */
@Singleton
public class VideoRepository extends ItemRepository<
    VideoRecord,
    VideoCloneRecord,
    VideoCatalogRecord,
    VideoRatingRecord>
{
    @Entity(name="VideoTagRecord")
    public static class VideoTagRecord extends TagRecord
    {
    }

    @Entity(name="VideoTagHistoryRecord")
    public static class VideoTagHistoryRecord extends TagHistoryRecord
    {
    }

    @Inject public VideoRepository (PersistenceContext ctx)
    {
        super(ctx);
    }

    @Override
    protected Class<VideoRecord> getItemClass ()
    {
        return VideoRecord.class;
    }
    
    @Override
    protected Class<VideoCatalogRecord> getCatalogClass ()
    {
        return VideoCatalogRecord.class;
    }

    @Override
    protected Class<VideoCloneRecord> getCloneClass ()
    {
        return VideoCloneRecord.class;
    }

    @Override
    protected Class<VideoRatingRecord> getRatingClass ()
    {
        return VideoRatingRecord.class;
    }

    @Override
    protected TagRecord createTagRecord ()
    {
        return new VideoTagRecord();
    }

    @Override
    protected TagHistoryRecord createTagHistoryRecord ()
    {
        return new VideoTagHistoryRecord();
    }
}
