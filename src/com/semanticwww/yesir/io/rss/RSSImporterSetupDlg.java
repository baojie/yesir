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

public class RSSImporterSetupDlg
    extends SetupDlg
{
    private JTextField name = new JTextField() ;
    private JTextField url = new JTextField() ;

    public String getName()
    {
        return name.getText() ;
    }

    public String getURL()
    {
        return url.getText() ;
    }

    public RSSImporterSetupDlg( Frame frame , String title ,
                                String nameStr , String urlStr )
    {
        super( frame , title ) ;

        try
        {
            addItem( "Name" , name , nameStr ) ;
            addItem( "Source URI" , url , urlStr ) ;

            this.setSize( 400 , 200 ) ;
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

        RSSImporterSetupDlg dlg = new RSSImporterSetupDlg(
            null , "RSSImporterSetupDlg" ,
            "RSS Importer from Bao Jie's homepage" ,
            "http://boole.cs.iastate.edu:9090/popeye/rss.rdf" ) ;
        dlg.show() ;

        if( dlg.getAction() == SetupDlg.OK )
        {
            Debug.trace( "Name = " + dlg.getName() + "\nURL = " +
                         dlg.getURL() ) ;
        }
    }

}
