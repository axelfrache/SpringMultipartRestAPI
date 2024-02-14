package io.github.axelfrache.savesyncserver.repository;

import io.github.axelfrache.savesyncserver.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository  extends JpaRepository<File, Long> {
}