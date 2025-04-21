package com.repsy_case.egemen_sevgi.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repsy_case.egemen_sevgi.entity.PackageEntity;


@Repository
public interface PackageEntityRepository extends JpaRepository<PackageEntity, Long> {
    boolean existsByNameAndVersion(String name, String version);
}
