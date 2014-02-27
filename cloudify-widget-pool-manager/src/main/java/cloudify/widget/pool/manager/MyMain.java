package cloudify.widget.pool.manager;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerApi;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 2:42 AM
 */
public class MyMain {
    private static Logger logger = LoggerFactory.getLogger(MyMain.class);
    public static void main(String[] args) throws IOException {
        ClassPathResource resource = new ClassPathResource("conf/dev/me.properties");
        File file = resource.getFile();
        Properties props = new Properties();
        props.load(new FileReader(file));
        String profile = props.getProperty("profile");
        logger.info("using profile [{}]", profile);


        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.getEnvironment().setActiveProfiles("standalone");
        ctx.load("*Context.xml");
        ctx.refresh();

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles(profile.split(","));
        applicationContext.setConfigLocation("app-context.xml");
        applicationContext.refresh();

//        PoolMonitor monitor = applicationContext.getBean(PoolMonitor.class);
//        PoolStatus poolStatus = monitor.getPoolStatus();
//        logger.info("status is [{}]", poolStatus);

        runTask(applicationContext, "monitorPool");

//        runTask(applicationContext, "cleanPool");

    }

    static private void runTask( ApplicationContext applicationContext, String name ){
        applicationContext.getBean(name, Runnable.class).run();
    }


}
