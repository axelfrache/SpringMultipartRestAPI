package io.github.axelfrache.savesyncserver.service.impl;

import io.github.axelfrache.savesyncserver.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path root = Paths.get("storage");

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize storage", e);
        }
    }

    @Override
    public void save(MultipartFile file) {
        String filename = Objects.requireNonNull(file.getOriginalFilename());
        try {
            Path destinationFile = this.root.resolve(Paths.get(filename)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.root.toAbsolutePath())) {
                // Security check
                throw new IllegalStateException("Cannot store file outside current directory.");
            }
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file.", e);
        }
    }

    @Override
    public void saveAll(MultipartFile file, String relativePath) {
        try {
            Path destinationPath = this.root.resolve(relativePath).normalize().toAbsolutePath();
            Files.createDirectories(destinationPath.getParent());

            if (!destinationPath.getParent().startsWith(this.root.toAbsolutePath())) {
                throw new IllegalStateException("Cannot store file outside current directory.");
            }
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file.", e);
        }
    }

    @Override
    public Resource read(String fileName) {
        try {
            Path file = root.resolve(fileName).normalize().toAbsolutePath();
            if (!file.startsWith(root.toAbsolutePath())) {
                throw new IllegalStateException("Resolution of the path is outside the storage directory");
            }
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalStateException("File not found or not readable");
            }
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Failed to read file", e);
        }
    }

    @Override
    public boolean delete(String fileName) {
        try {
            Path file = root.resolve(fileName);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to delete file", e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> readAll() {
        try {
            return Files.walk(this.root, 1)
                    .filter(path -> !path.equals(this.root))
                    .map(this.root::relativize);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read stored files", e);
        }
    }

}