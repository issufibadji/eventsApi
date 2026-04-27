package com.eventostec.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    @Override
    public String uploadFile(MultipartFile file) {
        log.info("Modo local: upload de '{}' ignorado", file.getOriginalFilename());
        return "";
    }
}
