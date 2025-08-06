package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.Media;
import org.example.petcareplus.entity.PetProfile;
import org.example.petcareplus.enums.MediaCategory;
import org.example.petcareplus.repository.MediaRepository;
import org.example.petcareplus.repository.PetProfileRepository;
import org.example.petcareplus.service.PetProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.*;
import java.util.UUID;
import java.util.ArrayList;

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
    public List<PetProfile> findByAccount(Account account) {
        if (account == null || account.getProfile() == null) {
            return new ArrayList<>();
        }
        return petProfileRepository.findByProfile(account.getProfile());
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
        pet.setAge(1);
        pet.setSpecies("");
        pet.setBreeds("");
        pet.setWeight(0.0f);
        return pet;
    }

    @Override
    public PetProfile save(PetProfile petProfile) {
        return petProfileRepository.save(petProfile);
    }

    @Override
    public List<PetProfile> findByProfileId(Long profileId) {
        return petProfileRepository.findAllByProfile_ProfileId(profileId);
    }

    @Override
    public void uploadPetImage(Long petProfileId, MultipartFile imageFile) {
        try {
            // Validate file
            if (imageFile == null || imageFile.isEmpty()) {
                return;
            }

            // Validate file size (5MB max)
            if (imageFile.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException("File size too large. Maximum 5MB allowed.");
            }

            // Validate file type
            String contentType = imageFile.getContentType();
            if (contentType == null || (!contentType.startsWith("image/"))) {
                throw new RuntimeException("Invalid file type. Only images are allowed.");
            }

            Media oldMedia = mediaRepository.findByPetProfile_petProfileId(petProfileId);
            if (oldMedia != null) {
                deleteFileFromS3(oldMedia.getUrl());
                mediaRepository.delete(oldMedia);
            }

            // Upload to local storage
            String fileUrl = saveFileToS3(imageFile, "uploads/images/");

            // Save media info to database
            Media media = new Media();
            media.setMediaCategory(MediaCategory.PET_IMAGE);
            media.setUrl(fileUrl);

            PetProfile petProfile = petProfileRepository.findById(petProfileId).orElse(null);
            if (petProfile != null) {
                media.setPetProfile(petProfile);
                mediaRepository.save(media);
            } else {
                throw new RuntimeException("PetProfile not found with ID: " + petProfileId);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<PetProfile> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<PetProfile> petProfiles = petProfileRepository.findAll(pageable);
        return petProfiles;
    }

    public Page<PetProfile> searchByName(String name, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return petProfileRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    //lưu file lên S3
    private String saveFileToS3(MultipartFile file, String folder) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String key = folder + fileName;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket("petcareplus")
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }

        return "https://petcareplus.s3.ap-southeast-2.amazonaws.com/" + key;
    }

    public void deleteFileFromS3(String url) {
        String prefix = "https://petcareplus.s3.ap-southeast-2.amazonaws.com/";
        String key = url.replace(prefix, "");

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket("petcareplus")
                        .key(key)
                        .build()
        );
    }
}
