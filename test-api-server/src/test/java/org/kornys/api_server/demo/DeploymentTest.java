package org.kornys.api_server.demo;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DeploymentTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentTest.class);

    @Test
    void testGetPods() {
        LOGGER.info("Starting test deployment");
        KubernetesClient client = KubeClient.getInstance().getClient();

    }

    private Deployment getApiServerDeployment() {
        return null; //TODO: Create kubernetes deployment
    }

    private Service getApiServerService() {
        return null; //TODO: Create kubernetes service for api-server-demo app
    }

    private Ingress getApiServerIngress() {
        return null; //TODO: Create kubernetes ingress for api-server service
    }
}
