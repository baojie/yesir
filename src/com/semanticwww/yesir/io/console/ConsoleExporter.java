package com.semanticwww.yesir.io.console ;

import javax.swing.JOptionPane ;

import com.semanticwww.yesir.gateway.DefaultGatewayExporter ;
import com.semanticwww.yesir.gateway.Meme ;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0 2003-12-08
 */
public class ConsoleExporter
    extends DefaultGatewayExporter
{
    public boolean dummy = true ;

    public boolean isDummy()
    {
        return dummy ;
    }

    public void setDummy( boolean dummy )
    {
        this.dummy = dummy ;
    }

    public void jbInit( String name )
    {
        setName( name ) ;
    }

    /**
     * exportData
     *
     * @param message Meme
     */
    public void exportData( Meme message )
    {

        String content = message.toString() ;
        if( content == null )
        {
            content = "" ;
        }
        if( content.length() > 2048 )
        {
            content = content.substring( 0 , 2048 ) + "..." ;

        }
        System.out.println( content ) ;
        if( dummy )
        {
            return ;
        }
        JOptionPane.showMessageDialog( null , content ,
                                       message.getSubject() ,
                                       JOptionPane.INFORMATION_MESSAGE ) ;

    }

    /**
     * GUISetup
     */
    public boolean GUISetup()
    {
        ConsoleExporterSetupDlg dlg = new ConsoleExporterSetupDlg(
            null , "ConsoleExporterSetupDlg" ,
            name , dummy ) ;
        dlg.show() ;

        if( dlg.getAction() == dlg.OK )
        {
            jbInit( dlg.getName() ) ;
            dummy = dlg.getDummy() ;
            return true ;
        }
        else
        {
            return false ;
        }
    }

}
