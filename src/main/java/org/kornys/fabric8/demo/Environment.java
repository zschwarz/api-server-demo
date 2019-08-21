package org.kornys.fabric8.demo;

import io.fabric8.kubernetes.client.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {
    private static Environment instance;
    private static Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    private String token = System.getenv("KUBERNETES_API_TOKEN");
    private String url = System.getenv("KUBERNETES_API_URL");
    private final String namespace = System.getenv("KUBERNETES_NAMESPACE");

    private Environment() {
        if (token == null || url == null) {
            Config config = Config.autoConfigure(System.getenv().getOrDefault("TEST_CLUSTER_CONTEXT", null));
            token = config.getOauthToken();
            url = config.getMasterUrl();
        }
        String debugFormat = "{}:{}";
        LOGGER.info(debugFormat, "KUBERNETES_API_TOKEN", token);
        LOGGER.info(debugFormat, "KUBERNETES_API_URL", url);
        LOGGER.info(debugFormat, "KUBERNETES_NAMESPACE", namespace);
    }

    public static synchronized Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public String getUrl() {
        return url;
    }

    public String getNamespace() {
        return namespace;
    }
}
