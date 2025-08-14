package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.Account;

import org.example.petcareplus.entity.PetProfile;

import org.example.petcareplus.service.PetProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping()
public class PetProfileController {

    @Autowired
    private PetProfileService petProfileService;



    @GetMapping("/customer/pet-profile")
    public String showPetProfilePage(Model model,
                                     HttpSession session,
                                     @RequestParam(value = "selectedId", required = false) Long selectedId) {
        Account account = (Account) session.getAttribute("loggedInUser");

        if (account == null) {
            return "redirect:/login";
        }

        List<PetProfile> petProfiles = petProfileService.findByAccount(account);

        PetProfile selectedPet = null;

        if (!petProfiles.isEmpty()) {
            selectedPet = (selectedId != null)
                    ? petProfileService.findById(selectedId)
                    : petProfiles.get(0);
        }

        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("selectedPet", selectedPet);
        model.addAttribute("edit", false);
        return "pet-profile";
    }

    @PostMapping("/customer/pet-profile/edit")
    public String editPetProfile(@RequestParam("petId") Long petId,
                                 @RequestParam("name") String name,
                                 @RequestParam("species") String species,
                                 @RequestParam("breeds") String breeds,
                                 @RequestParam("age") Integer age,
                                 @RequestParam("weight") Float weight,
                                 HttpSession session,
                                 Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        
        // Validate dữ liệu
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "Tên không được để trống");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        if (species == null || species.trim().isEmpty()) {
            model.addAttribute("error", "Loài không được để trống");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        if (breeds == null || breeds.trim().isEmpty()) {
            model.addAttribute("error", "Giống không được để trống");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        if (name.trim().length() > 50) {
            model.addAttribute("error", "Tên không được vượt quá 50 ký tự");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        if (species.trim().length() > 50) {
            model.addAttribute("error", "Loài không được vượt quá 50 ký tự");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        if (breeds.trim().length() > 50) {
            model.addAttribute("error", "Giống không được vượt quá 50 ký tự");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        
        // Lấy PetProfile hiện tại từ database
        PetProfile existingPet = petProfileService.findById(petId);
        if (existingPet != null) {
            // Cập nhật thông tin
            existingPet.setName(name.trim());
            existingPet.setSpecies(species.trim());
            existingPet.setBreeds(breeds.trim());
            existingPet.setAge(age);
            existingPet.setWeight(weight);
            
            petProfileService.updatePetProfile(petId, existingPet);
        }
        
        return "redirect:/customer/pet-profile?selectedId=" + petId;
    }

    @PostMapping("/customer/pet-profile/add")
    public String createNewPet(@RequestParam("name") String name,
                               @RequestParam("age") Integer age,
                               @RequestParam("species") String species,
                               @RequestParam("breeds") String breeds,
                               @RequestParam("weight") Float weight,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                               HttpSession session,
                               Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");

        // Validate dữ liệu
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "Tên không được để trống");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            model.addAttribute("modalError", true);
            model.addAttribute("showModal", true);
            return "pet-profile";
        }
        if (species == null || species.trim().isEmpty()) {
            model.addAttribute("error", "Loài không được để trống");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            model.addAttribute("modalError", true);
            return "pet-profile";
        }
        if (breeds == null || breeds.trim().isEmpty()) {
            model.addAttribute("error", "Giống không được để trống");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            model.addAttribute("modalError", true);
            return "pet-profile";
        }
        if (name.trim().length() > 50) {
            model.addAttribute("error", "Tên không được vượt quá 50 ký tự");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            model.addAttribute("modalError", true);
            return "pet-profile";
        }
        if (species.trim().length() > 50) {
            model.addAttribute("error", "Loài không được vượt quá 50 ký tự");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            model.addAttribute("modalError", true);
            return "pet-profile";
        }
        if (breeds.trim().length() > 50) {
            model.addAttribute("error", "Giống không được vượt quá 50 ký tự");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            model.addAttribute("modalError", true);
            return "pet-profile";
        }

        try {
            // Tạo PetProfile mới
            PetProfile newPet = new PetProfile();
            newPet.setName(name.trim());
            newPet.setSpecies(species.trim());
            newPet.setBreeds(breeds.trim());
            newPet.setAge(age);
            newPet.setWeight(weight);
            newPet.setProfile(account.getProfile());
            
            PetProfile savedPet = petProfileService.save(newPet);

            // Handle image upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    petProfileService.uploadPetImage(savedPet.getPetProfileId(), imageFile);
                } catch (Exception e) {
                    // Log error but don't fail the entire operation
                }
            }

            return "redirect:/customer/pet-profile?selectedId=" + savedPet.getPetProfileId();
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tạo thú cưng: " + e.getMessage());
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            return "pet-profile";
        }
    }

    @GetMapping("/customer/pet-profile/edit")
    public String switchToEdit(@RequestParam("selectedId") Long selectedId,
                               HttpSession session,
                               Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        List<PetProfile> petProfiles = petProfileService.findByAccount(account);
        PetProfile selectedPet = petProfileService.findById(selectedId);

        if (account == null) {
            return "redirect:/login";
        }

        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("selectedPet", selectedPet);
        model.addAttribute("edit", true);
        return "pet-profile";
    }

    @PostMapping("/customer/pet-profile/save")
    public String savePetProfile(@RequestParam(value = "petId", required = false) Long petId,
                                 @RequestParam("name") String name,
                                 @RequestParam("species") String species,
                                 @RequestParam("breeds") String breeds,
                                 @RequestParam("age") Integer age,
                                 @RequestParam("weight") Float weight,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 HttpSession session,
                                 Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");

        // Validate dữ liệu
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("error", "Tên không được để trống");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        if (species == null || species.trim().isEmpty()) {
            model.addAttribute("error", "Loài không được để trống");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        if (breeds == null || breeds.trim().isEmpty()) {
            model.addAttribute("error", "Giống không được để trống");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        if (name.trim().length() > 50) {
            model.addAttribute("error", "Tên không được vượt quá 50 ký tự");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        if (species.trim().length() > 50) {
            model.addAttribute("error", "Loài không được vượt quá 50 ký tự");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
        if (breeds.trim().length() > 50) {
            model.addAttribute("error", "Giống không được vượt quá 50 ký tự");
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }

        try {
            if (petId != null) {
                // Cập nhật PetProfile hiện tại
                PetProfile existingPet = petProfileService.findById(petId);
                if (existingPet != null) {
                    existingPet.setName(name.trim());
                    existingPet.setSpecies(species.trim());
                    existingPet.setBreeds(breeds.trim());
                    existingPet.setAge(age);
                    existingPet.setWeight(weight);
                    
                    petProfileService.updatePetProfile(petId, existingPet);
                }

                // Handle image upload if provided
                if (imageFile != null && !imageFile.isEmpty()) {
                    try {
                        petProfileService.uploadPetImage(petId, imageFile);
                    } catch (Exception uploadError) {
                        model.addAttribute("error", "Lưu thông tin thành công nhưng upload ảnh thất bại: " + uploadError.getMessage());
                    }
                }

                return "redirect:/customer/pet-profile?selectedId=" + petId;
            } else {
                // Tạo PetProfile mới
                PetProfile newPet = new PetProfile();
                newPet.setName(name.trim());
                newPet.setSpecies(species.trim());
                newPet.setBreeds(breeds.trim());
                newPet.setAge(age);
                newPet.setWeight(weight);
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

                return "redirect:/customer/pet-profile?selectedId=" + savedPet.getPetProfileId();
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi lưu thông tin thú cưng: " + e.getMessage());
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", petProfileService.findById(petId));
            model.addAttribute("edit", true);
            return "pet-profile";
        }
    }

    // Test endpoint để kiểm tra file upload
    @PostMapping("/customer/pet-profile/test-upload")
    @ResponseBody
    public String testUpload(@RequestParam("imageFile") MultipartFile imageFile) {
        System.out.println("=== Test Upload ===");
        System.out.println("File name: " + imageFile.getOriginalFilename());
        System.out.println("File size: " + imageFile.getSize());
        System.out.println("Content type: " + imageFile.getContentType());
        System.out.println("Is empty: " + imageFile.isEmpty());

        return "File received: " + imageFile.getOriginalFilename() + ", Size: " + imageFile.getSize();
    }

    @GetMapping("/list-pet-profile")
    public String GetHotelBookings(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "8") int size,
                                   @RequestParam(required = false) String from,
                                   @RequestParam(required = false) String keyword) {
        Page<PetProfile> petProfiles;

        if (keyword != null && !keyword.trim().isEmpty()) {
            petProfiles = petProfileService.searchByName(keyword, page, size, "petProfileId");
        } else {
            petProfiles = petProfileService.findAll(page, size, "petProfileId");
        }

        model.addAttribute("petProfiles", petProfiles.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", petProfiles.getTotalPages());
        model.addAttribute("from", from);
        model.addAttribute("keyword", keyword); // giữ lại từ khóa trên view

        return "list-pet-profile";
    }
}
