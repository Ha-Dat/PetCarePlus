package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.entity.Service;
import org.example.petcareplus.entity.SpaBooking;
import org.example.petcareplus.enums.ServiceCategory;
import org.example.petcareplus.service.PetProfileService;
import org.example.petcareplus.service.ServiceService;
import org.example.petcareplus.entity.*;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.SpaBookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping()
public class SpaBookingController {

    private final SpaBookingService spaBookingService;
    private final PetProfileService petProfileService;
    private final ServiceService serviceService;
    private final CategoryService categoryService;

    public SpaBookingController(SpaBookingService spaBookingService, PetProfileService petProfileService, ServiceService serviceService, CategoryService categoryService) {
        this.spaBookingService = spaBookingService;
        this.petProfileService = petProfileService;
        this.serviceService = serviceService;
        this.categoryService = categoryService;
    }

    @GetMapping("/pet-groomer/list-spa-booking")
    public String getAllSpaBookings(Model model,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "7") int size,
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookDate").descending());
        Page<SpaBooking> spaBookings;
        if (page < 0) {
            return "redirect:/list-spa-booking?page=0&size=" + size;
        }
        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            spaBookings = spaBookingService.findByBookDateBetween(startOfDay, endOfDay, pageable);
            model.addAttribute("selectedDate", date);
        } else {
            spaBookings = spaBookingService.findAll(page, size, "bookDate");
        }

        model.addAttribute("spaBookings", spaBookings.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", spaBookings.getTotalPages());
        return "list-spa-booking";
    }

    @PostMapping("/pet-groomer/list-spa-booking/approve-spa/{id}")
    @ResponseBody
    public String approveSpaBooking(@PathVariable("id") Long id){
        Optional<SpaBooking> booking = spaBookingService.findById(id);
        if (booking.isPresent()) {
            SpaBooking spaBooking = booking.get();
            if (BookingStatus.PENDING.equals(spaBooking.getStatus())){
                spaBooking.setStatus(BookingStatus.ACCEPTED);
                spaBookingService.save(spaBooking);
                return "Duyệt lịch thành công";
            }else {
                return "Lịch đặt đã được duyệt";
            }
        }
        return "Không tìm thấy lịch";
    }

    @PostMapping("/pet-groomer/list-spa-booking/reject-spa/{id}")
    @ResponseBody
    public String rejectSpaBooking(@PathVariable("id") Long id){
        Optional<SpaBooking> booking = spaBookingService.findById(id);
        if (booking.isPresent()) {
            SpaBooking spaBooking = booking.get();
            if (BookingStatus.PENDING.equals(spaBooking.getStatus())){
                spaBooking.setStatus(BookingStatus.REJECTED);
                spaBookingService.save(spaBooking);
                return "Từ chối lịch thành công";
            }else {
                return "Lịch đã được duyệt!, Không thể từ chối";
            }
        }
        return "Không tìm thấy lịch";
    }

    @PostMapping("/pet-groomer/list-spa-booking/complete-spa/{id}")
    @ResponseBody
    public String completeSpaBooking(@PathVariable("id") Long id){
        Optional<SpaBooking> booking = spaBookingService.findById(id);
        if (booking.isPresent()) {
            SpaBooking spaBooking = booking.get();
            spaBooking.setStatus(BookingStatus.COMPLETED);
            spaBookingService.save(spaBooking);
            return "Xác nhận hoàn thành đơn thành công!";
        }
        return "Không tìm thấy lịch";
    }


    @GetMapping("/spa-booking-form")
    public String showSpaBookingForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        List<Category> parentCategories = categoryService.getParentCategory();

        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) {
            return "redirect:/login";
        }

        // Kiểm tra role - chỉ CUSTOMER mới được đặt lịch spa
        if (account.getRole() != AccountRole.CUSTOMER) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ khách hàng mới được phép đặt lịch spa.");
            return "redirect:/home";
        }

        List<PetProfile> petProfiles = petProfileService.findByProfileId(account.getProfile().getProfileId());

        model.addAttribute("petProfiles", petProfiles);
        model.addAttribute("spaBooking", new SpaBooking());
        model.addAttribute("spaServices", serviceService.findActiveByServiceCategory(ServiceCategory.SPA));
        model.addAttribute("categories", parentCategories);
        return "spa-booking";
    }

    @PostMapping("/spa-booking/book/{id}")
    public String addSpaBooking(
            @RequestParam("bookDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bookDate,
            @RequestParam("note") String note,
            @RequestParam("serviceId") Long serviceId,
            @RequestParam("petName") String petName,
            @RequestParam("petSpecies") String petSpecies,
            @RequestParam("petBreed") String petBreed,
            @RequestParam("petWeight") float petWeight,
            @RequestParam("petAge") Integer petAge,
            @PathVariable("id") Long petProfileId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Account account = (Account) session.getAttribute("loggedInUser");
            if (account == null) return "redirect:/login";

            // Kiểm tra role - chỉ CUSTOMER mới được đặt lịch spa
            if (account.getRole() != AccountRole.CUSTOMER) {
                redirectAttributes.addFlashAttribute("errorMessage", "Chỉ khách hàng mới được phép đặt lịch spa.");
                return "redirect:/home";
            }
            PetProfile petProfile = petProfileService.findById(petProfileId);
            petProfile.setName(petName);
            petProfile.setSpecies(petSpecies);
            petProfile.setBreeds(petBreed);
            petProfile.setWeight(petWeight);
            petProfile.setAge(petAge);
            petProfile.setProfile(account.getProfile());
            petProfileService.updatePetProfile(petProfileId, petProfile);

            // gán dữ liệu từ form
            SpaBooking booking = new SpaBooking();
            booking.setBookDate(bookDate);
            booking.setNote(note);
            booking.setStatus(BookingStatus.PENDING);
            booking.setCreatedAt(LocalDateTime.now());

            booking.setPetProfile(petProfile);

            Optional<Service> service = spaBookingService.Service_findById(serviceId);
            if (service.isEmpty()) {
                model.addAttribute("error", "Dịch vụ không tồn tại");
                return "error";
            }
            booking.setService(service.get());

            spaBookingService.save(booking);
            return "redirect:/spa-booking-form";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi đặt lịch: " + e.getMessage());
            return "error";
        }
    }

}
