package ru.netology.diplomproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.diplomproject.model.FileCloud;

import java.util.List;

public interface FileRepository extends JpaRepository<FileCloud,Long> {

    List<FileCloud> findFileDataByUserDataId(long user_id);
    FileCloud findByFileName(String fileName);

    Page<FileCloud> findAllByUserDataId(long user_id, Pageable pageable);

    void save(FileCloud fileData);

    void delete(FileCloud fileCloud);
}
