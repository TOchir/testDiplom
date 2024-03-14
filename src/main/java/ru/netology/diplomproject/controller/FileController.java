package ru.netology.diplomproject.controller;


import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplomproject.dto.FileResponse;
import ru.netology.diplomproject.service.ServiceCloud;


import java.util.List;

@RestController
@AllArgsConstructor
public class FileController {

    private final ServiceCloud serviceCloud;
    public FileController(ServiceCloud serviceCloud) {
        this.serviceCloud = serviceCloud;
    }

    @PostMapping("/file")
    public ResponseEntity<?> inputFile(@RequestHeader("auth-token") String token, @RequestParam("filename") String name,
                                       MultipartFile file) {
        serviceCloud.upload(token, name, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<FileResponse>> list(@RequestHeader("auth-token") String token, Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return ResponseEntity.ok(filePage);
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> fileDownload(@RequestHeader("auth-token") String token,
                                                 @RequestParam("filename") String fileName) {

        return ResponseEntity.ok(serviceCloud.fileDownload(fileName, token));
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String token,
                                        @RequestParam("filename") String fileName) {
        serviceCloud.fileDelete(fileName, token);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}

