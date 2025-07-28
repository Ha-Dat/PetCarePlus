package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Media;
import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.enums.MediaCategory;
import org.example.petcareplus.repository.MediaRepository;
import org.example.petcareplus.repository.PetProfileRepository;
import org.example.petcareplus.service.PetProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.*;
import java.util.UUID;

@Service
public class PetProfileServiceImpl implements PetProfileService {

    private final PetProfileRepository petProfileRepository;
    private final MediaRepository mediaRepository;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public PetProfileServiceImpl(PetProfileRepository petProfileRepository, 
                               MediaRepository mediaRepository,
                               S3Client s3Client) {
        this.petProfileRepository = petProfileRepository;
        this.mediaRepository = mediaRepository;
        this.s3Client = s3Client;
    }

    @Override
    public List<PetProfile> findAll() {
        return petProfileRepository.findAll();
    }

    @Override
    public List<PetProfile> findByAccount(Account account) {
        return petProfileRepository.findByProfileAccount(account);
    }

    @Override
    public PetProfile findById(Long id) {
        return petProfileRepository.findById(Long.valueOf(id)).orElse(null);
    }

    @Override
    public void updatePetProfile(Long id, PetProfile updated) {
        PetProfile existing = findById(id);
        if (existing != null) {
            existing.setName(updated.getName());
            existing.setAge(updated.getAge());
            existing.setSpecies(updated.getSpecies());
            existing.setBreeds(updated.getBreeds());
            existing.setWeight(updated.getWeight());
            petProfileRepository.save(existing);
        }
    }

    @Override
    public PetProfile createEmptyPet() {
        PetProfile pet = new PetProfile();
        pet.setName("Thú cưng mới");
        pet.setAge(0);
        pet.setSpecies("");
        pet.setBreeds("");
        pet.setWeight(0.0f);
        return petProfileRepository.save(pet);
    }

    @Override
    public PetProfile save(PetProfile petProfile) {
        return petProfileRepository.save(petProfile);
    }

    @Override
    public void uploadPetImage(Long petProfileId, MultipartFile imageFile) {
        try {
            // Validate file
            if (imageFile == null || imageFile.isEmpty()) {
                System.out.println("No image file provided");
                return;
            }
            
            System.out.println("Starting image upload for pet ID: " + petProfileId);
            System.out.println("File name: " + imageFile.getOriginalFilename());
            System.out.println("File size: " + imageFile.getSize());
            System.out.println("Content type: " + imageFile.getContentType());
            
            // Validate file size (5MB max)
            if (imageFile.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException("File size too large. Maximum 5MB allowed.");
            }
            
            // Validate file type
            String contentType = imageFile.getContentType();
            if (contentType == null || (!contentType.startsWith("image/"))) {
                throw new RuntimeException("Invalid file type. Only images are allowed.");
            }
            
            // Try local storage first (simpler for testing)
            String fileUrl = uploadToLocal(petProfileId, imageFile);
            System.out.println("Image uploaded to local storage: " + fileUrl);
            
            // Lưu thông tin media vào database
            Media media = new Media();
            media.setMediaCategory(MediaCategory.PET_IMAGE);
            media.setUrl(fileUrl);
            
            PetProfile petProfile = petProfileRepository.findById(petProfileId).orElse(null);
            if (petProfile != null) {
                media.setPetProfile(petProfile);
                Media savedMedia = mediaRepository.save(media);
                System.out.println("Media saved to database with ID: " + savedMedia.getMediaId());
                System.out.println("Media URL: " + savedMedia.getUrl());
            } else {
                System.out.println("PetProfile not found with ID: " + petProfileId);
                throw new RuntimeException("PetProfile not found with ID: " + petProfileId);
            }
            
        } catch (Exception e) {
            System.err.println("Error uploading image: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }
    
    private String uploadToS3(Long petProfileId, MultipartFile imageFile) throws IOException {
        // Tạo unique key cho file
        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "image.jpg";
        }
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String key = "pet-images/" + petProfileId + "/" + UUID.randomUUID().toString() + fileExtension;
        
        System.out.println("Uploading image to S3: " + key);
        System.out.println("Bucket: " + bucketName);
        
        // Upload file lên S3
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(imageFile.getContentType())
                .build(),
                RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize()));
        
        // Tạo URL cho file
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
    
    private String uploadToLocal(Long petProfileId, MultipartFile imageFile) throws IOException {
        try {
            // Tạo thư mục uploads nếu chưa có
            String uploadDir = "uploads/pet-images/" + petProfileId;
            java.io.File dir = new java.io.File(uploadDir);
            System.out.println("Creating directory: " + dir.getAbsolutePath());
            
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                System.out.println("Directory created: " + created);
            }
            
            // Tạo tên file unique
            String originalFilename = imageFile.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "image.jpg";
            }
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            System.out.println("File name: " + fileName);
            System.out.println("File extension: " + fileExtension);
            
            // Lưu file
            java.io.File file = new java.io.File(dir, fileName);
            System.out.println("Saving file to: " + file.getAbsolutePath());
            
            imageFile.transferTo(file);
            
            // Kiểm tra file đã được lưu
            if (file.exists()) {
                System.out.println("File saved successfully. Size: " + file.length() + " bytes");
            } else {
                System.out.println("File was not saved!");
            }
            
            // Trả về URL local
            String url = "/uploads/pet-images/" + petProfileId + "/" + fileName;
            System.out.println("Returning URL: " + url);
            return url;
            
        } catch (Exception e) {
            System.err.println("Error in uploadToLocal: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
