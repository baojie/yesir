package com.semanticwww.yesir.io.news ;

import java.io.BufferedReader ;
import java.io.IOException ;
import java.io.PrintWriter ;
import java.io.Reader ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.StringTokenizer ;

import com.semanticwww.yesir.gateway.DefaultGatewayImporter ;
import com.semanticwww.yesir.gateway.Meme ;
import com.semanticwww.yesir.utils.ParseDate ;
import org.apache.commons.net.io.DotTerminatedMessageReader ;
import org.apache.commons.net.nntp.NNTPClient ;
import org.apache.commons.net.nntp.NewsgroupInfo ;
import com.semanticwww.yesir.utils.PrintCommandListener;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0 2003-12-08
 */
public class NewsImporter
    extends DefaultGatewayImporter
{
    NNTPClient client ;

    String nntpServer = "news.iastate.edu" ;
    String groupName = "isu.market" ;
    int backNumber = 20 ;

    public void jbInit( String name , String nntpServer , String groupName ,
                        int backNumberVal )
    {
        setName( name ) ;
        this.nntpServer = nntpServer ;
        this.groupName = groupName ;
        this.backNumber = backNumberVal ;

        client = new NNTPClient() ;
        client.addProtocolCommandListener( new PrintCommandListener(
            new PrintWriter( System.out ) ) ) ;
    }

    /**
     * importData
     *
     * @param cutoff Date
     * @return Meme[]
     */
    public Meme[] importData( Date cutoff )
    {
        Meme[] articles = null ;
        try
        {
            client.connect( nntpServer ) ;
            /*
             // AUTHINFO USER/AUTHINFO PASS
             boolean success = client.authenticate( user , password ) ;
             if( success )
             {
                  System.out.println( "Authentication succeeded" ) ;
             }
             else
             {
                   System.out.println( "Authentication failed, error =" +
                                                client.getReplyString() ) ;
             }
             */
            // XOVER
            NewsgroupInfo testGroup = new NewsgroupInfo() ;
            client.selectNewsgroup( groupName , testGroup ) ;
            int highArticleNumber = testGroup.getLastArticle() ;
            int lowArticleNumber = highArticleNumber - backNumber ; // testGroup.getFirstArticle();

            articles = getArticle( lowArticleNumber , highArticleNumber ,
                                   cutoff ) ;
//            System.out.println("articles "+articles.length);
            /*
                        for( int i = 0 ; i < articles.length ; ++i )
                        {

                            System.out.println( i + ": " + articles[i] ) ;

                        }*/
            /**
                         // LIST ACTIVE
             NewsgroupInfo[] fanGroups = client.listNewsgroups( "alt.fan.*" ) ;
                         for( int i = 0 ; i < fanGroups.length ; ++i )
                         {
                 System.out.println( fanGroups[i].getNewsgroup() ) ;
                         }
             */

        }
        catch( IOException e )
        {
            e.printStackTrace() ;
        }
        return articles ;

    }

    private Meme[] getArticle( int lowArticleNumber ,
                               int highArticleNumber ,
                               Date cutoff )
        throws IOException
    {
        ArrayList articles = new ArrayList() ;
        Reader reader = null ;

        reader = ( DotTerminatedMessageReader )
                 client.retrieveArticleInfo( lowArticleNumber ,
                                             highArticleNumber ) ;

        if( reader != null )
        {
            String theInfo = readerToString( reader ) ;

            // Each line is the header for a post
            StringTokenizer st = new StringTokenizer( theInfo , "\n" ) ;

            // Extract the article information
            // Mandatory format (from NNTP RFC 2980) is :
            // Subject\tAuthor\tDate\tID\tReference(s)\tByte Count\tLine Count
            while( st.hasMoreTokens() )
            {
                String header = st.nextToken() ;
                //Debug.trace(header);
                StringTokenizer stt = new StringTokenizer( header ,
                    "\t" ) ;

                Meme article = new Meme() ;

                try
                {
                    String ArticleNumber = stt.nextToken() ;
                    article.setGuid( ArticleNumber ) ;
                    //Debug.trace( ArticleNumber ) ;
                    String Subject = stt.nextToken() ;
                    //Debug.trace( Subject ) ;
                    article.setSubject( Subject ) ;
                    //Debug.trace( "Subject: "+article.getSubject() ) ;
                    String From = ( stt.nextToken() ) ;
                    article.setAuthor( From ) ;
                    article.setTo( new String[]
                                   {groupName} ) ;
                    String Date = ( stt.nextToken() ) ;
                    article.setDate( ParseDate.getDate( Date ) ) ;
                    String ArticleId = ( stt.nextToken() ) ;
                    //Debug.trace( ArticleId ) ;
                    article.setURL( ArticleId ) ;
                    String References = stt.nextToken() ;
                    //Debug.trace( References ) ;

                    article.setContent( readerToString( client.
                        retrieveArticleBody( ArticleId ) ) ) ;
                    //Debug.trace( "Content:\n "+article.getContent() ) ;
                    if( article.after( cutoff ) )
                    {
                        articles.add( article ) ;
                    }
                }
                catch( Exception ex )
                {
                    ex.printStackTrace() ;
                }

            }
        }
        else
        {
            return null ;
        }

        return( Meme[] ) articles.toArray( new Meme[articles.size()] ) ;
    }

    private String readerToString( Reader reader )
    {
        String temp ;
        StringBuffer sb = null ;
        BufferedReader bufReader = new BufferedReader( reader ) ;

        sb = new StringBuffer() ;
        try
        {
            temp = bufReader.readLine() ;
            while( temp != null )
            {
                sb.append( temp ) ;
                sb.append( "\n" ) ;
                temp = bufReader.readLine() ;
            }
        }
        catch( IOException e )
        {
            e.printStackTrace() ;
        }

        return sb.toString() ;
    }

    /**
     * GUISetup
     */
    public boolean GUISetup()
    {
        NewsImporterSetupDlg dlg = new NewsImporterSetupDlg(
            null , "NewsImporter Setup" ,
            getName() ,
            nntpServer ,
            groupName ,
            backNumber ) ;
        dlg.show() ;

        if( dlg.getAction() == dlg.OK )
        {
            jbInit( dlg.getName() , dlg.getNNTPServer() , dlg.getGroupName() ,
                    dlg.getBackNumber() ) ;
            return true ;
        }
        else
        {
            return false ;
        }

    }

    // for test purpose
    public static void main( String[] args )
    {
        NewsImporter importer = new NewsImporter() ;
        importer.jbInit( "isu.market" , "news.iastate.edu" , "isu.market" , 20 ) ;
        importer.importData( null ) ;
    }

    public int getBackNumber()
    {
        return backNumber ;
    }

    public void setBackNumber( int backNumber )
    {
        this.backNumber = backNumber ;
    }

    public String getGroupName()
    {
        return groupName ;
    }

    public String getNntpServer()
    {
        return nntpServer ;
    }

}


