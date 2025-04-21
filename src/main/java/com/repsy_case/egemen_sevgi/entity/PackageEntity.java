package com.repsy_case.egemen_sevgi.entity;




import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "packages")
@Data
public class PackageEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String version;
    
    private String author;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;
    
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "package_id")
    private List<DependencyEntity> dependencies = new ArrayList<>();
    
   
    
    

}