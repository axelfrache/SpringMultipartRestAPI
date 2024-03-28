package io.github.axelfrache.savesyncserver.controller;

import io.github.axelfrache.savesyncserver.model.FileInfo;
import io.github.axelfrache.savesyncserver.response.UploadResponse;
import io.github.axelfrache.savesyncserver.service.FileStorageService;
import io.github.axelfrache.savesyncserver.response.DeleteResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/savesync")
public class FileInfoController {

    @Autowired
    FileStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<List<UploadResponse>> uploadFiles(@RequestPart("files") MultipartFile[] files) {
        String backupId = String.valueOf(System.currentTimeMillis());
        String versionPath = "storage/" + backupId;

        List<UploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String filePath = versionPath + "/" + file.getOriginalFilename();
                storageService.saveAll(file, filePath);

                String downloadUrl = MvcUriComponentsBuilder
                        .fromMethodName(FileInfoController.class, "getFile", filePath).build().toUri().toString();

                responses.add(new UploadResponse(file.getOriginalFilename(), downloadUrl, file.getContentType(), file.getSize()));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.readAll().map(path -> {
            String fileName = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FileInfoController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(fileName, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Resource file = storageService.read(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/files/{fileName:.+}")
    public ResponseEntity<DeleteResponse> deleteFile(@PathVariable String fileName) {

        try {
            boolean existed = storageService.delete(fileName);

            if (existed) {
                return ResponseEntity.status(HttpStatus.OK).body(new DeleteResponse("File " + fileName + " has been deleted successfully"));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DeleteResponse("File " + fileName + " not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DeleteResponse("Failed to delete " + fileName + ": " + e.getMessage()));
        }
    }

    @GetMapping("/backups")
    public ResponseEntity<List<String>> getBackups() {
        File storageDir = new File("storage/");
        String[] backups = storageDir.list((current, name) -> new File(current, name).isDirectory());

        if (backups != null) {
            return ResponseEntity.ok(Arrays.asList(backups));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}