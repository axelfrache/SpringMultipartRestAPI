package io.github.axelfrache.savesyncserver.controller;

import io.github.axelfrache.savesyncserver.model.File;
import io.github.axelfrache.savesyncserver.service.FileService;
import io.github.axelfrache.savesyncserver.service.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @Autowired
    private ResourceLoader resourceLoader;

    /** This method is used for uploading a single file to the database
     *
     * @param file
     * @return Response
     */
    @PostMapping("/single/base")
    public Response uploadFile(@RequestParam("file") MultipartFile file) throws Exception {

        File attachment = null;
        String downloadUrl = "";
        attachment = fileService.saveAttachment(file);
        downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(attachment.getId())
                .toUriString();

        return new Response(attachment.getFileName(),
                downloadUrl,
                file.getContentType(),
                file.getSize());
    }


    /** This method is used for uploading multiple files to the database
     *
     * @param files
     * @return List<Response>
     */
    @PostMapping("/multiple/base")
    public List<Response> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<Response> responseList = new ArrayList<>();
        for (MultipartFile file : files) {
            File attachment = fileService.saveAttachment(file);
            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(attachment.getId())
                    .toUriString();
            Response response = new Response(attachment.getFileName(),
                    downloadUrl,
                    file.getContentType(),
                    file.getSize());
            responseList.add(response);
        }
        return responseList;
    }

    /** This method is used for retrieving all the files from the database
     *
     * @return ResponseEntity
     */
    @GetMapping("/all")
    public ResponseEntity<List<Response>> getAllFiles() {
        List<File> files = fileService.getAllFiles();
        List<Response> Responses = files.stream().map(file -> {
            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(file.getId())
                    .toUriString();
            return new Response(file.getFileName(),
                    downloadUrl,
                    file.getFileType(),
                    file.getData().length);
        }).collect(Collectors.toList());

        return ResponseEntity.ok().body(Responses);
    }

    //for uploading the SINGLE file to the File System
    @PostMapping("/single/file")
    public ResponseEntity<Response> handleFileUpload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            file.transferTo(new java.io.File("D:/Folder" + fileName));
            assert fileName != null;
            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(fileName)
                    .toUriString();
            Response response = new Response(fileName,
                    downloadUrl,
                    file.getContentType(),
                    file.getSize());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //for uploading the MULTIPLE file to the File system
    @PostMapping("/multiple/file")
    public ResponseEntity<List<Response>> handleMultipleFilesUpload(@RequestParam("files") MultipartFile[] files) {
        List<Response> responseList = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try {
                file.transferTo(new java.io.File("D:/Folder" + fileName));
                assert fileName != null;
                String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/download/")
                        .path(fileName)
                        .toUriString();
                Response response = new Response(fileName,
                        downloadUrl,
                        file.getContentType(),
                        file.getSize());
                responseList.add(response);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path fileStorageLocation = Paths.get("D:\\Folder").toAbsolutePath().normalize();
            Path filePath = fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Le fichier n'existe pas ou n'est pas lisible : " + filename);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la tentative de lecture du fichier : " + filename, ex);
        }
    }

}