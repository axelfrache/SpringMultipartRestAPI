package io.github.axelfrache.savesyncserver.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class FileInfo {
    private String name;
    private String downloadUrl;
}