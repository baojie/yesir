package com.semanticwww.yesir.io.bbs ;

import java.io.BufferedReader ;
import java.io.IOException ;
import java.io.InputStream ;
import java.io.InputStreamReader ;
import java.io.OutputStreamWriter ;
import java.io.UnsupportedEncodingException ;
import java.net.URL ;
import java.net.URLConnection ;
import java.net.URLEncoder ;
import java.text.DateFormat ;
import java.util.Vector ;

import com.semanticwww.yesir.gateway.DefaultGatewayExporter ;
import com.semanticwww.yesir.gateway.Meme ;
import com.semanticwww.yesir.utils.string.ParserUtils ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0 - 2003-11-12
 */
abstract public class BBSExporter
    extends DefaultGatewayExporter
{
    static ParserUtils parser ;

    public void jbInit( String name )
    {
        setName( name ) ;
    }

    public static String read( URL url )
    {
        try
        {
            return read( url.openStream() ) ;
        }
        catch( IOException ex )
        {
            return null ;
        }
    }

    /**
     * Reads an input stream and returns its contents.
     * @param input the input stream to be read.
     * @return the contents of the stream.
     * @throws IOException whenever the operation cannot be accomplished.
     */
    private static String read( InputStream input )
        throws IOException
    {
        StringBuffer t_sbResult = new StringBuffer() ;

        if( input != null )
        {
            /*
             * Instantiating an InputStreamReader object to read the contents.
             */
            InputStreamReader t_isrReader = new InputStreamReader( input ) ;

            /*
             * It's faster to use BufferedReader class.
             */
            BufferedReader t_brBufferedReader =
                new BufferedReader( t_isrReader ) ;

            String t_strLine = t_brBufferedReader.readLine() ;

            while( t_strLine != null )
            {
                t_sbResult.append( t_strLine + "\n" ) ;

                t_strLine = t_brBufferedReader.readLine() ;
            }
        }

        /*
         * End of the method.
         */
        return t_sbResult.toString() ;
    }

    protected static String doPost( String urlString , CGIFields[] parameters ,
                                    String enc )
    {
        String data = "" ;
        // Construct post
        for( int i = 0 ; i < parameters.length ; i++ )
        {
            try
            {
                data += URLEncoder.encode( parameters[i].field , enc ) +
                    "=" + URLEncoder.encode( parameters[i].value , enc ) ;
                if( i < parameters.length - 1 )
                {
                    data += "&" ;
                }
            }
            catch( UnsupportedEncodingException ex )
            {
                ex.printStackTrace( System.err ) ;
                return null ;
            }
        }
        // Send data
        StringBuffer buffer = new StringBuffer() ;
        try
        {
            URL url = new URL( urlString ) ;
            URLConnection conn = url.openConnection() ;
            conn.setDoOutput( true ) ;
            OutputStreamWriter wr = new OutputStreamWriter( conn.
                getOutputStream() ) ;
            wr.write( data ) ;
            wr.flush() ;

            // Get the response
            BufferedReader rd = new BufferedReader( new InputStreamReader(
                conn.
                getInputStream() ) ) ;
            String line ;
            int byteRead = 0 ;
            while( ( line = rd.readLine() ) != null )
            {
                // Process line...
                buffer.append( line ) ;
                byteRead += line.length() ;
            }
            wr.close() ;
            rd.close() ;
        }
        catch( IOException ex1 )
        {
            ex1.printStackTrace( System.err ) ;
            return null ;
        }
        String result = buffer.toString() ;
        return result ;

    }

    public static Vector findCGI( String htmlSource )
    {
        // find realative path
        return parser.findAll( "(?i)(?s)href=(.*?)(cgi|pl)(.*?)" , htmlSource ) ;
    }

    /**
     * find all input field in the source
     *
     * @param htmlSource String
     * @return list of <input .... >
     */
    public static Vector findCGIParameters( String htmlSource )
    {
        return parser.findAll( "(?i)(?s)<input.*?>" , htmlSource ) ;
    }

    /**
     *
     * @param inputString   <input .... >
     * @return : name and default value
     */
    public static CGIFields findFieldAndValue( String inputString )
    {
        String value = parser.findFirst( "(?i)(?s)(\\s*)value(\\s*)=(.*?)\\s" ,
                                         inputString ) ;
        if( value != null )
        {
            value = parser.replaceAll( "\"" , "" , value ) ;
            value = parser.replaceAll( "'" , "" , value ) ;
            value = parser.replaceAll( "value\\s*=" , "" , value ) ;
            value = value.trim() ;
        }
        String name = parser.findFirst( "(?i)(?s)(\\s*)name(\\s*)=(.*?)\\s" ,
                                        inputString ) ;
        if( name != null )
        {
            name = parser.replaceAll( "\"" , "" , name ) ;
            name = parser.replaceAll( "'" , "" , name ) ;
            name = parser.replaceAll( "name\\s*=" , "" , name ) ;
            name = name.trim() ;

        }

        return new CGIFields( name , value ) ;
    }

    /**
     * exportData
     *
     * @param message Meme
     */
    public void exportData( Meme message )
    {
        // construct the post title
        String dateStr = "" ;
        if( message.getDate() != null )
        {
            dateStr = DateFormat.getDateInstance().format( message.getDate() ) ;

        }
        String subjectStr = "" ;
        if( message.getSubject() != null )
        {
            subjectStr = message.getSubject() ;

        }
        String authorStr = "" ;
        if( message.getAuthor() != null )
        {
            authorStr = message.getAuthor()[0] ;

        }

        String title = subjectStr + ", " + dateStr + ", " + authorStr ;
        doPost( title , message.toString() ) ;
    }

    abstract String doPost( String subject , String toPost ) ;

    // for test purpose
    public static void main( String[] args )
    {
        final CGIFields parameters[] = new CGIFields[11] ;

        int i = 0 ;
        parameters[i++] = new CGIFields( "membername" , "Test" ) ; //用户名
        parameters[i++] = new CGIFields( "password" , "12345678" ) ; //密码
        parameters[i++] = new CGIFields( "intopictitle" , "Test2" ) ; // 主题标题
        parameters[i++] = new CGIFields( "uselbcode" , "yes" ) ; // 使用 LB5000 标签？
        parameters[i++] = new CGIFields( "inshowsignature" , "yes" ) ; //显示签名？
        parameters[i++] = new CGIFields( "notify" , "no" ) ; // 有回复时使用邮件通知您？
        parameters[i++] = new CGIFields( "inshowemoticons" , "no" ) ; //您是否希望<b>使用</b>表情字符转换？
        parameters[i++] = new CGIFields( "inpost" ,
                                         "这个帖子是用灌水机发的啦^_^" ) ; //内容
        parameters[i++] = new CGIFields( "addme" , "" ) ; //附件
        parameters[i++] = new CGIFields( "action" , "addnew" ) ; // action
        parameters[i++] = new CGIFields( "forum" , "13" ) ; // forum number

        String result = doPost( "http://boole.cs.iastate.edu/isubbs/post.cgi" ,
                                parameters , "GB2312" ) ;
    }

}
