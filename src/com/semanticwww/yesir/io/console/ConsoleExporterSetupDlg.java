package com.semanticwww.yesir.io.console ;

import java.awt.Frame ;
import javax.swing.JCheckBox ;
import javax.swing.JTextField ;

import com.semanticwww.yesir.gateway.SetupDlg ;
import com.semanticwww.yesir.utils.Debug ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */

public class ConsoleExporterSetupDlg
    extends SetupDlg
{
    private JTextField name = new JTextField() ;
    private JCheckBox dummy = new JCheckBox() ;

    public String getName()
    {
        return name.getText() ;
    }

    public boolean getDummy()
    {
        return dummy.isSelected() ;
    }

    public ConsoleExporterSetupDlg( Frame frame , String title ,
                                    String nameStr , boolean dummyStr )
    {
        super( frame , title ) ;

        try
        {
            addItem( "Name" , name , nameStr ) ;
            addItem( "Console only( No GUI dialogs)" , dummy ,
                     new Boolean( dummyStr ) ) ;

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

        ConsoleExporterSetupDlg dlg = new ConsoleExporterSetupDlg(
            null , "ConsoleExporter Setup" ,
            "ConsoleExporter" , false ) ;
        dlg.show() ;

        if( dlg.getAction() == SetupDlg.OK )
        {
            Debug.trace( "Name = " + dlg.getName() + "\nDummy = " +
                         dlg.getDummy() ) ;
        }
    }

}
