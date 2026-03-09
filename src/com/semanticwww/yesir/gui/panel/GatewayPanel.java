package com.semanticwww.yesir.gui.panel ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Component ;
import java.awt.Font ;
import java.awt.Graphics ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import javax.swing.Icon ;
import javax.swing.ImageIcon ;
import javax.swing.JLabel ;
import javax.swing.JMenuItem ;
import javax.swing.JPanel ;
import javax.swing.JPopupMenu ;
import javax.swing.JScrollPane ;
import javax.swing.JTree ;
import javax.swing.event.TreeSelectionEvent ;
import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.TreeCellRenderer ;
import javax.swing.tree.TreePath ;
import javax.swing.tree.TreeSelectionModel ;

import com.semanticwww.yesir.gateway.DefaultGatewayExporter ;
import com.semanticwww.yesir.gateway.DefaultGatewayImporter ;
import com.semanticwww.yesir.gateway.Gateway ;
import com.semanticwww.yesir.gui.YesirGUI ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

public class GatewayPanel
    extends JPanel
{
    YesirGUI parent ;

    //create the tree
    MyNode top = new MyNode( "Registered gateway" , MyNode.ROOT ) ;
    JTree tree = new JTree( top ) ;

    public GatewayPanel( YesirGUI parent )
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

    /**
     * update the tree with all registered gateways
     *
     * @param top MyNode
     */
    public void updateTree( Gateway[] gates )
    {
        top.removeAllChildren() ;
        top.setUserObject( "Registered gateway (" + gates.length + ")" ) ;
        for( int i = 0 ; i < gates.length ; i++ )
        {
            MyNode node1 = new MyNode( gates[i] , MyNode.GATEWAY ) ;
            top.add( node1 ) ;

            DefaultGatewayImporter importer = ( DefaultGatewayImporter ) gates[
                                              i].getGatewayImporter() ;
            DefaultGatewayExporter exporter = ( DefaultGatewayExporter ) gates[
                                              i].getGatewayExporter() ;
            MyNode child1 = new MyNode( importer , MyNode.IMPORTER ) ;
            node1.add( child1 ) ;
            child1 = new MyNode( exporter , MyNode.EXPORTER ) ;
            node1.add( child1 ) ;
            /*System.out.println(gates[i].toString() +"="+
                               importer.toString() + "+" +
                               exporter.toString());*/
        }
        //tree.expandPath( tree.getSelectionPath() ) ;
        //this.validate() ;
        tree.updateUI() ;
    }

    void jbInit()
        throws Exception
    {
        //setup the tree
        JScrollPane treeView = new JScrollPane( tree ) ;

        // set the tree attributes
        //initializes tree and creates tree selection event listener
        //createNodes( top ) ;
        tree.setEditable( false ) ;
        tree.getSelectionModel().setSelectionMode( TreeSelectionModel.
            SINGLE_TREE_SELECTION ) ;
        tree.setShowsRootHandles( true ) ;
        tree.setRootVisible( true ) ;
        tree.setCellRenderer( new TreeCellRendererEx() ) ;
        tree.addTreeSelectionListener( new javax.swing.event.
                                       TreeSelectionListener()
        {
            public void valueChanged( TreeSelectionEvent e )
            {
                treeSelectedListener( e ) ;
            }
        } ) ;
        tree.addMouseListener( new TreePopupListener() ) ;

        this.setLayout( new BorderLayout() ) ;
        add( treeView , BorderLayout.CENTER ) ;

    }

    /**
     * tree selection event
     *
     * @param e TreeSelectionEvent
     */
    void treeSelectedListener( TreeSelectionEvent e )
    {
        MyNode node = ( MyNode )
                      tree.getLastSelectedPathComponent() ;

        TreePath pathnode = ( TreePath ) tree.getLeadSelectionPath() ;
        if( tree.isVisible( pathnode ) )
        {
            if( node.getType() == MyNode.GATEWAY )
            {
                parent.updateMemeList( ( ( Gateway ) node.getUserObject() ).
                                       getMeme() ) ;
            }
            else
            {
                parent.updateMemeList( null ) ;
            }
        }
    }

    // popup menu on the tree
    class TreePopupListener
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
            TreePath selPath = tree.getPathForLocation( e.getX() , e.getY() ) ;
            if( e.isPopupTrigger() && selPath != null )
            {

                tree.setSelectionPath( selPath ) ;

                final MyNode selectedNode = ( MyNode ) selPath.
                                            getLastPathComponent() ;
                JMenuItem popupMenuItem ;
                JPopupMenu popup = new JPopupMenu() ;
                boolean showpopup = false ;

                if( selectedNode.getType() == MyNode.ROOT )
                {
                    popupMenuItem = new JMenuItem( "Add New Gateway..." ) ;
                    popupMenuItem.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            parent.jMenuGatewayAdd() ;
                        }
                    } ) ;

                    popup.add( popupMenuItem ) ;

                    showpopup = true ;
                }
                else if( selectedNode.getType() == MyNode.GATEWAY )
                {
                    popupMenuItem = new JMenuItem( "Importer & Exporter" ) ;
                    popupMenuItem.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            parent.jMenuGatewayRun( ( Gateway ) selectedNode.
                                getUserObject() ) ;
                        }
                    } ) ;
                    popup.add( popupMenuItem ) ;

                    popupMenuItem = new JMenuItem( "Import" ) ;
                    popupMenuItem.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            parent.jMenuGatewayImport( ( Gateway ) selectedNode.
                                getUserObject() ) ;
                        }
                    } ) ;
                    popup.add( popupMenuItem ) ;

                    popupMenuItem = new JMenuItem( "Export" ) ;
                    popupMenuItem.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            parent.jMenuGatewayExport( ( Gateway ) selectedNode.
                                getUserObject() ) ;
                        }
                    } ) ;
                    popup.add( popupMenuItem ) ;

                    popupMenuItem = new JMenuItem( "Delete" ) ;
                    popupMenuItem.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            parent.jMenuGatewayDelete( ( Gateway ) selectedNode.
                                getUserObject() ) ;
                        }
                    } ) ;

                    popup.add( popupMenuItem ) ;

                    popupMenuItem = new JMenuItem( "Properties" ) ;
                    popupMenuItem.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            parent.jMenuGatewayProperties( ( Gateway )
                                selectedNode.getUserObject() ) ;
                        }
                    } ) ;

                    popup.add( popupMenuItem ) ;

                    showpopup = true ;
                }
                else if( selectedNode.getType() == MyNode.IMPORTER )
                {
                    popupMenuItem = new JMenuItem( "Properties" ) ;
                    popupMenuItem.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            ( ( DefaultGatewayImporter ) selectedNode.
                              getUserObject() ).GUISetup() ;
                        }
                    } ) ;
                    popup.add( popupMenuItem ) ;

                    showpopup = true ;
                }
                else if( selectedNode.getType() == MyNode.EXPORTER )
                {
                    popupMenuItem = new JMenuItem( "Properties" ) ;
                    popupMenuItem.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            ( ( DefaultGatewayExporter ) selectedNode.
                              getUserObject() ).GUISetup() ;
                        }
                    } ) ;

                    popup.add( popupMenuItem ) ;

                    showpopup = true ;
                }

                if( showpopup )
                {
                    popup.show( e.getComponent() , e.getX() , e.getY() ) ;
                }
                tree.updateUI() ;

            }

        }
    } //}}}

} //}}}

class MyNode
    extends DefaultMutableTreeNode
{
    private short type ;

    public short getType()
    {
        return type ;
    }

    public MyNode( Object userObject , short type )
    {
        super( userObject ) ;
        this.type = type ;
    }

    public final static short ROOT = 0 ;
    public final static short GATEWAY = 1 ;
    public final static short IMPORTER = 2 ;
    public final static short EXPORTER = 3 ;

}

class TreeCellRendererEx
    extends JLabel
    implements TreeCellRenderer
{
    static protected Font defaultFont ;
    static protected ImageIcon rootIcon , gatewayIcon , importIcon ,
        exportIcon ;

    /** Color to use for the background when selected. */
    static protected final Color SelectedBackgroundColor = Color.LIGHT_GRAY ;

    static
    {
        try
        {
            defaultFont = new Font( "SansSerif" , 0 , 12 ) ;
        }
        catch( Exception e )
        {
            e.printStackTrace() ;
        }
        try
        {
            rootIcon = new ImageIcon( "resource/root.gif" ) ;
            gatewayIcon = new ImageIcon( "resource/gateway.gif" ) ;
            importIcon = new ImageIcon( "resource/importer.gif" ) ;
            exportIcon = new ImageIcon( "resource/exporter.gif" ) ;

        }
        catch( Exception e )
        {
            System.out.println( "Couldn't load images: " + e ) ;
        }
    }

    /** Whether or not the item that was last configured is selected. */
    protected boolean selected ;

    /**
     * This is messaged from JTree whenever it needs to get the size
     * of the component or it wants to draw it.
     * This attempts to set the font based on value, which will be
     * a TreeNode.
     */

    public Component getTreeCellRendererComponent( JTree tree ,
        Object value ,
        boolean selected ,
        boolean expanded ,
        boolean leaf , int row ,
        boolean hasFocus )
    {
        String stringValue = tree.convertValueToText( value , selected ,
            expanded , leaf , row , hasFocus ) ;
        // Set the text.
        setText( stringValue ) ;

// Tooltips used by the tree.
        setToolTipText( stringValue ) ;

// Set the image.
        switch( ( ( MyNode ) value ).getType() )
        {
            case MyNode.ROOT:
                setIcon( rootIcon ) ;
                break ;
            case MyNode.GATEWAY:
                setIcon( gatewayIcon ) ;
                break ;
            case MyNode.IMPORTER:
                setIcon( importIcon ) ;
                break ;
            case MyNode.EXPORTER:
                setIcon( exportIcon ) ;
                break ;
        }

        if( hasFocus )
        {
            setForeground( Color.blue ) ;
        }
        else
        {
            setForeground( Color.black ) ;
        }
        // Update the selected flag for the next paint.
//  }
        this.selected = selected ;

        return this ;
    }

    /**
      /**
      * paint is subclassed to draw the background correctly.  JLabel
      * currently does not allow backgrounds other than white, and it
      * will also fill behind the icon.  Something that isn't desirable.
      */
     public void paint( Graphics g )
     {
         Color bColor ;
         Icon currentI = getIcon() ;

         if( selected )
         {
             bColor = SelectedBackgroundColor ;
         }
         else if( getParent() != null )
         {

             /* Pick background color up from parent (which will come from
                the JTree we're contained in). */
             bColor = getParent().getBackground() ;
         }
         else
         {
             bColor = getBackground() ;
         }
         g.setColor( bColor ) ;
         if( currentI != null && getText() != null )
         {
             int offset = ( currentI.getIconWidth() + getIconTextGap() ) ;

             if( getComponentOrientation().isLeftToRight() )
             {
                 g.fillRect( offset , 0 , getWidth() - 1 - offset ,
                             getHeight() - 1 ) ;
             }
             else
             {
                 g.fillRect( 0 , 0 , getWidth() - 1 - offset ,
                             getHeight() - 1 ) ;
             }
         }
         else
         {
             g.fillRect( 0 , 0 , getWidth() - 1 , getHeight() - 1 ) ;
         }
         super.paint( g ) ;
     }
}
