package com.example.shoppingmall.service;

import com.example.shoppingmall.domain.Product;
import com.example.shoppingmall.domain.ProductImage;
import com.example.shoppingmall.exception.FileUploadException;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 상품 이미지 로컬 저장 + 썸네일 생성. 경로는 app.upload-dir 기준 products/ 하위.
 */
@Service
@RequiredArgsConstructor
public class FileStorageService {

	@Value("${app.upload-dir}")
	private String uploadRoot;

	private String productDir() {
		return normalize(uploadRoot) + "products" + File.separator;
	}

	private static String normalize(String path) {
		if (path == null || path.isBlank()) {
			return "";
		}
		return path.endsWith("/") || path.endsWith("\\") ? path : path + File.separator;
	}

	@PostConstruct
	void mkdir() {
		File dir = new File(productDir());
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IllegalStateException("업로드 디렉터리를 만들 수 없습니다: " + dir.getAbsolutePath());
		}
	}

	/**
	 * MultipartFile 을 저장하고 ProductImage 엔티티 정보를 만든다. DB 저장은 호출 측에서 cascade.
	 */
	public ProductImage storeProductImage(MultipartFile file, Product product, int sortOrder) {
		if (file == null || file.isEmpty()) {
			throw new FileUploadException("빈 파일입니다.", null);
		}
		String original = file.getOriginalFilename();
		if (original == null || original.isBlank()) {
			original = "image";
		}
		String stored = UUID.randomUUID() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
		String thumb = "s_" + stored;
		File dest = new File(productDir() + stored);
		File thumbDest = new File(productDir() + thumb);
		try {
			file.transferTo(dest);
			Thumbnails.of(dest).size(320, 320).toFile(thumbDest);
		}
		catch (IOException e) {
			if (dest.exists()) {
				//noinspection ResultOfMethodCallIgnored
				dest.delete();
			}
			throw new FileUploadException("파일 저장에 실패했습니다.", e);
		}
		return ProductImage.builder()
				.originalFilename(original)
				.storedFilename(stored)
				.thumbnailFilename(thumb)
				.directoryPath(productDir())
				.sortOrder(sortOrder)
				.product(product)
				.build();
	}

	public void deleteImageFiles(ProductImage image) {
		if (image == null) {
			return;
		}
		String base = image.getDirectoryPath() != null ? image.getDirectoryPath() : productDir();
		deleteIfExists(base + image.getStoredFilename());
		deleteIfExists(base + image.getThumbnailFilename());
	}

	private void deleteIfExists(String path) {
		File f = new File(path);
		if (f.exists()) {
			//noinspection ResultOfMethodCallIgnored
			f.delete();
		}
	}
}
