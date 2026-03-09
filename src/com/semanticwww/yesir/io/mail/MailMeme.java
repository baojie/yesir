package com.semanticwww.yesir.io.mail ;

import java.io.File ;
import java.io.FilenameFilter ;
import java.io.IOException ;
import java.util.Date ;
import java.util.Vector ;

import javax.activation.FileDataSource ;
import javax.mail.Address ;
import javax.mail.Message ;
import javax.mail.MessagingException ;
import javax.mail.Multipart ;
import javax.mail.Part ;
import javax.mail.Session ;
import javax.mail.internet.InternetAddress ;
import javax.mail.internet.MimeMessage ;

import com.semanticwww.yesir.gateway.Attachment ;
import com.semanticwww.yesir.gateway.Meme ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author  Jie Bao
 * @version 1.0
 */

public class MailMeme
    extends Meme
{
    public MailMeme()
    {
        super() ;
    }

    /**
     * Parse emails after cutoff date in a directory and return a vector of all emails
     * in Mail format
     *
     * @param path
     * @return
     */
    public static Vector fromDirectory( String path , Date cutoff )
    {
        try
        {
            Vector mails = new Vector() ;
            File dir = new File( path ) ;

            // It is also possible to filter the list of returned files.
            // This example does not return any files that start with `.'.
            FilenameFilter filter = new FilenameFilter()
            {
                public boolean accept( File dir , String name )
                {
                    return name.endsWith( ".eml" ) ;
                }
            } ;

            File[] files = dir.listFiles( filter ) ;
            for( int i = 0 ; i < files.length ; i++ )
            {
                Meme m = fromFile( files[i] ) ;
                if( m.after( cutoff ) )
                {
                    mails.add( m ) ;
                }
            }

            return mails ;
        }
        catch( Exception ex )
        {
            return null ;
        }
    }

    /**
     * read a stored MIME email
     *
     * @param file
     * @return
     *
     * @author Jie Bao
     * @version 2003-11-18
     */
    public static Meme fromFile( File file )
    {
        class MyMime
            extends MimeMessage
        {
            public MyMime( Session session )
            {
                super( session ) ;
            }

            public void parse( java.io.InputStream is )
                throws MessagingException
            {
                super.parse( is ) ;
            }
        }

        try
        {
            FileDataSource fds = new FileDataSource( file ) ;
            Session session = null ;
            MyMime msg = new MyMime( session ) ;
            msg.parse( fds.getInputStream() ) ;
            Meme newMail = fromMessage( msg ) ;
            newMail.setURL( file.getAbsolutePath() ) ;

            return newMail ;
        }
        catch( MessagingException ex )
        {
            ex.printStackTrace() ;
        }
        catch( IOException ex )
        {
            ex.printStackTrace() ;
        }
        return null ;
    }

    /**
     * convert  Email Address to string
     *
     * @param add
     * @param emailOnly
     * @return
     */
    private static String getEmailAddress( Address add , boolean emailOnly )
    {
        String email = ( ( InternetAddress ) add ).getAddress() ;
        email = ( email == null ) ? "" : email ;

        if( emailOnly )
        {
            return email ;
        }
        else
        {
            String name = ( ( InternetAddress ) add ).getPersonal() ;
            name = ( name == null ) ? "" : name ;
            return name + "<" + email + ">" ;
        }
    }

    /**
     * convert a group of email addresses to string
     *
     * @param add
     * @param emailOnly
     * @return
     */
    private static String[] getEmailAddress( Address add[] , boolean emailOnly )
    {
        if( add == null )
        {
            return null ;
        }

        String email[] = new String[add.length] ;
        for( int i = 0 ; i < add.length ; i++ )
        {
            email[i] = getEmailAddress( add[i] , emailOnly ) ;
        }
        return email ;
    }

    /**
     * parse an email and store it in structure Mail
     *
     * @param message
     * @return
     */
    public static Meme fromMessage( Message message )
    {
        Meme newMail = new Meme() ;
        try
        {
            // get from information
            newMail.setAuthor( getEmailAddress( message.getFrom() , false ) ) ;

            // get "to" information
            Address to[] = message.getRecipients( Message.RecipientType.TO ) ;
            newMail.setTo( getEmailAddress( to , false ) ) ;

            // get "cc" information
            Address cc[] = message.getRecipients( Message.RecipientType.CC ) ;
            newMail.setCc( getEmailAddress( cc , false ) ) ;

            //  , Message.RecipientType.CC, Message.RecipientType.BCC
            // get "bcc" information
            Address bcc[] = message.getRecipients( Message.RecipientType.BCC ) ;
            newMail.setBcc( getEmailAddress( bcc , false ) ) ;

            // get subject information
            String subject = message.getSubject() ;
            newMail.setSubject( subject ) ;

            //get date information
            newMail.setDate( message.getSentDate() ) ;
            // get content information

            Part messagePart = message ;
            Object content = messagePart.getContent() ;

            newMail.setContent( new MailParser().parsePart( message , false ) ) ;

            if( content instanceof Multipart )
            {
//              System.out.println("Multipart");

                //Part firstPart = ( ( Multipart ) content ).getBodyPart( 0 ) ;
                //newMail.content = firstPart.getContent().toString() ;
                newMail.setAttachment( Attachment.getAttachedFile(
                    messagePart ) ) ;
            }
            else
            {
//                System.out.println("Singlepart");
                //newMail.content = content2String( messagePart ) ;
            }

        }
        catch( Exception ex )
        {
            System.err.println( ex.getMessage() ) ;
            ex.printStackTrace() ;
        }
        return newMail ;
    }

    /**
     * to test if the meeage is sent to specified reciever
     *
     * @param restrictedReciever
     * @param msg
     * @return
     *
     * @author Jie Bao
     * @version 2003-11-18
     */
    public static boolean IsSendTo( String restrictedReciever , Message msg )
    {
        try
        {
            // if no restrictedReciever is given, we assume there is no
            // such limitation
            if( restrictedReciever == null )
            {
                return true ;
            }
            if( restrictedReciever.length() == 0 )
            {
                return true ;
            }

            // check all recipients
            Address add[] = msg.getAllRecipients() ;
            if( add == null )
            {
                return false ;
            }
            for( int i = 0 ; i < add.length ; i++ )
            {
                String email = ( ( InternetAddress ) add[i] ).getAddress() ;
                if( restrictedReciever.equalsIgnoreCase( email ) )
                {
                    return true ;
                }
            }
            return false ;
        }
        catch( MessagingException ex )
        {
            ex.printStackTrace() ;
            return false ;
        }
    }

}
