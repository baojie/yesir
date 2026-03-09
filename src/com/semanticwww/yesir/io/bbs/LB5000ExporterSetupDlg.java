package com.semanticwww.yesir.io.bbs ;

import java.awt.Frame ;
import javax.swing.JPasswordField ;
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

public class LB5000ExporterSetupDlg
    extends SetupDlg
{
    private JTextField name = new JTextField() ;
    private JTextField cgiURL = new JTextField() ;
    private JTextField boardNo = new JTextField() ;
    private JTextField user = new JTextField() ;
    private JPasswordField password = new JPasswordField() ;

    public String getName()
    {
        return name.getText() ;
    }

    public String getCgiURL()
    {
        return cgiURL.getText() ;
    }

    public String getBoardNo()
    {
        return boardNo.getText() ;
    }

    public String getUser()
    {
        return user.getText() ;
    }

    public String getPassword()
    {
        return new String( password.getPassword() ) ;
    }

    public LB5000ExporterSetupDlg( Frame frame , String title ,
                                   String nameStr ,
                                   String cgiURLStr ,
                                   String boardNoStr ,
                                   String userStr ,
                                   String passwordStr )
    {
        super( frame , title ) ;

        try
        {
            addItem( "Exporter Name" , name , nameStr ) ;
            addItem( "CGI URL" , cgiURL , cgiURLStr ) ;
            addItem( "Board Number" , boardNo , boardNoStr ) ;
            addItem( "User name" , user , userStr ) ;
            addItem( "Password" , password , passwordStr ) ;

            this.setSize( 400 , 250 ) ;
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

        LB5000ExporterSetupDlg dlg = new LB5000ExporterSetupDlg(
            null , "LB5000Exporter Setup" ,
            "www.ISUBBS.com" ,
            "http://boole.cs.iastate.edu/isubbs/post.cgi" ,
            "13" ,
            "Test" ,
            "12345678" ) ;
        dlg.show() ;

        if( dlg.getAction() == SetupDlg.OK )
        {
            Debug.trace( "Name = " + dlg.getName() +
                         "\nCGI URL = " + dlg.getCgiURL() +
                         "\nBoard Number = " + dlg.getBoardNo() +
                         "\nUser name = " + dlg.getUser() +
                         "\nPassword = " + dlg.getPassword()
                         ) ;
        }
    }

}
