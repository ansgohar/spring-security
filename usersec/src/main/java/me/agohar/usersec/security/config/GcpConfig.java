package me.agohar.usersec.security.config;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.directory.Directory;
import com.google.api.services.directory.DirectoryScopes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GcpConfig {

    private final GcpProperties gcpProperties;
    
    public GcpConfig(GcpProperties gcpProperties) {
        this.gcpProperties = gcpProperties;
    }

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return ServiceAccountCredentials.fromStream(
            new FileInputStream(gcpProperties.getServiceAccountKeyPath())
        ).createScoped(Collections.singletonList(DirectoryScopes.ADMIN_DIRECTORY_GROUP));
    }

    @Bean
    public Directory directoryService(GoogleCredentials credentials) throws GeneralSecurityException, IOException {
        return new Directory.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            request -> {
                try {
                    credentials.refreshIfExpired();
                    request.getHeaders().setAuthorization("Bearer " + credentials.getAccessToken().getTokenValue());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to refresh credentials", e);
                }
            }
        ).setApplicationName("GCP Identity Security Service").build();
    }

    public String getProjectId() {
        return gcpProperties.getProjectId();
    }

    public String getDomain() {
        return gcpProperties.getDomain();
    }
}