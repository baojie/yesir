package com.semanticwww.yesir.gateway ;

import java.util.Date ;

/**
 * A gateway importer knows how to import forum data from an external data
 * source. It can be combined with a gateway exporter to make a full gateway.
 *
 * @see Gateway
 * @see GatewayExporter
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */
public interface GatewayImporter
{

    /**
     * Import data from the data source into the specified forum. For example,
     * a concrete implementation of this interface might poll a newsgroup and
     * add new messages to the forum.
     *
     * @param date the oldest cutoff date for data to import.
     */
    public Meme[] importData( Date cutoff )
        throws GatewayException ;

    /**
     * Stop a running import.
     */
    public void stop()
        throws GatewayException ;
}
