/**
 */

package com.semanticwww.yesir.io.mail ;

import java.util.Date ;
import java.util.Properties ;
import java.util.Vector ;

import javax.mail.Flags ;
import javax.mail.Flags.Flag ;
import javax.mail.Folder ;
import javax.mail.Message ;
import javax.mail.Session ;
import javax.mail.Store ;

import javax.swing.JOptionPane ;

import com.semanticwww.yesir.gateway.DefaultGatewayImporter ;
import com.semanticwww.yesir.gateway.GatewayException ;
import com.semanticwww.yesir.gateway.Meme ;
/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */
public class Pop3Importer
    extends DefaultGatewayImporter
{

    String protocol ;
    String host ;
    String popUser ;
    String popPassword ;
    String restrictedReciever ;
    boolean keepOnHost ;

    public void jbInit( String name , String protocol , String host ,
                        String popUser ,
                        String popPassword , String restrictedReciever ,
                        boolean keepOnHost )

    {
        setName( name ) ;
        this.protocol = protocol ;
        this.host = host ;
        this.popUser = popUser ;
        this.popPassword = popPassword ;
        this.restrictedReciever = restrictedReciever ;
        this.keepOnHost = keepOnHost ;

    }

    /**
     * Check the pop server and return all mails
     *
     * @return
     */
    public Meme[] importData( Date cutoff )
        throws GatewayException
    {
        MailParser parser = new MailParser( protocol , host , popUser ,
                                            popPassword ) ;

        Vector list = new Vector() ;

        Store store = null ;
        Folder folder = null ;
        try
        {
            Properties props = System.getProperties() ;
            Session session = Session.getDefaultInstance( props , null ) ;
            store = session.getStore( protocol ) ;
            store.connect( host , popUser , popPassword ) ;
            folder = store.getDefaultFolder() ;
            if( folder == null )
            {
                throw new GatewayException( "No default folder" ) ;
            }
            folder = folder.getFolder( "INBOX" ) ;
            if( folder == null )
            {
                throw new GatewayException( "No POP3 INBOX" ) ;
            }
            folder.open( Folder.READ_WRITE ) ;
            Message[] msgs = folder.getMessages() ;

            System.out.println( "Messages on server: " + msgs.length ) ;

            for( int msgNum = 0 ; msgNum < msgs.length ; msgNum++ )
            {

                if( restrictedReciever == null ||
                    MailMeme.IsSendTo( restrictedReciever , msgs[msgNum] ) )
                {
                    System.out.println( parser.parseEnvelope( msgs[msgNum] ) ) ;
                    Meme m = MailMeme.fromMessage( msgs[msgNum] ) ;
                    m.setURL( restrictedReciever ) ;
                    if( m.after( cutoff ) )
                    {
                        list.add( m ) ;
                        msgs[msgNum].setFlag( Flag.SEEN , true ) ;
                    }

                }
            }
        }
        catch( Exception ex )
        {
//            System.err.println(ex.getMessage());
//            ex.printStackTrace() ;
            JOptionPane.showMessageDialog( null ,
                                           "Error on loading: " + ex.getMessage() ) ;
            throw new GatewayException( ex.getMessage() ) ;
        }
        finally
        {
            try
            {
                if( folder != null )
                {
                    folder.close( !keepOnHost ) ;
                }
                if( store != null )
                {
                    store.close() ;
                }
            }
            catch( Exception ex2 )
            {
                ex2.printStackTrace() ;
            }
        }

        Meme memes[] = new Meme[list.size()] ;
        for( int i = 0 ; i < memes.length ; i++ )
        {
            memes[i] = ( Meme ) list.get( i ) ;
        }
        return memes ;
    }

    /**
     * GUISetup
     */
    public boolean GUISetup()
    {

        Pop3ImporterSetupDlg dlg = new Pop3ImporterSetupDlg(
            null , "Pop3, IMAP Importer SetupDlg" ,
            getName() ,
            protocol , host , popUser , popPassword ,
            restrictedReciever , keepOnHost ) ;
        dlg.show() ;

        if( dlg.getAction() == dlg.OK )
        {
            jbInit( dlg.getName() ,
                    dlg.getProtocol() ,
                    dlg.getHost() ,
                    dlg.getUser() ,
                    dlg.getPassword() ,
                    dlg.getRestrictedReciever() ,
                    dlg.getKeepOnHost() ) ;
            return true ;
        }
        else
        {
            return false ;
        }

    }

}
