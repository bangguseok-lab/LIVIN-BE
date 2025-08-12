package org.livin.global.s3.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class S3ServiceImpl {
	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String uploadFile(MultipartFile multipartFile) throws IOException {
		if (multipartFile.isEmpty()) {
			return null;
		}
		String fileName = UUID.randomUUID().toString() + "-" + multipartFile.getOriginalFilename();
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(multipartFile.getContentType());
		objectMetadata.setContentLength(multipartFile.getSize());

		try (InputStream inputStream = multipartFile.getInputStream()) {
			amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
				.withCannedAcl(CannedAccessControlList.PublicRead)); // 공개 접근 권한 설정
		}
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	public String updateFile(String oldFileUrl, MultipartFile newFile) throws IOException {
		deleteFile(oldFileUrl); // 기존 파일 삭제
		return uploadFile(newFile); // 새 파일 업로드
	}

	public void deleteFile(String fileUrl) {
		String fileName = extractFileName(fileUrl);
		amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
	}

	public String getFileUrl(String fileName) {
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	private String extractFileName(String fileUrl) {
		String[] split = fileUrl.split("/");
		return split[split.length - 1];
	}
}
