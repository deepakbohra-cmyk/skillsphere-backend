package com.skillsphere.skillsphere.certificate.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/certificates")
@Tag(name = "Certificate Management", description = "APIs for certificate creation, retrieval, update and deletion")
public class CertificateController {
    
}
