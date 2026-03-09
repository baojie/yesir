package com.semanticwww.yesir.gui.panel ;

import java.awt.BorderLayout ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import javax.swing.JEditorPane ;
import javax.swing.JFrame ;
import javax.swing.JMenuItem ;
import javax.swing.JPanel ;
import javax.swing.JPopupMenu ;
import javax.swing.JScrollPane ;
import javax.swing.JTextArea ;

import com.semanticwww.yesir.gateway.Meme ;
import com.semanticwww.yesir.gui.YesirGUI ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

public class MemePreviewPanel
    extends JPanel
{
    YesirGUI parent ;
    JEditorPane previewWin = new JEditorPane( "text/html" , "" ) ;
    Meme meme ;

    public MemePreviewPanel( YesirGUI parent )
    {
        this.parent = parent ;
        try
        {
            jbInit() ;
        }
        catch( Exception ex )
        {
            ex.printStackTrace() ;
        }
    }

    void jbInit()
        throws Exception
    {
        this.setLayout( new BorderLayout() ) ;
        this.add( new JScrollPane( previewWin ) , BorderLayout.CENTER ) ;
        previewWin.setEditable( false ) ;

        previewWin.addMouseListener( new PopupListener() ) ;
    }

    public void updatePreview( Meme m )
    {
        this.meme = m ;
        previewWin.setText( m.report( true ) ) ;
    }

    class PopupListener
        extends MouseAdapter
    { //{{{
        public void mousePressed( MouseEvent e )
        {
            maybeShowPopup( e ) ;
        }

        public void mouseReleased( MouseEvent e )
        {
            maybeShowPopup( e ) ;
        }

        private void maybeShowPopup( MouseEvent e )
        {
            if( previewWin.getText() == null )
            {
                return ;
            }
            if( previewWin.getText().length() == 0 )
            {
                return ;
            }
            if( e.isPopupTrigger() )
            {
                System.out.println( "popup" ) ;
                JPopupMenu popup = new JPopupMenu() ;
                JMenuItem popupMenuItem ;
                popupMenuItem = new JMenuItem( "Show source..." ) ;
                popupMenuItem.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        showSource() ;
                    }
                } ) ;

                popup.add( popupMenuItem ) ;

                popup.show( e.getComponent() , e.getX() , e.getY() ) ;
            }
        }
    }

    // 2003-12-10
    public void showSource()
    {
        JFrame frame = new JFrame() ;
        JTextArea text = new JTextArea( meme.report( false ) ) ;
        frame.getContentPane().add( new JScrollPane( text ) ) ;

        frame.setSize( 400 , 300 ) ;
        frame.show() ;
    }
} //}}}
