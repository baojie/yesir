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

package com.semanticwww.yesir.utils.string ;

import java.nio.ByteBuffer ;
import java.nio.CharBuffer ;
import java.nio.charset.CharacterCodingException ;
import java.nio.charset.Charset ;
import java.nio.charset.CharsetDecoder ;

/**
 * Class provides methods to decode ISO encoded
 * characters.
 *
 * @author  <a href="mailto:bpasero@users.sourceforge.net">Benjamin Pasero</a>
 * @version 0.63b
 */
public class ISOCharConverter
{

    /**
     * Decode ISO encoded character
     * @param isoBytes Bytes representing the char
     * @return String representing the encoded char
     */
    public static String decodeChar( byte[] isoBytes )
    {

        /** Return the Entity value if decode fails */
        String decodedChar = String.valueOf( "&" + ( int ) isoBytes[0] + ";" ) ;

        /** Only decode if Charset ISO-8859-1 is available */
        if( Charset.isSupported( "ISO-8859-1" ) )
        {
            Charset isoCharset = Charset.forName( "ISO-8859-1" ) ;
            CharsetDecoder decoder = isoCharset.newDecoder() ;

            ByteBuffer isoBytesRef = ByteBuffer.wrap( isoBytes ) ;
            CharBuffer decodedCharBuf = null ;
            try
            {
                decodedCharBuf = decoder.decode( isoBytesRef ) ;
            }
            catch( CharacterCodingException e )
            {
                //RSSOwlGUI.logger.log("decodeChar()", e);
            }
            return String.valueOf( decodedCharBuf ) ;
        }
        return decodedChar ;
    }
}
