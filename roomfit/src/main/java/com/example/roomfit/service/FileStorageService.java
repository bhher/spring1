package com.example.roomfit.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private final Path root;

	public FileStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
		this.root = Paths.get(uploadDir);
		Files.createDirectories(root.resolve("interior"));
	}

	public String storeInteriorImage(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}
		String ext = getExtension(file.getOriginalFilename());
		String filename = UUID.randomUUID() + ext;
		Path target = root.resolve("interior").resolve(filename);
		Files.copy(file.getInputStream(), target);
		return "/uploads/interior/" + filename;
	}

	private String getExtension(String name) {
		if (name == null || !name.contains(".")) {
			return ".jpg";
		}
		return name.substring(name.lastIndexOf('.'));
	}
}
