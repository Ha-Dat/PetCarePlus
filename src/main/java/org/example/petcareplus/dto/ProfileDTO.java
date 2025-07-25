package org.example.petcareplus.dto;

public class ProfileDTO {
    private Long profileId;
    private String name;
    private String phone;

    private Long cityId;
    private Long districtId;
    private Long wardId;

    private String cityName;
    private String districtName;
    private String wardName;

    public ProfileDTO() {
    }

    public ProfileDTO(Long cityId, String cityName, Long districtId, String districtName, String name, String phone, Long profileId, Long wardId, String wardName) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.districtId = districtId;
        this.districtName = districtName;
        this.name = name;
        this.phone = phone;
        this.profileId = profileId;
        this.wardId = wardId;
        this.wardName = wardName;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getWardId() {
        return wardId;
    }

    public void setWardId(Long wardId) {
        this.wardId = wardId;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }
}
