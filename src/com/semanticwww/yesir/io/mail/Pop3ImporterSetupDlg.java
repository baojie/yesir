package com.semanticwww.yesir.io.mail ;

import java.awt.Frame ;
import javax.swing.JCheckBox ;
import javax.swing.JPasswordField ;
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

public class Pop3ImporterSetupDlg
    extends SetupDlg
{
    private JTextField name = new JTextField() ;
    public JTextField protocol = new JTextField() ;
    public JTextField host = new JTextField() ;
    public JTextField user = new JTextField() ;
    public JTextField password = new JPasswordField() ;
    public JTextField restrictedReciever = new JTextField() ;
    public JCheckBox keepOnHost = new JCheckBox() ;

    public String getName()
    {
        return name.getText() ;
    }

    public String getProtocol()
    {
        return protocol.getText() ;
    }

    public String getHost()
    {
        return host.getText() ;
    }

    public String getUser()
    {
        return user.getText() ;
    }

    public String getPassword()
    {
        return password.getText() ;
    }

    public String getRestrictedReciever()
    {
        return restrictedReciever.getText() ;
    }

    public boolean getKeepOnHost()
    {
        return keepOnHost.isSelected() ;
    }

    public Pop3ImporterSetupDlg( Frame frame , String title ,
                                 String nameStr ,
                                 String protocolStr ,
                                 String hostStr ,
                                 String userStr ,
                                 String passwordStr ,
                                 String restrictedRecieverStr ,
                                 boolean keepOnHostVal )
    {
        super( frame , title ) ;

        try
        {
            addItem( "Name" , name , nameStr ) ;
            addItem( "Protocol(pop3/imap)" , protocol , protocolStr ) ;
            addItem( "Host" , host , hostStr ) ;
            addItem( "User" , user , userStr ) ;
            addItem( "Password" , password , passwordStr ) ;
            addItem( "Restricted Reciever" , restrictedReciever ,
                     restrictedRecieverStr ) ;
            addItem( "Keep on host" , keepOnHost , new Boolean( keepOnHostVal ) ) ;

            this.setSize( 400 , 300 ) ;
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

        Pop3ImporterSetupDlg dlg = new Pop3ImporterSetupDlg(
            null , "Pop3, IMAP Importer SetupDlg" ,
            "Mail importer" ,
            "pop3" ,
            "pop.cs.iastate.edu" ,
            "baojie" ,
            "" ,
            "aiseminar@cs.iastate.edu" ,
            true ) ;
        dlg.show() ;

        if( dlg.getAction() == SetupDlg.OK )
        {
            Debug.trace( "Name = " + dlg.getName() ) ;
        }
    }

}
