package com.example.imggallery.web;

import com.example.imggallery.service.GalleryImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GalleryController {

    private final GalleryImageService galleryImageService;

    public GalleryController(GalleryImageService galleryImageService) {
        this.galleryImageService = galleryImageService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/gallery";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute("images", galleryImageService.findAllForGallery());
        return "gallery";
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> file(@PathVariable Long id) {
        Resource resource = galleryImageService.loadAsResource(id);
        String name = resource.getFilename() != null ? resource.getFilename() : "image";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + name + "\"")
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}
