package io.github.axelfrache.savesyncserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    private String filename;
    private String downloadUrl;
    private String contentType;
    private long size;
}