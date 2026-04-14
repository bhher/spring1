package com.example.shoppingmall.web;

import com.example.shoppingmall.dto.ProductFormDto;
import com.example.shoppingmall.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 관리자 전용 상품 CRUD (이미지 업로드 포함).
 */
@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

	private final ProductService productService;

	@GetMapping
	public String list(
			@RequestParam(required = false) String keyword,
			@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
			Model model) {
		model.addAttribute("page", productService.findProducts(keyword, pageable));
		model.addAttribute("keyword", keyword == null ? "" : keyword);
		return "admin/products/list";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("form", new ProductFormDto());
		model.addAttribute("editMode", false);
		return "admin/products/form";
	}

	@PostMapping
	public String create(
			@Valid @ModelAttribute("form") ProductFormDto form,
			BindingResult bindingResult,
			@RequestParam(value = "images", required = false) List<MultipartFile> images,
			Model model,
			RedirectAttributes ra) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("editMode", false);
			return "admin/products/form";
		}
		productService.saveProduct(form, images);
		ra.addFlashAttribute("message", "상품을 등록했습니다.");
		return "redirect:/admin/products";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("form", productService.getProductForm(id));
		model.addAttribute("product", productService.findDetail(id));
		model.addAttribute("editMode", true);
		return "admin/products/form";
	}

	@PostMapping("/{id}")
	public String update(
			@PathVariable Long id,
			@Valid @ModelAttribute("form") ProductFormDto form,
			BindingResult bindingResult,
			@RequestParam(value = "images", required = false) List<MultipartFile> images,
			Model model,
			RedirectAttributes ra) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("editMode", true);
			model.addAttribute("product", productService.findDetail(id));
			return "admin/products/form";
		}
		productService.updateProduct(id, form, images);
		ra.addFlashAttribute("message", "상품을 수정했습니다.");
		return "redirect:/admin/products";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes ra) {
		productService.deleteProduct(id);
		ra.addFlashAttribute("message", "상품을 삭제했습니다.");
		return "redirect:/admin/products";
	}
}
