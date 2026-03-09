/*   ********************************************************************   **
 **   Copyright notice                                                       **
 **                                                                          **
 **   (c) 2003 RSSOwl Development Team					                              **
 **   http://rssowl.sourceforge.net/   			                                **
 **                                                                          **
 **   All rights reserved                                                    **
 **                                                                          **
 **   This script is part of the RSSOwl project. The RSSOwl						      **
 **   project is free software; you can redistribute it and/or modify        **
 **   it under the terms of the GNU General Public License as published by   **
 **   the Free Software Foundation; either version 2 of the License, or      **
 **   (at your option) any later version.                                    **
 **                                                                          **
 **   The GNU General Public License can be found at                         **
 **   http://www.gnu.org/copyleft/gpl.html.                                  **
 **   A copy is found in the textfile GPL.txt and important notices to the   **
 **   license from the team is found in the textfile LICENSE.txt distributed **
 **   in these package.                                                      **
 **                                                                          **
 **   This copyright notice MUST APPEAR in all copies of the file!           **
 **   ********************************************************************   */

package com.semanticwww.yesir.io.rss.rssmodel ;

/**
 * Model for a RSSEnclosure.
 * A newsitem may contain one ore more enclosures
 * (e.g. link to a mp3 or other file)
 *
 * @author  <a href="mailto:bpasero@users.sourceforge.net">Benjamin Pasero</a>
 * @version 0.63b
 */
public class RSSEnclosure
{

    private String name ;
    private String url ;
    private String length ;
    private String type ;

    /** Default Constructor */
    public RSSEnclosure()
    {

    }

    /**
     * @return String
     */
    public String getLength()
    {
        return length ;
    }

    /**
     * @param length
     */
    public void setLength( String length )
    {
        this.length = length ;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return name ;
    }

    /**
     * @param name
     */
    public void setName( String name )
    {
        this.name = name ;
    }

    /**
     * @return String
     */
    public String getType()
    {
        return type ;
    }

    /**
     * @param type
     */
    public void setType( String type )
    {
        this.type = type ;
    }

    /**
     * @return String
     */
    public String getUrl()
    {
        return url ;
    }

    /**
     * @param url
     */
    public void setUrl( String url )
    {
        this.url = url ;
    }

    /**
     *
     * @return String
     *
     * @author Jie Bao
     * @version 2003-12-10
     */
    public String toString()
    {
        return getUrl() + " (" + getName() + " " + getType() + ")" ;
    }
}
