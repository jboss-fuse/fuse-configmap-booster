package com.redhat.fuse.boosters.configmap;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.openshift.client.OpenShiftClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class HttpRequestKT {

    @ArquillianResource
    OpenShiftClient client;

    @Test
    public void templateTest() throws Exception {
        File template = new File(".openshiftio/application.yaml");
        assertTrue(template.exists());
        HashMap<String, String> templateParameters = new HashMap<String, String>() {
            {
                put("SOURCE_REPOSITORY_URL", "https://github.com/jboss-fuse/fuse-configmap-booster");
            }
        };
        List<HasMetadata> resources = client.templates().load(template).process(templateParameters).getItems();

        for (HasMetadata res : resources) {
            client.resource(res).createOrReplace();
        }

        assertEquals("fuse-configmap-booster", client.buildConfigs().list().getItems().get(0).getMetadata().getName());
    }
}