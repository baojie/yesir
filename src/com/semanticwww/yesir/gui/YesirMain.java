package com.semanticwww.yesir.gui ;

/**
 * <p>Title: </p>
 * <p>Description: The running class of whole project</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

final public class YesirMain
    extends YesirAction
{

    public YesirMain()
    {
        super() ;
    }

    public void saveConfiguration()
    {
        // save the configuration
    }

    public static void main( String[] args )
    {
        YesirMain app = new YesirMain() ;
        app.preregisteredImporterExporter() ;
        app.createExampleMenu() ;
    }

}
