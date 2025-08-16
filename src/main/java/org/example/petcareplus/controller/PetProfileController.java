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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping()
public class PetProfileController {

    @Autowired
    private PetProfileService petProfileService;



    @GetMapping("/pet-profile")
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

    @PostMapping("/pet-profile/edit")
    public String editPetProfile(@RequestParam("petId") Long petId,
                                 @RequestParam("name") String name,
                                 @RequestParam("species") String species,
                                 @RequestParam("breeds") String breeds,
                                 @RequestParam("age") Integer age,
                                 @RequestParam("weight") Float weight,
                                 HttpSession session,
                                 Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        
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
        
        return "redirect:/pet-profile?selectedId=" + petId;
    }

    @PostMapping("/pet-profile/add")
    public String createNewPet(@RequestParam("name") String name,
                               @RequestParam("age") Integer age,
                               @RequestParam("species") String species,
                               @RequestParam("breeds") String breeds,
                               @RequestParam("weight") Float weight,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                               HttpSession session,
                               Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");

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

            return "redirect:/pet-profile?selectedId=" + savedPet.getPetProfileId();
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tạo thú cưng: " + e.getMessage());
            model.addAttribute("petProfiles", petProfileService.findByAccount(account));
            model.addAttribute("selectedPet", null);
            model.addAttribute("edit", false);
            return "pet-profile";
        }
    }

    @GetMapping("/pet-profile/edit")
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

    @PostMapping("/pet-profile/save")
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

                return "redirect:/pet-profile?selectedId=" + petId;
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

                return "redirect:/pet-profile?selectedId=" + savedPet.getPetProfileId();
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
    @PostMapping("/pet-profile/test-upload")
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

    @PostMapping("/pet-profile/delete")
    public String deletePet(@RequestParam("petId") Long petId,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Account account = (Account) session.getAttribute("loggedInUser");
        
        if (account == null) {
            System.out.println("DEBUG: User not logged in, redirecting to login");
            return "redirect:/login";
        }
        
        System.out.println("DEBUG: Attempting to delete pet with ID: " + petId);
        
        try {
            // Kiểm tra xem có thể xóa không
            if (!petProfileService.canDeletePet(petId)) {
                System.out.println("DEBUG: Pet cannot be deleted, getting error message");
                // Lấy thông tin chi tiết về lý do không thể xóa
                String errorMessage = getDeleteErrorMessage(petId);
                System.out.println("DEBUG: Error message: " + errorMessage);
                redirectAttributes.addFlashAttribute("error", errorMessage);
                return "redirect:/pet-profile";
            }
            
            System.out.println("DEBUG: Pet can be deleted, proceeding with deletion");
            
            // Xóa thú cưng
            petProfileService.deletePet(petId);
            
            System.out.println("DEBUG: Pet deleted successfully, redirecting with success message");

            redirectAttributes.addFlashAttribute("success", "Xóa thú cưng thành công");
            
            return "redirect:/pet-profile";
            
        } catch (Exception e) {
            System.out.println("DEBUG: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            String errorMessage = "Có lỗi xảy ra khi xóa thú cưng: " + e.getMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/pet-profile";
        }
    }

    private String getDeleteErrorMessage(Long petId) {
        try {
            // Kiểm tra từng loại booking để đưa ra thông báo cụ thể
            List<org.example.petcareplus.entity.AppointmentBooking> appointments = 
                petProfileService.getAppointmentsByPetId(petId);
            List<org.example.petcareplus.entity.HotelBooking> hotelBookings = 
                petProfileService.getHotelBookingsByPetId(petId);
            List<org.example.petcareplus.entity.SpaBooking> spaBookings = 
                petProfileService.getSpaBookingsByPetId(petId);
            
            StringBuilder message = new StringBuilder();
            message.append("Không thể xóa thú cưng vì đang sử dụng các dịch vụ sau: ");
            
            if (!appointments.isEmpty()) {
                message.append("Đặt lịch khám (").append(appointments.size()).append(" lịch hẹn), ");
            }
            if (!hotelBookings.isEmpty()) {
                message.append("Đặt khách sạn (").append(hotelBookings.size()).append(" lịch đặt phòng), ");
            }
            if (!spaBookings.isEmpty()) {
                message.append("Đặt spa (").append(spaBookings.size()).append(" lịch hẹn), ");
            }
            
            // Xóa dấu phẩy cuối cùng
            if (message.charAt(message.length() - 2) == ',') {
                message.setLength(message.length() - 2);
            }
            
            message.append(". Vui lòng hủy tất cả dịch vụ trước khi xóa thú cưng.");
            
            return message.toString();
        } catch (Exception e) {
            return "Không thể xóa thú cưng. Thú cưng đang sử dụng dịch vụ.";
        }
    }
}
