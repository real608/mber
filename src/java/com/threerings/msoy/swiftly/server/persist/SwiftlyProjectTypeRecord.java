//
// $Id$

package com.threerings.msoy.swiftly.server.persist;

import com.samskivert.jdbc.depot.PersistentRecord;
import com.samskivert.jdbc.depot.annotation.*; // for Depot annotations
import com.samskivert.jdbc.depot.expression.ColumnExp;

import com.threerings.msoy.web.data.SwiftlyProjectType;

/**
 * Contains the definition of a swiftly project type.
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"typeName"})})
public class SwiftlyProjectTypeRecord extends PersistentRecord
{
    // AUTO-GENERATED: FIELDS START
    /** The column identifier for the {@link #typeId} field. */
    public static final String TYPE_ID = "typeId";

    /** The qualified column identifier for the {@link #typeId} field. */
    public static final ColumnExp TYPE_ID_C =
        new ColumnExp(SwiftlyProjectTypeRecord.class, TYPE_ID);

    /** The column identifier for the {@link #typeName} field. */
    public static final String TYPE_NAME = "typeName";

    /** The qualified column identifier for the {@link #typeName} field. */
    public static final ColumnExp TYPE_NAME_C =
        new ColumnExp(SwiftlyProjectTypeRecord.class, TYPE_NAME);

    /** The column identifier for the {@link #displayName} field. */
    public static final String DISPLAY_NAME = "displayName";

    /** The qualified column identifier for the {@link #displayName} field. */
    public static final ColumnExp DISPLAY_NAME_C =
        new ColumnExp(SwiftlyProjectTypeRecord.class, DISPLAY_NAME);
    // AUTO-GENERATED: FIELDS END

    public static final int SCHEMA_VERSION = 2;

    /** The id of the project type. */
    @Id
    @GeneratedValue
    public int typeId;
    
    /** The project type name. */
    public String typeName;

    /** The project display name. Localized key? */
    public String displayName;

    /** 
     * Converts this persistent record to a runtime record.
     */
    public SwiftlyProjectType toSwiftlyProjectType ()
    {
        SwiftlyProjectType spt = new SwiftlyProjectType();
        spt.typeId = typeId;
        spt.typeName = typeName;
        return spt;
    }
}
