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

import java.io.File ;
import java.io.FileInputStream ;
import java.io.FileNotFoundException ;
import java.io.IOException ;
import java.io.InputStream ;
import java.io.InputStreamReader ;
import java.io.StringReader ;
import java.net.MalformedURLException ;
import java.net.URL ;
import java.net.URLConnection ;
import java.nio.charset.Charset ;
import java.security.Security ;

import org.jdom.Document ;
import org.jdom.JDOMException ;
import org.jdom.input.SAXBuilder ;
import org.xml.sax.EntityResolver ;
import org.xml.sax.InputSource ;
import org.xml.sax.SAXException ;
import sun.misc.BASE64Encoder ;

/**
 * This class initializes the parser with
 * a InputStream from the RSSUrl
 *
 * @author  <a href="mailto:bpasero@users.sourceforge.net">Benjamin Pasero</a>
 * @version 0.63b
 */
public class RSSXMLFactory
{

    private String rssUrl ;
    private RSSXMLParser parser ;
    private RSSChannel rssChannel ;
    private String proxyUsername ;
    private String proxyPassword ;
    private Document document ;

    /** BASE64 encoded auth value */
    private String baseAuthValue ;

    /** TRUE if the BASE64 auth is element of the URL */
    private boolean authInURL ;

    /** TRUE if https is used as protocol in the URL */
    private boolean httpsEnabled ;

    /** TRUE if a proxy should be used for the favorite */
    private boolean useProxy ;

    /** TRUE if BASE64 auth should be used for the favorite */
    private boolean useAuth ;

    /**
     * Create a new parser for the given URL to the RSS file
     * @param rssUrl URL or Path to the RSS XML
     * @throws RSSOwlXMLFactoryException
     */
    public RSSXMLFactory( String rssUrl )
        throws RSSXMLFactoryException
    {
        this.rssUrl = rssUrl ;
        useProxy = false ;
        useAuth = false ;
        authInURL = false ;
        httpsEnabled = false ;
        initXmlDocument() ;
        initXMLParser( document ) ;
        rssChannel = parser.getRSSChannel() ;
    }

    /**
     * Use this constructor to get a InputStream for a XML
     * that is not a RSS / RDF XML (like user.xml)
     *
     * @param rssUrl URL / Path to the XML
     * @param ignoreRSS Param indicates that its not intended
     * to retrieve a RSS / RDF XML InputStream
     */
    public RSSXMLFactory( String rssUrl , boolean ignoreRSS )
        throws RSSXMLFactoryException
    {
        if( ignoreRSS )
        {
            this.rssUrl = rssUrl ;
            useProxy = false ;
            useAuth = false ;
            authInURL = false ;
            httpsEnabled = false ;
            initXmlDocument() ;
        }
    }

    public static String charEncoding = "UTF8" ;

    /**
     * Init a new XML Parser from the RSS XML
     * @throws RSSOwlXMLFactoryException
     */
    private void initXmlDocument()
        throws RSSXMLFactoryException
    {

        /** Build Parser */
        SAXBuilder builder = new SAXBuilder( false ) ;
        document = null ;

        /** Ignore DTD */
        setIgnoreDTD( builder ) ;

        /** User chose a local file */
        if( new File( rssUrl ).exists() )
        {
            document = openRSSFromLocal( builder , document ) ;
        }

        /** User chose a file in the internet */
        else
        {
            try
            {

                /** Parse RSS from the URL */
                if( Charset.isSupported( charEncoding ) )
                {
                    document = builder.build( new InputStreamReader(
                        getUrlInputStream() , charEncoding ) ) ;
                }
                else
                {
                    document = builder.build( new InputStreamReader(
                        getUrlInputStream() ) ) ;

                }
            }
            catch( MalformedURLException e )
            {
                /** Not a valid URL */
                throw new RSSXMLFactoryException( ( "ERROR_INVALID_URL" ) +
                                                  "\n" + ( "ERROR_SOURCE" ) +
                                                  ": " + e.getMessage() + "\n" +
                                                  ( "TIP_PROTOCOL" ) ) ;
            }
            catch( JDOMException e )
            {
                /** Not a valid XML */
                throw new RSSXMLFactoryException( ( "ERROR_NOT_A_XML" ) + "\n" +
                                                  ( "ERROR_SOURCE" ) + ": " +
                                                  e.getMessage() ) ;
            }
            catch( IOException e )
            {
                throw new RSSXMLFactoryException( ( "ERROR_XML_NOT_FOUND" ) +
                                                  "\n" + ( "ERROR_SOURCE" ) +
                                                  ": " + e.getMessage() ) ;
            }
        }
    }

    /**
     * Initialize the RSSOwlXMLParser with the given Document
     * @param document The RSS / RDF Document
     * @throws RSSXMLFactoryException
     */
    private void initXMLParser( Document document )
        throws RSSXMLFactoryException
    {

        /** XML successfully retrieved and parsed */
        if( document != null )
        {

            /** Check if this is a RSS / RDF XML */
            if( !document.getRootElement().getName().equalsIgnoreCase( "rdf" ) &&
                !document.getRootElement().getName().equalsIgnoreCase( "rss" ) )
            {
                throw new RSSXMLFactoryException( ( "ERROR_NOT_A_RSS" ) + "\n" +
                                                  ( "ERROR_SOURCE" ) + ": " +
                                                  ( "ERROR_DOCUMENT_IS_UNKNOWN" ) ) ;
            }

            /** Create parser */
            parser = new RSSXMLParser( document ) ;

            /** Not a valid location / Authentification required */
        }
        else
        {
            throw new RSSXMLFactoryException( ( "ERROR_XML_NOT_FOUND" ) + "\n" +
                                              ( "ERROR_SOURCE" ) + ": " +
                                              ( "ERROR_DOCUMENT_NULL" ) ) ;
        }
    }

    /**
     * Parse RSS from the URL. This method considers
     * https-connects, BASE Auth connects and connects
     * via a proxy server.
     *
     * @return InputStream of the RSS XML
     * @throws JDOMException
     * @throws IOException
     */
    public InputStream getUrlInputStream()
        throws JDOMException , IOException , MalformedURLException
    {

        /** Enable https-connect if URL is "https://..." */
        checkHttpsInUrl() ;

        URLConnection urlConnection = new URL( rssUrl ).openConnection() ;

        /** Check if the user had typed in a URL like "http://user:pass@domain.de" */
        checkAuthInUrl() ;

        /** URL needs auth. Post encoded username / password to server */
        if( useAuth )
        {
            urlConnection.setDoInput( true ) ;
            urlConnection.setRequestProperty( "Authorization" , baseAuthValue ) ;
        }

        /** Connect via proxyserver */
        if( useProxy )
        {
            if( !proxyUsername.equals( "" ) || !proxyPassword.equals( "" ) )
            {
                String encoded = new BASE64Encoder().encode( new String(
                    proxyUsername + ":" + proxyPassword ).getBytes() ) ;
                urlConnection.setRequestProperty( "Proxy-Authorization" ,
                                                  "Basic " + encoded ) ;
            }
        }
        return urlConnection.getInputStream() ;
    }

    /**
     * Check if the user had typed in a URL like "http://user:pass@domain.de"
     * @throws MalformedURLException
     */
    private void checkAuthInUrl()
        throws MalformedURLException
    {
        if( new URL( rssUrl ).getUserInfo() != null )
        {
            useAuth = true ;
            baseAuthValue = userNamePasswordBase64( new URL( rssUrl ).
                getUserInfo() ) ;
            authInURL = true ;
        }
    }

    /**
     * Return BASE64 encoded username:password
     * @param userNamePassword
     * @return BASE64 encoded username:password
     */
    public static String userNamePasswordBase64( String userNamePassword )
    {

        if( userNamePassword != null && !userNamePassword.equals( "" ) &&
            !userNamePassword.equals( ":" ) )
        {
            String encs = new sun.misc.BASE64Encoder().encode( userNamePassword.
                getBytes() ) ;
            return "Basic " + encs ;
        }
        else
        {
            return null ;
        }
    }

    /** Add new Security Provider to support connects to "https://" */
    private void checkHttpsInUrl()
    {

        /** This is not possible on JDK below version 1.2 */
        if( rssUrl.indexOf( "https" ) >= 0 && !httpsEnabled &&
            1.2 <=
            new Double( System.getProperty( "java.version" ).substring( 0 , 3 ) ).
            doubleValue() )
        {
            httpsEnabled = true ;
            System.setProperty( "java.protocol.handler.pkgs" ,
                                "com.sun.net.ssl.internal.www.protocol" ) ;
            Security.addProvider( new com.sun.net.ssl.internal.ssl.Provider() ) ;
        }
    }

    /**
     * Trys to open the RSS from the local path
     * @param builder
     * @param document
     * @return The parsed Document
     * @throws RSSXMLFactoryException
     */
    private Document openRSSFromLocal( SAXBuilder builder , Document document )
        throws RSSXMLFactoryException
    {
        try
        {
            document = builder.build( rssUrl ) ;
        }
        catch( JDOMException e )
        {
            /** File is not a XML */
            throw new RSSXMLFactoryException( ( "ERROR_NOT_A_XML" ) + "\n" +
                                              ( "ERROR_SOURCE" ) + ": " +
                                              e.getMessage() ) ;
        }
        catch( IOException e )
        {

            /** File not found */
            throw new RSSXMLFactoryException( ( "ERROR_XML_NOT_FOUND" ) + "\n" +
                                              ( "ERROR_SOURCE" ) + ": " +
                                              e.getMessage() ) ;
        }
        return document ;
    }

    /**
     * Set builder to ignore the DTD in the XML. This speeds up
     * loading / parsing of the rss xml.
     *
     * @param builder
     */
    public static void setIgnoreDTD( SAXBuilder builder )
    {
        builder.setEntityResolver( new EntityResolver()
        {
            public InputSource resolveEntity( String publicId , String systemId )
                throws SAXException , IOException
            {
                return new InputSource( new StringReader( "" ) ) ;
            }
        } ) ;
    }

    /**
     * Get the generated RSSChannel
     * @return Generated RSSChannel from the RSS XML
     */
    public RSSChannel getRssChannel()
    {
        return rssChannel ;
    }

    /**
     * Method to get the contents of rss xml as InputStream
     * @param rssUrl Url / Path to the RSS XML
     * @return InputStream of the rss xml
     */
    public static InputStream getXMLStream( String rssUrl )
    {

        /** RSS XML is located locally */
        if( new File( rssUrl ).exists() )
        {

            try
            {
                return new FileInputStream( new File( rssUrl ) ) ;
            }
            catch( FileNotFoundException e )
            {
                // RSSOwlGUI.logger.log("getRssXMLStream()", e);
            }
        }

        /** RSS XML is located in the internet */
        try
        {
            return new RSSXMLFactory( rssUrl , true ).getUrlInputStream() ;
        }
        catch( JDOMException e )
        {
            /** Exception was already catched in initXmlParser() */
            return null ;
        }
        catch( IOException e )
        {
            /** Exception was already catched initXmlParser() */
            return null ;
        }
        catch( RSSXMLFactoryException e )
        {
            /** Exception was already catched initXmlParser() */
            return null ;
        }
    }

    /**
     * Get the XML Document
     * @return Document
     */
    public Document getDocument()
    {
        return document ;
    }
}
