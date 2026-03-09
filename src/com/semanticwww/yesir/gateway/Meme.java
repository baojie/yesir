package com.semanticwww.yesir.gateway ;

import java.util.Comparator ;
import java.util.Date ;




/**
 * meme - An element of culture that may be considered to be passed on by
 * non-genetic means£¬esp.imitation.
 *
 * meme is spreading on the internet and the brains of us people.
 *
 * Hopefully, software can speed up it's evolution speed, just like birds help
 * in spreading seeds.
 *
 * meme can be a piece of information, like email, sentences, post, acticle,
 * a book, a recipe ...
 *
 * @author Jie Bao
 * @version 1.1 - 2001-12-10
 *
 *   Refer To:
 *  Dublin Core Metadata Element  http://dublincore.org/documents/dces/
 *  RDF Site Summary (RSS) 1.0 http://web.resource.org/rss/1.0/spec
 */
public class Meme // called "Mail" in 1.0.0
{
    /*
        //to be reorganized
        dc : dc.Title //( #PCDATA )
        dc : dc.Creator //( #PCDATA )
        dc : dc.Subject //( #PCDATA )
        dc : dc.Description //( #PCDATA )
        dc : dc.Publisher //( #PCDATA )
        dc : dc.Contributor //( #PCDATA )
        dc : dc.Date //( #PCDATA )
        dc : dc.Type //( #PCDATA )
        dc : dc.Format //( #PCDATA )
        dc : dc.Identifier //( #PCDATA )
        dc : dc.Source //( #PCDATA )
        dc : dc.Language //( #PCDATA )
        dc : dc.Relation //( #PCDATA )
        dc : dc.Coverage //( #PCDATA )
        dc : dc.Rights // ( #PCDATA )
        rss : rss.Channel // An RSS information channel
        rss : rss.Image //An RSS image
        rss : rss.Item //An RSS item
        rss : rss.TextInput // An RSS text input
        rss : rss.Items // Points to a list of rss:item elements that are members of the subject channel
        rss : rss.Title // A descriptive title for the channel , subPropertyOf of dc:Title
        rss : rss.Link // The URL to which an HTML rendering of the subject will link, subPropertyOf of dc:Identifier
        rss : rss.URL //The URL of the image to used in the 'src' attribute of the channel's image tag when rendered as HTML, subPropertyOf dc:Identifier
        rss : rss.Description // short text description of the subject, subPropertyOf of dc:Description
        rss : rss.Name // The text input field's (variable) name.
        // not used yes, but definitely will be
     */
    public boolean concise = false ; // control the output of toString

    private String URL = null ;

    private String guid = null ; //unique identifier

    private String author[] = null ;

    private String to[] = null ;

    private String cc[] = null ;

    private String bcc[] = null ;

    private String title = null ; //

    private Date date = null ;

    private String content = null ; // also source


    /** Attributes describing the news item */
    private String summary = null ;

    private String publisher = null ;

    private String category = null ;

    private String pubDate = null ;

    private String creator = null ;

    private String source = null ;

    private boolean permaLink = false ;

    Attachment[] attachment ;

    public Object clone ()
    {
        Meme m = new Meme () ;

        m.concise = concise ;
        m.URL = URL ;
        m.guid = guid ; //unique identifier
        m.author = author ;
        m.to = to ;
        m.cc = cc ;
        m.bcc = bcc ;
        m.title = title ;
        m.date = date ;
        m.content = content ;
        m.summary = summary ;
        m.publisher = publisher ;
        m.category = category ;
        m.pubDate = pubDate ;
        m.creator = creator ;
        m.permaLink = permaLink ;
        m.attachment = attachment ;

        return m ;
    }

    public String[] getTo ()
    {
        return to ;
    }

    public String getSubject ()
    {
        return title ;
    }

    public String getContent ()
    {
        return content ;
    }

    public String[] getCc ()
    {
        return cc ;
    }

    public String[] getBcc ()
    {
        return bcc ;
    }

    public String getURL ()
    {
        return URL ;
    }

    public void setURL ( String URL )
    {
        this.URL = URL ;
    }

    public Date getDate ()
    {
        return date ;
    }

    public void setBcc ( String[] bcc )
    {
        this.bcc = bcc ;
    }

    public void setCc ( String[] cc )
    {
        this.cc = cc ;
    }

    public void setContent ( String content )
    {
        this.content = content ;
    }

    public void setTo ( String[] to )
    {
        this.to = to ;
    }

    public void setDate ( Date date )
    {
        this.date = date ;
    }

    public void setAttachment ( Attachment[] attachment )
    {
        this.attachment = attachment ;
    }

    public void setSubject ( String subject )
    {
        this.title = subject ;
    }

    public String[] getAuthor ()
    {
        return author ;
    }

    public String getCategory ()
    {
        return category ;
    }

    public String getCreator ()
    {
        return creator ;
    }

    public String getSummary ()
    {
        return summary ;
    }

    public String getGuid ()
    {
        return guid ;
    }

    public boolean isPermaLink ()
    {
        return permaLink ;
    }

    public String getPubDate ()
    {
        return pubDate ;
    }

    public String getPublisher ()
    {
        return publisher ;
    }

    public void setAuthor ( String[] author )
    {
        this.author = author ;
    }

    public void setAuthor ( String string )
    {
        if ( string == null )
        {
            author = null ;
        }
        else
        {
            this.author = new String[]
                          {string} ;
        }
    }

    public void setCategory ( String category )
    {
        this.category = category ;
    }

    public void setSummary ( String summary )
    {
        this.summary = summary ;
    }

    public void setCreator ( String creator )
    {
        this.creator = creator ;
    }

    public void setGuid ( String guid )
    {
        this.guid = guid ;
    }

    public void setPermaLink ( boolean permaLink )
    {
        this.permaLink = permaLink ;
    }

    public void setPubDate ( String pubDate )
    {
        this.pubDate = pubDate ;
    }

    public void setPublisher ( String publisher )
    {
        this.publisher = publisher ;
    }

    public Attachment[] getAttachment ()
    {
        return attachment ;
    }

    private String filter ( String tag , Object value[] )
    {
        if ( value == null )
        {
            return "" ;
        }
        else
        {
            String str = "" ;
            for ( int i = 0 ; i < value.length ; i++ )
            {
                str = str + value[ i ].toString () + "  " ;
            }

            if ( tag != null )
            {
                return tag + ": " + str + "\n" ;
            }
            else
            {
                return str + "\n" ;
            }
        }
    }




    /**
     *
     * @param tag String
     * @param value String
     * @return String
     *
     * @author Jie Bao
     * @version 2003-12-10
     */
    private String filter ( String tag , Object value )
    {
        if ( value == null )
        {
            return "" ;
        }
        else
        {
            if ( tag != null )
            {
                return tag + ": " + value.toString () + "\n" ;
            }
            else
            {
                return value.toString () + "\n" ;
            }
        }
    }

    public String report ( boolean html )
    {
        int len = 0 ;
        String str = len + "\n" ;
        if ( attachment != null )
        {
            len = attachment.length ;
            str = len + "\n" ;
            for ( int i = 0 ; i < attachment.length ; i++ )
            {
                str = str + "  " + attachment[ i ] + "\n" ;
            }
        }
        //        String PermaLink = permaLink?" Permanent Link":" Not Permanent Link" ;
        String LRCF = html ? "<br>" : "" ;
        return
                filter ( "URL" , toLink ( URL ) ) +
                filter ( LRCF + "GUID" , guid ) +
                filter ( LRCF + "Author" , author ) +
                filter ( LRCF + "Creator" , creator ) +
                filter ( LRCF + "To" , to ) +
                filter ( LRCF + "CC" , cc ) +
                filter ( LRCF + "BCC" , bcc ) +
                filter ( LRCF + "Subject" , title ) +
                filter ( LRCF + "Date" , date ) +
                filter ( LRCF + "Publish Date" , pubDate ) +
                filter ( LRCF + "Description" , summary ) +
                filter ( LRCF + "Publisher" , publisher ) +
                filter ( LRCF + "Source" , source ) +
                filter ( LRCF + "Category" , category ) +
                filter ( LRCF + "Content" , content ) +
                LRCF + "\nAttachment: " + str ;
    }

    private String toLink ( String link )
    {
        return "<a href=\""+link+"\">"+link+"</a>";
    }

    public String toString ()
    {
        if ( concise )
        {
            return title ;
        }
        else
        {
            return report ( false ) ;
        }
    }


// for test purpose
    public static void main ( String[] args )
    {

    }

    public String getSource ()
    {
        return source ;
    }

    public void setSource ( String source )
    {
        this.source = source ;
    }

    public boolean after ( Date when )
    {
        if ( when == null || this.date == null )
        {
            return true ;
        }
        else
        {
            return this.date.after ( when ) ;
        }
    }

}




/**
 * sort on date
 *
 */
class DateSorter
        implements Comparator
{
    boolean ascending ;

    DateSorter ( boolean ascending )
    {
        this.ascending = ascending ;
    }

    public int compare ( Object a , Object b )
    {
        Meme v1 = ( Meme ) a ;
        Meme v2 = ( Meme ) b ;
        Object o1 = v1.getDate () ;
        Object o2 = v2.getDate () ;

        // Sort nulls so they appear last, regardless
        // of sort order
        if ( o1 == null && o2 == null )
        {
            return 0 ;
        }
        else if ( o1 == null )
        {
            return 1 ;
        }
        else if ( o2 == null )
        {
            return -1 ;
        }
        else if ( o1 instanceof Comparable )
        {
            if ( ascending )
            {
                return ( ( Comparable ) o1 ).compareTo ( o2 ) ;
            }
            else
            {
                return ( ( Comparable ) o2 ).compareTo ( o1 ) ;
            }
        }
        else
        {
            if ( ascending )
            {
                return o1.toString ().compareTo ( o2.toString () ) ;
            }
            else
            {
                return o2.toString ().compareTo ( o1.toString () ) ;
            }
        }
    }
}
