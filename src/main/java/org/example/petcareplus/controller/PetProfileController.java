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
import org.springframework.web.bind.annotation.ResponseBody;

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

        List<PetProfile> petProfiles = petProfileService.findByAccount(account);
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
                            HttpSession session,
                            Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");

        if (result.hasErrors()) {
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            model.addAttribute("modalError", true);
            return "pet-profile";
        }

        try {
            // Assign pet profile to current account
            petProfile.setProfile(account.getProfile());
            PetProfile newPet = petProfileService.save(petProfile);

            // Handle image upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    petProfileService.uploadPetImage(newPet.getPetProfileId(), imageFile);
                } catch (Exception e) {
                    // Log error but don't fail the entire operation
                }
            }

            return "redirect:/pet-profile?selectedId=" + newPet.getPetProfileId();
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tạo thú cưng: " + e.getMessage());
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            return "pet-profile";
        }
    }

    @GetMapping("/edit")
    public String switchToEdit(@RequestParam("selectedId") Long selectedId,
                              HttpSession session,
                              Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        List<PetProfile> petProfiles = petProfileService.findByAccount(account);
        PetProfile selectedPet = petProfileService.findById(selectedId);
        List<Category> parentCategories = categoryService.getParentCategory();

        if (account == null) {
            return "redirect:/login";
        }

        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("selectedPet", selectedPet);
        model.addAttribute("edit", true);
        return "pet-profile";
    }

        @PostMapping("/save")
    public String savePetProfile(@RequestParam(value = "petId", required = false) Long petId,
                                 @Valid @ModelAttribute PetProfile petProfile,
                                 BindingResult result,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 HttpSession session,
                                 Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        
        if (result.hasErrors()) {
            model.addAttribute("selectedId", petId);
            model.addAttribute("petProfile", petProfile);
            model.addAttribute("edit", true);
            return "pet-profile";
        }

        try {
            if (petId != null) {
                petProfileService.updatePetProfile(petId, petProfile);
                
                // Handle image upload if provided
                if (imageFile != null && !imageFile.isEmpty()) {
                    try {
                        petProfileService.uploadPetImage(petId, imageFile);
                    } catch (Exception uploadError) {
                        model.addAttribute("error", "Lưu thông tin thành công nhưng upload ảnh thất bại: " + uploadError.getMessage());
                    }
                }
                
                return "redirect:/pet-profile?selectedId=" + petId;
            } else {
                PetProfile newPet = petProfileService.createEmptyPet();
                newPet.setProfile(account.getProfile());
                PetProfile savedPet = petProfileService.save(newPet);
                
                // Handle image upload if provided
                if (imageFile != null && !imageFile.isEmpty()) {
                    try {
                        petProfileService.uploadPetImage(savedPet.getPetProfileId(), imageFile);
                    } catch (Exception uploadError) {
                        model.addAttribute("error", "Tạo thú cưng thành công nhưng upload ảnh thất bại: " + uploadError.getMessage());
                    }
                }
                
                return "redirect:/pet-profile?selectedId=" + savedPet.getPetProfileId();
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi lưu thông tin thú cưng: " + e.getMessage());
            model.addAttribute("selectedId", petId);
            model.addAttribute("petProfile", petProfile);
            model.addAttribute("edit", true);
            return "pet-profile";
        }
    }

        // Test endpoint để kiểm tra file upload
    @PostMapping("/test-upload")
    @ResponseBody
    public String testUpload(@RequestParam("imageFile") MultipartFile imageFile) {
        System.out.println("=== Test Upload ===");
        System.out.println("File name: " + imageFile.getOriginalFilename());
        System.out.println("File size: " + imageFile.getSize());
        System.out.println("Content type: " + imageFile.getContentType());
        System.out.println("Is empty: " + imageFile.isEmpty());
        
        return "File received: " + imageFile.getOriginalFilename() + ", Size: " + imageFile.getSize();
    }
    
    // Test endpoint để kiểm tra database

}
