package cloudify.widget.common;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/26/14
 * Time: 4:16 PM
 */
public class TestXeger {

    @Test
    public void testXeger(){
        System.out.println("StringUtils.generateRandomFromRegex = " + StringUtils.generateRandomFromRegex("[a-zA-Z0-9]{10}"));
    }
}
