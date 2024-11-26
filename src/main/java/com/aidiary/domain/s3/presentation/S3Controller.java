package com.aidiary.domain.s3.presentation;

import com.aidiary.domain.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class S3Controller {

    private final S3Service s3Service;


    @GetMapping("/s3/presigned-url")
    public String getImagePresignedUrl(@RequestParam String bucketName,
                                       @RequestParam String objectKey,
                                       @RequestParam(defaultValue = "3600") int durationInSeconds) {
        return s3Service.generatePresignedUrl(bucketName, objectKey, durationInSeconds);
    }
}
