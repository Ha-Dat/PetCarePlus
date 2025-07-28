package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Category;
import jakarta.validation.Valid;
import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.PetProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
@RequestMapping("/pet-profile")
public class PetProfileController {

    @Autowired
    private PetProfileService petProfileService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String showPetProfilePage(Model model,
                                     HttpSession session,
                                     @RequestParam(value = "selectedId", required = false) Long selectedId) {
        Account account = (Account) session.getAttribute("loggedInUser");

        if (account == null) {
            return "redirect:/login";
        }

        List<PetProfile> petProfiles = petProfileService.findAll();
        List<Category> parentCategories = categoryService.getParentCategory();

        PetProfile selectedPet = null;

        if (!petProfiles.isEmpty()) {
            selectedPet = (selectedId != null)
                    ? petProfileService.findById(selectedId)
                    : petProfiles.get(0);
        }

        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("selectedPet", selectedPet);
        model.addAttribute("categories", parentCategories);
        model.addAttribute("edit", false);
        return "pet-profile";
    }

    @PostMapping("/edit")
    public String editPetProfile(@RequestParam("petId") Long petId,
                                 @ModelAttribute PetProfile petProfile) {
        petProfileService.updatePetProfile(petId, petProfile);
        return "redirect:/pet-profile?selectedId=" + petId;
    }

    @PostMapping("/add")
    public String createNewPet(@Valid @ModelAttribute PetProfile petProfile,
                            BindingResult result,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("petProfiles", petProfileService.findAll());
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            model.addAttribute("modalError", true);
            return "pet-profile";
        }

        PetProfile newPet = petProfileService.save(petProfile);
        return "redirect:/pet-profile?selectedId=" + newPet.getPetProfileId();
    }

    @GetMapping("/edit")
    public String switchToEdit(@RequestParam("selectedId") Long selectedId, Model model, HttpSession session) {
        List<PetProfile> petProfiles = petProfileService.findAll();
        PetProfile selectedPet = petProfileService.findById(selectedId);
        List<Category> parentCategories = categoryService.getParentCategory();

        Account account = (Account) session.getAttribute("loggedInUser");

        if (account == null) {
            return "redirect:/login";
        }

        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("selectedPet", selectedPet);
        model.addAttribute("categories", parentCategories);
        model.addAttribute("edit", true);
        return "pet-profile";
    }

    @PostMapping("/save")
    public String savePetProfile(@RequestParam(value = "petId", required = false) Long petId,
                                 @Valid @ModelAttribute PetProfile petProfile,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("selectedId", petId);
            model.addAttribute("petProfile", petProfile);
            model.addAttribute("edit", true);
            return "pet-profile";
        }

        if (petId != null) {
            petProfileService.updatePetProfile(petId, petProfile);
            return "redirect:/pet-profile?selectedId=" + petId;
        } else {
            PetProfile newPet = petProfileService.createEmptyPet();
            return "redirect:/pet-profile?selectedId=" + newPet.getPetProfileId();
        }
    }
}
