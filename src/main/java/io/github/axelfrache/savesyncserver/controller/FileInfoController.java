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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/savesync")
public class FileInfoController {

    @Autowired
    FileStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<List<UploadResponse>> uploadFile(@RequestParam("file") MultipartFile[] files) {
        List<UploadResponse> responseList = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                assert fileName != null;
                String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/savesync/files/")
                        .path(fileName)
                        .toUriString();
                storageService.save(file);
                UploadResponse response = new UploadResponse(fileName, downloadUrl, file.getContentType(), file.getSize());
                responseList.add(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(responseList);
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
}