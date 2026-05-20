package com.example.roomfit.web;

import com.example.roomfit.domain.InteriorStyle;
import com.example.roomfit.dto.ProductFormDto;
import com.example.roomfit.service.ProductAdminService;
import jakarta.validation.Valid;
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

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

	private final ProductAdminService productAdminService;

	@GetMapping
	public String list(
			@RequestParam(name = "keyword", required = false) String keyword,
			@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
			Model model) {
		model.addAttribute("page", productAdminService.findProducts(keyword, pageable));
		model.addAttribute("keyword", keyword == null ? "" : keyword);
		return "admin/products/list";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("form", new ProductFormDto());
		model.addAttribute("editMode", false);
		model.addAttribute("styles", InteriorStyle.values());
		return "admin/products/form";
	}

	@PostMapping
	public String create(
			@Valid @ModelAttribute("form") ProductFormDto form,
			BindingResult bindingResult,
			@RequestParam(name = "image", required = false) MultipartFile image,
			Model model,
			RedirectAttributes ra) throws Exception {
		if (bindingResult.hasErrors()) {
			model.addAttribute("editMode", false);
			model.addAttribute("styles", InteriorStyle.values());
			return "admin/products/form";
		}
		productAdminService.create(form, image);
		ra.addFlashAttribute("message", "상품을 등록했습니다.");
		return "redirect:/admin/products";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("form", productAdminService.getForm(id));
		model.addAttribute("product", productAdminService.getProduct(id));
		model.addAttribute("editMode", true);
		model.addAttribute("styles", InteriorStyle.values());
		return "admin/products/form";
	}

	@PostMapping("/{id}")
	public String update(
			@PathVariable("id") Long id,
			@Valid @ModelAttribute("form") ProductFormDto form,
			BindingResult bindingResult,
			@RequestParam(name = "image", required = false) MultipartFile image,
			Model model,
			RedirectAttributes ra) throws Exception {
		if (bindingResult.hasErrors()) {
			model.addAttribute("editMode", true);
			model.addAttribute("styles", InteriorStyle.values());
			return "admin/products/form";
		}
		form.setId(id);
		productAdminService.update(id, form, image);
		ra.addFlashAttribute("message", "상품을 수정했습니다.");
		return "redirect:/admin/products";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
		productAdminService.discontinue(id);
		ra.addFlashAttribute("message", "상품을 판매 중지했습니다.");
		return "redirect:/admin/products";
	}
}
