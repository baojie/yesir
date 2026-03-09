package com.semanticwww.yesir.gateway ;

import java.io.PrintStream ;
import java.io.PrintWriter ;

/**
 * Thrown when an error occurs during a Gateway operation.
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */

public class GatewayException
    extends Exception
{

    private Throwable nestedThrowable = null ;

    public GatewayException()
    {
        super() ;
    }

    public GatewayException( String msg )
    {
        super( msg ) ;
    }

    public GatewayException( Throwable nestedThrowable )
    {
        this.nestedThrowable = nestedThrowable ;
    }

    public GatewayException( String msg , Throwable nestedThrowable )
    {
        super( msg ) ;
        this.nestedThrowable = nestedThrowable ;
    }

    public void printStackTrace()
    {
        super.printStackTrace() ;
        if( nestedThrowable != null )
        {
            nestedThrowable.printStackTrace() ;
        }
    }

    public void printStackTrace( PrintStream ps )
    {
        super.printStackTrace( ps ) ;
        if( nestedThrowable != null )
        {
            nestedThrowable.printStackTrace( ps ) ;
        }
    }

    public void printStackTrace( PrintWriter pw )
    {
        super.printStackTrace( pw ) ;
        if( nestedThrowable != null )
        {
            nestedThrowable.printStackTrace( pw ) ;
        }
    }
}
