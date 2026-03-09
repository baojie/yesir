package com.semanticwww.yesir.io.blank ;

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

public class BlankExporterSetupDlg
    extends SetupDlg
{
    private JTextField name = new JTextField() ;

    public String getName()
    {
        return name.getText() ;
    }

    public BlankExporterSetupDlg( Frame frame , String title ,
                                  String nameStr )
    {
        super( frame , title ) ;

        try
        {
            addItem( "Name" , name , nameStr ) ;

            this.setSize( 400 , 125 ) ;
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

        BlankExporterSetupDlg dlg = new BlankExporterSetupDlg(
            null , "BlankExporter Setup" ,
            "BlankExporter" ) ;
        dlg.show() ;

        if( dlg.getAction() == SetupDlg.OK )
        {
            Debug.trace( "Name = " + dlg.getName() ) ;
        }
    }

}
