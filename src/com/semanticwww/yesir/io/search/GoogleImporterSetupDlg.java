package com.semanticwww.yesir.io.search ;

import java.awt.Frame ;
import javax.swing.JTextField ;

import com.semanticwww.yesir.gateway.SetupDlg ;
import com.semanticwww.yesir.utils.Debug ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

public class GoogleImporterSetupDlg
    extends SetupDlg
{
    private JTextField name = new JTextField() ;
    private JTextField clientKey = new JTextField() ;
    private JTextField keyword = new JTextField() ;

    public String getName()
    {
        return name.getText() ;
    }

    public String getClientKey()
    {
        return clientKey.getText() ;
    }

    public String getKeyword()
    {
        return keyword.getText() ;
    }

    public GoogleImporterSetupDlg( Frame frame , String title ,
                                   String nameStr , String clientKeyStr ,
                                   String keywordStr )
    {
        super( frame , title ) ;

        try
        {
            addItem( "Name" , name , nameStr ) ;
            addItem( "Google Liense Key" , clientKey , clientKeyStr ) ;
            addItem( "Keyword" , keyword , keywordStr ) ;

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

        GoogleImporterSetupDlg dlg = new GoogleImporterSetupDlg(
            null , "NewsImporter" ,
            "baojie @ goolge" ,
            "zBBOYPNQFHLV0NxJhRejnr3yFJD76oDW" ,
            "baojie" ) ;
        dlg.show() ;

        if( dlg.getAction() == SetupDlg.OK )
        {
            Debug.trace( "Name = " + dlg.getName() +
                         "\nGoogle Liense Key = " + dlg.getClientKey() +
                         "\nKeyword = " + dlg.getKeyword() ) ;
        }
    }

}
