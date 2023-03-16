package com.loonds.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class FirebaseConfig {

    ConcurrentMap<String, FirebaseMessaging> configMap = new ConcurrentHashMap<>();

    private final ResourceLoader resourceLoader;
//    private final TenantRepository tenantRepository;

//    @PostConstruct
//    public void init() {
//        tenantRepository.findAll().stream()
//                .filter(tenant -> StringUtils.hasText(tenant.getFcmKey()))
//                .forEach(tenant -> {
//                    try {
//                        configMap.put(tenant.getId(), firebaseMessaging(tenant.getId(), tenant.getFcmKey()));
//                    } catch (Exception e) {
//                        log.error("Error loading firebase configuration", e);
//                    }
//                });
//    }

    public FirebaseMessaging getConfig(String tenantId) {
        return configMap.get(tenantId);
    }

    private FirebaseMessaging firebaseMessaging(String tenantId, String keyFileName) throws IOException {
        try (final InputStream inputStream = resourceLoader.getResource("classpath:" + keyFileName).getInputStream()) {
            final FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build();
            final FirebaseApp firebaseApp = FirebaseApp.initializeApp(firebaseOptions, tenantId + "-app");
            log.info("Initialized firebase app successfully");
            return FirebaseMessaging.getInstance(firebaseApp);
        }
    }
}
