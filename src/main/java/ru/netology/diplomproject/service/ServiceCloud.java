package ru.netology.diplomproject.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplomproject.jwt.JWTUtil;
import ru.netology.diplomproject.dto.FileResponse;
import ru.netology.diplomproject.exceptions.ErrorFileException;
import ru.netology.diplomproject.exceptions.ErrorInputDataException;
import ru.netology.diplomproject.repository.FileRepository;
import ru.netology.diplomproject.repository.UserRepository;
import ru.netology.diplomproject.model.AppUser;
import ru.netology.diplomproject.model.FileCloud;

import java.awt.print.Pageable;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ServiceCloud {
    private final String fileRepositoryDir = "D:\\allFiles\\"; //Базовая директива для создания хранилища файлов;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private String userName;

    @Autowired
    public ServiceCloud(FileRepository fileRepository, UserRepository userRepository, JWTUtil jwtUtil) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public void upload(String token, String name, MultipartFile file) {
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        String userDir = fileRepositoryDir + userName;
        File createDir = new File(userDir);
        if (createDir.mkdir()) {
            log.info("Папка нового пользователя успешно создана");
        }
        File file1 = new File(createDir + "\\" + name);
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file1));
                stream.write(bytes);
                stream.close();
                var fileData = FileCloud.builder()
                        .fileName(file1.getName())
                        .size(Math.toIntExact(file1.length()))
                        .date(LocalDateTime.now())
                        .appUser(userRepository.findByEmail(userName)).build();
                fileRepository.save(fileData);
                log.info("Файл {} успешно загружен в хранилище по пути {}", name, userDir);
            } catch (IOException e) {
                log.error("Ошибка загрузки файла");
                throw new ErrorFileException("Ошибка загрузки файла " + e.getMessage());
            }
        }
    }

    public List<FileResponse> getAllFiles(String token, Integer limit) {
        String name = jwtUtil.resolveToken(token);
        if(name == null){
            log.error("Ошибка вывода списка всех файлов");
            throw new ErrorInputDataException("Ошибка вывода списка всех файлов");
        }
        AppUser data = userRepository.findByEmail(jwtUtil.getUsername(name));
        log.info("Список файлов успешно выведен на экран");

        Page<FileCloud> filePage = fileRepository.findAllByUserDataId(data.getId(), PageRequest.of(0, limit));
        return filePage.getContent().stream()
                .map(a -> new FileResponse(a.getFileName(), a.getSize()))
                .toList();
    }

//    public Page<FileResponse> getAllFiles(String token, Pageable pageable) {
//        String name = jwtUtil.resolveToken(token);
//        if(name == null){
//            log.error("Ошибка вывода списка всех файлов");
//            throw new ErrorInputDataException("Ошибка вывода списка всех файлов");
//        }
//        AppUser data = userRepository.findByEmail(jwtUtil.getUsername(name));
//        log.info("Список файлов успешно выведен на экран");
//        Page<FileCloud> filePage = fileRepository.findFileDataByUserDataId(data.getId(), pageable);
//        return filePage.map(file -> new FileResponse(file.getFileName(), file.getSize()));
//    }

    public Resource fileDownload(String name, String token)  {
        FileCloud fileCloud = fileRepository.findByFileName(name);
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        Path path = Paths.get(fileRepositoryDir + userName + "\\" + fileCloud.getFileName());
        try {
            log.info("Файл {} успешно загружен", fileCloud.getFileName());
            return new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            log.error("Ошибка загрузки файла");
            throw new ErrorFileException("Ошибка загрузки файла");
        }
    }

    public void fileDelete(String fileName, String token) {
        FileCloud fileCloud = fileRepository.findByFileName(fileName);
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        try {
            Files.delete(Path.of(fileRepositoryDir + userName + "\\" + fileName));
        } catch (IOException e) {
            log.error("Не удалось удалить файл");
            throw new ErrorFileException("Не удалось удалить файл");
        }
        fileRepository.delete(fileCloud);
        log.info("Файл {} успешно удален", fileCloud.getFileName());

    }


}

