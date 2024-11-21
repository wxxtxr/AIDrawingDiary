package com.aidiary.domain.s3.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {
	private final AmazonS3Client s3Client;

	@Value("${aws.s3.bucket}")
	private String bucket;

	public String upload(String imageUrl) throws IOException {
		InputStream inputStream = new URL(imageUrl).openStream();
		String fileName = createFileName();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("image/png");
		s3Client.putObject(bucket, fileName, inputStream, metadata);
		return fileName;
	}

	private String createFileName() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		return UUID.randomUUID() + "_" + LocalDateTime.now().format(formatter);
	}
}
