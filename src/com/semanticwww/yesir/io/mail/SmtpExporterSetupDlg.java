package com.semanticwww.yesir.io.mail ;

import javax.swing.JTextField ;
import com.semanticwww.yesir.gateway.SetupDlg ;
import java.awt.Frame ;
import com.semanticwww.yesir.utils.Debug ;




/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SmtpExporterSetupDlg
        extends SetupDlg
{
    private JTextField name = new JTextField () ;

    private JTextField smtpServer = new JTextField () ;

    private JTextField sender = new JTextField () ;
    private JTextField reciever = new JTextField () ;


    public String getName ()
    {
        return name.getText () ;
    }

    public String getSmtpServer ()
    {
        return smtpServer.getText () ;
    }

    public String getReciever ()
    {
        return reciever.getText () ;
    }
    public String getSender ()
    {
        return sender.getText () ;
    }


    public SmtpExporterSetupDlg ( Frame frame , String title ,
                                  String nameStr , String smtpServerStr ,
                                  String senderStr, String recieverStr )
    {
        super ( frame , title ) ;

        try
        {
            addItem ( "Exporter Name" , name , nameStr ) ;
            addItem ( "SMTP server" , smtpServer , smtpServerStr ) ;
            addItem ( "mail sender" , sender , senderStr ) ;
            addItem ( "mail reciever" , reciever , recieverStr ) ;

            this.setSize ( 400 , 210 ) ;
            //pack() ;
        }
        catch ( Exception ex )
        {
            ex.printStackTrace () ;
        }
    }

    // for test
    public static void main ( String[] args )
    {

        SmtpExporterSetupDlg dlg = new SmtpExporterSetupDlg (
                null , "smtp test" ,
                "Smtp Test" , "mailhub.iastate.edu" ,"baojie@cs.iastate.edu", "baojie@cs.iastate.edu" ) ;
        dlg.show () ;

        if ( dlg.getAction () == SetupDlg.OK )
        {
            Debug.trace ( "Name = " + dlg.getName () +
                          "\nSmtp server = " + dlg.getSmtpServer () +
                          "\nReciever = " + dlg.getReciever () ) ;
        }
    }

}
