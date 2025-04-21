package com.repsy_case.egemen_sevgi.dto;
import java.util.List;

import lombok.Data;

@Data
public class MetaData {
    private String name;
    private String version;
    private String author;
    private List<Dependency> dependencies;
    

    @Data
    public static class Dependency {
        private String package_;
        private String version;
        
        
       
    }
}