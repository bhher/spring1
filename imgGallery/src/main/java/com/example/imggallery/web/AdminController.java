package com.example.imggallery.web;

import com.example.imggallery.service.GalleryImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GalleryImageService galleryImageService;

    public AdminController(GalleryImageService galleryImageService) {
        this.galleryImageService = galleryImageService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "admin-login";
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("images", galleryImageService.findAllForGallery());
        return "admin";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {
        try {
            galleryImageService.save(file);
            ra.addFlashAttribute("msg", "업로드되었습니다.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("err", e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            galleryImageService.deleteById(id);
            ra.addFlashAttribute("msg", "삭제되었습니다.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("err", e.getMessage());
        }
        return "redirect:/admin";
    }
}
