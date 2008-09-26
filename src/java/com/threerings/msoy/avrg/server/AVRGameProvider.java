//
// $Id$

package com.threerings.msoy.avrg.server;

import com.threerings.msoy.avrg.client.AVRGameService;
import com.threerings.presents.client.InvocationService;
import com.threerings.presents.data.ClientObject;
import com.threerings.presents.server.InvocationException;
import com.threerings.presents.server.InvocationProvider;

/**
 * Defines the server-side of the {@link AVRGameService}.
 */
public interface AVRGameProvider extends InvocationProvider
{
    /**
     * Handles a {@link AVRGameService#awardPrize} request.
     */
    void awardPrize (ClientObject caller, String arg1, int arg2, InvocationService.InvocationListener arg3)
        throws InvocationException;

    /**
     * Handles a {@link AVRGameService#awardTrophy} request.
     */
    void awardTrophy (ClientObject caller, String arg1, int arg2, InvocationService.InvocationListener arg3)
        throws InvocationException;

    /**
     * Handles a {@link AVRGameService#completeTask} request.
     */
    void completeTask (ClientObject caller, int arg1, String arg2, float arg3, InvocationService.ConfirmListener arg4)
        throws InvocationException;
}
