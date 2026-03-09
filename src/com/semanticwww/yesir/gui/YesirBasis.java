package com.semanticwww.yesir.gui ;

import java.util.Vector ;

import javax.swing.JOptionPane ;

import com.semanticwww.yesir.gateway.Gateway ;
import com.semanticwww.yesir.gateway.GatewayExporter ;
import com.semanticwww.yesir.gateway.GatewayImporter ;
import com.semanticwww.yesir.gateway.GatewayManager ;

/**
 * <p>Title: </p>
 * <p>Description: Class to handle GUI insensitive information</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */
abstract public class YesirBasis
{
    protected GatewayManager gates = new GatewayManager() ;
    protected Vector importers = new Vector() ;
    protected Vector exporters = new Vector() ;

    /**
     * register all available importers and exporters
     * 2003-12-10
     */
    public void preregisteredImporterExporter()
    {
        importers.add( "com.semanticwww.yesir.io.rss.RSSImporter" ) ;
        importers.add( "com.semanticwww.yesir.io.mail.MailFileImporter" ) ;
        importers.add( "com.semanticwww.yesir.io.mail.Pop3Importer" ) ;
        importers.add( "com.semanticwww.yesir.io.news.NewsImporter" ) ;
        importers.add( "com.semanticwww.yesir.io.search.GoogleImporter" ) ;

        exporters.add( "com.semanticwww.yesir.io.blank.BlankExporter" ) ;
        exporters.add( "com.semanticwww.yesir.io.console.ConsoleExporter" ) ;
        exporters.add( "com.semanticwww.yesir.io.bbs.LB5000Exporter" ) ;
        exporters.add( "com.semanticwww.yesir.io.rss.RSSExporter" ) ;
        exporters.add( "com.semanticwww.yesir.io.mail.SmtpEporter" ) ;
        
    }

    /**
     *
     * @param className String
     * @param isImporter boolean
     *
     * 2003-12-10
     */
    public void registerNewImporterExporter( String className ,
                                             boolean isImporter )
    {
        Object obj ;
        try
        {
            ClassLoader loader = ClassLoader.getSystemClassLoader() ;
            obj = loader.loadClass( className ).newInstance() ;
        }
        catch( Exception ex )
        {
            ex.printStackTrace() ;
            JOptionPane.showMessageDialog( null ,
                                           "Class not found: " + className ,
                                           "Fatal Error" ,
                                           JOptionPane.WARNING_MESSAGE ) ;

            return ;
        }

        // class can be found
        if( isImporter )
        {
            if( obj instanceof GatewayImporter )
            {
                importers.add( className ) ;
            }
            else
            {
                JOptionPane.showMessageDialog( null ,
                                               "Class is not an importer: " +
                                               className ,
                                               "Fatal Error" ,
                                               JOptionPane.WARNING_MESSAGE ) ;
            }
        }
        else
        {
            if( obj instanceof GatewayExporter )
            {
                exporters.add( className ) ;
            }
            else
            {
                JOptionPane.showMessageDialog( null ,
                                               "Class is not an exporter: " +
                                               className ,
                                               "Fatal Error" ,
                                               JOptionPane.WARNING_MESSAGE ) ;
            }
        }
    }

    public Gateway addGateway( Gateway gate )
    {
        gates.addGateway( gate ) ;
        return gate ;
    }

    public Gateway addGateway( String name ,
                               GatewayImporter importer ,
                               GatewayExporter exporter ,
                               int repeatTime ,
                               int timerFreq ,
                               short mode )
    {
        Gateway gate = new Gateway( name , importer , exporter , repeatTime ,
                                    timerFreq , mode ) ;
        gates.addGateway( gate ) ;
        return gate ;
    }

    public void removeGateway( Gateway gate )
    {
        gates.removeGateway( gate ) ;
    }

}
