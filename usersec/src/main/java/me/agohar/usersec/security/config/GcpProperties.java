package me.agohar.usersec.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gcp")
public class GcpProperties {
    
    private String projectId;
    private String serviceAccountKeyPath;
    private String domain;
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getServiceAccountKeyPath() {
        return serviceAccountKeyPath;
    }
    
    public void setServiceAccountKeyPath(String serviceAccountKeyPath) {
        this.serviceAccountKeyPath = serviceAccountKeyPath;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
}
