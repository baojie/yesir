package com.semanticwww.yesir.gateway ;

import java.io.ByteArrayOutputStream ;
import java.io.IOException ;

import javax.mail.BodyPart ;
import javax.mail.MessagingException ;
import javax.mail.Multipart ;
import javax.mail.Part ;

/**
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */
public class Attachment
{
    String type ;
    String disposition ;
    String fileName ;
    byte content[] ;

    private String filter( String obj )
    {
        if( obj == null )
        {
            return "None" ;
        }
        else
        {
            return obj.toString() ;
        }
    }

    public String toString()
    {
        int len = 0 ;
        if( content != null )
        {
            len = content.length ;
        }

        return "Type: " + filter( type ) +
            ", Disposition: " + filter( disposition ) +
            ", FileName: " + filter( fileName ) +
            ", Size: " + len ;
    }

    public static Attachment[] getAttachedFile( Part messagePart )
        throws IOException , MessagingException
    {
        Attachment obj[] = null ;

        Object content = messagePart.getContent() ;
        try
        {
            if( content instanceof Multipart )
            {
                //这种情况下的邮件都是用multi模式发送的,
                // 这种模式包括有附件的邮件和用html表示content的邮件
                //如果是MULTI模式发送的，BodyPart(0).getContent()肯定就是content

                //getCount()可以得到content中bodyPart的个数，content就是第一个
                //bodyPart，其它的附件按照顺序类推。
                Multipart contentTmp = ( Multipart ) content ;
                //System.out.println(contentTmp.getCount() + " parts");

                obj = new Attachment[contentTmp.getCount() - 1] ;
                for( int i = 1 ; i < contentTmp.getCount() ; i++ )
                {
                    obj[i - 1] = saveAttacheFile( contentTmp.getBodyPart( i ) ) ;
                }
            }

            else
            {
                //这种情况中邮件是纯文本形式，并且没有附件
//                obj = new Attachment[1] ;
//                obj[0] = saveAttacheFile( contentTmp.getBodyPart( 0 ) ) ;
            }
        }
        catch( Exception ie )
        {
            System.out.println( "exception====" + ie.getMessage() ) ;
        }

        return obj ;
    }

    private static Attachment saveAttacheFile( BodyPart part )
    {
        try
        {
            Attachment att = new Attachment() ;

            att.type = part.getContentType() ;
            att.disposition = part.getDisposition() ;
            att.fileName = part.getFileName() ;

            ByteArrayOutputStream out = new ByteArrayOutputStream() ;
            part.writeTo( out ) ;
            /*          // other choice
                        InputStream is = part.getInputStream() ;
                        BufferedReader reader = new BufferedReader( new
                            InputStreamReader( is ) ) ;
                        byte[] buf = new byte[1024] ;
                        int len ;
                        while( ( len = is.read( buf ) ) > 0 )
                        {
                            out.write( buf , 0 , len ) ;
                        }
             */
            out.close() ;
            att.content = out.toByteArray() ;
            return att ;
        }
        catch( IOException ex )
        {
            ex.printStackTrace() ;
        }
        catch( MessagingException ex )
        {
            ex.printStackTrace() ;
        }
        return null ;
    }

    public byte[] getContent()
    {
        return content ;
    }

    public String getDisposition()
    {
        return disposition ;
    }

    public String getFileName()
    {
        return fileName ;
    }

    public String getType()
    {
        return type ;
    }

}
