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
        
        System.out.println("=== Create New Pet ===");
        System.out.println("ImageFile: " + (imageFile != null ? imageFile.getOriginalFilename() : "null"));
        System.out.println("ImageFile empty: " + (imageFile != null ? imageFile.isEmpty() : "null"));
        
        if (result.hasErrors()) {
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            model.addAttribute("modalError", true);
            return "pet-profile";
        }

        // Gán pet profile cho account hiện tại
        petProfile.setProfile(account.getProfile());
        PetProfile newPet = petProfileService.save(petProfile);
        
        // Xử lý upload ảnh nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                petProfileService.uploadPetImage(newPet.getPetProfileId(), imageFile);
                System.out.println("Image uploaded successfully for new pet");
            } catch (Exception e) {
                System.err.println("Failed to upload image for new pet: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return "redirect:/pet-profile?selectedId=" + newPet.getPetProfileId();
    }

    @GetMapping("/edit")
    public String switchToEdit(@RequestParam("selectedId") Long selectedId, 
                              HttpSession session,
                              Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        List<PetProfile> petProfiles = petProfileService.findByAccount(account);
        PetProfile selectedPet = petProfileService.findById(selectedId);
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
        
        System.out.println("=== Save Pet Profile ===");
        System.out.println("PetId: " + petId);
        System.out.println("ImageFile: " + (imageFile != null ? imageFile.getOriginalFilename() : "null"));
        System.out.println("ImageFile empty: " + (imageFile != null ? imageFile.isEmpty() : "null"));
        System.out.println("ImageFile size: " + (imageFile != null ? imageFile.getSize() : "null"));
        System.out.println("ImageFile content type: " + (imageFile != null ? imageFile.getContentType() : "null"));
        
        // Test: In ra tất cả request parameters
        System.out.println("=== Request Parameters ===");
        System.out.println("Request contains multipart: " + (imageFile != null));
        if (imageFile != null) {
            System.out.println("File name: " + imageFile.getOriginalFilename());
            System.out.println("File size: " + imageFile.getSize());
            System.out.println("Content type: " + imageFile.getContentType());
            System.out.println("Is empty: " + imageFile.isEmpty());
        }
        
        if (result.hasErrors()) {
            System.out.println("Validation errors: " + result.getAllErrors());
            model.addAttribute("selectedId", petId);
            model.addAttribute("petProfile", petProfile);
            model.addAttribute("edit", true);
            return "pet-profile";
        }

        try {
            if (petId != null) {
                System.out.println("Updating existing pet with ID: " + petId);
                petProfileService.updatePetProfile(petId, petProfile);
                
                // Xử lý upload ảnh nếu có
                if (imageFile != null && !imageFile.isEmpty()) {
                    System.out.println("Uploading image for existing pet");
                    try {
                        petProfileService.uploadPetImage(petId, imageFile);
                        System.out.println("Image upload completed successfully");
                    } catch (Exception uploadError) {
                        System.err.println("Image upload failed: " + uploadError.getMessage());
                        uploadError.printStackTrace();
                        model.addAttribute("error", "Lưu thông tin thành công nhưng upload ảnh thất bại: " + uploadError.getMessage());
                    }
                } else {
                    System.out.println("No image file provided for upload");
                }
                
                return "redirect:/pet-profile?selectedId=" + petId;
            } else {
                System.out.println("Creating new pet");
                PetProfile newPet = petProfileService.createEmptyPet();
                // Gán pet profile cho account hiện tại
                newPet.setProfile(account.getProfile());
                PetProfile savedPet = petProfileService.save(newPet);
                
                System.out.println("New pet created with ID: " + savedPet.getPetProfileId());
                
                // Xử lý upload ảnh nếu có
                if (imageFile != null && !imageFile.isEmpty()) {
                    System.out.println("Uploading image for new pet");
                    try {
                        petProfileService.uploadPetImage(savedPet.getPetProfileId(), imageFile);
                        System.out.println("Image upload completed successfully");
                    } catch (Exception uploadError) {
                        System.err.println("Image upload failed: " + uploadError.getMessage());
                        uploadError.printStackTrace();
                        model.addAttribute("error", "Tạo thú cưng thành công nhưng upload ảnh thất bại: " + uploadError.getMessage());
                    }
                } else {
                    System.out.println("No image file provided for upload");
                }
                
                return "redirect:/pet-profile?selectedId=" + savedPet.getPetProfileId();
            }
        } catch (Exception e) {
            System.err.println("Error saving pet profile: " + e.getMessage());
            e.printStackTrace();
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
}
