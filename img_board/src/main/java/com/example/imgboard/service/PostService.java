package com.example.imgboard.service;

import com.example.imgboard.dto.PostDto;
import com.example.imgboard.entity.Image;
import com.example.imgboard.entity.Post;
import com.example.imgboard.repository.ImageRepository;
import com.example.imgboard.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final ImageRepository imageRepository;

	@Value("${app.upload-dir}")
	private String uploadDir;

	@PostConstruct
	void ensureUploadDir() {
		File dir = new File(uploadDir);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IllegalStateException("업로드 폴더를 만들 수 없습니다: " + uploadDir);
		}
	}

	private String normalizeDir(String dir) {
		if (dir == null || dir.isBlank()) {
			return "";
		}
		return dir.endsWith("/") || dir.endsWith("\\") ? dir : dir + File.separator;
	}

	@Transactional
	public Long save(PostDto dto) {
		String dir = normalizeDir(uploadDir);

		Post post = Post.builder()
				.title(dto.getTitle())
				.content(dto.getContent())
				.build();
		postRepository.save(post);

		if (dto.getFiles() != null) {
			for (MultipartFile file : dto.getFiles()) {
				if (file == null || file.isEmpty()) {
					continue;
				}
				try {
					String originalName = file.getOriginalFilename();
					if (originalName == null || originalName.isBlank()) {
						originalName = "file";
					}
					String uuid = UUID.randomUUID().toString();
					String savedName = uuid + "_" + originalName;

					File saveFile = new File(dir + savedName);
					file.transferTo(saveFile);

					String thumbnailName = "s_" + savedName;
					File thumbnailFile = new File(dir + thumbnailName);
					Thumbnails.of(saveFile)
							.size(200, 200)
							.toFile(thumbnailFile);

					Image image = Image.builder()
							.originalName(originalName)
							.savedName(savedName)
							.filePath(dir)
							.post(post)
							.build();
					imageRepository.save(image);
					post.getImages().add(image);
				}
				catch (IOException e) {
					throw new RuntimeException("파일 업로드 실패", e);
				}
			}
		}

		return post.getId();
	}

	@Transactional(readOnly = true)
	public Post findById(Long id) {
		return postRepository.findByIdWithImages(id)
				.orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
	}

	@Transactional(readOnly = true)
	public java.util.List<Post> findAll() {
		return postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	@Transactional
	public void delete(Long id) {
		Post post = postRepository.findByIdWithImages(id)
				.orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

		for (Image img : post.getImages()) {
			String base = img.getFilePath() != null ? img.getFilePath() : dir();
			File file = new File(base + img.getSavedName());
			File thumbnail = new File(base + img.getThumbnailSavedName());
			if (file.exists()) {
				//noinspection ResultOfMethodCallIgnored
				file.delete();
			}
			if (thumbnail.exists()) {
				//noinspection ResultOfMethodCallIgnored
				thumbnail.delete();
			}
		}

		postRepository.delete(post);
	}

	private String dir() {
		return normalizeDir(uploadDir);
	}
}
