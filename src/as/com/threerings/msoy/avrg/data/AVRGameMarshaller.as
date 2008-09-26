//
// $Id$

package com.threerings.msoy.avrg.data {

import com.threerings.msoy.avrg.client.AVRGameService;
import com.threerings.presents.client.Client;
import com.threerings.presents.client.InvocationService_ConfirmListener;
import com.threerings.presents.client.InvocationService_InvocationListener;
import com.threerings.presents.data.InvocationMarshaller;
import com.threerings.presents.data.InvocationMarshaller_ConfirmMarshaller;
import com.threerings.presents.data.InvocationMarshaller_ListenerMarshaller;
import com.threerings.util.Float;
import com.threerings.util.Integer;

/**
 * Provides the implementation of the <code>AVRGameService</code> interface
 * that marshalls the arguments and delivers the request to the provider
 * on the server. Also provides an implementation of the response listener
 * interfaces that marshall the response arguments and deliver them back
 * to the requesting client.
 */
public class AVRGameMarshaller extends InvocationMarshaller
    implements AVRGameService
{
    /** The method id used to dispatch <code>awardPrize</code> requests. */
    public static const AWARD_PRIZE :int = 1;

    // from interface AVRGameService
    public function awardPrize (arg1 :Client, arg2 :String, arg3 :int, arg4 :InvocationService_InvocationListener) :void
    {
        var listener4 :InvocationMarshaller_ListenerMarshaller = new InvocationMarshaller_ListenerMarshaller();
        listener4.listener = arg4;
        sendRequest(arg1, AWARD_PRIZE, [
            arg2, Integer.valueOf(arg3), listener4
        ]);
    }

    /** The method id used to dispatch <code>awardTrophy</code> requests. */
    public static const AWARD_TROPHY :int = 2;

    // from interface AVRGameService
    public function awardTrophy (arg1 :Client, arg2 :String, arg3 :int, arg4 :InvocationService_InvocationListener) :void
    {
        var listener4 :InvocationMarshaller_ListenerMarshaller = new InvocationMarshaller_ListenerMarshaller();
        listener4.listener = arg4;
        sendRequest(arg1, AWARD_TROPHY, [
            arg2, Integer.valueOf(arg3), listener4
        ]);
    }

    /** The method id used to dispatch <code>completeTask</code> requests. */
    public static const COMPLETE_TASK :int = 3;

    // from interface AVRGameService
    public function completeTask (arg1 :Client, arg2 :int, arg3 :String, arg4 :Number, arg5 :InvocationService_ConfirmListener) :void
    {
        var listener5 :InvocationMarshaller_ConfirmMarshaller = new InvocationMarshaller_ConfirmMarshaller();
        listener5.listener = arg5;
        sendRequest(arg1, COMPLETE_TASK, [
            Integer.valueOf(arg2), arg3, Float.valueOf(arg4), listener5
        ]);
    }
}
}
