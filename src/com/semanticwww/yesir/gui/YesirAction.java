package com.semanticwww.yesir.gui ;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import javax.swing.JMenu ;
import javax.swing.JMenuBar ;
import javax.swing.JMenuItem ;
import javax.swing.JOptionPane ;

import com.semanticwww.yesir.gateway.Gateway ;
import com.semanticwww.yesir.gateway.GatewayException ;
import com.semanticwww.yesir.gateway.SetupDlg ;
import com.semanticwww.yesir.gui.dlg.GatewayCreateDlg ;
import com.semanticwww.yesir.gui.dlg.GatewaySetupDlg ;
import com.semanticwww.yesir.io.blank.BlankExporter ;
import com.semanticwww.yesir.io.console.ConsoleExporter ;
import com.semanticwww.yesir.io.mail.MailFileImporter ;
import com.semanticwww.yesir.io.mail.Pop3Importer ;
import com.semanticwww.yesir.io.news.NewsImporter ;
import com.semanticwww.yesir.io.rss.RSSExporter ;
import com.semanticwww.yesir.io.rss.RSSImporter ;
import com.semanticwww.yesir.io.search.GoogleImporter ;
import com.semanticwww.yesir.io.mail.SmtpExporterSetupDlg ;
import com.semanticwww.yesir.io.mail.SmtpExporter ;




/**
 * <p>Title: </p>
 * <p>Description: Actions handlers </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

abstract public class YesirAction
        extends YesirGUI
{

    public YesirAction ()
    {
        super () ;
    }

    JMenuBar createMenuBar ()
    {

        // Gateway
        JMenu jMenuGateway = new JMenu () ;

        jMenuGateway.setText ( "Gateway" ) ;

        JMenuItem jMenuNew = new JMenuItem () ;
        jMenuNew.setText ( "New Gateway..." ) ;
        jMenuNew.addActionListener ( new ActionListener ()
        {
            public void actionPerformed ( ActionEvent e )
            {
                jMenuGatewayAdd () ;
            }
        } ) ;
        jMenuGateway.add ( jMenuNew ) ;

        JMenuItem jMenuGatewayRegisterImporter = new JMenuItem () ;
        jMenuGatewayRegisterImporter.setText ( "Register Import..." ) ;
        jMenuGatewayRegisterImporter.addActionListener ( new ActionListener ()
        {
            public void actionPerformed ( ActionEvent e )
            {
                jMenuGatewayRegisterImporter () ;
            }
        } ) ;
        jMenuGateway.add ( jMenuGatewayRegisterImporter ) ;

        JMenuItem jMenuGatewayRegisterExporter = new JMenuItem () ;
        jMenuGatewayRegisterExporter.setText ( "Register Export..." ) ;
        jMenuGatewayRegisterExporter.addActionListener ( new ActionListener ()
        {
            public void actionPerformed ( ActionEvent e )
            {
                jMenuGatewayRegisterExporter () ;
            }
        } ) ;
        jMenuGateway.add ( jMenuGatewayRegisterExporter ) ;

        jMenuGateway.addSeparator () ;

        JMenuItem jMenuExit = new JMenuItem () ;
        jMenuExit.setText ( "Exit" ) ;
        jMenuExit.addActionListener ( new ActionListener ()
        {
            public void actionPerformed ( ActionEvent e )
            {
                jMenuExit ( e ) ;
            }
        } ) ;
        jMenuGateway.add ( jMenuExit ) ;

        // Help
        JMenu jMenuHelp = new JMenu () ;
        jMenuHelp.setText ( "Help" ) ;

        JMenuItem jMenuAbout = new JMenuItem () ;
        jMenuAbout.setText ( "About" ) ;
        jMenuAbout.addActionListener ( new ActionListener ()
        {
            public void actionPerformed ( ActionEvent e )
            {
                String info = "<html>" +
                              "<font color=\"#FF0099\"><b>" +
                              "Yesir! information agent</b></font><br>Version 1.0.1<br>" +
                              "<br><b>Jie Bao</b><br>2003-12-10<br>" +
                              "Iowa State University<br><a href=\"mailto:baojie@iastate.edu\">" +
                              "baojie@iastate.edu</a><br><a href=\"http://www.cs.iastate.edu/~baojie\">" +
                              "http://www.cs.iastate.edu/~baojie</a><br>"
                              + "</html>" ;
                JOptionPane.showMessageDialog ( null , info ) ;

            }
        } ) ;
        jMenuHelp.add ( jMenuAbout ) ;

        //  add all menus
        mainMenu.add ( jMenuGateway ) ;
        mainMenu.add ( jMenuHelp ) ;
        createExampleMenu () ;

        return mainMenu ;
    }

    void jMenuExit ( ActionEvent e )
    {
        mainFrame.dispose () ;
        System.exit ( 0 ) ;
    }

    public void jMenuGatewayAdd ()
    {
        // list all register importer and exporter
        GatewayCreateDlg dlg = new GatewayCreateDlg ( mainFrame ,
                "Add new gateway" ,
                true , importers , exporters ) ;
        dlg.show () ;

        Gateway g = dlg.getGate () ;
        if ( g != null )
        {
            addGateway ( g ) ;
            updateGatewayTree ( gates.getGateways () ) ;
            g.start () ;
        }
    }

    public void jMenuGatewayRun ( Gateway g )
    {
        try
        {
            if ( g != null )
            {
                g.run () ;
                updateMemeList ( g.getMeme () ) ;
            }
        }
        catch ( GatewayException ex )
        {
            JOptionPane.showMessageDialog ( mainFrame , ex.getMessage () ) ;
            ex.printStackTrace () ;
        }

    }

    public void jMenuGatewayImport ( Gateway g )
    {
        try
        {
            if ( g != null )
            {
                g.importData ( null ) ;
                updateMemeList ( g.getMeme () ) ;
            }
        }
        catch ( GatewayException ex )
        {
            JOptionPane.showMessageDialog ( mainFrame , ex.getMessage () ) ;
            ex.printStackTrace () ;
        }

    }

    public void jMenuGatewayExport ( Gateway g )
    {
        try
        {
            if ( g != null )
            {
                if ( g.getMemeCount () > 0 )
                {
                    g.exportData () ;
                }
            }
        }
        catch ( GatewayException ex )
        {
            JOptionPane.showMessageDialog ( mainFrame , ex.getMessage () ) ;
            ex.printStackTrace () ;
        }

    }




    /**
     * 2003-12-10
     * @param g Gateway
     */
    public void jMenuGatewayDelete ( Gateway g )
    {
        if ( g != null )
        {
            removeGateway ( g ) ;
            updateGatewayTree ( gates.getGateways () ) ;
        }
    }

    public void jMenuGatewayProperties ( Gateway gate )
    {
        if ( gate != null )
        {
            GatewaySetupDlg dlg = new GatewaySetupDlg (
                    null , "Gateway Setup" ,
                    gate.getName () ,
                    gate.getRepeatTime () + "" ,
                    gate.getTimerFreq () + "" ,
                    new Boolean ( gate.getMode () == Gateway.AUTO ) ,
                    gate.getGatewayImporter () ,
                    gate.getGatewayExporter () ,
                    gate.getMemeCount () ,
                    gate.getExecutedTime () ) ;
            dlg.show () ;

            if ( dlg.getAction () == SetupDlg.OK )
            {
                gate.updateProperties ( dlg.getName () , dlg.getRepeatTime () ,
                                        dlg.getTimerFreq () , dlg.getMode () ) ;
            }

        }
    }

    void jMenuGatewayRegisterImporter ()
    {
        String importerName = JOptionPane.showInputDialog (
                "Please Give the importer class name" ) ;
        registerNewImporterExporter ( importerName , true ) ;
    }

    void jMenuGatewayRegisterExporter ()
    {
        String exporterName = JOptionPane.showInputDialog (
                "Please Give the exporter class name" ) ;
        registerNewImporterExporter ( exporterName , true ) ;
    }

    public void createExampleMenu ()
    {
        // example
        JMenu jMenuExample = new JMenu () ;
        jMenuExample.setText ( "Example" ) ;

        JMenuItem example = new JMenuItem ( "1. RSS -> Blank " ) ;
        example.addActionListener ( new ActionListener ()
        {
            public void actionPerformed ( ActionEvent e )
            {
                OnExample1 ( e ) ;
            }
        } ) ;
        jMenuExample.add ( example ) ;
        example = new JMenuItem ( "2. Google -> RSS " ) ;
        example.addActionListener ( new ActionListener ()
        {
            public void actionPerformed ( ActionEvent e )
            {
                OnExample2 ( e ) ;
            }
        } ) ;
        jMenuExample.add ( example ) ;
        example = new JMenuItem ( "3. NNTP News -> Smtp mail " ) ;
        example.addActionListener ( new ActionListener ()
        {
            public void actionPerformed ( ActionEvent e )
            {
                OnExample3 ( e ) ;
            }
        } ) ;
        jMenuExample.add ( example ) ;
        example = new JMenuItem ( "4. Pop3 mail -> RSS " ) ;
        example.addActionListener ( new ActionListener ()
        {
            public void actionPerformed ( ActionEvent e )
            {
                OnExample4 ( e ) ;
            }
        } ) ;
        jMenuExample.add ( example ) ;
        example = new JMenuItem ( "5. Mail File -> Console " ) ;
        example.addActionListener ( new ActionListener ()
        {
            public void actionPerformed ( ActionEvent e )
            {
                OnExample5 ( e ) ;
            }
        } ) ;
        jMenuExample.add ( example ) ;

        mainMenu.add ( jMenuExample ) ;
    }




    /**
     *
     * @param e ActionEvent
     *
     * @author Jie Bao
     * @version 2003-12-08
     */
    void OnExample1 ( ActionEvent e ) //1. RSS -> Blank
    {
        try
        {
            RSSImporter importer = new RSSImporter () ;
            importer.jbInit ( "Iowa Weather " ,
                              "http://weather.gov/alerts/ia.rss" ) ;

            BlankExporter exporter = new BlankExporter () ;
            exporter.jbInit ( "Blank Exporter" ) ;

            Gateway g = addGateway ( "RSS -> Blank" , importer , exporter ,
                                     Gateway.ONCE , 1 , Gateway.AUTO ) ;
            updateGatewayTree ( gates.getGateways () ) ;
            g.start () ;
        }
        catch ( Exception ex )
        {
            JOptionPane.showMessageDialog ( null , "Error : " + ex.getMessage () ) ;
            ex.printStackTrace () ;
        }
    }

    void OnExample2 ( ActionEvent e ) //2. Google -> RSS
    {
        try
        {
            GoogleImporter importer = new GoogleImporter () ;
            importer.jbInit ( "Bao Jie on Goole" ,
                              null ,
                              "baojie" ) ;

            RSSExporter exporter = new RSSExporter () ;
            exporter.jbInit ( "Local RSS" , "baojie_google.xml" ) ;

            Gateway g = addGateway ( "Google -> RSS" , importer , exporter ,
                                     Gateway.ONCE , 1 , Gateway.AUTO ) ;
            updateGatewayTree ( gates.getGateways () ) ;
            g.start () ;
        }
        catch ( Exception ex )
        {
            JOptionPane.showMessageDialog ( null , "Error : " + ex.getMessage () ) ;
            ex.printStackTrace () ;
        }
    }

    void OnExample3 ( ActionEvent e ) //3. NNTP News -> Smtp mail
    {
        try
        {
            NewsImporter importer = new NewsImporter () ;
            importer.jbInit ( "isu.market" , "news.iastate.edu" , "isu.market" ,
                              5 ) ;

            SmtpExporter exporter = new SmtpExporter () ;
            exporter.jbInit ( "my cs mail" ,
                              "mailhub.iastate.edu" ,
                              SmtpExporter.DEFAULT_SENDER , "" ) ;
            exporter.GUISetup () ;

            Gateway g = addGateway ( "NNTP News -> SMTP mail" , importer ,
                                     exporter ,
                                     Gateway.ONCE , 1 , Gateway.AUTO ) ;
            updateGatewayTree ( gates.getGateways () ) ;
            g.start () ;
        }
        catch ( Exception ex )
        {
            JOptionPane.showMessageDialog ( null , "Error : " + ex.getMessage () ) ;
            ex.printStackTrace () ;
        }
    }

    void OnExample4 ( ActionEvent e ) //"4. Pop3 mail -> RSS "
    {
        try
        {
            Pop3Importer importer = new Pop3Importer () ;
            importer.jbInit ( "Mail on Popeye" , "pop3" , "pop.cs.iastate.edu" ,
                              "baojie" , "" , "baojie@cs.iastate.edu" , true ) ;
            importer.GUISetup () ;

            RSSExporter exporter = new RSSExporter () ;
            exporter.jbInit ( "Local RSS" , "baojie_popeye.xml" ) ;

            Gateway g = addGateway ( "Pop3 mail -> RSS" , importer ,
                                     exporter ,
                                     Gateway.ONCE , 1 , Gateway.AUTO ) ;
            updateGatewayTree ( gates.getGateways () ) ;
            g.start () ;
        }
        catch ( Exception ex )
        {
            JOptionPane.showMessageDialog ( null , "Error : " + ex.getMessage () ) ;
            ex.printStackTrace () ;
        }
    }

    void OnExample5 ( ActionEvent e ) //"5. Mail File -> Console "
    {
        try
        {
            MailFileImporter importer = new MailFileImporter () ;
            importer.jbInit ( "Local sample mails" ,
                              ".\\sample" ) ;
            importer.GUISetup () ;

            ConsoleExporter exporter = new ConsoleExporter () ;
            exporter.jbInit ( "Screen Dialog" ) ;
            exporter.dummy = false ;

            Gateway g = addGateway ( "Mail File -> Console" , importer ,
                                     exporter ,
                                     Gateway.ONCE , 1 , Gateway.AUTO ) ;
            updateGatewayTree ( gates.getGateways () ) ;
            g.start () ;
        }
        catch ( Exception ex )
        {
            JOptionPane.showMessageDialog ( null , "Error : " + ex.getMessage () ) ;
            ex.printStackTrace () ;
        }

    }

}
