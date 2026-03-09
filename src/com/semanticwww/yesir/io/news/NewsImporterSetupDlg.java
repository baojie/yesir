package com.semanticwww.yesir.io.news ;

import java.awt.Frame ;
import javax.swing.JTextField ;

import com.semanticwww.yesir.gateway.SetupDlg ;
import com.semanticwww.yesir.utils.Debug ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */
public class NewsImporterSetupDlg
    extends SetupDlg
{
    private JTextField name = new JTextField() ;
    private JTextField nntpServer = new JTextField() ;
    private JTextField groupName = new JTextField() ;
    private JTextField backNumber = new JTextField() ;

    public String getName()
    {
        return name.getText() ;
    }

    public String getNNTPServer()
    {
        return nntpServer.getText() ;
    }

    public String getGroupName()
    {
        return groupName.getText() ;
    }

    public int getBackNumber()
    {
        return Integer.parseInt( backNumber.getText() ) ;
    }

    public NewsImporterSetupDlg( Frame frame , String title ,
                                 String nameStr , String nntpServerStr ,
                                 String groupNamestr , int backNumberVal )
    {
        super( frame , title ) ;

        try
        {
            addItem( "Name" , name , nameStr ) ;
            addItem( "NNTP Server" , nntpServer , nntpServerStr ) ;
            addItem( "Group Name" , groupName , groupNamestr ) ;
            addItem( "Seen only the n newest" , backNumber , backNumberVal + "" ) ;

            this.setSize( 400 , 200 ) ;
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

        NewsImporterSetupDlg dlg = new NewsImporterSetupDlg(
            null , "NewsImporter" ,
            "isu.market <- news.iastate.edu" ,
            "news.iastate.edu" ,
            "isu.market" , 20 ) ;
        dlg.show() ;

        if( dlg.getAction() == SetupDlg.OK )
        {
            Debug.trace( "Name = " + dlg.getName() +
                         "\nNNTP Server = " + dlg.getNNTPServer() +
                         "\nGroup Name = " + dlg.getGroupName() +
                         "\nBack Number = " + dlg.getBackNumber() ) ;
        }
    }

}
