package com.semanticwww.yesir.io.mail ;

import java.awt.Frame ;
import javax.swing.JTextField ;

import com.semanticwww.yesir.gateway.SetupDlg ;
import com.semanticwww.yesir.utils.Debug ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author  Jie Bao
 * @version 1.0
 */

public class MailFileImporterSetupDlg
    extends SetupDlg
{
    private JTextField name = new JTextField() ;
    private JTextField path = new JTextField() ;

    public String getName()
    {
        return name.getText() ;
    }

    public String getPath()
    {
        return path.getText() ;
    }

    public MailFileImporterSetupDlg( Frame frame , String title ,
                                     String nameStr , String pathStr )
    {
        super( frame , title ) ;

        try
        {
            addItem( "Name" , name , nameStr ) ;
            addItem( "Path with mail files" , path , pathStr ) ;

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

        MailFileImporterSetupDlg dlg = new MailFileImporterSetupDlg(
            null , "MailFileImporterSetupDlg" ,
            "Local Mail" ,
            "C:\\mail" ) ;
        dlg.show() ;

        if( dlg.getAction() == SetupDlg.OK )
        {
            Debug.trace( "Name = " + dlg.getName() +
                         "\nFile Path = " + dlg.getPath() ) ;
        }
    }

}
