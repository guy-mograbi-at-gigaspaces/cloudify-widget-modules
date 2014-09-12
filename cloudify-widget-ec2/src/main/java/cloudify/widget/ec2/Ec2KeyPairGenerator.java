/*******************************************************************************
 * Copyright (c) 2013 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package cloudify.widget.ec2;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.KeyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ec2KeyPairGenerator {

private static Logger logger = LoggerFactory.getLogger(Ec2KeyPairGenerator.class);

    String keyPrefix = "cloudify-widget-";

    //todo : add region support
    public Data  generate(String user, String apiKey ) {
        logger.info("generating private key");
        final AWSCredentials credentials = new BasicAWSCredentials(user, apiKey);
        AmazonEC2 ec2 = new AmazonEC2Client(credentials);

        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
        // setting the key name. Important: must be unique!
        String name = "cloudify-widget-" + System.currentTimeMillis();
        createKeyPairRequest.withKeyName(name);
        CreateKeyPairResult createKeyPairResult = ec2.createKeyPair(createKeyPairRequest);



        // Getting the unencrypted PEM-encoded private key
        KeyPair keyPair = createKeyPairResult.getKeyPair();
        String privateKey = keyPair.getKeyMaterial();


        Data data = new Data();
        data.setName(name);
        data.setContent(privateKey);

        logger.info("success");
        return data;

    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }


    public static class Data{
        String name;
        String content;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "name='" + name + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}
