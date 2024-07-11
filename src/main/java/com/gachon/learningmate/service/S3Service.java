package com.gachon.learningmate.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gachon.learningmate.exception.FileDeleteFailureException;
import com.gachon.learningmate.exception.FileUploadFailureException;
import com.gachon.learningmate.exception.InvalidImageExtension;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    private Set<String> uploadedFileNames = new HashSet<>();

    private Set<String> uploadedFileSizes = new HashSet<>();

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 단일 파일 저장
    public String saveFile(MultipartFile file) {
        String randomFileName = generateRandomFileName(file);

        log.info("File upload started: " + randomFileName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(bucket, "study/img/" + randomFileName, file.getInputStream(), objectMetadata);
        } catch (AmazonS3Exception e) {
            log.error("Amazon S3 error while uploading file: " + e.getMessage());
            throw new FileUploadFailureException("파일 업로드에 실패했습니다.");
        } catch (SdkClientException e) {
            log.error("AWS SDK client error while uploading file: " + e.getMessage());
            throw new FileUploadFailureException("파일 업로드에 실패했습니다.");
        } catch (IOException e) {
            log.error("IO error while uploading file: " + e.getMessage());
            throw new FileUploadFailureException("파일 업로드에 실패했습니다.");
        }

        log.info("File upload completed: " + randomFileName);
        return amazonS3.getUrl(bucket, "study/img/" + randomFileName).toString();
    }

    // 파일 삭제
    public void deleteFile(String fileUrl) {
        String[] urlParts = fileUrl.split("/");
        String fileBucket = urlParts[2].split("\\.")[0];

        if (!fileBucket.equals(bucket)) {
            throw new FileDeleteFailureException("이미지가 존재하지 않습니다.");
        }

        String objectKey = String.join("/", Arrays.copyOfRange(urlParts, 3, urlParts.length));

        if (!amazonS3.doesObjectExist(bucket, objectKey)) {
            throw new FileDeleteFailureException("이미지가 존재하지 않습니다.");
        }

        try {
            amazonS3.deleteObject(bucket, objectKey);
        } catch (AmazonS3Exception e) {
            log.error("File delete fail : " + e.getMessage());
            throw new FileDeleteFailureException("이미지 삭제에 실패했습니다.");
        } catch (SdkClientException e) {
            log.error("AWS SDK client error : " + e.getMessage());
            throw new FileDeleteFailureException("이미지 삭제에 실패했습니다.");
        }

        log.info("File delete complete: " + objectKey);
    }

    private void clear() {
        uploadedFileNames.clear();
        uploadedFileSizes.clear();
    }

    // 랜덤파일명 생성 (파일명 중복 방지)
    private String generateRandomFileName(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = validateFileExtension(originalFilename);
        String randomFilename = UUID.randomUUID() + "." + fileExtension;
        return randomFilename;
    }

    // 파일 확장자 체크
    private String validateFileExtension(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "jpeg");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new InvalidImageExtension("이미지 확장자가 잘못되었습니다.");
        }
        return fileExtension;
    }

}
