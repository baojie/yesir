package com.semanticwww.yesir.io.rss ;

import java.awt.Frame ;
import javax.swing.JTextField ;

import com.semanticwww.yesir.gateway.SetupDlg ;
import com.semanticwww.yesir.utils.Debug ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class RSSExporterSetupDlg
    extends SetupDlg
{
    private JTextField name = new JTextField() ;
    private JTextField rssFilePath = new JTextField() ;

    public String getName()
    {
        return name.getText() ;
    }

    public String getRSSFilePath()
    {
        return rssFilePath.getText() ;
    }

    public RSSExporterSetupDlg( Frame frame , String title ,
                                String nameStr , String rssFilePathStr )
    {
        super( frame , title ) ;

        try
        {
            addItem( "Name" , name , nameStr ) ;
            addItem( "RSS File Path" , rssFilePath , rssFilePathStr ) ;

            this.setSize( 400 , 150 ) ;
            //pack() ;
        }
        catch( Exception ex )
        {
            ex.printStackTrace() ;
        }
    }

    // for test
    public static void main( String[] args )
    {

        RSSExporterSetupDlg dlg = new RSSExporterSetupDlg(
            null , "RSSExporter Setup" ,
            "RSSExporter" , "c:\\baojie-outer" ) ;
        dlg.show() ;

        if( dlg.getAction() == SetupDlg.OK )
        {
            Debug.trace( "Name = " + dlg.getName() +
                         "RSS File Path = " + dlg.getRSSFilePath() ) ;
        }
    }

}
