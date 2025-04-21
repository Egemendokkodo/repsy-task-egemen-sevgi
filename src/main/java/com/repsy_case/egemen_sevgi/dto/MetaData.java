package com.repsy_case.egemen_sevgi.dto;
import java.util.List;

public class MetaData {
    private String name;
    private String version;
    private String author;
    private List<Dependency> dependencies;
    

    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public List<Dependency> getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
    
    public static class Dependency {
        private String package_;
        private String version;
        
        
        
        public String getPackage() {
            return package_;
        }
        
        public void setPackage(String package_) {
            this.package_ = package_;
        }
        
        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
    }
}