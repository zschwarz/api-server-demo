package org.kornys.api_server.demo;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.HttpClientUtils;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import java.util.Collections;

class KubeClient {
    private KubernetesClient client;
    private static KubeClient instance;

    private KubeClient() {
        Config config = new ConfigBuilder().withMasterUrl(Environment.getInstance().getUrl())
                .withOauthToken(Environment.getInstance().getToken())
                .build();

        OkHttpClient httpClient = HttpClientUtils.createHttpClient(config);
        httpClient = httpClient.newBuilder().protocols(Collections.singletonList(Protocol.HTTP_1_1)).build();
        client = new DefaultOpenShiftClient(httpClient, new OpenShiftConfig(config));
    }

    static synchronized KubeClient getInstance() {
        if (instance == null) {
            instance = new KubeClient();
        }
        return instance;
    }

    KubernetesClient getClient() {
        return client;
    }
}
