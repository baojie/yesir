package com.semanticwww.yesir.gateway ;

import java.util.Date ;




/**
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */


abstract public class DefaultGatewayImporter
        implements GatewayImporter
{
    private String name ;

    public DefaultGatewayImporter ()
    {
    }

    abstract public Meme[] importData ( Date cutoff )
            throws GatewayException ;


    public String getName ()
    {
        return name ;
    }

    public void setName ( String name )
    {
        this.name = name ;
    }

    public String toString ()
    {
        return name ;
    }

    public void stop ()
    {}

    abstract public boolean GUISetup () ;

}
