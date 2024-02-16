package io.github.axelfrache.savesyncserver.service;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    public void init();

    public void save(MultipartFile file);

    public void saveAll(List<MultipartFile> files);

    public Resource load(String filename);

    public boolean delete(String filename);

    public void deleteAll();

    public Stream<Path> loadAll();
}
