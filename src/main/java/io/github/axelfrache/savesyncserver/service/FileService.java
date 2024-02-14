package io.github.axelfrache.savesyncserver.service;

import java.util.List;

import io.github.axelfrache.savesyncserver.model.File;
import org.springframework.web.multipart.MultipartFile;
public interface FileService {

    File saveAttachment(MultipartFile file) throws Exception;
    void saveFiles(MultipartFile[] files) throws Exception;
    List<File> getAllFiles();
}