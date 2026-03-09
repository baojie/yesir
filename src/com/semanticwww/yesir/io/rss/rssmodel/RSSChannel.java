/*   ********************************************************************   **
 **   Copyright notice                                                       **
 **                                                                          **
 **   (c) 2003 RSSOwl Development Team					                              **
 **   http://rssowl.sourceforge.net/   			                                **
 **                                                                          **
 **   All rights reserved                                                    **
 **                                                                          **
 **   This script is part of the RSSOwl project. The RSSOwl						      **
 **   project is free software; you can redistribute it and/or modify        **
 **   it under the terms of the GNU General Public License as published by   **
 **   the Free Software Foundation; either version 2 of the License, or      **
 **   (at your option) any later version.                                    **
 **                                                                          **
 **   The GNU General Public License can be found at                         **
 **   http://www.gnu.org/copyleft/gpl.html.                                  **
 **   A copy is found in the textfile GPL.txt and important notices to the   **
 **   license from the team is found in the textfile LICENSE.txt distributed **
 **   in these package.                                                      **
 **                                                                          **
 **   This copyright notice MUST APPEAR in all copies of the file!           **
 **   ********************************************************************   */

package com.semanticwww.yesir.io.rss.rssmodel ;

import java.util.Hashtable ;
import java.util.Vector ;

import org.jdom.Document ;

/**
 * Model for a RSSChannel

 * @author  <a href="mailto:bpasero@users.sourceforge.net">Benjamin Pasero</a>
 * @version 0.63b
 */
public class RSSChannel
{

    /** Must Attributes */
    private String title ;
    private String description ;
    private String link ;
    private String language ;

    /** May Attributes */
    private RSSChannelImage image ;
    private String copyright ;
    private String pubdate ; //Date when channel was published.
    private String webmaster ; //The email address of the webmaster for the site, the person to contact if there are technical problems with the channel
    private String managingEditor ; //The email address of the managing editor of the site, the person to contact for editorial inquiries
    private String lastBuildDate ; //The last time the channel was modified.
    private String category ;
    private String generator ; //A string indicating the program used to generate the channel.
    private String rating ; // Should not use?
    private String creator ;
    private String publisher ;
    private String docs ; //This tag should contain a URL that references a description of the channel
    private Vector skipHours ; //A list of <hour>s indicating the hours in the day, GMT, when the channel is unlikely to be updated.
    private Vector skipDays ; //A list of <day>s of the week, in English, indicating the days of the week when your channel will not be updated.
    private String ttl ; //ttl stands for time to live. It's a number of minutes that indicates how long a channel can be cached before refreshing from the source.
    private String updatePeriod ;
    private String updateFrequency ;

    /** Newsitems */
    private Hashtable items ;

    /** URL of this RSS */
    private String rssUrl ;

    /** Version of this RSS */
    private String rssVersion ;
    private String name ;

    /** Number of items (= news) in this RSS */
    private int itemCount ;

    /** Save order of the news from the XML */
    private Vector newsItemOrder ;

    /** Save what channel infos are available */
    private Vector newsChannelInfos ;

    /** Save what newsitem infos are available */
    private Vector newsItemInfos ;

    /** Items that match the search */
    private Hashtable searchResultsItems ;

    /** Sorted items that match the search */
    private Vector searchResultsItemOrder ;

    /** Save the Document (rss xml) */
    private Document document ;

    /**
     * Instantiates a news RSSChannel
     * @param title
     * @param description
     * @param link
     * @param language
     */
    public RSSChannel( String title , String description , String link ,
                       String language )
    {
        this.title = title ;
        this.description = description ;
        this.link = link ;
        this.language = language ;
        items = new Hashtable() ;
        newsItemOrder = new Vector() ;
        newsChannelInfos = new Vector() ;
        newsItemInfos = new Vector() ;
        searchResultsItems = new Hashtable() ;
        searchResultsItemOrder = new Vector() ;

        /** Default values */
        newsItemInfos.add( "TABLE_HEADER_NEWSTITLE" ) ;
    }

    /**
     * Default constructor.
     */
    public RSSChannel()
    {
        items = new Hashtable() ;
        newsItemOrder = new Vector() ;
        newsChannelInfos = new Vector() ;
        newsItemInfos = new Vector() ;
        searchResultsItems = new Hashtable() ;
        searchResultsItemOrder = new Vector() ;

        /** Default values */
        newsItemInfos.add( "TABLE_HEADER_NEWSTITLE" ) ;
    }

    /**
     * @return String
     */
    public String getDocs()
    {
        return docs ;
    }

    /**
     * @param docs
     */
    public void setDocs( String docs )
    {
        this.docs = docs ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_DOCS" ) ;
    }

    /**
     * @return String
     */
    public String getDescription()
    {
        return description ;
    }

    /**
     * @param description
     */
    public void setDescription( String description )
    {
        this.description = description ;
    }

    /**
     * @return RSSChannelImage
     */
    public RSSChannelImage getImage()
    {
        return image ;
    }

    /**
     * @param image
     */
    public void setImage( RSSChannelImage image )
    {
        this.image = image ;
    }

    /**
     * @return Hashtable
     */
    public Hashtable getItems()
    {
        return items ;
    }

    /**
     * @param items
     */
    public void setItems( Hashtable items )
    {
        this.items = items ;
    }

    /**
     * @return String
     */
    public String getLanguage()
    {
        return language ;
    }

    /**
     * @param language
     */
    public void setLanguage( String language )
    {
        this.language = language ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_LANGUAGE" ) ;
    }

    /**
     * @return String
     */
    public String getLink()
    {
        return link ;
    }

    /**
     * @param link
     */
    public void setLink( String link )
    {
        this.link = link ;
    }

    /**
     * @return String
     */
    public String getTitle()
    {
        return title ;
    }

    /**
     * @param title
     */
    public void setTitle( String title )
    {
        this.title = title ;
    }

    /**
     * If titles are the same, append (n)
     * @param item NewsItem
     */
    public void insertItem( RSSNewsItem item )
    {
        String title = item.getTitle() ;
        String oldTitle = item.getTitle() ;

        if( title == null )
        {
            item.setTitle( "No Title" ) ;

        }
        int number = 1 ;
        while( items.containsKey( title ) )
        {
            title = oldTitle ;
            title = title + " (" + number + ")" ;
            number++ ;
        }
        item.setTitle( title ) ;
        items.put( title , item ) ;
        newsItemOrder.add( title ) ;
        itemCount++ ;
    }

    /**
     * @return int
     */
    public int getItemCount()
    {
        return itemCount ;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return name ;
    }

    /**
     * @param name
     */
    public void setName( String name )
    {
        this.name = name ;
    }

    /**
     * @return String
     */
    public String getRssUrl()
    {
        return rssUrl ;
    }

    /**
     * @param rssUrl
     */
    public void setRssUrl( String rssUrl )
    {
        this.rssUrl = rssUrl ;
    }

    /**
     * @return Vector
     */
    public Vector getNewsItemOrder()
    {
        return newsItemOrder ;
    }

    /**
     * @param newsItemOrder
     */
    public void setNewsItemOrder( Vector newsItemOrder )
    {
        this.newsItemOrder = newsItemOrder ;
    }

    /**
     * @return String
     */
    public String getCopyright()
    {
        return copyright ;
    }

    /**
     * @param copyright
     */
    public void setCopyright( String copyright )
    {
        this.copyright = copyright ;
    }

    /**
     * @return String
     */
    public String getRssVersion()
    {
        return rssVersion ;
    }

    /**
     * @param rssVersion
     */
    public void setRssVersion( String rssVersion )
    {
        this.rssVersion = rssVersion ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_RSSVERSION" ) ;
    }

    /**
     * @return String
     */
    public String getPubdate()
    {
        return pubdate ;
    }

    /**
     * @param pubdate
     */
    public void setPubdate( String pubdate )
    {
        this.pubdate = pubdate ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_PUBDATE" ) ;
    }

    /**
     * @return String
     */
    public String getWebmaster()
    {
        return webmaster ;
    }

    /**
     * @param webmaster
     */
    public void setWebmaster( String webmaster )
    {
        this.webmaster = webmaster ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_WEBMASTER" ) ;
    }

    /**
     * @return String
     */
    public String getRating()
    {
        return rating ;
    }

    /**
     * @param rating
     */
    public void setRating( String rating )
    {
        this.rating = rating ;
    }

    /**
     * @return String
     */
    public String getGenerator()
    {
        return generator ;
    }

    /**
     * @param generator
     */
    public void setGenerator( String generator )
    {
        this.generator = generator ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_GENERATOR" ) ;
    }

    /**
     * @return String
     */
    public String getCategory()
    {
        return category ;
    }

    /**
     * @param category
     */
    public void setCategory( String category )
    {
        this.category = category ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_CATEGORY" ) ;
    }

    /**
     * @return String
     */
    public String getLastBuildDate()
    {
        return lastBuildDate ;
    }

    /**
     * @param lastBuildDate
     */
    public void setLastBuildDate( String lastBuildDate )
    {
        this.lastBuildDate = lastBuildDate ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_LASTBUILDDATE" ) ;
    }

    /**
     * @return String
     */
    public String getManagingEditor()
    {
        return managingEditor ;
    }

    /**
     * @param managingEditor
     */
    public void setManagingEditor( String managingEditor )
    {
        this.managingEditor = managingEditor ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_MANAGINGEDITOR" ) ;
    }

    /**
     * @return String
     */
    public String getCreator()
    {
        return creator ;
    }

    /**
     * @param creator
     */
    public void setCreator( String creator )
    {
        this.creator = creator ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_CREATOR" ) ;
    }

    /**
     * @return String
     */
    public String getPublisher()
    {
        return publisher ;
    }

    /**
     * @param publisher
     */
    public void setPublisher( String publisher )
    {
        this.publisher = publisher ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_PUBLISHER" ) ;
    }

    /**
     * @return Vector
     */
    public Vector getSkipDays()
    {
        return skipDays ;
    }

    /**
     * @param skipDays
     */
    public void setSkipDays( Vector skipDays )
    {
        this.skipDays = skipDays ;
    }

    /**
     * @return Vector
     */
    public Vector getSkipHours()
    {
        return skipHours ;
    }

    /**
     * @param skipHours
     */
    public void setSkipHours( Vector skipHours )
    {
        this.skipHours = skipHours ;
    }

    /**
     * @return String
     */
    public String getTtl()
    {
        return ttl ;
    }

    /**
     * @param ttl
     */
    public void setTtl( String ttl )
    {
        this.ttl = ttl ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_TTL" ) ;
    }

    /**
     * @return Vector
     */
    public Vector getAvailableNewsItemInfos()
    {
        return newsItemInfos ;
    }

    /**
     * @return Vector
     */
    public Vector getAvailableNewsChannelInfos()
    {
        return newsChannelInfos ;
    }

    /**
     * @param i18nName
     */
    public void addAvailableNewsChannelInfo( String i18nName )
    {
        if( !newsChannelInfos.contains( i18nName ) )
        {
            newsChannelInfos.add( i18nName ) ;
        }
    }

    /**
     * @param i18nName
     */
    public void addAvailableNewsItemInfo( String i18nName )
    {
        if( !newsItemInfos.contains( i18nName ) )
        {
            newsItemInfos.add( i18nName ) ;
        }
    }

    /**
     * @param itemCount
     */
    public void setItemCount( int itemCount )
    {
        this.itemCount = itemCount ;
    }

    /**
     * @return Vector
     */
    public Vector getSearchResultsItemOrder()
    {
        return searchResultsItemOrder ;
    }

    /**
     * @param searchResultsItemOrder
     */
    public void setSearchResultsItemOrder( Vector searchResultsItemOrder )
    {
        this.searchResultsItemOrder = searchResultsItemOrder ;
    }

    /**
     * @return Hashtable
     */
    public Hashtable getSearchResultsItems()
    {
        return searchResultsItems ;
    }

    /**
     * @param searchResultsItems
     */
    public void setSearchResultsItems( Hashtable searchResultsItems )
    {
        this.searchResultsItems = searchResultsItems ;
    }

    /**
     * @return String
     */
    public String getUpdateFrequency()
    {
        return updateFrequency ;
    }

    /**
     * @param updateFrequency
     */
    public void setUpdateFrequency( String updateFrequency )
    {
        this.updateFrequency = updateFrequency ;
    }

    /**
     * @return String
     */
    public String getUpdatePeriod()
    {
        return updatePeriod ;
    }

    /**
     * @return Document
     */
    public Document getDocument()
    {
        return document ;
    }

    /**
     * @param document
     */
    public void setDocument( Document document )
    {
        this.document = document ;
    }

    /**
     * @param updatePeriod
     */
    public void setUpdatePeriod( String updatePeriod )
    {
        this.updatePeriod = updatePeriod ;
        addAvailableNewsChannelInfo( "CHANNEL_INFO_UPDATE_PERIOD" ) ;
    }

    /**
     * Perform the search and add the NewsItems that match
     * the pattern to the searchResultsItems Hashtable
     *
     * @param parsedPattern
     * @param regEx
     */
    public void setNewsThatMatchSearch( Hashtable parsedPattern , Vector regEx )
    {
        boolean success ;

        /** Reset Hashtables */
        searchResultsItems = new Hashtable() ;
        searchResultsItemOrder = new Vector() ;

        Vector not = ( Vector ) parsedPattern.get( "NOT" ) ;

        for( int c = 0 ; c < newsItemOrder.size() ; c++ )
        {
            RSSNewsItem item = ( RSSNewsItem ) items.get( newsItemOrder.get( c ) ) ;

            /** Clear Vector */
            item.setHighlightWords( new Vector() ) ;

            success = true ;

            String oneLineTitle = "" ;
            if( item.getTitle() != null )
            {
                oneLineTitle = item.getTitle().replaceAll( "\n" , " " ) ;

            }
            String oneLineDescription = "" ;
            if( item.getDescription() != null )
            {
                oneLineDescription = item.getDescription().replaceAll( "\n" ,
                    " " ) ;

            }
            StringBuffer sb = new StringBuffer( oneLineTitle ) ;
            sb.append( " " ) ;
            sb.append( oneLineDescription ) ;

            String oneLineComplete = sb.toString() ;

            /** Perform case insensitive search */
            if( !RSSSearchPatternParser.searchCaseSensitive )
            {
                oneLineComplete = oneLineComplete.toLowerCase() ;

                /** Check for NOT searchwords */
            }
            for( int a = 0 ; a < not.size() ; a++ )
            {
                sb = new StringBuffer( "(.*)" ) ;

                if( !RSSSearchPatternParser.searchCaseSensitive )
                {
                    sb.append( ( ( String ) not.get( a ) ).toLowerCase() ) ;
                }
                else
                {
                    sb.append( ( ( String ) not.get( a ) ) ) ;

                }
                sb.append( "(.*)" ) ;

                if( oneLineComplete.matches( sb.toString() ) )
                {
                    success = false ;
                    break ;
                }
            }

            if( success )
            {
                /** Check for AND / OR searchwords */
                for( int b = 0 ; b < regEx.size() ; b++ )
                {
                    String curRegEx = ( String ) regEx.get( b ) ;

                    if( !RSSSearchPatternParser.searchCaseSensitive )
                    {
                        curRegEx = curRegEx.toLowerCase() ;

                    }
                    if( !oneLineComplete.matches( curRegEx ) )
                    {
                        success = false ;
                    }
                }
            }

            /** Remove all newsItems that do not match the search */
            if( success )
            {

                /** Save news item in searchResultsItems */
                searchResultsItems.put( item.getTitle() , item ) ;
                searchResultsItemOrder.add( item.getTitle() ) ;

                /** Add words to Vector for highlighted words */
                Vector and = ( Vector ) parsedPattern.get( "AND" ) ;
                for( int a = 0 ; a < and.size() ; a++ )
                {
                    Vector words = ( Vector ) and.get( a ) ;
                    for( int b = 0 ; b < words.size() ; b++ )
                    {
                        item.insertHighlightWord( ( String ) words.get( b ) ) ;
                    }
                }
            }
        }
        setItemCount( searchResultsItems.size() ) ;
    }

    /**
     * Perform the search and add the NewsItems that match
     * the pattern to the RegEx
     *
     * @param pattern The RegEx pattern
     */
    public void setNewsThatMatchSearch( String pattern )
    {
        boolean success ;

        /** Reset Hashtables */
        searchResultsItems = new Hashtable() ;
        searchResultsItemOrder = new Vector() ;

        for( int c = 0 ; c < newsItemOrder.size() ; c++ )
        {
            RSSNewsItem item = ( RSSNewsItem ) items.get( newsItemOrder.get( c ) ) ;

            /** Clear Vector */
            item.setHighlightWords( new Vector() ) ;

            success = true ;

            String oneLineTitle = "" ;
            if( item.getTitle() != null )
            {
                oneLineTitle = item.getTitle().replaceAll( "\n" , " " ) ;

            }
            String oneLineDescription = "" ;
            if( item.getDescription() != null )
            {
                oneLineDescription = item.getDescription().replaceAll( "\n" ,
                    " " ) ;

            }
            StringBuffer sb = new StringBuffer( oneLineTitle ) ;
            sb.append( " " ) ;
            sb.append( oneLineDescription ) ;

            String oneLineComplete = sb.toString() ;

            if( !oneLineComplete.matches( pattern ) )
            {
                success = false ;
            }

            /** Remove all newsItems that do not match the search */
            if( success )
            {

                /** Save news item in searchResultsItems */
                searchResultsItems.put( item.getTitle() , item ) ;
                searchResultsItemOrder.add( item.getTitle() ) ;
            }
        }
        setItemCount( searchResultsItems.size() ) ;
    }
}
