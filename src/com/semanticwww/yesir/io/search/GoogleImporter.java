package com.semanticwww.yesir.io.search ;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.GregorianCalendar ;

import com.google.soap.search.GoogleSearch ;
import com.google.soap.search.GoogleSearchFault ;
import com.google.soap.search.GoogleSearchResult ;
import com.google.soap.search.GoogleSearchResultElement ;
import com.semanticwww.yesir.gateway.DefaultGatewayImporter ;
import com.semanticwww.yesir.gateway.Meme ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */

public class GoogleImporter
    extends DefaultGatewayImporter
{
    static String clientKey = "zBBOYPNQFHLV0NxJhRejnr3yFJD76oDW" ;
    String keyWord = new String() ;

    public void jbInit( String name , String clientKey , String keyword )
    {
        setName( name ) ;
        if( clientKey != null )
        {
            this.clientKey = clientKey ;
        }
        this.keyWord = keyword ;
    }

    public boolean GUISetup()
    {
        GoogleImporterSetupDlg dlg = new GoogleImporterSetupDlg(
            null , "GoogleImporter Setup" ,
            getName() ,
            clientKey ,
            keyWord ) ;
        dlg.show() ;

        if( dlg.getAction() == dlg.OK )
        {
            jbInit( dlg.getName() , dlg.getClientKey() , dlg.getKeyword() ) ;
            return true ;
        }
        else
        {
            return false ;
        }

    }

    public static void main( String[] args )
    {
        GoogleImporter importer = new GoogleImporter() ;
        importer.jbInit( "Google" , null , "baojie" ) ;
        //importer.importData1( null ) ;

        Meme meme[] = importer.importData( null ) ;
        for( int i = 0 ; i < meme.length ; i++ )
        {
            System.out.println( meme[i] ) ;
        }
    }

    /**
     * importData
     *
     * @param cutoff Date
     * @return Meme[]
     */
    public Meme[] importData( Date cutoff )
    {
        // Create a Google Search object, set authorization key
        GoogleSearch s = new GoogleSearch() ;
        s.setKey( clientKey ) ;
        GoogleSearchResult r = null ;

        try
        {
            s.setQueryString( this.keyWord ) ;
            r = s.doSearch() ;
            //System.out.println( r.toString() ) ;

        }
        catch( GoogleSearchFault f )
        {
            System.out.println( "The call to the Google Web APIs failed:" ) ;
            System.out.println( f.toString() ) ;
            f.printStackTrace() ;
        }

        GoogleSearchResultElement rs[] = r.getResultElements() ;
        //System.out.println( "results: " + rs.length ) ;

        ArrayList articles = new ArrayList() ;
        for( int i = 0 ; i < rs.length ; i++ )
        {
            Meme result = new Meme() ;

            //rs[i].getCachedSize() ;
            //rs[i].getDirectoryTitle() ;
            //rs[i].getHostName() ;
            //rs[i].getRelatedInformationPresent() ;
            result.setContent( rs[i].getSnippet() ) ;
            result.setSummary( rs[i].getSummary() ) ;
            result.setSubject( rs[i].getTitle() ) ;
            result.setURL( rs[i].getURL() ) ;
            result.setAuthor( "www.google.com" ) ;
            result.setCategory( rs[i].getDirectoryCategory().toString() ) ;
            result.setDate( new GregorianCalendar().getTime() ) ;

            if( result.after( cutoff ) )
            {
                articles.add( result ) ;
            }
        }
        return( Meme[] ) articles.toArray( new Meme[articles.size()] ) ;
    }

}
