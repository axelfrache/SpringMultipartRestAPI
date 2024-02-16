package io.github.axelfrache.savesyncserver.service;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    void init();

    void save(MultipartFile file);

    void saveAll(List<MultipartFile> files);

    Resource read(String filename);

    boolean delete(String filename);

    void deleteAll();

    Stream<Path> readAll();
}