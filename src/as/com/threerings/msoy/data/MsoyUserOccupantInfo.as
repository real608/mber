//
// $Id$

package com.threerings.msoy.data {

public interface MsoyUserOccupantInfo
{
    /**
     * Is this user a subscriber?
     */
    function isSubscriber () :Boolean;
    
    /**
     * Is this user a member of staff?
     */
    function isSupport () :Boolean;
}
}
