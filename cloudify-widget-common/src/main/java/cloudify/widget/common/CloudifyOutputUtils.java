package cloudify.widget.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/11/14
 * Time: 7:30 PM
 */
public class CloudifyOutputUtils {

    private static Logger logger = LoggerFactory.getLogger(CloudifyOutputUtils.class);

    public static String getBootstrapIp(String output) {
        String restUrlRegex = "Rest service is available at: (http[s]*://(.*):8100)";
        Pattern restPattern = Pattern.compile(restUrlRegex);
        Matcher restMatcher = restPattern.matcher(output);
        if (!restMatcher.find()) {
            return null;
        }
        String restUrl = restMatcher.group(1);
        return restUrl.substring(restUrl.indexOf("//") + 2, restUrl.lastIndexOf(':'));

    }

    public static List<String> split( String content, String regex )
    {
        if ( content == null ){
            return new ArrayList<String>(0);
        }

        String[] splitStr = content.split( regex );
        return Arrays.asList(splitStr);
    }

    // guy - TODO _ this is an ugly method. it has 2 loops and an "if/else" pattern. We need to organize this somehow.
    // lets use a prototype bean that has a list (order matters) of filters.
    // each filter can return null - which means, remove this line.
    // the filters can have state - as they can be prototypes as well.
    // we can unite the "filterOutputLines" and "filterOutputStrings" to a regex.
    /** Format string output by different patters */
    public static List<String> formatOutput( String str, String substringPrefix , Collection<String> filterOutputLines, Collection<String> filterOutputStrings )
    {
        logger.debug(str);

        List<String> list = split(str, "\n");
        for( int i=0; i < list.size(); i++ )
        {
            // remove by filter
            for( String f : filterOutputLines )
            {
                if ( list.get(i).contains(f) )
                    list.set(i, "");
            }

            for( String f : filterOutputStrings )
            {
                String s = list.get(i);
                if ( s.contains( f ) )
                    list.set(i, StringUtils.replace(s, f, "").trim());
            }

            // remove by pattern
            String s = list.get(i);
            if ( s.contains(substringPrefix) )
            {
                int start = s.indexOf(substringPrefix);
                String afterStr = s.substring(start + substringPrefix.length(), s.length()).trim();
                list.set(i, afterStr);
            }
        }// for

        logger.debug("starting output format phase 2");
        List<String> newList = new ArrayList<String>();
        for( String s : list )
        {
            logger.debug("handling line [{}]", s);
            if ( s.toLowerCase().contains("operation failed")){
                logger.debug("detected operation failed");
                newList.add("Operation Failed");
                break;
            }
            if ( s.startsWith("[") ){ // guy - ugly formatting logic..
                String substr = s.substring( 1, s.indexOf("]"));
                if ( substr.split("\\.").length > 2 ){ // detect lines that have [ip]
                    logger.debug("removing ip square-brackets from start of line");
                    continue;
                }
            }
            if (s.toLowerCase().startsWith("successfully created cloudify manager on provider")) {
                s = "Cloudify manager created successfully.";
            }
            // trim empty lines, or with a single dot, or lines starting with "->" / "- >" / "-    >"
            if ( !s.equals("") && !s.equals(".") && !s.matches("^\\-\\s*>.*") ){
                newList.add( s );
            }
        }

        return newList;
    }

}
