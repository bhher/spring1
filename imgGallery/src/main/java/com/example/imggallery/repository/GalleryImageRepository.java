package com.example.imggallery.repository;

import com.example.imggallery.domain.GalleryImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {

    List<GalleryImage> findAllByOrderByCreatedAtDesc();
}
