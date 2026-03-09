package com.semanticwww.yesir.io.mail ;

import java.io.BufferedOutputStream ;
import java.io.BufferedReader ;
import java.io.File ;
import java.io.FileOutputStream ;
import java.io.IOException ;
import java.io.InputStream ;
import java.io.InputStreamReader ;
import java.io.OutputStream ;
import java.util.Date ;
import java.util.Properties ;

import javax.mail.Address ;
import javax.mail.FetchProfile ;
import javax.mail.Flags ;
import javax.mail.Folder ;
import javax.mail.Message ;
import javax.mail.MessagingException ;
import javax.mail.Multipart ;
import javax.mail.Part ;
import javax.mail.Session ;
import javax.mail.Store ;
import javax.mail.URLName ;
import javax.mail.event.StoreEvent ;
import javax.mail.event.StoreListener ;
import javax.mail.internet.ContentType ;
import javax.mail.internet.MimeMessage ;
import javax.mail.internet.ParseException ;

/*
 * Show information about and contents of messages.
 *
 * @author Jie Bao ,based on work of John Mani, Bill Shannon
 */

public class MailParser
{
    public MailParser()
    {}

    public MailParser( String protocol , String host , String user ,
                       String password )
    {
        this.protocol = protocol ;
        this.host = host ;
        this.user = user ;
        this.password = password ;
    }

    public boolean concise = true ; // output concise mail content

    public String protocol ;
    public String host = null ;
    public String user = null ;
    public String password = null ;
    public String mbox = "INBOX" ;
    public String url = null ;
    public int port = -1 ;
    public boolean verbose = false ;
    public boolean debug = false ;
    public boolean showStructure = false ;
    public boolean showMessage = false ;
    public boolean showAlert = false ;
    public boolean saveAttachments = false ;
    static int attnum = 1 ;

    public void showMail( String args[] )
    {
        int msgnum = -1 ;
        int optind ;
        InputStream msgStream = System.in ;

        for( optind = 0 ; optind < args.length ; optind++ )
        {
            if( args[optind].equals( "-T" ) )
            {
                protocol = args[++optind] ;
            }
            else if( args[optind].equals( "-H" ) )
            {
                host = args[++optind] ;
            }
            else if( args[optind].equals( "-U" ) )
            {
                user = args[++optind] ;
            }
            else if( args[optind].equals( "-P" ) )
            {
                password = args[++optind] ;
            }
            else if( args[optind].equals( "-v" ) )
            {
                verbose = true ;
            }
            else if( args[optind].equals( "-D" ) )
            {
                debug = true ;
            }
            else if( args[optind].equals( "-f" ) )
            {
                mbox = args[++optind] ;
            }
            else if( args[optind].equals( "-L" ) )
            {
                url = args[++optind] ;
            }
            else if( args[optind].equals( "-p" ) )
            {
                port = Integer.parseInt( args[++optind] ) ;
            }
            else if( args[optind].equals( "-s" ) )
            {
                showStructure = true ;
            }
            else if( args[optind].equals( "-S" ) )
            {
                saveAttachments = true ;
            }
            else if( args[optind].equals( "-m" ) )
            {
                showMessage = true ;
            }
            else if( args[optind].equals( "-a" ) )
            {
                showAlert = true ;
            }
            else if( args[optind].equals( "--" ) )
            {
                optind++ ;
                break ;
            }
            else if( args[optind].startsWith( "-" ) )
            {
                System.out.println(
                    "Usage: MailParser [-L url] [-T protocol] [-H host] [-p port] [-U user]" ) ;
                System.out.println(
                    "\t[-P password] [-f mailbox] [msgnum] [-v] [-D] [-s] [-S] [-a]" ) ;
                System.out.println(
                    "or     MailParser -m [-v] [-D] [-s] [-S] < msg" ) ;
                System.exit( 1 ) ;
            }
            else
            {
                break ;
            }
        }

        try
        {
            if( optind < args.length )
            {
                msgnum = Integer.parseInt( args[optind] ) ;

                // Get a Properties object
            }
            Properties props = System.getProperties() ;

            // Get a Session object
            Session session = Session.getInstance( props , null ) ;
            session.setDebug( debug ) ;

            if( showMessage )
            {
                MimeMessage msg = new MimeMessage( session , msgStream ) ;
                String thismail = parsePart( msg , true ) ;
                System.out.println( thismail ) ;
                System.exit( 0 ) ;
            }

            // Get a Store object
            Store store = null ;
            if( url != null )
            {
                URLName urln = new URLName( url ) ;
                store = session.getStore( urln ) ;
                if( showAlert )
                {
                    store.addStoreListener( new StoreListener()
                    {
                        public void notification( StoreEvent e )
                        {
                            String s ;
                            if( e.getMessageType() == StoreEvent.ALERT )
                            {
                                s = "ALERT: " ;
                            }
                            else
                            {
                                s = "NOTICE: " ;
                            }
                            System.out.println( s + e.getMessage() ) ;
                        }
                    } ) ;
                }
                store.connect() ;
            }
            else
            {
                if( protocol != null )
                {
                    store = session.getStore( protocol ) ;
                }
                else
                {
                    store = session.getStore() ;

                    // Connect
                }
                if( host != null || user != null || password != null )
                {
                    store.connect( host , port , user , password ) ;
                }
                else
                {
                    store.connect() ;
                }
            }

            // Open the Folder

            Folder folder = store.getDefaultFolder() ;
            if( folder == null )
            {
                System.out.println( "No default folder" ) ;
                System.exit( 1 ) ;
            }

            folder = folder.getFolder( mbox ) ;
            if( folder == null )
            {
                System.out.println( "Invalid folder" ) ;
                System.exit( 1 ) ;
            }

            // try to open read/write and if that fails try read-only
            try
            {
                folder.open( Folder.READ_WRITE ) ;
            }
            catch( MessagingException ex )
            {
                folder.open( Folder.READ_ONLY ) ;
            }
            int totalMessages = folder.getMessageCount() ;

            if( totalMessages == 0 )
            {
                System.out.println( "Empty folder" ) ;
                folder.close( false ) ;
                store.close() ;
                System.exit( 1 ) ;
            }

            if( verbose )
            {
                int newMessages = folder.getNewMessageCount() ;
                System.out.println( "Total messages = " + totalMessages ) ;
                System.out.println( "New messages = " + newMessages ) ;
                System.out.println( "-------------------------------" ) ;
            }

            if( msgnum == -1 )
            {
                // Attributes & Flags for all messages ..
                Message[] msgs = folder.getMessages() ;

                // Use a suitable FetchProfile
                FetchProfile fp = new FetchProfile() ;
                fp.add( FetchProfile.Item.ENVELOPE ) ;
                fp.add( FetchProfile.Item.FLAGS ) ;
                fp.add( "X-Mailer" ) ;
                folder.fetch( msgs , fp ) ;

                for( int i = 0 ; i < msgs.length ; i++ )
                {
                    System.out.println( "--------------------------" ) ;
                    System.out.println( "MESSAGE #" + ( i + 1 ) + ":" ) ;
                    //                   dumpEnvelope( msgs[i] ) ;
                    String mails = parsePart( msgs[i] , true ) ;
                    System.out.print( mails ) ;
                }
            }
            else
            {
                System.out.println( "Getting message number: " + msgnum ) ;
                Message m = null ;

                try
                {
                    m = folder.getMessage( msgnum ) ;
                    String thismail = parsePart( m , true ) ;
                    System.out.print( thismail ) ;
                }
                catch( IndexOutOfBoundsException iex )
                {
                    System.out.println( "Message number out of range" ) ;
                }
            }

            folder.close( false ) ;
            store.close() ;
        }
        catch( Exception ex )
        {
            System.out.println( "Oops, got exception! " + ex.getMessage() ) ;
            ex.printStackTrace() ;
            System.exit( 1 ) ;
        }
        System.exit( 0 ) ;
    }

    public String parsePart( Part p , boolean withEnvelope )
        throws Exception
    {
        StringBuffer buffer = new StringBuffer() ;
        if( p instanceof Message && withEnvelope )
        {
            buffer.append( parseEnvelope( ( Message ) p ) ) ;

            /** Dump input stream ..
              InputStream is = p.getInputStream();
              // If "is" is not already buffered, wrap a BufferedInputStream
              // around it.
              if (!(is instanceof BufferedInputStream))
                is = new BufferedInputStream(is);
              int c;
              while ((c = is.read()) != -1)
                System.out.write(c);
             **/

        }
        String ct = p.getContentType() ;

        //part header
        if( !concise )
        {
            try
            {
                pr( "CONTENT-TYPE: " + ( new ContentType( ct ) ).toString() ,
                    buffer ) ;
            }
            catch( ParseException pex )
            {
                pr( "BAD CONTENT-TYPE: " + ct , buffer ) ;
            }
        }
        String filename = p.getFileName() ;
        if( filename != null )
        {
            pr( "FILENAME: " + filename , buffer ) ;

            /*
             * Using isMimeType to determine the content type avoids
             * fetching the actual content data until we need it.
             */
        }
        if( p.isMimeType( "text/plain" ) )
        {
            if( !showStructure && !saveAttachments )
            {
                pr( ( String ) p.getContent() , buffer ) ;
            }
        }
        else if( p.isMimeType( "multipart/*" ) )
        {
            Multipart mp = ( Multipart ) p.getContent() ;
            level++ ;
            int count = mp.getCount() ;
            for( int i = 0 ; i < count ; i++ )
            {
                buffer.append( parsePart( mp.getBodyPart( i ) , true ) ) ;
            }
            level-- ;
        }
        else if( p.isMimeType( "message/rfc822" ) )
        {
            pr( "This is a Nested Message" , buffer ) ;
            pr( "---------------------------" , buffer ) ;
            level++ ;
            buffer.append( parsePart( ( Part ) p.getContent() , true ) ) ;
            level-- ;
        }
        else
        {
            if( !showStructure && !saveAttachments )
            {
                /*
                 * If we actually want to see the data, and it's not a
                 * MIME type we know, fetch it and check its Java type.
                 */
                Object o = p.getContent() ;
                if( o instanceof String )
                {
                    pr( "This is a string" , buffer ) ;
                    pr( "---------------------------" , buffer ) ;
                    pr( ( String ) o , buffer ) ;
                }
                else if( o instanceof InputStream )
                {
                    pr( "This is just an input stream" , buffer ) ;
                    pr( "---------------------------" , buffer ) ;
                    InputStream is = ( InputStream ) o ;
                    int c ;
                    while( ( c = is.read() ) != -1 )
                    {
                        System.out.write( c ) ;
                    }
                }
                else
                {
                    pr( "This is an unknown type" , buffer ) ;
                    pr( "---------------------------" , buffer ) ;
                    pr( o.toString() , buffer ) ;
                }
            }
            else
            {
                // just a separator
                pr( "---------------------------" , buffer ) ;
            }
        }

        /*
         * If we're saving attachments, write out anything that
         * looks like an attachment into an appropriately named
         * file.  Don't overwrite existing files to prevent
         * mistakes.
         */
        if( saveAttachments && level != 0 && !p.isMimeType( "multipart/*" ) )
        {
            String disp = p.getDisposition() ;
            // many mailers don't include a Content-Disposition
            if( disp == null || disp.equalsIgnoreCase( Part.ATTACHMENT ) )
            {
                if( filename == null )
                {
                    filename = "Attachment" + attnum++ ;
                }
                pr( "Saving attachment to file " + filename , buffer ) ;
                try
                {
                    File f = new File( filename ) ;
                    if( f.exists() )
                    {

                        // XXX - could try a series of names
                        throw new IOException( "file exists" ) ;
                    }
                    OutputStream os =
                        new BufferedOutputStream( new FileOutputStream( f ) ) ;
                    InputStream is = p.getInputStream() ;
                    int c ;
                    while( ( c = is.read() ) != -1 )
                    {
                        os.write( c ) ;
                    }
                    os.close() ;
                }
                catch( IOException ex )
                {
                    pr( "Failed to save attachment: " + ex , buffer ) ;
                }
                pr( "---------------------------" , buffer ) ;
            }
        }
        return buffer.toString() ;
    }

    public String parseEnvelope( Message m )
        throws Exception
    {
        StringBuffer buffer = new StringBuffer() ;

        Address[] a ;
        // FROM
        if( ( a = m.getFrom() ) != null )
        {
            for( int j = 0 ; j < a.length ; j++ )
            {
                pr( "FROM: " + a[j].toString() , buffer ) ;
            }
        }

        // TO
        if( ( a = m.getRecipients( Message.RecipientType.TO ) ) != null )
        {
            for( int j = 0 ; j < a.length ; j++ )
            {
                pr( "TO: " + a[j].toString() , buffer ) ;
            }
        }

        // SUBJECT
        pr( "SUBJECT: " + m.getSubject() , buffer ) ;

        // DATE
        Date d = m.getSentDate() ;
        pr( "SendDate: " +
            ( d != null ? d.toString() : "UNKNOWN" ) , buffer ) ;

        // FLAGS
        Flags flags = m.getFlags() ;
        StringBuffer sb = new StringBuffer() ;
        Flags.Flag[] sf = flags.getSystemFlags() ; // get the system flags

        boolean first = true ;
        for( int i = 0 ; i < sf.length ; i++ )
        {
            String s ;
            Flags.Flag f = sf[i] ;
            if( f == Flags.Flag.ANSWERED )
            {
                s = "\\Answered" ;
            }
            else if( f == Flags.Flag.DELETED )
            {
                s = "\\Deleted" ;
            }
            else if( f == Flags.Flag.DRAFT )
            {
                s = "\\Draft" ;
            }
            else if( f == Flags.Flag.FLAGGED )
            {
                s = "\\Flagged" ;
            }
            else if( f == Flags.Flag.RECENT )
            {
                s = "\\Recent" ;
            }
            else if( f == Flags.Flag.SEEN )
            {
                s = "\\Seen" ;
            }
            else
            {
                continue ; // skip it
            }
            if( first )
            {
                first = false ;
            }
            else
            {
                sb.append( ' ' ) ;
            }
            sb.append( s ) ;
        }

        String[] uf = flags.getUserFlags() ; // get the user flag strings
        for( int i = 0 ; i < uf.length ; i++ )
        {
            if( first )
            {
                first = false ;
            }
            else
            {
                sb.append( ' ' ) ;
            }
            sb.append( uf[i] ) ;
        }
        pr( "FLAGS: " + sb.toString() , buffer ) ;

        // X-MAILER
        String[] hdrs = m.getHeader( "X-Mailer" ) ;
        if( hdrs != null )
        {
            pr( "X-Mailer: " + hdrs[0] , buffer ) ;
        }
        else
        {
            pr( "X-Mailer NOT available" , buffer ) ;
        }
        return buffer.toString() ;

    }

    private static String indentStr =
        "                                               " ;
    private static int level = 0 ;

    /**
     * Print a, possibly indented, string.
     */
    private void pr( String s , StringBuffer buffer )
    {
        if( showStructure )
        {
//            System.out.print( indentStr.substring( 0 , level * 2 ) ) ;
            buffer.append( indentStr.substring( 0 , level * 2 ) ) ;
        }
//        System.out.println( s ) ;
        buffer.append( s + "\n" ) ;
    }

    /**
     * convert text message part to string
     * only text/plain and text/html are treated
     * @param messagePart
     * @return
     */
    private String content2String( Part messagePart )
    {
        String mailContent = "" ;
        try
        {
            Object content = messagePart.getContent() ;

            // summary of the content
            String contentType = messagePart.getContentType() ;
            mailContent = mailContent + "\n" + "CONTENT:" + contentType ;

            // full text - ignore non text
            if( contentType.startsWith( "text/plain" ) ||
                contentType.startsWith( "text/html" ) )
            {

                InputStream is = messagePart.getInputStream() ;
                BufferedReader reader = new BufferedReader( new
                    InputStreamReader( is ) ) ;
                String thisLine = reader.readLine() ;
                while( thisLine != null )
                {
                    mailContent = mailContent + "\n" + ( thisLine ) ;
                    thisLine = reader.readLine() ;
                }
            }
        }
        catch( IOException ex )
        {
        }
        catch( MessagingException ex )
        {
        }
        return mailContent ;
    }

}
