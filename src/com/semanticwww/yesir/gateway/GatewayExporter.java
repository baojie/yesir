package com.semanticwww.yesir.gateway ;

/**
 * A gateway exporter knows how to export forum messages to an external data
 * source. It can be combined with a gateway importer to make a full gateway.
 *
 * @author Jie Bao
 * @version 1.0 2003-12-10
 *
 * @see Gateway
 * @see GatewayImporter
 */
public interface GatewayExporter
{

    /**
     * Export a single message to the data source. For example, a concrete
     * implementation of this interface might post the message to a newsgroup.
     *
     * @param message a message to export.
     */
    public void exportData( Meme message )
        throws GatewayException ;

    /**
     * Export a group of messages to the data source.
     *
     * @param messages an array of messages to export.
     */
    public void exportData( Meme[] messages )
        throws GatewayException ;

    /**
     * Stop a running export.
     */
    public void stop()
        throws GatewayException ;
}
