package com.semanticwww.yesir.gui.dlg ;

import java.util.Vector ;

import java.awt.BorderLayout ;
import java.awt.Frame ;
import java.awt.GridLayout ;
import java.awt.event.ActionEvent ;
import javax.swing.JButton ;
import javax.swing.JCheckBox ;
import javax.swing.JDialog ;
import javax.swing.JList ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTextField ;

import com.semanticwww.yesir.gateway.DefaultGatewayExporter ;
import com.semanticwww.yesir.gateway.DefaultGatewayImporter ;
import com.semanticwww.yesir.gateway.Gateway ;
import com.semanticwww.yesir.gateway.GatewayException ;
import com.semanticwww.yesir.utils.LabelledItemPanel ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */

public class GatewayCreateDlg
    extends JDialog
{
    LabelledItemPanel panelNorth = new LabelledItemPanel() ;
    JPanel panelCenter = new JPanel() ;
    JPanel panelSouth = new JPanel() ;

    Gateway gate = null ;

    JList jListImporter ;
    JList jListExporter ;

    JButton jButtonCreate = new JButton() ;
    JButton jButtonCancel = new JButton() ;

    JTextField nameText = new JTextField( "New gateway" ) ;
    JTextField timeText = new JTextField( "1" ) ;
    JTextField freqText = new JTextField( "60" ) ;
    JCheckBox modeText = new JCheckBox() ;

    public GatewayCreateDlg( Frame frame , String title , boolean modal ,
                             Vector importerList , Vector exporterList )
    {
        super( frame , title , modal ) ;
        jListImporter = new JList( importerList ) ;
        jListExporter = new JList( exporterList ) ;
        try
        {
            jbInit() ;
//            pack() ;
        }
        catch( Exception ex )
        {
            ex.printStackTrace() ;
        }

    }

    private void jbInit()
        throws Exception
    {
        // north panel
        getContentPane().add( panelNorth , BorderLayout.NORTH ) ;
        panelNorth.addItem( "Gateway Name" , nameText ) ;
        panelNorth.addItem( "Recurrent Time (-1 for forever)" , timeText ) ;
        panelNorth.addItem( "Recurrent Frequency(Minutes)" , freqText ) ;
        panelNorth.addItem( "Automatic Export with import" , modeText ) ;

        // center panel
        getContentPane().add( panelCenter ) ;
        panelCenter.setLayout( new GridLayout() ) ;

        // --------- set default selection
        if( jListImporter.getModel().getSize() > 0 )
        {
            jListImporter.setSelectedIndex( 0 ) ;
        }
        if( jListExporter.getModel().getSize() > 0 )
        {
            jListExporter.setSelectedIndex( 0 ) ;

        }
        panelCenter.add( new JScrollPane( jListImporter ) , null ) ;
        panelCenter.add( new JScrollPane( jListExporter ) , null ) ;

        // south panel
        this.getContentPane().add( panelSouth , BorderLayout.SOUTH ) ;
        panelSouth.add( jButtonCreate , null ) ;
        panelSouth.add( jButtonCancel , null ) ;

        jButtonCreate.setText( "Create" ) ;
        jButtonCreate.addActionListener( new java.awt.event.ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                OnCreate( e ) ;
            }
        } ) ;
        jButtonCancel.setText( "Cancel" ) ;
        jButtonCancel.addActionListener( new java.awt.event.ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                OnCancel( e ) ;
            }
        } ) ;

        this.setSize( 800 , 350 ) ;
        this.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE ) ;
    }

    public Gateway getGate()
    {
        return gate ;
    }

    void OnCancel( ActionEvent e )
    {
        this.setVisible( false ) ;
        this.dispose() ;
    }

    /**
     * Create the gateway with selected importer and exporter type
     *
     * @param e ActionEvent
     */
    void OnCreate( ActionEvent e )
    {
        String name = nameText.getText() ;
        int time ;
        int freq ;
        short mode ;

        try
        {
            time = Integer.parseInt( timeText.getText() ) ;
            freq = Integer.parseInt( freqText.getText() ) ;
            mode = modeText.isSelected() ? Gateway.AUTO : Gateway.MANUAL ;
        }
        catch( NumberFormatException ex1 )
        {
            ex1.printStackTrace() ;
            JOptionPane.showMessageDialog( this ,
                                           "Time and frequency format error" ,
                                           "Error" ,
                                           JOptionPane.WARNING_MESSAGE ) ;

            return ;
        }

        if( jListImporter.getSelectedValue() == null ||
            jListExporter.getSelectedValue() == null )
        {
            JOptionPane.showMessageDialog( this ,
                                           "Class of importer/exporter don't not selected" ,
                                           "Error" ,
                                           JOptionPane.WARNING_MESSAGE ) ;
            return ;
        }

        //Debug.trace(jListImporter.getSelectedValue().toString());
        //Debug.trace(jListExporter.getSelectedValue().toString());

        DefaultGatewayImporter importer = null ;
        DefaultGatewayExporter exporter = null ;

        try
        {
            ClassLoader loader = ClassLoader.getSystemClassLoader() ;
            importer = ( DefaultGatewayImporter ) loader.loadClass(
                jListImporter.getSelectedValue().toString() ).newInstance() ;
            exporter = ( DefaultGatewayExporter ) loader.loadClass(
                jListExporter.getSelectedValue().toString() ).newInstance() ;
        }
        catch( Exception ex )
        {
            ex.printStackTrace() ;
            JOptionPane.showMessageDialog( this ,
                                           "Class not found: " + ex.getMessage() ,
                                           "Fatal Error" ,
                                           JOptionPane.WARNING_MESSAGE ) ;

            return ;
        }

        //Debug.trace( importer.getClass().getName()  ) ;
        //Debug.trace( exporter.getClass().getName()  ) ;
        if( importer.GUISetup() == false )
        {
            return ;
        }
        if( exporter.GUISetup() == false )
        {
            return ;
        }

        gate = new Gateway( name , importer , exporter ,
                            time , freq , mode ) ;
        if( gate != null )
        {
            try
            {
                gate.run() ;
            }
            catch( GatewayException ex2 )
            {
                ex2.printStackTrace() ;
            }
            System.out.println( "Gateway [" + name + "] is created" ) ;
            this.setVisible( false ) ;
        }
        else
        {
            JOptionPane.showMessageDialog( this ,
                                           "Gateway can't be created" ,
                                           "Fatal Error" ,
                                           JOptionPane.WARNING_MESSAGE ) ;

        }

    }

    // for test
    public static void main( String[] args )
    {
        Vector a = new Vector() ;
        a.add( "com.semanticwww.yesir.io.rss.RSSImporter" ) ;
        Vector b = new Vector() ;
        b.add( "com.semanticwww.yesir.io.console.ConsoleExporter" ) ;
        GatewayCreateDlg dlg = new GatewayCreateDlg( null , "Add new gateway" , true ,
            a , b ) ;
        dlg.show() ;
    }

}
