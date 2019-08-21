package org.kornys.fabric8.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ResourceTest {
    private static Logger LOGGER = LoggerFactory.getLogger(ResourceTest.class);

    @Test
    void testGetPods() {
        KubeClient kube = new KubeClient();
        kube.getClient().pods().inAnyNamespace().list().getItems().forEach(pod -> LOGGER.info(pod.getMetadata().getName()));
    }
}
