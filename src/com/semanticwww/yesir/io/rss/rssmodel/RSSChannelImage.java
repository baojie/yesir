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
 * Model for a RSSChannelImage.
 * A RSSChannel may contain one image
 *
 * @author  <a href="mailto:bpasero@users.sourceforge.net">Benjamin Pasero</a>
 * @version 0.63b
 */
public class RSSChannelImage
{

    private String imgUrl ;
    private String title ;
    private String link ;
    private String description ;

    /**
     * Instantiates a new RSSChannelImage
     * @param imgUrl URL to the image
     * @param title Title of the image
     * @param link Link to HP of the image
     * @param description Descriptive Text
     */
    public RSSChannelImage( String imgUrl , String title , String link ,
                            String description )
    {
        this.imgUrl = imgUrl ;
        this.title = title ;
        this.link = link ;
        this.description = description ;
    }

    /**
     * Default constructor
     */
    public RSSChannelImage()
    {
    }

    /**
     * @return String
     */
    public String getDescription()
    {
        return description ;
    }

    /**
     * @param description
     */
    public void setDescription( String description )
    {
        this.description = description ;
    }

    /**
     * @return String
     */
    public String getLink()
    {
        return link ;
    }

    /**
     * @param link
     */
    public void setLink( String link )
    {
        this.link = link ;
    }

    /**
     * @return String
     */
    public String getTitle()
    {
        return title ;
    }

    /**
     * @param title
     */
    public void setTitle( String title )
    {
        this.title = title ;
    }

    /**
     * @return String
     */
    public String getImgUrl()
    {
        return imgUrl ;
    }

    /**
     * @param imgUrl
     */
    public void setImgUrl( String imgUrl )
    {
        this.imgUrl = imgUrl ;
    }
}
