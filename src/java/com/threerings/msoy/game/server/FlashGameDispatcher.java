//
// $Id$

package com.threerings.msoy.game.server;

import com.threerings.msoy.game.client.FlashGameService;
import com.threerings.msoy.game.data.FlashGameMarshaller;
import com.threerings.presents.client.Client;
import com.threerings.presents.client.InvocationService;
import com.threerings.presents.data.ClientObject;
import com.threerings.presents.data.InvocationMarshaller;
import com.threerings.presents.server.InvocationDispatcher;
import com.threerings.presents.server.InvocationException;

/**
 * Dispatches requests to the {@link FlashGameProvider}.
 */
public class FlashGameDispatcher extends InvocationDispatcher
{
    /**
     * Creates a dispatcher that may be registered to dispatch invocation
     * service requests for the specified provider.
     */
    public FlashGameDispatcher (FlashGameProvider provider)
    {
        this.provider = provider;
    }

    // documentation inherited
    public InvocationMarshaller createMarshaller ()
    {
        return new FlashGameMarshaller();
    }

    // documentation inherited
    public void dispatchRequest (
        ClientObject source, int methodId, Object[] args)
        throws InvocationException
    {
        switch (methodId) {
        case FlashGameMarshaller.END_GAME:
            ((FlashGameProvider)provider).endGame(
                source,
                (int[])args[0], (InvocationService.InvocationListener)args[1]
            );
            return;

        case FlashGameMarshaller.END_TURN:
            ((FlashGameProvider)provider).endTurn(
                source,
                ((Integer)args[0]).intValue(), (InvocationService.InvocationListener)args[1]
            );
            return;

        default:
            super.dispatchRequest(source, methodId, args);
            return;
        }
    }
}
