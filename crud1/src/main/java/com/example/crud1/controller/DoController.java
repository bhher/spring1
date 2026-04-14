package com.example.crud1.controller;

import com.example.crud1.dto.DoDto;
import com.example.crud1.entity.DoIt;
import com.example.crud1.repository.DoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DoController {
    @Autowired
    private DoRepository doRepository;

    @GetMapping("/mains/add")
    public String addForm(Model model) {
        model.addAttribute("pageTitle", "새 글");
        return "mains/add";
    }

    @GetMapping("/list/{num}")
    public String detail(@PathVariable Long num, Model model) {
        DoIt doIt = doRepository.findById(num).orElse(null);
        model.addAttribute("detail", doIt);
        model.addAttribute("pageTitle", "상세");
        return "mains/detail";
    }

    @GetMapping("/list")
    public String list(Model model) {
        List<DoIt> doList = doRepository.findAll();
        model.addAttribute("DoList", doList);
        model.addAttribute("pageTitle", "목록");
        return "mains/doList";
    }

    @GetMapping("/list/{num}/edit")
    public String updateForm(@PathVariable Long num, Model model) {
        DoIt toDo = doRepository.findById(num).orElse(null);
        model.addAttribute("editData", toDo);
        model.addAttribute("pageTitle", "수정");
        return "mains/edit";
    }

    @GetMapping("/list/{num}/delete")
    public String delete(@PathVariable Long num, RedirectAttributes rttr){
        DoIt target = doRepository.findById(num).orElse(null);
        if(target != null){
            doRepository.delete(target);
            rttr.addFlashAttribute("msg", "삭제가 완료되었습니다.");
        }
        return "redirect:/list";
    }

    @PostMapping("/mains/create")
    public String create(DoDto dto){
        DoIt doit = dto.toEntity();
        DoIt save = doRepository.save(doit);
        return "redirect:/list/"+save.getNum();
    }

    @PostMapping("/mains/update")
    public String update(DoDto dto){
        DoIt entity= dto.toEntity();
        DoIt target = doRepository.findById(entity.getNum()).orElse(null);
        if(target != null) {
            doRepository.save(entity);
        }
        return "redirect:/list/" + entity.getNum();
    }
}
