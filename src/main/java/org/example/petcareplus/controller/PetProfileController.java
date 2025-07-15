package org.example.petcareplus.controller;

import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.service.PetProfileService;
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
    private PetProfileService petProfileService;

    @GetMapping
    public String showPetProfilePage(Model model,
                                     @RequestParam(value = "selectedId", required = false) Long selectedId) {
        List<PetProfile> petProfiles = petProfileService.findAll();

        PetProfile selectedPet = null;

        if (!petProfiles.isEmpty()) {
            selectedPet = (selectedId != null)
                    ? petProfileService.findById(selectedId)
                    : petProfiles.get(0);
        }

        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("selectedPet", selectedPet);
        model.addAttribute("editMode", false);
        return "pet-profile";
    }

    @PostMapping("/edit")
    public String editPetProfile(@RequestParam("petId") Long petId,
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
    public String switchToEdit(@RequestParam("selectedId") Long selectedId, Model model) {
        List<PetProfile> petProfiles = petProfileService.findAll();
        PetProfile selectedPet = petProfileService.findById(selectedId);
        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("selectedPet", selectedPet);
        model.addAttribute("editMode", true);
        return "pet-profile";
    }

    @PostMapping
    public String savePetProfile(@RequestParam(value = "petId", required = false) Long petId,
                                 @ModelAttribute PetProfile petProfile) {
        if (petId != null) {
            petProfileService.updatePetProfile(petId, petProfile);
            return "redirect:/pet-profile?selectedId=" + petId;
        } else {
            PetProfile newPet = petProfileService.createEmptyPet();
            return "redirect:/pet-profile?selectedId=" + newPet.getPetProfileId();
        }
    }
}
