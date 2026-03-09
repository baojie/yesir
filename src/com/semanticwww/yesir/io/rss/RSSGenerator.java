package com.semanticwww.yesir.io.rss;

import com.semanticwww.yesir.gateway.GatewayException ;
import com.semanticwww.yesir.gateway.Meme ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

public class RSSGenerator
{
//  private WikiEngine m_engine ;

    private String m_channelDescription = "Yesir Information Agent RSS" ;
    private String m_channelLanguage = "en-us" ;

    Meme memeList[] ;

    /**
     *  Initialize the RSS generator.
     */
    public RSSGenerator( Meme meme[] )
        throws GatewayException
    {
        this.memeList = meme ;
    }

    /**
     *  Does the required formatting and entity replacement for XML.
     */
    private static String format( String s )
    {
        if( s == null )
        {
            return "" ;
        }
        s = s.replaceAll( "&" , "&amp;" ) ;
        s = s.replaceAll( "<" , "&lt;" ) ;
        s = s.replaceAll( "]]>" , "]]&gt;" ) ;

        return s.trim() ;
    }

    /**
     *  Generates the RSS resource.  You probably want to output this
     *  result into a file or something, or serve as output from a servlet.
     */
    public String generate()
    {
        StringBuffer result = new StringBuffer() ;

        //
        //  Preamble
        //
        result.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" ) ;

        result.append(
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
            "   xmlns=\"http://purl.org/rss/1.0/\"\n" +
            "   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"" +
            "   >\n" ) ;

        //
        //  Channel.
        //

        result.append( " <channel rdf:about=\"" +
                       "http://boole.cs.iastate.edu:9090/popeye/Wiki.jsp?page=Programming.Java.Yesir" +
                       "\">\n" ) ;

        result.append( "  <title>" ).append( "Yesir" ).
            append( "</title>\n" ) ;

        // FIXME: This might fail in case the base url is not defined.
        result.append( "  <link>" ).append(
            "http://boole.cs.iastate.edu:9090/popeye/Wiki.jsp?page=Programming.Java.Yesir" ).
            append(
            "</link>\n" ) ;

        result.append( "  <description>" ) ;
        result.append( format( m_channelDescription ) ) ;
        result.append( "</description>\n" ) ;

        result.append( "  <language>" ) ;
        result.append( m_channelLanguage ) ;
        result.append( "</language>\n" ) ;

        //  We need two lists, which is why we gotta make a separate list if
        //  we want to do just a single pass.
        StringBuffer itemBuffer = new StringBuffer() ;

        result.append( "  <items>\n   <rdf:Seq>\n" ) ;

        for( int i = 0 ; i < memeList.length ; i++ )
        {
            Meme m = memeList[i] ;

            result.append( "    <rdf:li rdf:resource=\"" + format( m.getURL() ) +
                           "\" />\n" ) ;

            itemBuffer.append( " <item rdf:about=\"" + format( m.getURL() ) +
                               "\">\n" ) ;

            itemBuffer.append( "  <title>" ) ;
            itemBuffer.append( m.getSubject() ) ;
            itemBuffer.append( "</title>\n" ) ;

            itemBuffer.append( "  <link>" ) ;
            itemBuffer.append( format( m.getURL() ) ) ;
            itemBuffer.append( "</link>\n" ) ;

            itemBuffer.append( "  <description>" ) ;

            itemBuffer.append( format( m.getContent() ) ) ;

            itemBuffer.append( "</description>\n" ) ;

            //
            //  Modification date.
            //

            itemBuffer.append( "  <dc:date>" ) ;
            itemBuffer.append( m.getDate() ) ;
            itemBuffer.append( "</dc:date>\n" ) ;

            //
            //  Author.
            //
            if( m.getAuthor() != null )
            {
                itemBuffer.append( "  <dc:contributor>\n" ) ;
                itemBuffer.append( "   <rdf:Description" ) ;
                itemBuffer.append( ">\n" ) ;
                itemBuffer.append( "    <rdf:value>" + m.getAuthor()[0] +
                                   "</rdf:value>\n" ) ;
                itemBuffer.append( "   </rdf:Description>\n" ) ;
                itemBuffer.append( "  </dc:contributor>\n" ) ;
            }
            //  Close up.
            itemBuffer.append( " </item>\n" ) ;
        }

        result.append( "   </rdf:Seq>\n  </items>\n" ) ;
        result.append( " </channel>\n" ) ;

        result.append( itemBuffer.toString() ) ;

        //
        //  Close All
        //

        result.append( "</rdf:RDF>" ) ;

        return result.toString() ;
    }
}
