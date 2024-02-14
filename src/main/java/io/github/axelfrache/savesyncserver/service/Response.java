package io.github.axelfrache.savesyncserver.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private String fileName;
    private String downloadUrl;
    private String fileType;
    private long fileSize;
}