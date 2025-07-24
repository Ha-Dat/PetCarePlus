package org.example.petcareplus.controller;

import org.example.petcareplus.entity.*;
import org.example.petcareplus.entity.Enum.BookingStatus;
import org.example.petcareplus.repository.*;
import org.example.petcareplus.service.CategoryService;
import org.example.petcareplus.service.SpaBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping()
public class SpaBookingController {

    private final SpaBookingRepository spaBookingRepository;
    private final SpaBookingService spaBookingService;
    private final ServiceRepository serviceRepository;
    private final PetProfileRepository petProfileRepository;

    @Autowired
    private CategoryService categoryService;

    public SpaBookingController(SpaBookingRepository spaBookingRepository, SpaBookingService spaBookingService, ServiceRepository serviceRepository, PetProfileRepository petProfileRepository) {
        this.spaBookingRepository = spaBookingRepository;
        this.spaBookingService = spaBookingService;
        this.serviceRepository = serviceRepository;
        this.petProfileRepository = petProfileRepository;
    }

    @GetMapping("/list-spa-booking")
    public String getAllSpaBookings(Model model,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "8") int size) {
        Page<SpaBooking> spaBookings= spaBookingService.findAll(page, size, "bookDate");
        if (page < 0) {
            return "redirect:/list-spa-booking?page=0&size=" + size;
        }

        model.addAttribute("spaBookings", spaBookings);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", spaBookings.getTotalPages());
        return "list-spa-booking";
    }

    @PostMapping("/list-spa-booking/approve-spa/{id}")
    @ResponseBody
    public String approveSpaBooking(@PathVariable("id") Long id){
        Optional<SpaBooking> booking = spaBookingRepository.findById(id);
        if (booking.isPresent()) {
            SpaBooking spaBooking = booking.get();
            if (BookingStatus.PENDING.equals(spaBooking.getStatus())){
                spaBooking.setStatus(BookingStatus.ACCEPTED);
                spaBookingRepository.save(spaBooking);
                return "Duyệt lịch thành công";
            }else {
                return "Lịch đặt đã được duyệt";
            }
        }
        return "Không tìm thấy lịch";
    }

    @PostMapping("/list-spa-booking/reject-spa/{id}")
    @ResponseBody
    public String rejectSpaBooking(@PathVariable("id") Long id){
        Optional<SpaBooking> booking = spaBookingRepository.findById(id);
        if (booking.isPresent()) {
            SpaBooking spaBooking = booking.get();
            if (BookingStatus.PENDING.equals(spaBooking.getStatus())){
                spaBooking.setStatus(BookingStatus.REJECTED);
                spaBookingRepository.save(spaBooking);
                return "Từ chối lịch thành công";
            }else {
                return "Lịch đặt đã từ chối";
            }
        }
        return "Không tìm thấy lịch";
    }

    @GetMapping("/spa-booking")
    public String showSpaBookingForm(Model model) {
        List<Category> parentCategories = categoryService.getParentCategory();

        // TODO: Add Authen
        model.addAttribute("spaBooking", new SpaBooking());
        model.addAttribute("spaServices", serviceRepository.findByServiceCategory("SPA"));
        model.addAttribute("categories", parentCategories);
        return "spa-booking";
    }

    @PostMapping("/spa-booking/book")
    public String submitSpaBooking(
            @RequestParam("bookDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bookDate,
            @RequestParam("note") String note,
            @RequestParam("serviceId") Long serviceId,
            @RequestParam("ownerInfo") String ownerInfo,
            @RequestParam("petName") String petName,
            @RequestParam("petSpecies") String petSpecies,
            @RequestParam("petBreed") String petBreed,
            @RequestParam("phoneNumber") String phoneNumber,
            Model model){

        try {
            // Tạo pet profile mới từ form
            PetProfile petProfile = new PetProfile();
            petProfile.setName(petName);
            petProfile.setSpecies(petSpecies);
            petProfile.setBreeds(petBreed);
            petProfile.setAge(2);
            petProfileRepository.save(petProfile);

            // gán dữ liệu từ form
            SpaBooking booking = new SpaBooking();
            booking.setBookDate(bookDate);
            booking.setNote(note);
            booking.setStatus(BookingStatus.PENDING);
            booking.setCreatedAt(LocalDateTime.now());

            booking.setPetProfile(petProfile);

            Optional<Service> service = serviceRepository.findById(serviceId);
            if (service.isEmpty()) {
                model.addAttribute("error", "Dịch vụ không tồn tại");
                return "error";
            }
            booking.setService(service.get());

            spaBookingRepository.save(booking);
            return "redirect:/spa-booking/form";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi đặt lịch: " + e.getMessage());
            return "error";
        }
    }

}
