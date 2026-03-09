package com.semanticwww.yesir.io.rss ;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.Hashtable ;
import java.util.Vector ;

import com.semanticwww.yesir.gateway.DefaultGatewayImporter ;
import com.semanticwww.yesir.gateway.Meme ;
import com.semanticwww.yesir.io.rss.rssmodel.RSSChannel ;
import com.semanticwww.yesir.io.rss.rssmodel.RSSNewsItem ;
import com.semanticwww.yesir.io.rss.rssmodel.RSSXMLFactory ;
import com.semanticwww.yesir.io.rss.rssmodel.RSSXMLFactoryException ;
import com.semanticwww.yesir.utils.ParseDate ;

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
public class RSSImporter
    extends DefaultGatewayImporter
{
    String rssUrl ;
    public void jbInit( String name , String rssUrl )
    {
        setName( name ) ;
        this.rssUrl = rssUrl ;
    }

    /**
     * importData
     *
     * @param cutoff Date
     * @return Meme[]
     */
    public Meme[] importData( Date cutoff )
    {
        ArrayList newsList = new ArrayList() ;
        try
        {
            RSSXMLFactory rssFactory = new RSSXMLFactory( rssUrl ) ;
            RSSChannel rssChannel = rssFactory.getRssChannel() ;
            /** Display some news channel informations */
            Hashtable newsItems = rssChannel.getItems() ;
            Vector newsItemOrder = rssChannel.getNewsItemOrder() ;

            /** Check what infos are available for the news items */
            Vector newsItemInfos = rssChannel.getAvailableNewsItemInfos() ;

            String[] titles = new String[newsItemInfos.size()] ;
            for( int a = 0 ; a < titles.length ; a++ )
            {
                titles[a] = ( ( String ) newsItemInfos.get( a ) ) ;
            }

            /** Insert the newsheader */
            for( int a = 0 ; a < newsItemOrder.size() ; a++ )
            {
                Meme news = new Meme() ;

                RSSNewsItem rssNewsItem = ( RSSNewsItem ) newsItems.get(
                    newsItemOrder.get( a ) ) ;

                news.setSubject( rssNewsItem.getTitle() ) ; //title

                if( rssNewsItem.getPubDate() != null )
                {
                    Date date = ParseDate.getDate( rssNewsItem.getPubDate() ) ;
                    news.setDate( date ) ;
                }
                news.setAuthor( rssNewsItem.getAuthor() ) ;
                news.setCategory( rssNewsItem.getCategory() ) ;
                news.setPublisher( rssNewsItem.getPublisher() ) ;
                /** Try link or guid as URL */
                String url = null ;
                if( rssNewsItem.getLink() != null )
                {
                    url = rssNewsItem.getLink() ;
                }
                else if( rssNewsItem.getGuid() != null )
                {
                    url = rssNewsItem.getGuid() ;
                    /** If this newsItem has no description and the user chose to directOpen, open Link */
                }
                news.setURL( url ) ;
                news.setContent( rssNewsItem.getDescription() ) ;
                /** Enclosure */
                if( rssNewsItem.getEnclosure() != null )
                {
                    rssNewsItem.getEnclosure().toString() ;
                }

                news.setSource( rssNewsItem.getSource() ) ;
                if( news.after( cutoff ) )
                {
                    newsList.add( news ) ;
                }
            }
            //Debug.trace( ""+newsList.size() ) ;
        }
        catch( RSSXMLFactoryException ex )
        {
            ex.printStackTrace() ;
        }
        return( Meme[] ) newsList.toArray( new Meme[newsList.size()] ) ;
    }

    public static void main( String[] args )
    {
        RSSImporter importer = new RSSImporter() ;
        importer.jbInit( "BaoJie RSS" ,
                         "http://boole.cs.iastate.edu:9090/popeye/rss.rdf" ) ;
        importer.importData( null ) ;
    }

    /**
     * GUISetup
     */
    public boolean GUISetup()
    {
        RSSImporterSetupDlg dlg = new RSSImporterSetupDlg(
            null , "RSSImporter Setup" ,
            getName() , rssUrl ) ;
        dlg.show() ;

        if( dlg.getAction() == dlg.OK )
        {
            jbInit( dlg.getName() , dlg.getURL() ) ;
            return true ;
        }
        else
        {
            return false ;
        }

    }
}
