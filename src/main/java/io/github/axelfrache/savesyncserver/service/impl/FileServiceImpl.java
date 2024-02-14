package io.github.axelfrache.savesyncserver.service.impl;

import io.github.axelfrache.savesyncserver.model.File;
import io.github.axelfrache.savesyncserver.repository.FileRepository;
import io.github.axelfrache.savesyncserver.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileRepository fileRepository;
    @Override
    public File saveAttachment(MultipartFile file) throws Exception {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {

            if(fileName.contains("..")) {
                throw  new Exception("Filename contains invalid path sequence " + fileName);
            }
            if (file.getBytes().length > (1024 * 1024)) {
                throw new Exception("File size exceeds maximum limit");
            }
            File attachment = new File(fileName, file.getContentType(), file.getBytes());
            return fileRepository.save(attachment);
        } catch (MaxUploadSizeExceededException e) {
            throw new MaxUploadSizeExceededException(file.getSize());
        } catch (Exception e) {
            throw new Exception("Could not save File: " + fileName);
        }
    }
    @Override
    public void saveFiles(MultipartFile[] files) {

        Arrays.asList(files).forEach(file -> {
            try {
                saveAttachment(file);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    @Override
    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }
}