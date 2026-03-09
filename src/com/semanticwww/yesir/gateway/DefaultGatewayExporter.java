package com.semanticwww.yesir.gateway ;

/**
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */


abstract public class DefaultGatewayExporter
    implements GatewayExporter
{
    protected String name = null ;

    abstract public void exportData( Meme message )
        throws GatewayException ;

    /**
     * exportData
     *
     * @param messages Meme[]
     */
    public void exportData( Meme[] messages )
        throws GatewayException
    {
        for( int i = 0 ; i < messages.length ; i++ )
        {
            exportData( messages[i] ) ;
        }
    }

    public void stop()
        throws GatewayException
    {
    }

    public String getName()
    {
        return name ;
    }

    public void setName( String name )
    {
        this.name = name ;
    }

    public String toString()
    {
        return name ;
    }

    abstract public boolean GUISetup() ;
}
