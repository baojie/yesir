/**
 * $RCSfile: GatewayManager.java,v $
 * $Revision: 1.8 $
 * $Date: 2002/06/21 05:55:59 $
 *
 * Copyright (C) 1999-2002 CoolServlets, Inc. All rights reserved.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.semanticwww.yesir.gateway ;

import java.io.BufferedOutputStream ;
import java.io.File ;
import java.io.FileOutputStream ;
import java.io.IOException ;
import java.io.OutputStream ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.Iterator ;
import java.util.Map ;
import java.util.TimerTask ;

import com.jivesoftware.jdom.Document ;
import com.jivesoftware.jdom.Element ;
import com.jivesoftware.jdom.output.XMLOutputter ;
import com.semanticwww.yesir.utils.TaskEngine ;
import com.semanticwww.yesir.utils.log.BeanUtils ;
import com.semanticwww.yesir.utils.log.XMLProperties ;

/**
 * Manages gateways for a forum, which synchronize the forum with external
 * data sources such as an NNTP newsgroup or email account.
 *
 * Notes: The default GatewayManager implementation stores configuration
 * properties in a file called <tt>gateway_config.xml</tt> under the
 * </tt>Home</tt> directory. The default implementation also only knows how
 * to instantiate Gateway implementations that have either a default constructor
 * or one that accepts a ForumFactory and Forum as arguments.
 *
 * @see Gateway
 */
public class GatewayManager
{

    /**
     * The list of the class names of the gateways that are available for
     * installation by default. These values are automatically added into
     * the <tt>gateways.xml</tt> file when it is first created. Further
     * classes can be defined by editing the <tt>Gateways.gatewayClasses</tt>
     * property.
     */
    public static final String[] DEFAULT_GATEWAY_CLASSES = new String[]
        {} ;

    private static XMLProperties properties = null ;

    private Gateway[] gateways ;

    /**
     * Context for this manager instance in the XML property file.
     */
    private String context = null ;

    /**
     * The scheduled task for gateway imports.
     */
    private TimerTask timerTask = null ;
    private long lastImport ;

    private boolean importEnabled = true ;
    private boolean exportEnabled = true ;
    private String exportFooter = "SemanticWWW.com" ;
    private int importInterval = 15 ;

    public GatewayManager()
    {
        gateways = new Gateway[0] ;
    }

    /**
     * Creates a new GatewayManager. It will load existing values from the
     * persistent store.
     */
    public GatewayManager( String nothing )
    {
        loadProperties() ;
        String name = "forum" ;
        context = name + "." ;

        // load up gateways for this manager.
        String gCount = properties.getProperty( context + "gatewayCount" ) ;
        if( gCount == null )
        {
            gCount = "0" ;
        }
        int gatewayCount = 0 ;
        try
        {
            gatewayCount = Integer.parseInt( gCount ) ;
        }
        catch( NumberFormatException nfe )
        {}

        // See if the time that the last import was performed is stored.
        String importTimestamp = properties.getProperty( context + "lastImport" ) ;
        if( importTimestamp != null )
        {
            try
            {
                lastImport = Long.parseLong( importTimestamp ) ;
            }
            catch( NumberFormatException nfe )
            {
                lastImport = System.currentTimeMillis() ;
            }
        }
        // Not stored, so create new timestamp.
        else
        {
            lastImport = System.currentTimeMillis() ;
        }

        // Load up all gateways
        gateways = new Gateway[gatewayCount] ;

        for( int i = 0 ; i < gatewayCount ; i++ )
        {
            try
            {
                String gatewayContext = context + "gateway" + i + "." ;
                String className = properties.getProperty( gatewayContext +
                    "className" ) ;
                Class gClass = Class.forName( className ) ;
                gateways[i] = ( Gateway ) gClass.newInstance() ;

                // Load import gateway properties.
                String importContext = gatewayContext + "gatewayImporter." ;

                String[] importPropNames = properties.getChildrenProperties(
                    importContext + "properties" ) ;
                Map importGatewayProps = new HashMap() ;
                for( int j = 0 ; j < importPropNames.length ; j++ )
                {
                    // Get the bean property name, which is everything after
                    // the last '.' in the xml property name.
                    importGatewayProps.put( importPropNames[j] ,
                                            properties.getProperty(
                        importContext + "properties." + importPropNames[j] ) ) ;
                }
                // Set properties on the bean
                GatewayImporter gatewayImporter = gateways[i].
                                                  getGatewayImporter() ;
                BeanUtils.setProperties( gatewayImporter , importGatewayProps ) ;

                // Load export gateway properties.
                String exportContext = gatewayContext + "gatewayExporter." ;

                String[] exportPropNames = properties.getChildrenProperties(
                    exportContext + "properties" ) ;
                Map exportGatewayProps = new HashMap() ;
                for( int j = 0 ; j < exportPropNames.length ; j++ )
                {
                    // Get the bean property name, which is everything after
                    // the last '.' in the xml property name.
                    exportGatewayProps.put( exportPropNames[j] ,
                                            properties.getProperty(
                        exportContext + "properties." + exportPropNames[j] ) ) ;
                }
                // Set properties on the bean
                GatewayExporter gatewayExporter = gateways[i].
                                                  getGatewayExporter() ;
                BeanUtils.setProperties( gatewayExporter , exportGatewayProps ) ;
            }
            catch( Exception e )
            {
                System.err.println( "Error loading gateway " + i +
                                    " for context "
                                    + context ) ;
                e.printStackTrace() ;
            }
        }

        // Now load general properties
        try
        {
            String propValue = properties.getProperty( context +
                "importEnabled" ) ;
            if( propValue != null )
            {
                this.importEnabled = Boolean.valueOf( propValue ).booleanValue() ;
            }
            propValue = properties.getProperty( context + "exportEnabled" ) ;
            if( propValue != null )
            {
                this.exportEnabled = Boolean.valueOf( propValue ).booleanValue() ;
            }
            propValue = properties.getProperty( context + "importInterval" ) ;
            if( propValue != null )
            {
                this.setImportInterval( Integer.parseInt( propValue ) ) ;
            }
            exportFooter = properties.getProperty( context + "exportFooter" ) ;
        }
        catch( NumberFormatException nfe )
        {
            /* ignore */
        }

//        this.startImportTask() ;
    }

    /**
     * Returns true if gateway importing is turned on. When importing is on,
     * the importData method of each installed gateway will be invoked at the
     * specified import interval. By default, importing is enabled.
     *
     * @return true if gateway imports are enabled.
     */
    public boolean isImportEnabled()
    {
        return importEnabled ;
    }

    /**
     * Toggles gateway importing on or off. When importing is on,
     * the importData method of each installed gateway will be invoked at the
     * specified import interval. By default, importing is enabled.
     *
     * @param importEnabled true if gateway importing should be enabled.
     */
    public void setImportEnabled( boolean importEnabled )
    {
        this.importEnabled = importEnabled ;
        properties.setProperty( context + "importEnabled" , "" + importEnabled ) ;
        this.startImportTask() ;
    }

    /**
     * Returns true if gateway exporting is turned on. When exporting is on,
     * the exportData method of each installed gateway will be invoked on each
     * new message that is created in the forum. By default, exporting is
     * enabled.
     *
     * @return true if gateway exports are enabled.
     */
    public boolean isExportEnabled()
    {
        return exportEnabled ;
    }

    /**
     * Toggles gateway exporting on or off. When exporting is on,
     * the exportData method of each installed gateway will be invoked on each
     * new message that is created in the forum. By default, exporting is
     * enabled.
     *
     * @param exportEnabled true if gateway exporting should be enabled.
     */
    public void setExportEnabled( boolean exportEnabled )
    {
        this.exportEnabled = exportEnabled ;
        properties.setProperty( context + "exportEnabled" , "" + exportEnabled ) ;
        saveGateways() ;
    }

    /**
     * Returns the footer that will be appended to messages as they're exported.
     * By default, this value is <tt>null</tt>, which means that no footer will
     * be used.<p>
     *
     * A number of tokens can be inserted into the filter. Each token will be
     * dynamically replaced as the message is sent out with the real value.
     * Valid tokens are:<ul>
     *      {threadID}, {threadName}, {forumID}, {forumName}, {messageID}
     * </ul>
     *
     * @return the footer that will be appended to messages being exported.
     */
    public String getExportFooter()
    {
        return exportFooter ;
    }

    /**
     * Sets the footer that will be appended to messages as they're exported.
     * By default, this value is <tt>null</tt>, which means that no footer will
     * be used.<p>
     *
     * A number of tokens can be inserted into the filter. Each token will be
     * dynamically replaced as the message is sent out with the real value.
     * Valid tokens are:<ul>
     *      {threadID}, {threadName}, {forumID}, {forumName}, {messageID}
     * </ul>
     *
     * @param exportFooter the footer that will be appended to messages being
     *      exported.
     */
    public void setExportFooter( String exportFooter )
    {
        this.exportFooter = exportFooter ;
        // If we are setting the value to null, we should delete the property.
        if( exportFooter == null )
        {
            properties.deleteProperty( context + "exportFooter" ) ;
        }
        // Otherwise, set the property with the new value.
        else
        {
            properties.setProperty( context + "exportFooter" , exportFooter ) ;
        }
    }

    /**
     * Returns the number of minutes that the manager waits between each
     * gateway import. Default is 15 minutes.
     *
     * @return the number of minutes between automatic index updates.
     */
    public int getImportInterval()
    {
        return importInterval ;
    }

    /**
     * Sets the number of minutes that the manager waits between each
     * gateway import. Default is 15 minutes.
     */
    public void setImportInterval( int importInterval )
    {
        this.importInterval = importInterval ;
        properties.setProperty( context + "importInterval" ,
                                "" + importInterval ) ;
    }

    /**
     * Returns the Gateway at the specified index.
     *
     * @return the filter at the specified index.
     */
    public Gateway getGateway( int index )
    {
        if( index < 0 || index > gateways.length - 1 )
        {
            throw new IllegalArgumentException( "Index " + index +
                                                " is not valid." ) ;
        }
        return gateways[index] ;
    }

    /**
     * Returns the count of currently active gateways for the forum.
     *
     * @return a count of the currently active filters.
     */
    public int getGatewayCount()
    {
        return gateways.length ;
    }

    /**
     * Adds a new Gateway to the end of the gateway list.
     *
     * @param gateway Gateway to add to the gateway list.
     */
    public void addGateway( Gateway gateway )
    {
        addGateway( gateway , gateways.length ) ;
    }

    /**
     * Inserts a new Gateway at specified index in the gateway list.
     *
     * @param gateway Gateway to add to the gateway list.
     * @param index position in gateway list to insert new gateway.
     */
    public void addGateway( Gateway gateway , int index )
    {
        ArrayList newGateways = new ArrayList( gateways.length + 1 ) ;
        for( int i = 0 ; i < gateways.length ; i++ )
        {
            newGateways.add( gateways[i] ) ;
        }
        newGateways.add( index , gateway ) ;
        Gateway[] newArray = new Gateway[newGateways.size()] ;
        for( int i = 0 ; i < newArray.length ; i++ )
        {
            newArray[i] = ( Gateway ) newGateways.get( i ) ;
        }
        // Finally, overwrite gateways with the new array
        gateways = newArray ;
//        saveGateways() ; //Jie Bao comment off 2003-12-08
    }

    /**
     * Removes a Gateway at the specified index in the gateway list.
     *
     * @param index position in gateway list to remove gateway from.
     */
    public void removeGateway( int index )
    {
        ArrayList newGateways = new ArrayList( gateways.length ) ;
        for( int i = 0 ; i < gateways.length ; i++ )
        {
            newGateways.add( gateways[i] ) ;
        }
        newGateways.remove( index ) ;
        Gateway[] newArray = new Gateway[newGateways.size()] ;
        for( int i = 0 ; i < newArray.length ; i++ )
        {
            newArray[i] = ( Gateway ) newGateways.get( i ) ;
        }
        //Finally, overwrite gateways with the new array
        gateways = newArray ;
        saveGateways() ;
    }

    /**
     * removeGateway
     *
     * @param gate Gateway
     *
     * @author Jie Bao
     * @version 2003-12-08
     */
    public void removeGateway( Gateway gate )
    {
        ArrayList newGateways = new ArrayList( gateways.length ) ;
        for( int i = 0 ; i < gateways.length ; i++ )
        {
            newGateways.add( gateways[i] ) ;
        }
        int index = newGateways.indexOf( gate ) ;
        if( index != -1 )
        {
            try
            {
                gate.stop() ;
                newGateways.remove( index ) ;
            }
            catch( GatewayException ex )
            {
            }
        }
        else
        {
            return ;
        }

        Gateway[] newArray = new Gateway[newGateways.size()] ;
        for( int i = 0 ; i < newArray.length ; i++ )
        {
            newArray[i] = ( Gateway ) newGateways.get( i ) ;
        }

        //Finally, overwrite gateways with the new array
        gateways = newArray ;
//        saveGateways() ; // Jie Bao comments off 2003-12-10
    }

    /**
     * Saves all Gateways to the persistent store. This method
     * should be called after setting any properties on individual gateways
     * that are being managed by this gateway manager.
     */
    public void saveGateways()
    {
        saveGateways( true ) ;
    }

    /**
     * Saves all Gateways to the persistent store. This method
     * should be called after setting any properties on individual gateways
     * that are being managed by this gateway manager.
     */
    public synchronized void saveGateways( boolean restartImports )
    {
        // Delete all properties.
        properties.deleteProperty( context.substring( 0 , context.length() - 1 ) ) ;

        // Write out basic props.
        properties.setProperty( context + "importEnabled" , "" + importEnabled ) ;
        properties.setProperty( context + "exportEnabled" , "" + exportEnabled ) ;
        properties.setProperty( context + "importInterval" ,
                                "" + importInterval ) ;
        if( exportFooter != null )
        {
            properties.setProperty( context + "exportFooter" , exportFooter ) ;
        }

        // Write out all data. First, the count.
        if( gateways.length > 0 )
        {
            properties.setProperty( context + "gatewayCount" ,
                                    Integer.toString( gateways.length ) ) ;

            // Next, the lastImport date
            if( importEnabled )
            {
                properties.setProperty( context + "lastImport" ,
                                        String.valueOf( lastImport ) ) ;
            }
        }

        // Each gateway...
        for( int i = 0 ; i < gateways.length ; i++ )
        {
            String gatewayContext = context + "gateway" + i + "." ;
            // Write out class name.
            properties.setProperty( gatewayContext + "className" ,
                                    gateways[i].getClass().getName() ) ;

            // Write out all importer properties.
            GatewayImporter gatewayImporter = gateways[i].getGatewayImporter() ;
            Map importGatewayProps = BeanUtils.getProperties( gatewayImporter ) ;
            String importContext = gatewayContext + "gatewayImporter." ;
            for( Iterator iter = importGatewayProps.keySet().iterator() ;
                                 iter.hasNext() ; )
            {
                String name = ( String ) iter.next() ;
                String value = ( String ) importGatewayProps.get( name ) ;
                properties.setProperty( importContext + "properties." + name ,
                                        value ) ;
            }

            // Write out all exporter properties.
            GatewayExporter gatewayExporter = gateways[i].getGatewayExporter() ;
            Map exportGatewayProps = BeanUtils.getProperties( gatewayExporter ) ;
            String exportContext = gatewayContext + "gatewayExporter." ;
            for( Iterator iter = exportGatewayProps.keySet().iterator() ;
                                 iter.hasNext() ; )
            {
                String name = ( String ) iter.next() ;
                String value = ( String ) exportGatewayProps.get( name ) ;
                properties.setProperty( exportContext + "properties." + name ,
                                        value ) ;
            }
        }

        // Since the gateway information likely changed, restart the import task.
        if( restartImports )
        {
            this.startImportTask() ;
        }
    }

    /**
     * Exports an individual message through all gateways. This method is
     * only intended to be called by other internal Jive classes.
     */
    public void exportData( Meme message[] )
    {
        if( gateways.length == 0 || exportEnabled == false )
        {
            return ;
        }
        TaskEngine.addTask( TaskEngine.LOW_PRIORITY ,
                            new GatewayExportTask( message ) ) ;
    }

    /**
     * Deletes all gateways for this context by removing their properties and
     * stopping them from running.
     */
    public void delete()
    {
        // Remove all properties.
        properties.deleteProperty( context.substring( 0 , context.length() - 1 ) ) ;
        // Stop the import task
        if( timerTask != null )
        {
            timerTask.cancel() ;
        }
    }

    /**
     * Start/restart the gateway import task
     */
    private void startImportTask()
    {
        // if we've just disabled importing, disable the import task
        if( !importEnabled && timerTask != null )
        {
            timerTask.cancel() ;
            return ;
        }
        else if( !importEnabled )
        {
            return ;
        }

        // Restart timer with new schedule.
        if( timerTask != null )
        {
            timerTask.cancel() ;
        }
        timerTask = TaskEngine.scheduleTask(
            new GatewayImportTask() , importInterval * 60 * 1000 ,
            importInterval * 60 * 1000 ) ;
    }

    /**
     * Loads a property manager for gateway persistence if it isn't already
     * loaded. If an XML file for the gateways isn't already created, it will
     * attempt to make a file with default value.
     */
    private static void loadProperties()
    {
        if( properties == null )
        {
            String gatewayXML = "gateways.xml" ;
            // Make sure the file actually exists. If it doesn't, a new file
            // will be created.
            File file = new File( gatewayXML ) ;
            // If it doesn't exists we have to create it.
            if( !file.exists() )
            {
                Document doc = new Document( new Element( "Gateways" ) ) ;
                Element gatewayClasses = new Element( "gatewayClasses" ) ;
                doc.getRootElement().addContent( gatewayClasses ) ;
                // Add in default gateway classes.
                for( int i = 0 ; i < DEFAULT_GATEWAY_CLASSES.length ; i++ )
                {
                    Element newClass = new Element( "gateway" + i ) ;
                    newClass.setText( DEFAULT_GATEWAY_CLASSES[i] ) ;
                    gatewayClasses.addContent( newClass ) ;
                }
                // Now, write out to the file.
                OutputStream out = null ;
                try
                {
                    // Use JDOM's XMLOutputter to do the writing and formatting.
                    XMLOutputter outputter = new XMLOutputter( "    " , true ) ;
                    out = new BufferedOutputStream( new FileOutputStream( file ) ) ;
                    outputter.output( doc , out ) ;
                }
                catch( Exception e )
                {
                    e.printStackTrace() ;
                }
                finally
                {
                    try
                    {
                        out.close() ;
                    }
                    catch( Exception e )
                    {}
                }
            }
            // Finally, create xml properties with the file.
            try
            {
                properties = new XMLProperties( gatewayXML ) ;
            }
            catch( IOException ioe )
            {
                ioe.printStackTrace() ;
            }
        }
    }

    public Gateway[] getGateways()
    {
        return gateways ;
    }

    /**
     * A task that performs an import for all installed gateways.
     */
    private class GatewayImportTask
        implements Runnable
    {

        public void run()
        {
            // If importing is turned off, return.
            if( !importEnabled || gateways.length < 1 )
            {
                return ;
            }

            long now = System.currentTimeMillis() ;
            // Set the cutoff date for imports to be 24 hours before
            // the last import. This will give us a good buffer since
            // messages can get delayed in transport or other mail
            // servers can have their time set wrong. It's still
            // possible that we'll loose some messages even with a 24
            // hour buffer, but this occurrence should be quite rare.
            Date cutoffDate = new Date( lastImport - 60 * 60 * 100 * 24 ) ;
            for( int i = 0 ; i < gateways.length ; i++ )
            {
                try
                {
                    gateways[i].importData( cutoffDate ) ;
                }
                catch( GatewayException ge )
                {
                    ge.printStackTrace() ;
                }
            }
            // Done with the import so set the new import timestamp.
            lastImport = now ;
            // Save the timestamp.
            properties.setProperty( context + "lastImport" ,
                                    String.valueOf( lastImport ) ) ;
        }
    }

    /**
     * A task that exports a message through all installed gateways.
     */
    private class GatewayExportTask
        implements Runnable
    {

        Meme message[] ;

        GatewayExportTask( Meme message[] )
        {
            this.message = message ;
        }

        public void run()
        {
            // If exporting is turned off, return.
            if( !exportEnabled )
            {
                return ;
            }

            for( int i = 0 ; i < gateways.length ; i++ )
            {
                try
                {
                    gateways[i].setMeme( message ) ;
                    gateways[i].exportData() ;
                }
                catch( GatewayException ge )
                {
                    ge.printStackTrace() ;
                }
            }
        }
    }
}
