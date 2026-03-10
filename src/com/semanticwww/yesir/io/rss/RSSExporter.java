package com.semanticwww.yesir.io.rss ;

import java.io.File ;
import java.io.FileNotFoundException ;
import java.io.FileWriter ;
import java.io.IOException ;
import java.io.PrintWriter ;

import javax.swing.JFrame ;
import javax.swing.JScrollPane ;
import javax.swing.JTextArea ;

import com.semanticwww.yesir.gateway.DefaultGatewayExporter ;
import com.semanticwww.yesir.gateway.GatewayException ;
import com.semanticwww.yesir.gateway.Meme ;


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
public class RSSExporter
    extends DefaultGatewayExporter
{
    String rssFilePath ;

    public String getRssFilePath()
    {
        return rssFilePath ;
    }

    public void setRssFilePath( String rssFilePath )
    {
        this.rssFilePath = rssFilePath ;
    }

    public void jbInit( String name , String rssFilePath )
    {
        setName( name ) ;
        this.rssFilePath = rssFilePath ;
    }

    /**
     * exportData
     *
     * @param message Meme
     */
    public void exportData( Meme message )
    {
        Meme[] messages = new Meme[1] ;
        messages[0] = message ;

        try
        {
            RSSGenerator g = new RSSGenerator( messages ) ;
            String rss = g.generate() ;
            writeFile( new File( rssFilePath ) , rss ) ;
            showSource( rss ) ;
        }
        catch( IOException ex )
        {
        }
        catch( SecurityException ex )
        {
        }
        catch( GatewayException ex )
        {
        }

    }

    /**
     * exportData
     *
     * @param messages Meme[]
     */
    public void exportData( Meme[] messages )
        throws GatewayException
    {
        try
        {
            RSSGenerator g = new RSSGenerator( messages ) ;
            String rss = g.generate() ;
            writeFile( new File( rssFilePath ) , rss ) ;
            showSource( rss ) ;
        }
        catch( IOException ex )
        {
        }
        catch( SecurityException ex )
        {
        }
        catch( GatewayException ex )
        {
        }
    }

    /**
     * GUISetup
     */
    public boolean GUISetup()
    {
        RSSExporterSetupDlg dlg = new RSSExporterSetupDlg(
            null , "RSSExporter Setup" ,
            getName() , rssFilePath
            ) ;
        dlg.show() ;

        if( dlg.getAction() == dlg.OK )
        {
            jbInit( dlg.getName() , dlg.getRSSFilePath() ) ;
            return true ;
        }
        else
        {
            return false ;
        }
    }

    /**
     * Writes a file with given contents.
     * @param file the file to write.
     * @param contents the text to write.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the security manager forbids this
               operation.
     * @throws IOException if any other I/O error occurs.
     */
    public static void writeFile( File file , String contents )
        throws FileNotFoundException ,
        SecurityException ,
        IOException
    {
        if( ( file != null )
            && ( contents != null ) )
        {
            FileWriter t_fwWriter = new FileWriter( file ) ;
            PrintWriter t_pwWriter = new PrintWriter( t_fwWriter ) ;
            t_pwWriter.println( contents ) ;

            t_pwWriter.close() ;
            t_fwWriter.close() ;
        }

    }

    // 2003-12-10
    public void showSource( String contents )
    {
        JFrame frame = new JFrame() ;
        JTextArea text = new JTextArea( contents ) ;
        frame.getContentPane().add( new JScrollPane( text ) ) ;

        frame.setSize( 400 , 300 ) ;
        frame.show() ;
    }

}
