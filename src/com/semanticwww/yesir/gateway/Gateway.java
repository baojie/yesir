/**
 */

package com.semanticwww.yesir.gateway ;

import java.util.Date ;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import javax.swing.Timer ;

import com.semanticwww.yesir.utils.Debug ;

/**
 * A Gateway synchronizes a forum and an external data source such as a NNTP
 * newsgroup, or mailing list (through a POP3 account and SMTP). Each gateway must
 * know how to import data from the external source as new messages that are
 * added to an existing forum, and how to export individual messages to the
 * source.<p>
 *
 * The GatewayManager class manages Gateways and is responsible for periodically
 * calling the import and export methods (as specified by the end user).
 *
 * @see GatewayManager
 */

/**
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */

public class Gateway
{
    // constants
    public static short FOREVER = -1 ;
    public static short ONCE = 1 ;

    public static short AUTO = 1 ; // automatic export with import
    public static short MANUAL = 0 ; // manual export

    private String name ;
    private GatewayImporter importer ;
    private GatewayExporter exporter ;

    // timer parameters
    private Timer mytimer = null ;
    private int repeatTime = this.FOREVER ; // -1 means forever
    private int executedTime = 0 ;
    private int timerFreq = 0 ;

    private short mode = MANUAL ;

    private Meme[] meme = new Meme[0] ;

    public int getMemeCount()
    {
        if( meme == null )
        {
            return 0 ;
        }
        else
        {
            return meme.length ;
        }
    }

    /**
     * Gateway
     *
     * @param importer GatewayImporter
     * @param exporter GatewayExporter
     * @param repeatTime int
     * @param timerFreq int in Minutes
     */
    public Gateway( String name , GatewayImporter importer ,
                    GatewayExporter exporter ,
                    int repeatTime , int timerFreq , short mode )
    {
        this.name = name ;
        this.importer = importer ;
        this.exporter = exporter ;
        this.repeatTime = repeatTime ;
        this.timerFreq = timerFreq ;
        this.mode = mode ;
    }

    public void updateProperties( String name ,
                                  int repeatTime , int timerFreq , short mode )
    {
        this.name = name ;
        this.repeatTime = repeatTime ;
        this.timerFreq = timerFreq ;
        this.mode = mode ;
    }

    // Set the cutoff date for imports to be 24 hours before
    // the last import. This will give us a good buffer since
    // messages can get delayed in transport or other mail
    // servers can have their time set wrong. It's still
    // possible that we'll loose some messages even with a 24
    // hour buffer, but this occurrence should be quite rare.
    public void importData( Date cutoff )
        throws GatewayException
    {
        meme = importer.importData( cutoff ) ;
    }

    public void exportData()
        throws GatewayException
    {
        exporter.exportData( meme ) ;
    }

    /**
     * Returns the GatewayImporter associated with this gateway.
     *
     * @return the importer.
     */
    public GatewayImporter getGatewayImporter()
    {
        return importer ;
    }

    /**
     * Returns the GatewayExporter associated with this gateway.
     *
     * @return the exporter.
     */
    public GatewayExporter getGatewayExporter()
    {
        return exporter ;
    }

    public void run()
        throws GatewayException
    {
//        Debug.trace("run");
        try
        {
            if( ( executedTime >= repeatTime ) && ( repeatTime != this.FOREVER ) )
            {
                stop() ;
                throw new GatewayException( "Reach gatway's run time limit (" +
                                            repeatTime + ")" ) ;
            }
            else
            {
                importData( null ) ;
                if( mode == Gateway.AUTO )
                {
                    exportData() ;
                }
                executedTime++ ;
            }
        }
        catch( GatewayException ex )
        {
            ex.printStackTrace() ;
            throw new GatewayException( "Reach gatway's run time limit (" +
                                        repeatTime + ")" ) ;
        }
    }

    public void start()
    {
        executedTime = 0 ;
        try
        {
            run() ;
            if( repeatTime > this.ONCE )
            {
                mytimer = new Timer( timerFreq * 60 * 1000 , new ActionListener()
                {
                    public void actionPerformed( ActionEvent evt )
                    {
                        try
                        {
                            run() ;
                        }
                        catch( Exception ex )
                        {
                            ex.printStackTrace() ;
                        }
                    }
                } ) ;
                mytimer.start() ;
            }
        }
        catch( Exception ex )
        {
            ex.printStackTrace() ;
        }

    }

    /**
     * restart
     */
    public void restart()
    {
        executedTime = 0 ;
        mytimer.restart() ;
    }

    /**
     * 2003-11-27
     */
    public void resume()
    {
        if( !mytimer.isRunning() )
        {
            mytimer.start() ;
        }
    }

    /**
     * Stops any currently running imports and exports.
     *
     * @throws GatewayException
     */
    public void stop()
        throws GatewayException
    {
        if( mytimer == null )
        {
            return ;
        }
        if( mytimer.isRunning() )
        {
            mytimer.stop() ;
        }
        importer.stop() ;
        exporter.stop() ;
    }

    public void finalize()
    {
        Debug.trace( "finished" ) ;
        if( mytimer.isRunning() )
        {
            mytimer.stop() ;
        }
    }

    public String getName()
    {
        return name ;
    }

    public void setName( String name )
    {
        this.name = name ;
    }

    public Meme[] getMeme()
    {
        return meme ;
    }

    public String toString()
    {
        return name + "(" + meme.length + " items)" ;
    }

    public short getMode()
    {
        return mode ;
    }

    public void setMode( short mode )
    {
        this.mode = mode ;
    }

    public int getRepeatTime()
    {
        return repeatTime ;
    }

    public int getTimerFreq()
    {
        return timerFreq ;
    }

    public int getExecutedTime()
    {
        return executedTime ;
    }

    public void setRepeatTime( int repeatTime )
    {
        this.repeatTime = repeatTime ;
    }

    public void setMeme( Meme[] meme )
    {
        this.meme = meme ;
    }

}
