package org.livin.global.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
	String uploadFile(MultipartFile multipartFile);

	String updateFile(String oldFileUrl, MultipartFile newFile);

	void deleteFile(String fileUrl);

	String getFileUrl(String fileName);

	String extractFileName(String fileUrl);
}
