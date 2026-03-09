package com.semanticwww.yesir.gateway ;

import java.awt.BorderLayout ;
import java.awt.Frame ;
import java.awt.event.ActionEvent ;
import javax.swing.JButton ;
import javax.swing.JCheckBox ;
import javax.swing.JComboBox ;
import javax.swing.JComponent ;
import javax.swing.JDialog ;
import javax.swing.JEditorPane ;
import javax.swing.JFormattedTextField ;
import javax.swing.JList ;
import javax.swing.JPanel ;
import javax.swing.JPasswordField ;
import javax.swing.JTextArea ;
import javax.swing.JTextField ;
import javax.swing.JTextPane ;

import com.semanticwww.yesir.utils.LabelledItemPanel ;

/**
 *
 * <p>Title: </p>
 * <p>Description: Common GUI setup dialog for importer/exporter</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0 2003-12-10
 */
abstract public class SetupDlg
    extends JDialog
{
    static public short OK = 0 ;
    static public short CANCEL = 1 ;

    private short action = 1 ;

    LabelledItemPanel controlPanel = new LabelledItemPanel() ;
    JPanel buttonPanel = new JPanel() ;

    JButton jButtonOK = new JButton() ;
    JButton jButtonCancel = new JButton() ;

    public SetupDlg( Frame frame , String title )
    {
        super( frame , title , true ) ;

        try
        {
            jbInit() ;
        }
        catch( Exception ex )
        {
            ex.printStackTrace() ;
        }
    }

    private void jbInit()
        throws Exception
    {
        jButtonOK.setText( "OK" ) ;
        jButtonOK.addActionListener( new java.awt.event.ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                OnOK( e ) ;
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
        getContentPane().add( controlPanel , BorderLayout.CENTER ) ;
        this.getContentPane().add( buttonPanel , BorderLayout.SOUTH ) ;
        buttonPanel.add( jButtonOK , null ) ;
        buttonPanel.add( jButtonCancel , null ) ;

//        this.setSize( 400 , 400 ) ;
        this.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE ) ;
    }

    void OnOK( ActionEvent e )
    {
        action = this.OK ;
        this.setVisible( false ) ;
    }

    void OnCancel( ActionEvent e )
    {
        action = this.CANCEL ;
        this.setVisible( false ) ;
        this.dispose() ;
    }

    public void addItem( String labelText , JComponent item ,
                         Object initialValue )
    {
        controlPanel.addItem( labelText , item ) ;

        if( initialValue == null )
        {
            return ;
        }
        if( item instanceof JTextField )
        {
            ( ( JTextField ) item ).setText( ( String ) initialValue ) ;
        }
        else if( ( item instanceof JTextArea ) )
        {
            ( ( JTextArea ) item ).setText( ( String ) initialValue ) ;
        }
        else if( ( item instanceof JTextPane ) )
        {
            ( ( JTextPane ) item ).setText( ( String ) initialValue ) ;
        }
        else if( ( item instanceof JEditorPane ) )
        {
            ( ( JEditorPane ) item ).setText( ( String ) initialValue ) ;
        }
        else if( ( item instanceof JPasswordField ) )
        {
            ( ( JPasswordField ) item ).setText( ( String ) initialValue ) ;
        }
        else if( ( item instanceof JFormattedTextField ) )
        {
            ( ( JFormattedTextField ) item ).setText( ( String ) initialValue ) ;
        }
        else if( ( item instanceof JCheckBox ) )
        {
            ( ( JCheckBox ) item ).setSelected( ( ( Boolean ) initialValue ).
                                                booleanValue() ) ;
        }
        else if( ( item instanceof JComboBox ) )
        {
            ( ( JComboBox ) item ).addItem( initialValue ) ;
        }
        else if( ( item instanceof JList ) )
        {
            ( ( JList ) item ).setListData( ( Object[] ) initialValue ) ;
        }
        else if( ( item instanceof JButton ) )
        {
            String buttonName = "<Unnamed>" ;
            if( initialValue.toString() != null &&
                initialValue.toString().length() > 0 )
            {
                buttonName = initialValue.toString() ;
            }
            ( ( JButton ) item ).setText( buttonName ) ;
        }
    }

    public short getAction()
    {
        return action ;
    }

}
