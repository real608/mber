//
// $Id$

package com.threerings.msoy.data.all {

import com.threerings.msoy.client.DeploymentConfig;

/**
 * Utility routines relating to a member's email address.
 */
public class MemberMailUtil
{

    public static function isPermaguest (email :String) :Boolean
    {
        //Check if the person has the anon and @whirled.com strings in their emails
        return (email.indexOf("anon") >= 0) && (email.indexOf("@www.whirled.com") >= 0)
    }
}
}
