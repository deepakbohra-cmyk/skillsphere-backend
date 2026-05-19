package com.skillsphere.skillsphere.certificate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skillsphere.skillsphere.certificate.entity.Certificate;

import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    
}
