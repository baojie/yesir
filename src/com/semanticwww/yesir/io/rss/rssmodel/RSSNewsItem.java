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

import java.nio.charset.Charset ;
import java.util.HashSet ;
import java.util.Hashtable ;
import java.util.Vector ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import com.semanticwww.yesir.utils.string.ISOCharConverter ;

/**
 * Model for a RSSNewsItem.
 * A RSSChannel may contain more than one item.
 *
 * @author  <a href="mailto:bpasero@users.sourceforge.net">Benjamin Pasero</a>
 * @version 0.63b
 */
public class RSSNewsItem
{

    /** Attributes describing the news item */
    private String title ;
    private String description ;
    private String link ;
    private String guid ; //unique identifier
    private String author ;
    private String publisher ;
    private String category ;
    private RSSEnclosure enclosure ; //e.g. link to a mp3 or other file
    private String pubDate ;
    private String source ;
    private String date ;
    private String creator ;
    private boolean permaLink ;
    private Hashtable enclosures ;

    /** Vector holding all words that should be highlighted */
    private Vector highlightWords ;

    /** TRUE if the user has read this news */
    private boolean isRead ;

    /** Title of the feed this news is from */
    private String newsfeedTitle ;

    /** Vector holding all links from the text */
    private HashSet linkList ;

    /**
     * Instantiates a new RSSNewsItem
     * @param title
     * @param description
     * @param link
     * @param guid
     */
    public RSSNewsItem( String title , String description , String link ,
                        String guid )
    {
        this.title = decodeEntitys( title ) ;
        this.description = decodeEntitys( description ) ;
        this.link = link ;
        this.guid = guid ;
        enclosures = new Hashtable() ;
        highlightWords = new Vector() ;
        linkList = new HashSet() ;
        isRead = false ;
    }

    /**
     * Default constructor
     */
    public RSSNewsItem()
    {
        enclosures = new Hashtable() ;
        highlightWords = new Vector() ;
        linkList = new HashSet() ;
        isRead = false ;
    }

    /**
     * @return String description
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

        /** Decode entitys */
        this.description = decodeEntitys( description ) ;

        /** Parse for links and add to linkList */
        parseForLinks( description ) ;
    }

    /**
     * @return String link
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
     * @return String title
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
        this.title = decodeEntitys( title ) ;
    }

    /**
     * @return String guid
     */
    public String getGuid()
    {
        return guid ;
    }

    /**
     * @param guid
     */
    public void setGuid( String guid )
    {
        this.guid = guid ;
    }

    /**
     * @return String Author
     */
    public String getAuthor()
    {
        return author ;
    }

    /**
     * @param author
     */
    public void setAuthor( String author )
    {
        this.author = author ;
    }

    /**
     * @return String Category
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
    }

    /**
     * @return RSSEnclosure Enclosure
     */
    public RSSEnclosure getEnclosure()
    {
        return enclosure ;
    }

    /**
     * @param enclosure
     */
    public void setEnclosure( RSSEnclosure enclosure )
    {
        this.enclosure = enclosure ;
    }

    /**
     * @return String PubDate
     */
    public String getPubDate()
    {
        return pubDate ;
    }

    /**
     * @param pubDate
     */
    public void setPubDate( String pubDate )
    {
        this.pubDate = pubDate ;
    }

    /**
     * @return String Source
     */
    public String getSource()
    {
        return source ;
    }

    /**
     * @param source
     */
    public void setSource( String source )
    {
        this.source = source ;
    }

    /**
     * @param rssEnclosure
     */
    public void insertEnclosure( RSSEnclosure rssEnclosure )
    {
        enclosures.put( rssEnclosure.getName() , rssEnclosure ) ;
    }

    /**
     * @return String Creator
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
    }

    /**
     * @return String Date
     */
    public String getDate()
    {
        return date ;
    }

    /**
     * @param date
     */
    public void setDate( String date )
    {
        this.date = date ;
    }

    /**
     * @return Hashtable Enclosures
     */
    public Hashtable getEnclosures()
    {
        return enclosures ;
    }

    /**
     * @return boolean TRUE if permalink
     */
    public boolean isPermaLink()
    {
        return permaLink ;
    }

    /**
     * @param permaLink
     */
    public void setPermaLink( boolean permaLink )
    {
        this.permaLink = permaLink ;
    }

    /**
     * @return Vector Highlighted Words
     */
    public Vector getHighlightWords()
    {
        return highlightWords ;
    }

    /**
     * @param highlightWords
     */
    public void setHighlightWords( Vector highlightWords )
    {
        this.highlightWords = highlightWords ;
    }

    /**
     * @param word
     */
    public void insertHighlightWord( String word )
    {
        if( !highlightWords.contains( word ) )
        {
            highlightWords.add( word ) ;
        }
    }

    /**
     * @return String Publisher
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
    }

    /**
     * @return boolean TRUE if read
     */
    public boolean isRead()
    {
        return isRead ;
    }

    /**
     * @param isRead
     */
    public void setRead( boolean isRead )
    {
        this.isRead = isRead ;
    }

    /**
     * Remove some HTML-Tags from the description
     * @return HTML removed description
     */
    public String getHTMLRemovedDescription()
    {
        String description = getDescription() ;

        if( description != null )
        {
            description = description.replaceAll( "<br>" , "\n" ) ;
            description = description.replaceAll( "<br />" , "\n" ) ;
            description = description.replaceAll( "<br/>" , "\n" ) ;
            description = description.replaceAll( "<i>" , "" ) ;
            description = description.replaceAll( "</i>" , "" ) ;
            description = description.replaceAll( "<b>" , "" ) ;
            description = description.replaceAll( "</b>" , "" ) ;
            description = description.replaceAll( "<u>" , "" ) ;
            description = description.replaceAll( "</u>" , "" ) ;
            description = description.replaceAll( "<p>" , "" ) ;
            description = description.replaceAll( "</p>" , "\n" ) ;
        }
        return description ;
    }

    /**
     * @return String The Title of the newsfeed where this item is in
     */
    public String getNewsfeedTitle()
    {
        return newsfeedTitle ;
    }

    /**
     * Set the Title of the newsfeed where this item is in
     * @param newsfeedTitle
     */
    public void setNewsfeedTitle( String newsfeedTitle )
    {
        this.newsfeedTitle = newsfeedTitle ;
    }

    /**
     * Decode all encoded entitys if existing
     * @param text Current Text
     * @return Decoded Text
     */
    private String decodeEntitys( String text )
    {

        /** This text seems to hold ISO encoded chars */
        if( Charset.isSupported( "ISO-8859-1" ) &&
            text.matches( "(.)*&#[0-9]{3}(.)*" ) )
        {
            for( int i = 160 ; i < 256 ; i++ )
            {
                if( text.indexOf( "&#" + i + ";" ) >= 0 )
                {
                    text = text.replaceAll( "&#" + i + ";" ,
                                            ISOCharConverter.decodeChar( new byte[]
                        { ( byte ) i} ) ) ;
                }
            }
        }
        return text ;
    }

    /**
     * Parse the text for links
     * @param text Current Text
     */
    private void parseForLinks( String text )
    {

        String urlRegEx = "(www([\\wv\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?)|(http|ftp|https):\\/\\/[\\w]+(.[\\w]+)([\\wv\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?" ;

        Pattern pattern = Pattern.compile( urlRegEx ) ;
        Matcher match = pattern.matcher( text ) ;

        while( match.find() )
        {
            linkList.add( match.group( 0 ) ) ;
        }
    }

    /**
     * Get the Link List
     * @return HashSet
     */
    public HashSet getLinkList()
    {
        return linkList ;
    }

    /**
     * Set the Link List
     * @param linkList
     */
    public void setLinkList( HashSet linkList )
    {
        this.linkList = linkList ;
    }
}
