package cloudify.widget.pool.manager;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 2:58 AM
 */
public class BootstrapMonitor {
    private static Logger logger = LoggerFactory.getLogger(BootstrapMonitor.class);

    public String applicationUrlPattern;

    public BootstrapMonitor() {

    }

    public HttpClient getHttpClient() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//                    logger.info("I trust");
                    return true;
                }
            });
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                    sslsf).build();
            return httpclient;
        } catch (Exception e) {
            throw new RuntimeException("unable to create httpclient", e);
        }
    }

    public boolean isCloudifyManagementAvailable(String ip) {
        try {
            HttpResponse response = get("http://" + ip + ":8099");

            int statusCode = response.getStatusLine().getStatusCode();
            logger.trace("status is [{}]", statusCode);

            String body = IOUtils.toString(response.getEntity().getContent());

            return statusCode == 200 && body.contains("gs_webui.nocache.js");

        } catch (Exception e) {
            logger.debug("unable to check management status", e);
            return false;
        }

    }

    public HttpResponse get(String url) throws IOException {
        // Create an instance of HttpClient.


        HttpClient client = getHttpClient();

        HttpUriRequest request = new HttpGet(url);

        // Create a method instance.

        HttpResponse response = client.execute(request);
        // Provide custom retry handler is necessary
        return response;
    }

    public boolean isApplicationAvailable(String ip) {
        try {
            String url = String.format(applicationUrlPattern, ip);

            HttpResponse response = get(url);
            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("response on [{}] generated status [{}]", url, statusCode);

            return statusCode == 200;

        } catch(SSLException e){
            return e.getMessage().contains("certificate didn't match");
        }catch (Exception e) {
            logger.debug("unable to check if application is online", e);
            return false;
        }
    }

    public String getApplicationUrlPattern() {
        return applicationUrlPattern;
    }

    public void setApplicationUrlPattern(String applicationUrlPattern) {
        this.applicationUrlPattern = applicationUrlPattern;
    }
}
