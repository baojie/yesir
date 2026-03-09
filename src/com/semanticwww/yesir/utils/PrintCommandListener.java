package com.semanticwww.yesir.utils ;

import java.io.PrintWriter ;

import org.apache.commons.net.ProtocolCommandEvent ;
import org.apache.commons.net.ProtocolCommandListener ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PrintCommandListener
        implements ProtocolCommandListener
{
    private PrintWriter __writer ;

    public PrintCommandListener ( PrintWriter writer )
    {
        __writer = writer ;
    }

    public void protocolCommandSent ( ProtocolCommandEvent event )
    {
        __writer.print ( event.getMessage () ) ;
        __writer.flush () ;
    }

    public void protocolReplyReceived ( ProtocolCommandEvent event )
    {
        __writer.print ( event.getMessage () ) ;
        __writer.flush () ;
    }
}
