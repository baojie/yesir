package com.semanticwww.yesir.io.mail ;

import java.io.IOException ;
import java.io.PrintWriter ;
import java.io.Writer ;

import com.semanticwww.yesir.gateway.DefaultGatewayExporter ;
import com.semanticwww.yesir.gateway.GatewayException ;
import com.semanticwww.yesir.gateway.Meme ;
import com.semanticwww.yesir.utils.PrintCommandListener ;
import org.apache.commons.net.smtp.SMTPClient ;
import org.apache.commons.net.smtp.SMTPReply ;
import org.apache.commons.net.smtp.SimpleSMTPHeader ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

public class SmtpExporter
        extends DefaultGatewayExporter
{
    public static final String DEFAULT_SENDER = "baojie@cs.iastate.edu" ;

    String smtpServer = "mailhub.cs.iastate.edu";

    String sender = DEFAULT_SENDER ;

    String reciever ;


    public void jbInit ( String name , String smtpServer , String sender ,
                         String reciever )
    {
        setName ( name ) ;
        this.smtpServer = smtpServer ;
        this.sender = sender ;
        this.reciever = reciever ;
    }

    public boolean GUISetup ()
    {
        SmtpExporterSetupDlg dlg = new SmtpExporterSetupDlg (
                null , "SmtpExporter Setup" ,
                getName () , this.smtpServer , this.sender , this.reciever ) ;
        dlg.show () ;

        if ( dlg.getAction () == dlg.OK )
        {
            jbInit ( dlg.getName () , dlg.getSmtpServer () ,
                     dlg.getSender () , dlg.getReciever () ) ;
            return true ;
        }
        else
        {
            return false ;
        }

    }

    public void exportData ( Meme message )
            throws GatewayException
    {
        String subject ;
        Writer writer ;
        SimpleSMTPHeader header ;
        SMTPClient client ;

        try
        {

            subject = message.getSubject () ;
            header = new SimpleSMTPHeader ( sender , reciever , subject ) ;

            String body ;
            body = message.report ( false ) ;

            client = new SMTPClient () ;
            client.addProtocolCommandListener ( new PrintCommandListener (
                    new PrintWriter ( System.out ) ) ) ;

            client.connect ( smtpServer ) ;
            int reply = client.getReplyCode () ;

            if ( !SMTPReply.isPositiveCompletion ( reply ) )
            {
                client.disconnect () ;
                System.err.println ( "SMTP server refused connection." ) ;
                System.exit ( 1 ) ;
            }

            client.login () ;

            client.setSender ( sender ) ;
            client.addRecipient ( reciever ) ;
            /*
                        enum = ccList.elements() ;

                        while( enum.hasMoreElements() )
                        {
             client.addRecipient( ( String ) enum.nextElement() ) ;

                        }
                        }*/
            writer = client.sendMessageData () ;

            if ( writer != null )
            {
                writer.write ( header.toString () ) ;
                writer.write ( body ) ;
                writer.close () ;
                boolean r = client.completePendingCommand () ;
                if ( r )
                {
                    System.out.println ( "Mail is sent successfully." ) ;
                }
                else
                {
                    System.out.println ( "Mail sending fails." ) ;
                }
            }

            client.logout () ;
            client.disconnect () ;

        }
        catch ( IOException e )
        {
            e.printStackTrace () ;
            System.exit ( 1 ) ;
        }
    }

    public static void main ( String[] args )
    {
        SmtpExporter smtpEporter1 = new SmtpExporter () ;
        smtpEporter1.jbInit ( "smtp test" , "mailhub.iastate.edu" ,
                              DEFAULT_SENDER ,
                              DEFAULT_SENDER ) ;

        Meme m = new Meme () ;
        m.setAuthor ( "baojie@iastate.edu" ) ;
        m.setSubject ( "Test" ) ;
        m.setContent ( "This is a test letter" ) ;
        try
        {
            smtpEporter1.exportData ( m ) ;
        }
        catch ( GatewayException ex )
        {
            ex.printStackTrace () ;
        }
    }
}
