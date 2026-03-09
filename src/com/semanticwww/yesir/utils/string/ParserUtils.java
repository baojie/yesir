package com.semanticwww.yesir.utils.string ;

import java.io.FileInputStream ;
import java.io.IOException ;
import java.io.InputStreamReader ;
import java.io.Reader ;
import java.net.MalformedURLException ;
import java.net.URI ;
import java.net.URISyntaxException ;
import java.net.URL ;
import java.net.URLConnection ;
import java.nio.ByteBuffer ;
import java.nio.CharBuffer ;
import java.nio.channels.FileChannel ;
import java.nio.charset.Charset ;
import java.util.ArrayList ;
import java.util.List ;
import java.util.Vector ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import javax.swing.text.BadLocationException ;
import javax.swing.text.EditorKit ;
import javax.swing.text.SimpleAttributeSet ;
import javax.swing.text.html.HTML ;
import javax.swing.text.html.HTMLDocument ;
import javax.swing.text.html.HTMLEditorKit ;
import javax.swing.text.html.HTMLEditorKit.ParserCallback ;

/**
 * <p>Title: ParserUtils</p>
 * <p>Description: Some regular expression routines </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Iowa State University</p>
 * @author Jie Bao
 * @version 1.0 - 2003-11-07
 *
 * note:
 * CharBuffer, String, StringBuffer are all implement of CharSequence interface
 *
 */
class PatternEx
{
//  Characters
    public static String BACKSLASH = "\\" ; // The backslash character
    public static String TABLE = "\\t" ; // The tab character ('\u0009')
    public static String LINEFEED = "\\n" ; // The newline (line feed) character ('\\u000')
    public static String RETURN = "\\r" ; // The carriage-return character ('\\u000')
    public static String FORMFEED = "\\f" ; // The form-feed character ('\u000C')
    public static String BELL = "\\a" ; // The alert (bell) character ('\u0007')
    public static String ESCAPE = "\\e" ; // The escape character ('\u001B')
    public static String CONTROL = "\\c" ; // \cx The control character corresponding to x

//  Character classes  , [abc] a , b , or c( simple class )
    public static String IN_SET( char[] chars )
    {
        String total = new String() ;
        for( int i = 0 ; i < chars.length ; i++ )
        {
            total = total + chars[i] ;
        }
        return "[" + total + "]" ;
    }

//  [ ^ abc] Any character except a , b , or c( negation )

    public static String NOT_IN_SET( char[] chars )
    {
        String total = new String() ;
        for( int i = 0 ; i < chars.length ; i++ )
        {
            total = total + chars[i] ;
        }
        return "[^" + total + "]" ;
    }

    //   [a - zA - Z] a through z or A through Z , inclusive( range )
    //   [a - d[m - p]] a through d , or m through p : [a - dm - p] ( union )
    //   [a - z && [def]] d , e , or f( intersection )
    //   [a - z && [ ^ bc]] a through z , except for b and c :
    //   [ad - z] ( subtraction )
    //   [a - z && [ ^ m - p]] a through z ,and not m through p : [a - lq - z] ( subtraction )

//   Predefined character classes
    public static String DIGITS = "\\d" ; // A digit : [0 - 9]
    public static String NON_DIGITS = "\\D" ; //A non - digit : [ ^ 0 - 9]
    public static String WHITESPACE = "\\s" ; // A whitespace character : [\t\n\x0B\f\r]
    public static String NON_WHITESPACE = "\\S" ; // A non - whitespace character : [^\s]
    public static String WORD = "\\w" ; // A word character : [a - zA - Z_0 - 9]
    public static String NON_WORD = "\\W" ; // A non - word character : [^\w]

//   Boundary matchers
    public static String WORD_MIDDLE = "\\B" ; // Match in the middle of a word
    public static String NON_WORD_MIDDLE = "\\b" ; //  Match at the beginning or end of a word
    public static String WORD_BEGIN = "\\<" ; //  Match at the beginning of a word
    public static String WORD_END = "\\>" ; // Match at the end of a word
    public static String LINE_BEGIN = "^" ; // Match at the beginning of a line
    public static String LINE_END = "$" ; //Match at the end of a line
    public static String INPUT_BEGIN = "\\A" ; // The beginning of the input
    public static String INPUT_END = "\\z" ; // The end of the input
    public static String LAST_END = "\\G" ; // The end of the previous match
    public static String INPUT_END_FINAL = "\\Z" ; // The end of the input but for the final terminator , if any

//  Greedy quantifiers
    public static String ANY_ONE = "." ; // Match any one character
    public static String ONE_OR_ZERO = "?" ; // Match any character one time, if it exists
    public static String ZERO_OR_MORE = "*" ; // Match declared element multiple times, if it exists
    public static String ONE_OR_MORE = "+" ; // Match declared element one or more times
    public static String N_TIME( int n ) // Match declared element exactly n times
    {
        return "{" + n + "}" ;
    }

    public static String N_TIME_OR_MORE( int n ) // Match declared element at least n times
    {
        return "{" + n + ",}" ;
    }

    public static String N_TO_M( int n , int m ) // Match declared element at least n but not more than m times
    {
        return "{" + n + "," + m + "}" ;
    }

    // type can be N_TO_M(),N_TIME_OR_MORE(),N_TIME()
    public static String RepeatString( String patternStr , String type )
    {
        return GROUP( patternStr ) + type ;
    }

    // type can be N_TO_M(),N_TIME_OR_MORE(),N_TIME(),ZERO_OR_MORE,ONE_OR_MORE
    public static String RepeatChar( char patternStr , String type )
    {
        return patternStr + type ;
    }

//                Logical operators
    public static String FOLLOWEDBY = "" ; //XY X followed by Y
    public static String OR = "|" ; //    X | Y Either X or Y
    public static String GROUP( String X ) //( X ) X , as a capturing group
    {
        return "(" + X + ")" ;
    }

    public static String GROUP_NONCAPTURING( String X ) //( X ) X , as a capturing group
    {
        return "(?:" + X + ")" ;
    }

//                Back references
    public static String NTH_GROUP( int n ) //\n Whatever the nth capturing group matched
    {
        return "\\" + n ;
    }

    //               Quotation
    public static String QUOTE = "\\" ; // Nothing , but quotes the following character
    public static String QUOTE_BEGIN = "\\Q" ; // Nothing , but quotes all characters until \E
    public static String QUOTE_END = "\\E" ; // Nothing , but ends quoting started by \Q

    //               Special constructs( non - capturing )
    /*                ( ? : X ) X , as a non - capturing group
         ( ? idmsux - idmsux ) Nothing , but turns match flags on - off
                    ( ? idmsux - idmsux : X ) X ,
                    as a non - capturing group with the given flags on - off
                    ( ? = X ) X , via zero - width positive lookahead
                    ( ? !X ) X , via zero - width negative lookahead
                    ( ? <= X ) X , via zero - width positive lookbehind
                    ( ? < !X ) X , via zero - width negative lookbehind
                    ( ? > X ) X , as an independent , non - capturing group
     */

    //flags
    // In this mode, only the '\n' line terminator is recognized in the
    // behavior of ., ^, and $.
    // Should be at the begining of a group
    public static String UNIX_LINES = "(?d)" ; // Enables Unix lines mode.

    //Enables case-insensitive matching. By default, case-insensitive
    //matching assumes that only characters in the US-ASCII charset are
    //being matched. Unicode-aware case-insensitive matching can be enabled
    // by specifying the UNICODE_CASE flag in conjunction with this flag.
    // Should be at the begining of a group
    public static String CASE_INSENSITIVE = "(?i)" ;
    public static String CASE_SENSITIVE = "(?-i)" ;

    //Permits whitespace and comments in pattern. In this mode, whitespace is
    //ignored, and embedded comments starting with # are ignored until the end
    //of a line.
    // Should be at the begining of a group
    public static String COMMENTS = "(?x)" ;

    //Enables multiline mode. In multiline mode the expressions ^ and $
    //match just after or just before, respectively, a line terminator or
    //the end of the input sequence. By default these expressions only match
    // at the beginning and the end of the entire input sequence.
    // Should be at the begining of a group
    public static String MULTILINE = "(?m)" ;
    public static String SINGLELINE = "(?-m)" ;

    //Enables dotall mode. In dotall mode, the expression . matches any
    //character, including a line terminator. By default this expression
    //does not match line terminators.
    // Should be at the begining of a group
    public static String DOTALL = "(?s)" ;

    // Enables Unicode-aware case folding. When this flag is specified then
    // case-insensitive matching, when enabled by the CASE_INSENSITIVE flag,
    // is done in a manner consistent with the Unicode Standard. By default,
    //case-insensitive matching assumes that only characters in the US-ASCII
    // charset are being matched.
    // Should be at the begining of a group
    public static String UNICODE_CASE = "(?u)" ;

    //Enables canonical equivalence. When this flag is specified then two
    //characters will be considered to match if, and only if, their full
    //canonical decompositions match. The expression "a\u030A", for example,
    //will match the string "?" when this flag is specified. By default,
    //matching does not take canonical equivalence into account. There is no
    //embedded flag character for enabling canonical equivalence.
    //public static String CANON_EQ

    //======================== advance tokens
    // all contiguous whitespace characters
    // space. Line terminators are treated like whitespace.
    public static String BLANKS = "\\s+" ;

    // any character at any length
    public static String ANY_WORD = ".+" ;

}

public class ParserUtils
    extends PatternEx
{
    // Private constructor to avoid accidental instantiation.
    protected ParserUtils()
    {}

    /** Converts the contents of a file into a CharSequence
        suitable for use by the regex package.
      example
        Matcher matcher = pattern.matcher(fromFile("infile.txt"));
     */
    public static CharSequence fromFile( String filename )
        throws IOException
    {
        FileInputStream fis = new FileInputStream( filename ) ;
        FileChannel fc = fis.getChannel() ;

        // Create a read-only CharBuffer on the file
        ByteBuffer bbuf = fc.map( FileChannel.MapMode.READ_ONLY , 0 ,
                                  ( int ) fc.size() ) ;
        CharBuffer cbuf = Charset.forName( "8859_1" ).newDecoder().
                          decode( bbuf ) ;
        return cbuf ;
    }

    /**
       Determining If a String Matches a Pattern Exactly
     * @param patternStr
     * @param inputStr
     * @return
     */
    public static boolean isFound( String patternStr ,
                                   CharSequence inputStr )
    {
        // Compile regular expression
        Pattern pattern = Pattern.compile( patternStr ) ;

        // Determine if there is an exact match
        Matcher matcher = pattern.matcher( inputStr ) ;
        return matcher.matches() ; //
    }

    /**
         // Returns a version of the input where all contiguous
         // whitespace characters are replaced with a single
         // space. Line terminators are treated like whitespace.
     * @param inputStr
     * @return
     */
    public static CharSequence removeDuplicateBlanks( CharSequence
        inputStr )
    {
        String patternStr = BLANKS ;
        String replaceStr = " " ;
        Pattern pattern = Pattern.compile( patternStr ) ;
        Matcher matcher = pattern.matcher( inputStr ) ;
        return matcher.replaceAll( replaceStr ) ;
    }

    /**
     * Returns the first substring in input that matches the pattern.
     * Returns null if no match found.
     *
     * @param patternStr
     * @param input
     * @return
     */
    public static String findFirst( String patternStr , CharSequence input )
    {
        Pattern pattern = Pattern.compile( patternStr ) ;
        Matcher matcher = pattern.matcher( input ) ;
        if( matcher.find() )
        {
            return matcher.group() ;
        }
        return null ;
    }

    public static String findFirst( String head , String tail ,
                                    CharSequence input ,
                                    boolean greedy )
    {
        String mid = greedy ? ".*" : ".*?" ;
        return findFirst( head + mid + tail , input ) ;
    }

    /**
     * Returns all substrings in input that matches the pattern.
     * @param patternStr
     * @param input
     * @return
     */
    public static Vector findAll( String patternStr , CharSequence input )
    {
        Vector list = new Vector() ;
        Pattern pattern = Pattern.compile( patternStr ) ;
        Matcher matcher = pattern.matcher( input ) ;
        while( matcher.find() )
        {
            list.add( matcher.group() ) ;
        }
        return list ;
    }

    public static Vector findAll( String head , String tail ,
                                  CharSequence input ,
                                  boolean greedy )
    {
        String mid = greedy ? ".*" : ".*?" ;
        return findAll( head + mid + tail , input ) ;
    }

    public static String replaceAll( String patternStr , String replacementStr ,
                                     CharSequence input )
    {
        Pattern pattern = Pattern.compile( patternStr ) ;
        Matcher matcher = pattern.matcher( input ) ;
        return matcher.replaceAll( replacementStr ) ;
    }

    /**
     * Getting the Links in an HTML Document
     * This method takes a URI which can be either a filename
     * (e.g. file://c:/dir/file.html)or a URL (e.g. http://host.com/page.html)
     * and returns all HREF links in the document.
     *
     * @param uriStr
     * @return
     */
    public static String[] getLinks( String uriStr )
    {
        List result = new ArrayList() ;

        try
        {
            // Create a reader on the HTML content
            URL url = new URI( uriStr ).toURL() ;
            URLConnection conn = url.openConnection() ;
            Reader rd = new InputStreamReader( conn.getInputStream() ) ;

            // Parse the HTML
            EditorKit kit = new HTMLEditorKit() ;
            HTMLDocument doc = ( HTMLDocument ) kit.createDefaultDocument() ;
            kit.read( rd , doc , 0 ) ;

            // Find all the A elements in the HTML document
            HTMLDocument.Iterator it = doc.getIterator( HTML.Tag.A ) ;
            while( it.isValid() )
            {
                SimpleAttributeSet s = ( SimpleAttributeSet ) it.getAttributes() ;

                String link = ( String ) s.getAttribute( HTML.Attribute.HREF ) ;
                if( link != null )
                {
                    // Add the link to the result list
                    result.add( link ) ;
                }
                it.next() ;
            }
        }
        catch( MalformedURLException e )
        {
        }
        catch( URISyntaxException e )
        {
        }
        catch( BadLocationException e )
        {
        }
        catch( IOException e )
        {
        }

        // Return all found links
        return( String[] ) result.toArray( new String[result.size()] ) ;
    }

    /**
         // Getting the Text in an HTML Document
         // This method takes a URI which can be either a filename (e.g. file://c:/dir/file.html)
         // or a URL (e.g. http://host.com/page.html) and returns all text in the document.
     * @param uriStr
     * @return
     */
    public static String getText( String uriStr )
    {
        final StringBuffer buf = new StringBuffer( 1000 ) ;

        try
        {
            // Create an HTML document that appends all text to buf
            HTMLDocument doc = new HTMLDocument()
            {
                public HTMLEditorKit.ParserCallback getReader( int pos )
                {
                    return new HTMLEditorKit.ParserCallback()
                    {
                        // This method is whenever text is encountered in the HTML file
                        public void handleText( char[] data , int pos )
                        {
                            buf.append( data ) ;
                            buf.append( '\n' ) ;
                        }
                    } ;
                }
            } ;

            // Create a reader on the HTML content
            URL url = new URI( uriStr ).toURL() ;
            URLConnection conn = url.openConnection() ;
            Reader rd = new InputStreamReader( conn.getInputStream() ) ;

            // Parse the HTML
            EditorKit kit = new HTMLEditorKit() ;
            kit.read( rd , doc , 0 ) ;
        }
        catch( Exception e )
        {
            System.out.println( e.getMessage() ) ;
            e.printStackTrace( System.err ) ;
        }

        // Return the text
        return buf.toString() ;
    }

    // Returns a pattern where all punctuation characters are escaped.
    /**
         String patternStr = "i.e.";
         boolean matchFound = Pattern.matches(patternStr, "i.e.");// true
         matchFound = Pattern.matches(patternStr, "ibex");        // true
         // Escape the pattern
         patternStr = escapeRE(patternStr);                       // i\.e\.
         matchFound = Pattern.matches(patternStr, "i.e.");        // true
         matchFound = Pattern.matches(patternStr, "ibex");        // false
     */
    public static String escapeRE( String str )
    {
        Pattern escaper = Pattern.compile( "([^a-zA-z0-9])" ) ;
        return escaper.matcher( str ).replaceAll( "\\\\$1" ) ;
    }

    /**
     * Reading Lines from a String Using a Regular Expression
     * The lines can be terminated with any of the legal line termination
     *  character sequences: \r, \r\n, or \n.
     *
     * @return
     */
    public static Vector readLines( CharSequence inputStr ,
                                    boolean keepLineTerminator )
    {
        Vector list = new Vector() ;
        // Compile the pattern
        String patternStr = "^(.*)$" ;
        Pattern pattern = Pattern.compile( patternStr , Pattern.MULTILINE ) ;
        Matcher matcher = pattern.matcher( inputStr ) ;

        // Read the lines
        while( matcher.find() )
        {
            if( keepLineTerminator )
            {
                // Get the line with the line termination character sequence
                list.add( matcher.group( 0 ) ) ;
            }
            else
            {
                // Get the line without the line termination character sequence
                list.add( matcher.group( 1 ) ) ;
            }
        }
        return list ;
    }

    /**
     * return the content of the highest <tag>...</tag> region
     *
     * @param inputStr
     * @return null if not found, tag and content if found
     */
    public static Vector getTopNestdBlock( CharSequence inputStr )
    {
        // Compile regular expression with a back reference to group 1
        String patternStr = "(?s)<(\\S+?).*?>(.*)</\\1>" ;
        Pattern pattern = Pattern.compile( patternStr ) ;
        Matcher matcher = pattern.matcher( inputStr ) ;

// Get tagname and contents of tag
        boolean matchFound = matcher.find() ; // true
        if( matchFound )
        {
            Vector list = new Vector() ;
            list.add( matcher.group( 1 ) ) ; // tag
            list.add( matcher.group( 2 ) ) ; //  content
            return list ;
        }
        else
        {
            return null ;
        }
    }

    /**
     * return all tags begin with 'tag'
     * no repeated nesting is detected, like <tag><tag></tag></tag>
     * if this case happens, only the top tag nesting is added into list.
     * @param tag
     * @param input
     * @return
     *
     * NOT implemented yet
     */
    public static Vector getNestdBlock( String tag , CharSequence input ,
                                        boolean greedy )
    {
        String head = "<" + tag + ".*?>" ;
        String tail = "</" + tag + ">" ;
        return getNestdBlock( head , tail , input , greedy ) ;
    }

    /**
     * get all block with given head and tail
     * head and tail are not included
     *
     * @param head
     * @param tail
     * @param input
     * @param greedy
     * @return
     */
    public static Vector getNestdBlock( String head , String tail ,
                                        CharSequence input , boolean greedy )
    {
        String mid = greedy ? "(.*)" : "(.*?)" ;
        String patternStr = head + mid + tail ;

        Vector list = new Vector() ;
        Pattern pattern = Pattern.compile( patternStr ) ;
        Matcher matcher = pattern.matcher( input ) ;
        while( matcher.find() )
        {
            list.add( matcher.group( 1 ) ) ;
        }
        return list ;
    }

    // Returns a version of the input where all line terminators
    // are replaced with a space.
    public static CharSequence removeLineTerminators( CharSequence inputStr )
    {
        String patternStr = "(?m)$^|[\\r\\n]+\\z" ;
        String replaceStr = " " ;
        Pattern pattern = Pattern.compile( patternStr ) ;
        Matcher matcher = pattern.matcher( inputStr ) ;
        return matcher.replaceAll( replaceStr ) ;
    }

    public static Vector convertToParagraphs( CharSequence inputStr )
    {
        Vector list = new Vector() ;
        String patternStr = "(?<=(\r\n|\r|\n))([ \\t]*$)+" ;

        // Parse the input into paragraphs
        String[] paras = Pattern.compile( patternStr ,
                                          Pattern.MULTILINE ).split( inputStr ) ;

        // Get paragraphs
        for( int i = 0 ; i < paras.length ; i++ )
        {
            list.add( paras[i] ) ;
        }
        return list ;
    }

    public static void main( String[] args )
    {
        /*        String str =
                    "<html>this<html> is     a  <b h=8>tes<b>ttes</b>ttesttest</b></html>     \r\n\n     \n<b>string</b></html>" ;
                Vector list = convertToParagraphs( str ) ;
                //       Vector list = getNestdBlock("b",str);
//          System.out.println((str));
                for( int i = 0 ; i < list.size() ; i++ )
                {
                    System.out.println( i + ":  " + list.elementAt( i ) ) ;
                }
         */
    }
}