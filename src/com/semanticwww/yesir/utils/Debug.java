package com.semanticwww.yesir.utils ;

/**
 * <p>Title: Debug utilities </p>
 * <p>Description: Some Debug Routines </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Iowa State University</p>
 * @author Jie Bao
 * @version 2003-09-19
 */

import java.awt.Window ;
import javax.swing.JOptionPane ;

public class Debug
{

    static boolean debugMode = true ;

    public Debug( boolean mode )
    {
        debugMode = mode ;
    }

    public Debug()
    {
        debugMode = true ;
    }

    /**
     * Set debug mode
     *
     * @param mode
     */
    public void setDebugMode( boolean mode )
    {
        debugMode = mode ;
    }

    /**
     * If in debug mode
     *
     * @return the debug status
     */
    public boolean getDebugMode()
    {
        return debugMode ;
    }

    /**
     * Show a string in a message box
     *
     * @param str : the string to show
     */
    static public void trace( String str )
    {
        if( !debugMode )
        {
            return ;
        }
        JOptionPane.showMessageDialog( null , str ) ;
    }

    /**
     * Called when application is crashing . the function prints an error
     * message and exits with the error code specifed.
     * @param view The window from which the exit was called.
     * @param errormsg The error message to display.
     * @param errorcode The errorcode to exit with.
     */
    public static void exitError( Window frame , String errormsg ,
                                  int errorcode )
    { //{{{
        if( frame != null )
        {
            JOptionPane.showMessageDialog( frame , errormsg ,
                                           "Fatal Error" ,
                                           JOptionPane.WARNING_MESSAGE ) ;
        }

        //print the error to the command line also.
        System.err.println( errormsg ) ;
        System.exit( errorcode ) ;
    } //}}}

}
