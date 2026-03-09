package com.semanticwww.yesir.io.bbs ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

final public class LB5000Exporter
    extends BBSExporter
{
    String cgiURL ;
    String boardNo ;
    String user ;
    String password ;

    public void jbInit( String name ,
                        String cgiURL ,
                        String boardNo ,
                        String user ,
                        String password )
    {
        setName( name ) ;
        this.cgiURL = cgiURL ;
        this.boardNo = boardNo ;
        this.user = user ;
        this.password = password ;
    }

    /**
     * doPost2LB5000
     *
     *  send post to LB5000 BBS system
     *
     * @param cgiURL String
     * @param boardNo String
     * @param user String
     * @param password String
     * @param subject String
     * @param toPost String
     * @return String
     */
    String doPost( String subject , String toPost )
    {
        String title = "No Title" ;
        if( subject != null )
        {
            if( subject.length() > 0 )
            {
                title = subject ;
            }
        }

        final CGIFields parameters[] = new CGIFields[11] ;
        int i = 0 ;
        parameters[i++] = new CGIFields( "membername" , user ) ; //用户名
        parameters[i++] = new CGIFields( "password" , password ) ; //密码
        parameters[i++] = new CGIFields( "intopictitle" , title ) ; // 主题标题
        parameters[i++] = new CGIFields( "uselbcode" , "yes" ) ; // 使用 LB5000 标签？
        parameters[i++] = new CGIFields( "inshowsignature" , "yes" ) ; //显示签名？
        parameters[i++] = new CGIFields( "notify" , "no" ) ; // 有回复时使用邮件通知您？
        parameters[i++] = new CGIFields( "inshowemoticons" , "no" ) ; //您是否希望<b>使用</b>表情字符转换？
        parameters[i++] = new CGIFields( "inpost" , toPost ) ; //内容
        parameters[i++] = new CGIFields( "addme" , "" ) ; //附件
        parameters[i++] = new CGIFields( "action" , "addnew" ) ; // action
        parameters[i++] = new CGIFields( "forum" , boardNo ) ; // forum number

        String result = BBSExporter.doPost(
            cgiURL , //"http://boole.cs.iastate.edu/isubbs/post.cgi" ,
            parameters , "GB2312" ) ;
        return result ;
    }

    /**
     * GUISetup
     */
    public boolean GUISetup()
    {
        LB5000ExporterSetupDlg dlg = new LB5000ExporterSetupDlg(
            null , "LB5000Exporter Setup" ,
            getName() ,
            this.cgiURL ,
            this.boardNo ,
            this.user ,
            this.password ) ;
        dlg.show() ;

        if( dlg.getAction() == dlg.OK )
        {
            jbInit( dlg.getName() ,
                    dlg.getCgiURL() ,
                    dlg.getBoardNo() ,
                    dlg.getUser() ,
                    dlg.getPassword() ) ;
            return true ;
        }
        else
        {
            return false ;
        }

    }

}
