package com.gachon.learningmate.config;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class FileUploadUtil {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    public static boolean isExtensionValid(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(fileExtension);
    }

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        // 디렉토리 경로 가져오기
        Path uploadPath = Paths.get(uploadDir);

        // 해당 경로에 디렉토리 존재하는지 확인
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // InputStream을 통해 파일 읽고 업로드
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("이미지 저장에 실패했습니다.");
        }
    }
}
