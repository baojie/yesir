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

import java.util.Hashtable ;
import java.util.Vector ;

/**
 * Class for parsing the search pattern
 * (Operators: AND, OR, NOT).
 *
 * @author  <a href="mailto:bpasero@users.sourceforge.net">Benjamin Pasero</a>
 * @version 0.63b
 */
public class RSSSearchPatternParser
{

    /**
     * TRUE:  Pattern must match a complete word (Pattern "dog" matches "dog")<br />
     * FALSE: Pattern may match part of a word (Pattern "dog" matches "hotdog")
     */
    public static boolean searchOnlyWholeWords = false ;

    /**
     * TRUE:  Pattern matches words in same case (Pattern "DoG" matches "DoG", not "dog")<br />
     * FALSE: Pattern matches words in any case (Pattern "DoG" matches "dog", "dOg", ...)
     */
    public static boolean searchCaseSensitive = false ;

    /**
     * TRUE:  Pattern is interpreted as a regular Expression<br />
     * FALSE: Pattern is not interpreted as a regular Expression
     */
    public static boolean searchRegEx = false ;

    /** Save the last search-pattern that was performed */
    public static String lastSearch = "" ;

    /**
     * Parse the search pattern and return a Hashtable. The Hashtable
     * contains one Vector for the Keywords that should NOT match the
     * result and one Vector that should match the result (AND). The
     * Vector for the AND-Keywords holds one Vector for each OR-Keyword.
     *
     * For example:
     * A pattern "Car AND Audi OR Mercedes" would result in:
     * [[Car], [Audi, Mercedes]]
     * beeing equivalent to "Car && (Audi || Mercedes)"
     *
     * @param pattern The search pattern (e.g. "Car AND Audi NOT Ferrari")
     * @return parsed pattern holding the Keywords for AND, OR and NOT
     */
    public static Hashtable parsePattern( String pattern )
    {
        Hashtable parsedPattern = new Hashtable() ;

        /** Parse Words that should NOT match the result */
        int notIndex = pattern.indexOf( " NOT " ) ;

        Vector not = new Vector() ;
        while( notIndex > 0 )
        {
            int start = notIndex ;
            int end = getClosestKeyword( pattern , notIndex ) ;

            String notPattern = pattern.substring( start , end ) ;
            String notWords[] = notPattern.split( " NOT " ) ;

            /** Remove the NOT statement from the pattern */
            pattern = pattern.replaceFirst( notPattern , "" ) ;

            /** Add NOT Keywords to Vector */
            for( int a = 1 ; a < notWords.length ; a++ )
            {
                if( !not.contains( notWords[a] ) )
                {
                    not.add( notWords[a] ) ;
                }
            }
            notIndex = pattern.indexOf( " NOT " , notIndex + 1 ) ;
        }

        Vector and = parseMustAndMayWords( pattern ) ;
        parsedPattern.put( "AND" , and ) ;
        parsedPattern.put( "NOT" , not ) ;
        return parsedPattern ;
    }

    /**
     * Finds the next index of one of the keywords AND, OR
     * @param pattern The search pattern
     * @param notIndex Current position in search pattern
     * @return Closest index to next keyword
     */
    private static int getClosestKeyword( String pattern , int notIndex )
    {
        int end = -1 ;

        int nextAndIndex = pattern.indexOf( " AND " , notIndex ) ;
        int nextOrIndex = pattern.indexOf( " OR " , notIndex ) ;

        /** Next keyword is OR */
        if( ( nextOrIndex < nextAndIndex && nextOrIndex > 0 ) ||
            nextAndIndex < 0 )
        {
            end = nextOrIndex ;

            /** Next keyword is AND */
        }
        else if( ( nextAndIndex < nextOrIndex && nextAndIndex > 0 ) ||
                 nextOrIndex < 0 )
        {
            end = nextAndIndex ;

        }
        if( end < 1 )
        {
            end = pattern.length() ;
        }
        return end ;
    }

    /**
     * Parse Words that should match the result (AND / OR)
     * @param pattern The search pattern (NOT words are removed)
     * @return Vector holding AND plus OR keywords
     */
    private static Vector parseMustAndMayWords( String pattern )
    {
        String[] andPattern = pattern.split( " AND " ) ;
        Vector and = new Vector() ;
        Vector or ;
        for( int a = 0 ; a < andPattern.length ; a++ )
        {
            or = new Vector() ;
            String[] orPattern = andPattern[a].split( " OR " ) ;
            for( int b = 0 ; b < orPattern.length ; b++ )
            {
                or.add( orPattern[b] ) ;
            }
            and.add( or ) ;
        }
        return and ;
    }

    /**
     * Generate a RegEx from the AND / OR keywords. AND-Keywords
     * and groups of OR-Keywords are surrounded by (.*) to match
     * any result where the Keywords are in.
     *
     * @param keys Vector holding the AND / OR Keywords
     * @return RegEx matching the keywords
     */
    public static Vector generateRegEx( Vector keys )
    {
        Vector regex = new Vector() ;

        String wrapperRegExStart = "(.*)" ;
        String wrapperRegExEnd = "(.*)" ;

        if( RSSSearchPatternParser.searchOnlyWholeWords )
        {
            wrapperRegExStart = wrapperRegExStart + "( )" ;
            wrapperRegExEnd = "( )" + wrapperRegExEnd ;
        }

        /** Generate RegEx for the words that should match the result */
        for( int a = 0 ; a < keys.size() ; a++ )
        {
            Vector and = ( Vector ) keys.get( a ) ;

            /** Only 1 key */
            if( and.size() == 1 )
            {
                StringBuffer sb = new StringBuffer( wrapperRegExStart ) ;
                sb.append( ( String ) and.get( 0 ) ) ;
                sb.append( wrapperRegExEnd ) ;
                regex.add( sb.toString() ) ;
            }

            /** More than 1 key. These are OR statements */
            else
            {
                StringBuffer sb = new StringBuffer( wrapperRegExStart + "(" ) ;
                sb.append( ( String ) and.get( 0 ) ) ;

                for( int b = 1 ; b < and.size() ; b++ )
                {
                    sb.append( "|" ) ;
                    sb.append( ( String ) and.get( b ) ) ;
                }
                sb.append( ")" + wrapperRegExEnd ) ;
                regex.add( sb.toString() ) ;
            }
        }
        return regex ;
    }
}
