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

import java.util.Iterator ;
import java.util.List ;
import java.util.Vector ;

import org.jdom.Document ;
import org.jdom.Element ;
import org.jdom.Namespace ;

/**
 * Class to Parse a RSS XML. Supported RSS Versions are
 * 0.91, 0.92, 1.0 (RDF) and 2.0
 *
 * @author  <a href="mailto:bpasero@users.sourceforge.net">Benjamin Pasero</a>
 * @version 0.63b
 */
public class RSSXMLParser
{

    /** Current document */
    private Document document ;

    /** Current RSSChannel */
    private RSSChannel rssChannel ;

    /**
     * Instantiate a new RSSXMLParser
     * @param xml Document holding the rss xml
     */
    public RSSXMLParser( Document xml )
    {
        this.document = xml ;
        rssChannel = new RSSChannel() ;
    }

    /**
     * Parse the RSS XML and build a new RSSChannel
     * @return The RSSChannel from the XML
     */
    public RSSChannel getRSSChannel()
        throws RSSXMLFactoryException
    {

        /** XML is a RDF */
        if( document.getRootElement().getName().equalsIgnoreCase( "rdf" ) )
        {
            rssChannel.setRssVersion( "1.0 (RDF)" ) ;
            parseRDF() ;
        }

        /** XML is a RSS */
        else
        {

            /** Parse common attributes */
            parseCommon() ;

            /** Check the RSS Version */
            if( document.getRootElement().getAttributeValue( "version" ) != null )
            {
                rssChannel.setRssVersion( document.getRootElement().
                                          getAttribute( "version" ).getValue() ) ;

                if( rssChannel.getRssVersion().equals( "0.91" ) )
                {
                    parseRSSVersion_0_91() ;

                }
                else if( rssChannel.getRssVersion().equals( "0.92" ) )
                {
                    parseRSSVersion_0_92() ;

                }
                else if( rssChannel.getRssVersion().equals( "1.0" ) )
                {
                    parseRDF() ;

                }
                else if( rssChannel.getRssVersion().equals( "2.0" ) )
                {
                    parseRSSVersion_2_0() ;
                }
            }
            else
            {
                parseRSSVersion_0_91() ;
            }
        }

        /** Save document in RSSChannel */
        rssChannel.setDocument( document ) ;

        return rssChannel ;
    }

    /** Parse attributes in this RDF document */
    protected void parseRDF()
        throws RSSXMLFactoryException
    {
        Element root = document.getRootElement() ;

        Namespace dc = null ;
        Namespace sy = null ;
        Namespace rdf = null ;
        Namespace defNs = Namespace.NO_NAMESPACE ;

        /** Check namespaces for this RDF */
        if( root.getNamespace().getPrefix().equals( "rdf" ) )
        {
            rdf = root.getNamespace() ;
        }
        else if( root.getNamespace().getPrefix().equals( "sy" ) )
        {
            sy = root.getNamespace() ;
        }
        else if( root.getNamespace().getPrefix().equals( "dc" ) )
        {
            dc = root.getNamespace() ;
        }
        else if( root.getNamespace().getPrefix().equals( "" ) )
        {
            defNs = root.getNamespace() ;

            /** Get additional namespaces */
        }
        Iterator namespaces = root.getAdditionalNamespaces().iterator() ;
        while( namespaces.hasNext() )
        {
            Namespace namespace = ( Namespace ) namespaces.next() ;
            if( namespace.getPrefix().equalsIgnoreCase( "dc" ) )
            {
                dc = namespace ;
            }
            else if( namespace.getPrefix().equalsIgnoreCase( "sy" ) )
            {
                sy = namespace ;
            }
            else if( namespace.getPrefix().equalsIgnoreCase( "rdf" ) )
            {
                rdf = namespace ;

                /** Default Namespace */
            }
            else if( namespace.getPrefix().equalsIgnoreCase( "" ) )
            {
                defNs = namespace ;
            }
        }

        /** Get Channel Element */
        Element channel = root.getChild( "channel" , defNs ) ;

        if( channel != null )
        {

            /** Parse Elements with default namespace */

            /** Title of channel */
            if( channel.getChild( "title" , defNs ) != null )
            {
                rssChannel.setTitle( channel.getChildTextTrim( "title" , defNs ) ) ;

                /** Link of channel */
            }
            if( channel.getChild( "link" , defNs ) != null )
            {
                rssChannel.setLink( channel.getChildTextTrim( "link" , defNs ) ) ;

                /** Description of channel */
            }
            if( channel.getChild( "description" , defNs ) != null )
            {
                rssChannel.setDescription( channel.getChildTextTrim(
                    "description" , defNs ) ) ;

                /** pubDate of channel */
            }
            if( channel.getChild( "pubDate" , defNs ) != null )
            {
                rssChannel.setPubdate( channel.getChildTextTrim( "pubDate" ,
                    defNs ) ) ;

                /** lastBuildDate of channel */
            }
            if( channel.getChild( "lastBuildDate" , defNs ) != null )
            {
                rssChannel.setLastBuildDate( channel.getChildTextTrim(
                    "lastBuildDate" , defNs ) ) ;

                /** Copyright of channel */
            }
            if( channel.getChild( "copyright" , defNs ) != null )
            {
                rssChannel.setCopyright( channel.getChildTextTrim( "copyright" ,
                    defNs ) ) ;

                /** Parse Elements with Dublin Core Module namespace */
            }
            if( dc != null )
            {
                /** Language of channel */
                if( channel.getChild( "language" , dc ) != null )
                {
                    rssChannel.setLanguage( channel.getChildTextTrim(
                        "language" , dc ) ) ;
                }
                else
                {

                    /** Check for language declaration in the first NewsItem */
                    Element firstNewsItem = ( Element ) root.getChildren(
                        "item" , defNs ).get( 0 ) ;
                    if( firstNewsItem.getChild( "language" , dc ) != null )
                    {
                        rssChannel.setLanguage( firstNewsItem.getChildTextTrim(
                            "language" , dc ) ) ;
                    }
                }

                /** Publisher of the channel */
                if( channel.getChild( "publisher" , dc ) != null )
                {
                    rssChannel.setPublisher( channel.getChildTextTrim(
                        "publisher" , dc ) ) ;

                    /** Copyrights of the channel */
                }
                if( channel.getChild( "rights" , dc ) != null )
                {
                    rssChannel.setCopyright( channel.getChildTextTrim( "rights" ,
                        dc ) ) ;

                    /** Date of the channel */
                }
                if( channel.getChild( "date" , dc ) != null )
                {
                    rssChannel.setLastBuildDate( channel.getChildTextTrim(
                        "date" , dc ) ) ;

                    /** Creator of the channel */
                }
                if( channel.getChild( "creator" , dc ) != null )
                {
                    rssChannel.setCreator( channel.getChildTextTrim( "creator" ,
                        dc ) ) ;
                }
            }

            /** Parse Elements with Syndication Module namespace */
            if( sy != null )
            {

                /** updatePeriod of the channel */
                if( channel.getChild( "updatePeriod" , sy ) != null )
                {
                    rssChannel.setUpdatePeriod( channel.getChildTextTrim(
                        "updatePeriod" , sy ) ) ;

                    /** updateFrequency of the channel */
                }
                if( channel.getChild( "updateFrequency" , sy ) != null )
                {
                    rssChannel.setUpdateFrequency( channel.getChildTextTrim(
                        "updateFrequency" , sy ) ) ;
                }
            }

            /** Parse Elements with RDF Module namespace */
            if( rdf != null )
            {

                /** Image of the channel */
                if( channel.getChild( "image" , defNs ) != null )
                {

                    if( channel.getChild( "image" ,
                                          defNs ).getAttributeValue( "resource" ,
                        rdf ) != null )
                    {
                        RSSChannelImage rssChannelImage = new RSSChannelImage() ;
                        rssChannelImage.setImgUrl( channel.getChild( "image" ,
                            defNs ).getAttributeValue( "resource" , rdf ) ) ;
                        rssChannel.setImage( rssChannelImage ) ;
                    }
                }
            }
        }

        /** Get the Channel Newsitems */
        List items = root.getChildren( "item" , defNs ) ;
        Iterator itemsIt = items.iterator() ;

        while( itemsIt.hasNext() )
        {
            Element item = ( Element ) itemsIt.next() ;
            RSSNewsItem newsItem = new RSSNewsItem() ;

            /** Parse NewsItems w/o namespaces */

            /** Title of NewsItem */
            if( item.getChild( "title" , defNs ) != null )
            {
                newsItem.setTitle( item.getChildTextTrim( "title" , defNs ) ) ;

                /** Link of NewsItem */
            }
            if( item.getChild( "link" , defNs ) != null )
            {
                newsItem.setLink( item.getChildTextTrim( "link" , defNs ) ) ;

                /** Set Title to Link if Title is NULL */
                if( newsItem.getTitle() == null )
                {
                    newsItem.setTitle( newsItem.getLink() ) ;
                }
                else if( newsItem.getTitle().equals( "" ) )
                {
                    newsItem.setTitle( newsItem.getLink() ) ;
                }
            }

            /** Description of NewsItem */
            if( item.getChild( "description" , defNs ) != null )
            {
                newsItem.setDescription( item.getChildTextTrim( "description" ,
                    defNs ) ) ;

                /** Parse Elemenets with Dublin Core Module namespace */

                /** Title of the NewsItem */
            }
            if( item.getChild( "title" , dc ) != null && newsItem.getTitle() == null )
            {
                newsItem.setTitle( channel.getChildTextTrim( "title" , dc ) ) ;

                /** Description of the NewsItem */
            }
            if( item.getChild( "description" , dc ) != null &&
                newsItem.getDescription() == null )
            {
                newsItem.setDescription( channel.getChildTextTrim(
                    "description" , dc ) ) ;

                /** Date of the NewsItem */
            }
            if( item.getChild( "date" , dc ) != null )
            {
                newsItem.setPubDate( item.getChildTextTrim( "date" , dc ) ) ;
                rssChannel.addAvailableNewsItemInfo( "TABLE_HEADER_PUBDATE" ) ;
            }

            /** publisher of the NewsItem */
            if( item.getChild( "publisher" , dc ) != null )
            {
                newsItem.setPublisher( item.getChildTextTrim( "publisher" , dc ) ) ;
                rssChannel.addAvailableNewsItemInfo( "TABLE_HEADER_PUBLISHER" ) ;
            }

            /** creator of the NewsItem equals author */
            if( item.getChild( "creator" , dc ) != null )
            {
                newsItem.setAuthor( item.getChildTextTrim( "creator" , dc ) ) ;
                rssChannel.addAvailableNewsItemInfo( "TABLE_HEADER_AUTHOR" ) ;
            }

            /** Add NewsItem to RSSChannel */
            rssChannel.insertItem( newsItem ) ;
        }
    }

    /**
     * Parse Attributes that all RSS Versions have
     */
    protected void parseCommon()
        throws RSSXMLFactoryException
    {
        Element root = document.getRootElement() ;

        /** Get Channel Element */
        Element channel = root.getChild( "channel" ) ;

        if( channel == null )
        {
            throw new RSSXMLFactoryException( "<channel> Element not found!" ) ;
        }

        /** Parse Channel Must Attributes */

        /** Title of channel */
        if( channel.getChild( "title" ) != null )
        {
            rssChannel.setTitle( channel.getChildTextTrim( "title" ) ) ;

            /** Link of channel */
        }
        if( channel.getChild( "link" ) != null )
        {
            rssChannel.setLink( channel.getChildTextTrim( "link" ) ) ;

            /** Description of channel */
        }
        if( channel.getChild( "description" ) != null )
        {
            rssChannel.setDescription( channel.getChildTextTrim( "description" ) ) ;

            /** Language of channel */
        }
        if( channel.getChild( "language" ) != null )
        {
            rssChannel.setLanguage( channel.getChildTextTrim( "language" ) ) ;

            /** Parse Channel May Attributes */

            /** Image of channel */
        }
        if( channel.getChild( "image" ) != null )
        {

            Element image = channel.getChild( "image" ) ;
            RSSChannelImage rssChannelImage = new RSSChannelImage() ;

            /** URL to the Image */
            if( image.getChild( "url" ) != null )
            {
                rssChannelImage.setImgUrl( image.getChildTextTrim( "url" ) ) ;

                /** Title of the Image */
            }
            if( image.getChild( "title" ) != null )
            {
                rssChannelImage.setTitle( image.getChildTextTrim( "title" ) ) ;

            }
            rssChannel.setImage( rssChannelImage ) ;
        }

        /** Copyright of channel */
        if( channel.getChild( "copyright" ) != null )
        {
            rssChannel.setCopyright( channel.getChildTextTrim( "copyright" ) ) ;

            /** pubDate of channel */
        }
        if( channel.getChild( "pubDate" ) != null )
        {
            rssChannel.setPubdate( channel.getChildTextTrim( "pubDate" ) ) ;

            /** lastBuildDate of channel */
        }
        if( channel.getChild( "lastBuildDate" ) != null )
        {
            rssChannel.setLastBuildDate( channel.getChildTextTrim(
                "lastBuildDate" ) ) ;

            /** Docs of channel */
        }
        if( channel.getChild( "docs" ) != null )
        {
            rssChannel.setDocs( channel.getChildTextTrim( "docs" ) ) ;

            /** Managing Editor of channel */
        }
        if( channel.getChild( "managingEditor" ) != null )
        {
            rssChannel.setManagingEditor( channel.getChildTextTrim(
                "managingEditor" ) ) ;

            /** Webmaster of channel */
        }
        if( channel.getChild( "webmaster" ) != null )
        {
            rssChannel.setWebmaster( channel.getChildTextTrim( "webmaster" ) ) ;

            /** skipHours of channel */
        }
        if( channel.getChild( "skipHours" ) != null )
        {
            Vector skippedHours = new Vector() ;
            List hourList = channel.getChild( "skipHours" ).getChildren() ;
            Iterator it = hourList.iterator() ;
            while( it.hasNext() )
            {
                skippedHours.add( ( ( Element ) it.next() ).getText() ) ;
            }
        }

        /** skipDays of channel */
        if( channel.getChild( "skipDays" ) != null )
        {
            Vector skippedDays = new Vector() ;
            List dayList = channel.getChild( "skipDays" ).getChildren() ;
            Iterator it = dayList.iterator() ;
            while( it.hasNext() )
            {
                skippedDays.add( ( ( Element ) it.next() ).getText() ) ;
            }
        }
    }

    /**
     * Parse Attributes for RSS Version 0.91
     */
    protected void parseRSSVersion_0_91()
    {
        Element root = document.getRootElement() ;

        /** Get Channel Element */
        Element channel = root.getChild( "channel" ) ;

        /** Parse Channel May Attributes */

        /** Rating of channel */
        if( channel.getChild( "rating" ) != null )
        {
            rssChannel.setRating( channel.getChildTextTrim( "rating" ) ) ;

            /** !! SKIP textinput !! */

            /** Get the Channel Newsitems */
        }
        List items = channel.getChildren( "item" ) ;
        Iterator itemsIt = items.iterator() ;

        while( itemsIt.hasNext() )
        {
            Element item = ( Element ) itemsIt.next() ;
            RSSNewsItem newsItem = new RSSNewsItem() ;

            /** Parse NewsItem Must Attributes */

            /** Title of NewsItem */
            if( item.getChild( "title" ) != null )
            {
                newsItem.setTitle( item.getChildTextTrim( "title" ) ) ;

                /** Link of NewsItem */
            }
            if( item.getChild( "link" ) != null )
            {
                newsItem.setLink( item.getChildTextTrim( "link" ) ) ;

                /** Set Title to Link if Title is NULL */
                if( newsItem.getTitle() == null )
                {
                    newsItem.setTitle( newsItem.getLink() ) ;
                }
                else if( newsItem.getTitle().equals( "" ) )
                {
                    newsItem.setTitle( newsItem.getLink() ) ;
                }
            }

            /** Description of NewsItem */
            if( item.getChild( "description" ) != null )
            {
                newsItem.setDescription( item.getChildTextTrim( "description" ) ) ;

                /** Add NewsItem to RSSChannel */
            }
            rssChannel.insertItem( newsItem ) ;
        }
    }

    /**
     * Parse Attributes for RSS Version 0.92
     */
    protected void parseRSSVersion_0_92()
    {
        Element root = document.getRootElement() ;

        /** Get Channel Element */
        Element channel = root.getChild( "channel" ) ;

        /** Parse Channel May Attributes */

        /** !! SKIP Cloud !! */

        /** Rating of channel */
        if( channel.getChild( "rating" ) != null )
        {
            rssChannel.setRating( channel.getChildTextTrim( "rating" ) ) ;

            /** Get the Channel Newsitems */
        }
        List items = channel.getChildren( "item" ) ;
        Iterator itemsIt = items.iterator() ;

        while( itemsIt.hasNext() )
        {
            Element item = ( Element ) itemsIt.next() ;
            RSSNewsItem newsItem = new RSSNewsItem() ;

            /** Parse NewsItem Must Attributes */

            /** Title of NewsItem */
            if( item.getChild( "title" ) != null )
            {
                newsItem.setTitle( item.getChildTextTrim( "title" ) ) ;

                /** Link of NewsItem */
            }
            if( item.getChild( "link" ) != null )
            {
                newsItem.setLink( item.getChildTextTrim( "link" ) ) ;

                /** Set Title to Link if Title is NULL */
                if( newsItem.getTitle() == null )
                {
                    newsItem.setTitle( newsItem.getLink() ) ;
                }
                else if( newsItem.getTitle().equals( "" ) )
                {
                    newsItem.setTitle( newsItem.getLink() ) ;
                }
            }

            /** Description of NewsItem */
            if( item.getChild( "description" ) != null )
            {
                newsItem.setDescription( item.getChildTextTrim( "description" ) ) ;

                /** Source of NewsItem */
            }
            if( item.getChild( "source" ) != null )
            {
                if( item.getChild( "source" ).getAttribute( "url" ) != null )
                {
                    newsItem.setSource( item.getChild( "source" ).
                                        getAttributeValue( "url" ).trim() ) ;
                }
                else if( item.getChild( "source" ).getText() != null &&
                         !item.getChild( "source" ).getText().equals( "" ) )
                {
                    newsItem.setSource( item.getChildTextTrim( "source" ) ) ;
                }
            }

            /** Enclosure of NewsItem */
            if( item.getChild( "enclosure" ) != null )
            {
                Element enclosure = item.getChild( "enclosure" ) ;
                RSSEnclosure rssEnclosure = new RSSEnclosure() ;
                rssEnclosure.setName( item.getChildTextTrim( "enclosure" ) ) ;

                /** URL of enclosure */
                if( enclosure.getAttribute( "url" ) != null )
                {
                    rssEnclosure.setUrl( enclosure.getAttributeValue( "url" ) ) ;

                    /** Length of enclosure */
                }
                if( enclosure.getAttribute( "length" ) != null )
                {
                    rssEnclosure.setLength( enclosure.getAttributeValue(
                        "length" ) ) ;

                    /** Type of enclosure */
                }
                if( enclosure.getAttribute( "type" ) != null )
                {
                    rssEnclosure.setType( enclosure.getAttributeValue( "type" ) ) ;

                }
                newsItem.setEnclosure( rssEnclosure ) ;
            }

            /** Category of NewsItem */
            if( item.getChild( "category" ) != null )
            {
                newsItem.setCategory( item.getChildTextTrim( "category" ) ) ;
                rssChannel.addAvailableNewsItemInfo( "TABLE_HEADER_CATEGORY" ) ;
            }

            if( newsItem.getTitle() == null )
            {
                newsItem.setTitle( "NO_TITLE" ) ;

                /** Add NewsItem to RSSChannel */
            }
            rssChannel.insertItem( newsItem ) ;
        }
    }

    /**
     * Parse Attributes for RSS Version 2.0
     */
    protected void parseRSSVersion_2_0()
    {
        Element root = document.getRootElement() ;

        /** Get Channel Element */
        Element channel = root.getChild( "channel" ) ;

        /** Parse Channel May Attributes */

        /** !! SKIP cloud, textInput !! */

        /** TTL of channel */
        if( channel.getChild( "ttl" ) != null )
        {
            rssChannel.setTtl( channel.getChildTextTrim( "ttl" ) ) ;

            /** Category of channel */
        }
        if( channel.getChild( "category" ) != null )
        {
            rssChannel.setCategory( channel.getChildTextTrim( "category" ) ) ;

            /** Generator of channel */
        }
        if( channel.getChild( "generator" ) != null )
        {
            rssChannel.setGenerator( channel.getChildTextTrim( "generator" ) ) ;

            /** Rating of channel */
        }
        if( channel.getChild( "rating" ) != null )
        {
            rssChannel.setRating( channel.getChildTextTrim( "rating" ) ) ;

            /** Get the Channel Newsitems */
        }
        List items = channel.getChildren( "item" ) ;
        Iterator itemsIt = items.iterator() ;

        while( itemsIt.hasNext() )
        {
            Element item = ( Element ) itemsIt.next() ;
            RSSNewsItem newsItem = new RSSNewsItem() ;

            /** Parse NewsItem Must Attributes */

            /** !! SKIP Comments !! */

            /** Title of NewsItem */
            if( item.getChild( "title" ) != null )
            {
                newsItem.setTitle( item.getChildTextTrim( "title" ) ) ;

                /** Link of NewsItem */
            }
            if( item.getChild( "link" ) != null )
            {
                newsItem.setLink( item.getChildTextTrim( "link" ) ) ;

                /** Set Title to Link if Title is NULL */
                if( newsItem.getTitle() == null )
                {
                    newsItem.setTitle( newsItem.getLink() ) ;
                }
                else if( newsItem.getTitle().equals( "" ) )
                {
                    newsItem.setTitle( newsItem.getLink() ) ;
                }
            }

            /** Description of NewsItem */
            if( item.getChild( "description" ) != null )
            {
                newsItem.setDescription( item.getChildTextTrim( "description" ) ) ;

                /** pubDate of NewsItem */
            }
            if( item.getChild( "pubDate" ) != null )
            {
                newsItem.setPubDate( item.getChildTextTrim( "pubDate" ) ) ;
                rssChannel.addAvailableNewsItemInfo( "TABLE_HEADER_PUBDATE" ) ;
            }

            /** Author of NewsItem */
            if( item.getChild( "author" ) != null )
            {
                newsItem.setAuthor( item.getChildTextTrim( "author" ) ) ;
                rssChannel.addAvailableNewsItemInfo( "TABLE_HEADER_AUTHOR" ) ;
            }

            /** Category of NewsItem */
            if( item.getChild( "category" ) != null )
            {
                newsItem.setCategory( item.getChildTextTrim( "category" ) ) ;
                rssChannel.addAvailableNewsItemInfo( "TABLE_HEADER_CATEGORY" ) ;
            }

            /** GUID of NewsItem */
            if( item.getChild( "guid" ) != null )
            {
                newsItem.setGuid( item.getChildTextTrim( "guid" ) ) ;

                if( item.getChild( "guid" ).getAttribute( "isPermaLink" ) != null )
                {
                    if( item.getChild( "guid" ).getAttributeValue(
                        "isPermaLink" ).equals( "true" ) )
                    {
                        newsItem.setPermaLink( true ) ;

                        /** If Title is NULL, set guid as title */
                    }
                }
                if( newsItem.getTitle() == null )
                {
                    newsItem.setTitle( newsItem.getGuid() ) ;
                }
                else if( newsItem.getTitle().equals( "" ) )
                {
                    newsItem.setTitle( newsItem.getGuid() ) ;
                }
            }

            /** Source of NewsItem */
            if( item.getChild( "source" ) != null )
            {
                if( item.getChild( "source" ).getAttribute( "url" ) != null )
                {
                    newsItem.setSource( item.getChild( "source" ).
                                        getAttributeValue( "url" ).trim() ) ;
                }
                else if( item.getChild( "source" ).getText() != null &&
                         !item.getChild( "source" ).getText().equals( "" ) )
                {
                    newsItem.setSource( item.getChildTextTrim( "source" ) ) ;
                }
            }

            /** Enclosure of NewsItem */
            if( item.getChild( "enclosure" ) != null )
            {
                Element enclosure = item.getChild( "enclosure" ) ;
                RSSEnclosure rssEnclosure = new RSSEnclosure() ;
                rssEnclosure.setName( item.getChildTextTrim( "enclosure" ) ) ;

                /** URL of enclosure */
                if( enclosure.getAttribute( "url" ) != null )
                {
                    rssEnclosure.setUrl( enclosure.getAttributeValue( "url" ) ) ;

                    /** Length of enclosure */
                }
                if( enclosure.getAttribute( "length" ) != null )
                {
                    rssEnclosure.setLength( enclosure.getAttributeValue(
                        "length" ) ) ;

                    /** Type of enclosure */
                }
                if( enclosure.getAttribute( "type" ) != null )
                {
                    rssEnclosure.setType( enclosure.getAttributeValue( "type" ) ) ;

                }
                newsItem.insertEnclosure( rssEnclosure ) ;
            }

            /** Add NewsItem to RSSChannel */
            rssChannel.insertItem( newsItem ) ;
        }
    }
}
