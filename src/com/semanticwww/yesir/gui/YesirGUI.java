package com.semanticwww.yesir.gui ;

import java.awt.Dimension ;
import java.awt.Toolkit ;
import javax.swing.JFrame ;
import javax.swing.JMenuBar ;
import javax.swing.JScrollPane ;
import javax.swing.JSplitPane ;

import com.semanticwww.yesir.gateway.Gateway ;
import com.semanticwww.yesir.gateway.Meme ;
import com.semanticwww.yesir.gui.panel.GatewayPanel ;
import com.semanticwww.yesir.gui.panel.MemeListPanel ;
import com.semanticwww.yesir.gui.panel.MemePreviewPanel ;

/**
 * <p>Title: </p>
 * <p>Description: GUI of the project</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0 2003-11-16
 */

abstract public class YesirGUI
    extends YesirBasis
{
    JMenuBar mainMenu = new JMenuBar() ;
    protected JFrame mainFrame ;

    private final GatewayPanel gatewayPanel = new GatewayPanel( this ) ;
    private final MemeListPanel memeListPanel = new MemeListPanel( this ) ;
    private final MemePreviewPanel memePreviewPanel = new MemePreviewPanel( this ) ;

    public YesirGUI()
    {
        super() ;
        jbInit() ;
    }

    private void jbInit()
    {
        mainFrame = new JFrame() ;
        mainFrame.setTitle( "Yesir Information Agent" ) ;
        mainFrame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE ) ;

// whole panel

        JSplitPane jPanelVertical ;
        jPanelVertical = new JSplitPane( JSplitPane.VERTICAL_SPLIT ,
                                         memeListPanel ,
                                         memePreviewPanel ) ;
        jPanelVertical.setContinuousLayout( false ) ;
        jPanelVertical.setOneTouchExpandable( true ) ;

        JSplitPane jPanelHorizontal ;

        jPanelHorizontal = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ,
                                           new JScrollPane( gatewayPanel ) ,
                                           jPanelVertical ) ;
        jPanelHorizontal.setContinuousLayout( false ) ;
        jPanelHorizontal.setOneTouchExpandable( true ) ;

        mainFrame.setContentPane( jPanelHorizontal ) ;

// menu
        mainMenu = createMenuBar() ;
        mainFrame.setJMenuBar( mainMenu ) ;

        mainFrame.pack() ;

        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize() ;
        Dimension frameSize = screenSize ;
        frameSize.height *= 0.9 ;
        frameSize.width *= 0.9 ;

        mainFrame.setSize( frameSize ) ;
        mainFrame.setLocation( 0 , 0 ) ;
        mainFrame.setVisible( true ) ;

        jPanelVertical.setDividerLocation( 0.5 ) ;
        jPanelHorizontal.setDividerLocation( 0.3 ) ;

        mainFrame.show() ;
//        mainFrame.getContentPane().add(jPanelHorizontal, null);
    }

    /**
     * updateGatewayTree
     *
     * @author Jie Bao
     * @version 2003-12-10
     */
    public void updateGatewayTree( Gateway[] gates )
    {
        gatewayPanel.updateTree( gates ) ;
    }

    public void updateMemeList( Meme[] meme )
    {
        memeListPanel.updateList( meme ) ;
    }

    public void updateMemePreview( Meme meme )
    {
        memePreviewPanel.updatePreview( meme ) ;
    }

    // menu and handler
    abstract JMenuBar createMenuBar() ;

    abstract public void jMenuGatewayAdd() ;

    abstract public void jMenuGatewayRun( Gateway g ) ;

    abstract public void jMenuGatewayImport( Gateway g ) ;

    abstract public void jMenuGatewayExport( Gateway g ) ;

    abstract public void jMenuGatewayDelete( Gateway g ) ;

    abstract public void jMenuGatewayProperties( Gateway g ) ;
}
