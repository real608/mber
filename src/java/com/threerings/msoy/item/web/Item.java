//
// $Id$

package com.threerings.msoy.item.web;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.threerings.io.Streamable;

/**
 * The base class for all digital items in the MSOY system.
 *
 * <p><em>Note:</em> this class and all derived classes are very strictly
 * limited in their contents as they must be translatable into JavaScript
 * ({@link IsSerializable}) and must work with the Presents streaming system
 * ({@link Streamable}).
 */
public abstract class Item implements Streamable, IsSerializable
{
    // DON'T EVER CHANGE THE MAGIC NUMBERS ASSIGNED TO EACH CLASS
    public static final byte NOT_A_TYPE = (byte) 0;
    public static final byte PHOTO = registerItemType(Photo.class, 1);
    public static final byte DOCUMENT = registerItemType(Document.class, 2);
    public static final byte FURNITURE = registerItemType(Furniture.class, 3);
    public static final byte GAME = registerItemType(Game.class, 4);
    public static final byte AVATAR = registerItemType(Avatar.class, 5);
    public static final byte PET = registerItemType(Pet.class, 6);
    // DON'T EVER CHANGE THE MAGIC NUMBERS ASSIGNED TO EACH CLASS
    
    /** This item's unique identifier. <em>Note:</em> this identifier is not
     * globally unique among all digital items. Each type of item has its own
     * identifier space. */
    public int itemId;

    /** The item ID from which this object was cloned, or -1 if this is not
     * a clone. */
    public int parentId = -1;
    
    /** A bit-mask of flags that we need to know about every digital item
     * without doing further database lookups or network requests. */
    public byte flags;

    /** The member id of the member that created this item. */
    public int creatorId;

    /** The member id of the member that owns this item, or -1 if the item
     *  is an immutable catalog listing. */
    public int ownerId;

    /** The current rating of this item, either 0 or between 1 and 5. */
    public float rating;
    
    /** The media used to display this item's thumbnail representation. */
    public MediaDesc thumbMedia;

    /** The media used to display this item's furniture representation. */
    public MediaDesc furniMedia;

    /**
     * Get the class for the specified item type.
     */
    public static Class getClassForType (byte itemType)
    {
        return (Class) _mapping.get(new Byte(itemType));
    }

    /**
     * Get the item type for the specified item class.
     */
    public static byte getTypeForClass (Class iclass)
    {
        Byte val = (Byte) _reverseMapping.get(iclass);
        return (val != null) ? val.byteValue() : NOT_A_TYPE;
    }

    /**
     * Get the Stringy name of the specified item type.
     */
    public static String getTypeName (byte type)
    {
        // we can't use reflection here... gawdammy TODO
        // We also can't use a switch statement because our final
        // variables are not actually constants (they are assigned values
        // at class initialization time).
        if (type == PHOTO) {
            return "PHOTO";
        } else if (type == AVATAR) {
            return "AVATAR";
        } else if (type == GAME) {
            return "GAME";
        } else if (type == PHOTO) {
            return "PHOTO";
        } else if (type == DOCUMENT) {
            return "DOCUMENT";
        } else if (type == PET) {
            return "PET";
        } else {
            return null;
        }
    }

    /**
     * Returns this item's composite identifier.
     */
    public ItemIdent getIdent ()
    {
        return new ItemIdent(getType(), itemId);
    }

    /**
     * Get the type code for this item.
     */
    public abstract byte getType ();

    /**
     * Get a textual description of this item.
     */
    public abstract String getDescription ();

    /**
     * Returns the parentId, or the itemId if this item is not a clone.
     */
    public int getProgenitorId ()
    {
        return (parentId == -1) ? itemId : parentId;
    }

    /**
     * Returns a media descriptor for the media that should be used to display
     * our thumbnail representation.
     */
    public MediaDesc getThumbnailMedia ()
    {
        return (thumbMedia == null) ? getDefaultThumbnailMedia() : thumbMedia;
    }

    /**
     * Returns a media descriptor for the media that should be used to display
     * our furniture representation.
     */
    public MediaDesc getFurniMedia ()
    {
        return (furniMedia == null) ? getDefaultFurniMedia() : furniMedia;
    }

    /**
     * Returns the path to a thumbnail image for this item.
     */
    public String getThumbnailPath ()
    {
        return getThumbnailMedia().getMediaPath();
    }

    /**
     * Called during item creation to ensure that media descriptors are not
     * duplicated in this item more than necessary.
     */
    public void checkConsolidateMedia ()
    {
        if (thumbMedia != null &&
            getThumbnailMedia().equals(getDefaultThumbnailMedia())) {
            thumbMedia = null;
        }

        if (furniMedia != null &&
            getFurniMedia().equals(getDefaultFurniMedia())) {
            furniMedia = null;
        }
    }

    /**
     * Verify that all the required fields in this particular Item subclass are
     * filled in, make sense, and are consistent with each other. This is used
     * to verify the data being edited by a user during item creation, and also
     * that the final uploaded item isn't hacked.
     */
    public boolean isConsistent ()
    {
        return true;
    }

    // @Override
    public int hashCode ()
    {
        return itemId;
    }

    // @Override
    public boolean equals (Object other)
    {
        if (other instanceof Item) {
            Item that = (Item) other;
            // cheap comparison first...
            return (this.itemId == that.itemId) &&
                (this.getType() == that.getType());
        }
        return false;
    }

    /**
     * Returns the default thumbnail media for use if this item has no provided
     * custom media.
     */
    protected MediaDesc getDefaultThumbnailMedia ()
    {
        return new StaticMediaDesc(StaticMediaDesc.THUMBNAIL, getType());
    }

    /**
     * Returns the default furni media for use if this item has no provided
     * custom media.
     */
    protected MediaDesc getDefaultFurniMedia ()
    {
        return new StaticMediaDesc(StaticMediaDesc.FURNI, getType());
    }

    /**
     * A handy method that makes sure that the specified text is not null or
     * all-whitespace. Usually used by isConsistent().
     */
    protected static boolean nonBlank (String text)
    {
        return (text != null) && (text.trim().length() > 0);
    }

    /**
     * Register a concrete subclass and it's associated type code.
     */
    private static byte registerItemType (Class iclass, int itype)
    {
        byte type = (byte) itype;
        if (itype != type) {
            throw new IllegalArgumentException("not a byte");
        }

        if (_mapping == null) {
            _mapping = new HashMap();
            _reverseMapping = new HashMap();
        }

        Byte otype = new Byte(type);
        _mapping.put(otype, iclass);
        _reverseMapping.put(iclass, otype);

        return type;
    }

    private static HashMap _mapping;
    private static HashMap _reverseMapping;
}
