package com.semanticwww.yesir.gui.dlg ;

import java.awt.Frame ;
import java.awt.event.ActionEvent ;
import javax.swing.JButton ;
import javax.swing.JCheckBox ;
import javax.swing.JTextField ;

import com.semanticwww.yesir.gateway.DefaultGatewayExporter ;
import com.semanticwww.yesir.gateway.DefaultGatewayImporter ;
import com.semanticwww.yesir.gateway.Gateway ;
import com.semanticwww.yesir.gateway.GatewayExporter ;
import com.semanticwww.yesir.gateway.GatewayImporter ;
import com.semanticwww.yesir.gateway.SetupDlg ;
import com.semanticwww.yesir.io.blank.BlankExporter ;
import com.semanticwww.yesir.io.rss.RSSImporter ;
import com.semanticwww.yesir.utils.Debug ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

public class GatewaySetupDlg
    extends SetupDlg
{
    // editable
    JTextField nameText = new JTextField() ;
    JTextField timeText = new JTextField() ;
    JTextField freqText = new JTextField() ;
    JCheckBox modeText = new JCheckBox() ;
    JButton importerButton = new JButton() ;
    JButton exporterButton = new JButton() ;

    // read only
    JTextField memeNumber = new JTextField() ;
    JTextField executedTime = new JTextField() ;

    public String getName()
    {
        return nameText.getText() ;
    }

    public int getRepeatTime()
    {
        return Integer.parseInt( timeText.getText() ) ;
    }

    public int getTimerFreq()
    {
        return Integer.parseInt( freqText.getText() ) ;
    }

    public short getMode()
    {
        return modeText.isSelected() ? Gateway.AUTO : Gateway.MANUAL ;
    }

    public GatewaySetupDlg( Frame frame , String title ,
                            String nameStr ,
                            String timeStr ,
                            String fregStr ,
                            Boolean modeVal ,
                            final GatewayImporter importer ,
                            final GatewayExporter exporter ,
                            int memeNumberVal ,
                            int executedTimeVal )
    {
        super( frame , title ) ;

        try
        {
            addItem( "Gateway Name" , nameText , nameStr ) ;
            addItem( "Recurrent Time (-1 for forever)" , timeText , timeStr ) ;
            addItem( "Recurrent Frequency(Minutes)" , freqText , fregStr ) ;
            addItem( "Automatic Export with import" , modeText , modeVal ) ;
            addItem( "Set Importer Properties" , importerButton , importer ) ;
            addItem( "Set Exporter Properties" , exporterButton , exporter ) ;
            addItem( "The number of message" , memeNumber , memeNumberVal + "" ) ;
            addItem( "Executed Time" , executedTime , executedTimeVal + "" ) ;

            memeNumber.setEditable( false ) ;
            executedTime.setEditable( false ) ;

            importerButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( ActionEvent e )
                {
                    ( ( DefaultGatewayImporter ) importer ).GUISetup() ;
                }
            } ) ;
            exporterButton.addActionListener( new java.awt.event.ActionListener()
            {
                public void actionPerformed( ActionEvent e )
                {
                    ( ( DefaultGatewayExporter ) exporter ).GUISetup() ;
                }
            } ) ;

            this.setSize( 400 , 400 ) ;
            //pack() ;
        }
        catch( Exception ex )
        {
            ex.printStackTrace() ;
        }
    }

    // for test
    public static void main( String[] args )
    {

        RSSImporter importer = new RSSImporter() ;
        importer.setName( "RSSImporter" ) ;

        BlankExporter exporter = new BlankExporter() ;
        exporter.setName( "BlankExporter" ) ;

        Gateway gate = new Gateway( "Gateway" , importer , exporter ,
                                    Gateway.ONCE , 1 , Gateway.MANUAL ) ;

        GatewaySetupDlg dlg = new GatewaySetupDlg(
            null , "Gateway Setup" ,
            gate.getName() ,
            gate.getRepeatTime() + "" ,
            gate.getTimerFreq() + "" ,
            new Boolean( gate.getMode() == Gateway.AUTO ) ,
            gate.getGatewayImporter() ,
            gate.getGatewayExporter() ,
            gate.getMemeCount() ,
            gate.getExecutedTime() ) ;
        dlg.show() ;

        if( dlg.getAction() == SetupDlg.OK )
        {
            Debug.trace( "Name = " + dlg.getName() +
                         "\nRepeat time = " + dlg.getRepeatTime() +
                         "\nTimer Freq = " + dlg.getTimerFreq() +
                         "\nAuto Mode = " + ( dlg.getMode() == Gateway.AUTO ) ) ;
            gate.updateProperties( dlg.getName() , dlg.getRepeatTime() ,
                                   dlg.getTimerFreq() , dlg.getMode() ) ;
        }
    }

}
