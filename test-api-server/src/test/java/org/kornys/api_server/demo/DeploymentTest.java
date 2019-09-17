package org.kornys.api_server.demo;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.api.model.extensions.IngressBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

class DeploymentTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentTest.class);
    private String namespace = "zuz";
    private String nameASD = "api-server-demo";
    KubernetesClient client = KubeClient.getInstance().getClient();


    @BeforeEach
    void setup() throws InterruptedException {
        LOGGER.info("setup");
        client.namespaces().createOrReplace(new NamespaceBuilder().withNewMetadata().withName(namespace).endMetadata().build());
        startDeployment();
        checkDeploymentReady();
    }

    @AfterEach
    void cleanup() {
        LOGGER.info("cleanup");
        client.apps().deployments().withName(nameASD).delete();
        client.services().withName(nameASD).delete();
        client.extensions().ingresses().withName(nameASD).delete();
        client.namespaces().withName(namespace).delete();
    }

    @Test
    void testScalePod() {
        client.apps().deployments().inNamespace(namespace).withName(nameASD).scale(10, true);
        List<Pod> pods = client.pods().inNamespace(namespace).list().getItems();
        assertEquals(10, pods.size());
    }

    @Test
    void testHttpRequest() throws IOException {
        Ingress ingress = client.extensions().ingresses().inNamespace(namespace).withName(nameASD).get();
        String endpointURL = "http://" + ingress.getSpec().getRules().get(0).getHost() + ":80";
        LOGGER.info(endpointURL);

        URL url = new URL(endpointURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream response = connection.getInputStream();

        StringBuilder content;

        try (BufferedReader input = new BufferedReader(new InputStreamReader(response))) {
            String line;
            content = new StringBuilder();
            while ((line = input.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
        } finally {
            connection.disconnect();
        }

        LOGGER.info(content.toString());
        //assertThat(content.toString(), startsWith("Hello from http server"));
    }

    private void startDeployment () {
        LOGGER.info("Starting test deployment");

        client.configMaps().inNamespace(namespace).createOrReplace(getConfigMap());
        client.apps().deployments().inNamespace(namespace).create(getApiServerDeployment());
        client.services().inNamespace(namespace).create(getApiServerService());
        client.extensions().ingresses().inNamespace(namespace).create(getApiServerIngress());
    }

    private void checkDeploymentReady() throws InterruptedException {
        boolean deployed = false;
        while (!deployed) {
            List<Pod> pods = client.pods().inNamespace(namespace).list().getItems();
            for (Pod pod : pods) {
                LOGGER.info("{} status {}", pod.getMetadata().getName(), pod.getStatus().getPhase());
                if (pod.getStatus().getPhase().equals("Running")) {
                    deployed = true;
                } else {
                    deployed = false;
                } }
            Thread.sleep(1000);
        }
    }

    private Deployment getApiServerDeployment() {
        return new DeploymentBuilder()
                .withNewMetadata()
                .withNewName(nameASD)
                .endMetadata()
                .withNewSpec()
                .withReplicas(3)
                .withNewSelector()
                .addToMatchLabels("app", nameASD)
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", nameASD)
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(nameASD)
                .withImage("docker.io/zschwarz/api-server-demo:latest")
                .addNewPort()
                .withContainerPort(8899)
                .endPort()
                .addNewVolumeMount()
                .withName(nameASD)
                .withMountPath("/opt/pepa")
                .endVolumeMount()
                .endContainer()
                .addNewVolume()
                .withName(nameASD)
                .withNewConfigMap()
                .withName(nameASD)
                .endConfigMap()
                .endVolume()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
    }

    private Service getApiServerService() {
        Service service = new ServiceBuilder()
                .withNewMetadata()
                .withName(nameASD)
                .endMetadata()
                .withNewSpec()
                .addToSelector("app", nameASD)
                .addNewPort()
                .withName("http")
                .withPort(8899)
                .withProtocol("TCP")
                .withTargetPort(new IntOrString(8899))
                .endPort()
                .endSpec()
                .build();

        return service;
    }

    private Ingress getApiServerIngress() {
        Ingress ingress = new IngressBuilder()
                .withNewMetadata()
                .withName(nameASD)
                .endMetadata()
                .withNewSpec()
                .addNewRule()
                .withHost("api-server-demo.<ip-address>.nip.io")
                .withNewHttp()
                .withPaths()
                .addNewPath()
                .withPath("/")
                .withNewBackend()
                .withServiceName(nameASD)
                .withServicePort(new IntOrString(8899))
                .endBackend()
                .endPath()
                .endHttp()
                .endRule()
                .endSpec()
                .build();

        return ingress;

    }

    private ConfigMap getConfigMap(){

        return new ConfigMapBuilder()
                .withNewMetadata()
                .withName(nameASD)
                .endMetadata()
                .addToData("file.txt", "cau")
                .build();
    }
}