package org.example.petcareplus.controller;

import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.service.impl.PetProfileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/pet-profile")
public class PetProfileController {

    @Autowired
    private PetProfileServiceImpl petProfileService;

    // Hiển thị trang "Thú cưng của tôi"
    @GetMapping
    public String showPetProfilePage(Model model,
                                     @RequestParam(value = "selectedId", required = false) Integer selectedId) {
        List<PetProfile> petProfiles = petProfileService.findAll();
        if (petProfiles.isEmpty()) {
            return "pet-profile"; // hiển thị trang rỗng
        }

        PetProfile selectedPet = selectedId != null
                ? petProfileService.findById(selectedId)
                : petProfiles.get(0);

        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("selectedPet", selectedPet);
        model.addAttribute("editMode", false);
        return "pet-profile";
    }

    @PostMapping("/edit")
    public String editPetProfile(@RequestParam("petId") Integer petId,
                                 @ModelAttribute PetProfile petProfile) {
        petProfileService.updatePetProfile(petId, petProfile);
        return "redirect:/pet-profile?selectedId=" + petId;
    }

    @PostMapping("/new")
    public String createNewPet() {
        PetProfile newPet = petProfileService.createEmptyPet();
        return "redirect:/pet-profile?selectedId=" + newPet.getPetProfileId();
    }

    @GetMapping("/edit-mode")
    public String switchToEdit(@RequestParam("selectedId") Integer selectedId, Model model) {
        List<PetProfile> petProfiles = petProfileService.findAll();
        PetProfile selectedPet = petProfileService.findById(selectedId);
        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("selectedPet", selectedPet);
        model.addAttribute("editMode", true);
        return "pet-profile";
    }
}
