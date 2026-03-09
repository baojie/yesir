package com.semanticwww.yesir.io.blank ;

import com.semanticwww.yesir.gateway.DefaultGatewayExporter ;
import com.semanticwww.yesir.gateway.Meme ;

/**
 *
 * <p>Title: </p>
 * <p>Description: A expoter that does nothing</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */
public class BlankExporter
    extends DefaultGatewayExporter
{
    public void jbInit( String name )
    {
        setName( name ) ;
    }

    /**
     * GUISetup
     *
     * @return boolean
     */
    public boolean GUISetup()
    {
        BlankExporterSetupDlg dlg = new BlankExporterSetupDlg(
            null , "BlankExporter Setup" ,
            getName() ) ;
        dlg.show() ;

        if( dlg.getAction() == dlg.OK )
        {
            jbInit( dlg.getName() ) ;
            return true ;
        }
        else
        {
            return false ;
        }
    }

    /**
     * exportData
     *
     * @param message Meme
     */
    public void exportData( Meme message )
    {
    }
}
