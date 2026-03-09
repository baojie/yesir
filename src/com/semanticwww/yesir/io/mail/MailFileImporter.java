package com.semanticwww.yesir.io.mail ;

import java.util.Date ;
import java.util.Vector ;

import com.semanticwww.yesir.gateway.DefaultGatewayImporter ;
import com.semanticwww.yesir.gateway.Meme ;

/**
 * <p>Title: MailFileImporter</p>
 *
 * <p>Description: Import local mail files</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 *
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0 2003-12-07
 */
public class MailFileImporter
    extends DefaultGatewayImporter
{
    String path ;
    public void jbInit( String name , String path )
    {
        setName( name ) ;
        this.path = path ;
    }

    /**
     * importData
     *
     * @return Meme[]
     */
    public Meme[] importData( Date cutoff )
    {
        //       Debug.trace( "MailFileImporter.importData" ) ;
        Vector data = MailMeme.fromDirectory( path , cutoff ) ;
//        Debug.trace( "Memes in importer 1: " + data.size() ) ;
        Meme memes[] = new Meme[data.size()] ;
        for( int i = 0 ; i < memes.length ; i++ )
        {
            memes[i] = ( Meme ) data.get( i ) ;
        }
        //Debug.trace( "Memes in importer: " + memes.length ) ;
        return memes ;
    }

    /**
     * GUISetup
     */
    public boolean GUISetup()
    {
        MailFileImporterSetupDlg dlg = new MailFileImporterSetupDlg(
            null , "MailFileImporter Setup" ,
            getName() , path ) ;
        dlg.show() ;

        if( dlg.getAction() == dlg.OK )
        {
            jbInit( dlg.getName() , dlg.getPath() ) ;
            return true ;
        }
        else
        {
            return false ;
        }
    }
}
