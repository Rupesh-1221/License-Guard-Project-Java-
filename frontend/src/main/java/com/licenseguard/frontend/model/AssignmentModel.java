package com.licenseguard.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignmentModel {
    private Integer assignmentId;
    private Integer licenseId;
    private String licenseKey;
    private String softwareName;
    private Integer userId;
    private String userName;
    private String userEmail;
    private LocalDate assignedDate;
    private LocalDate unassignedDate;
    private String status;

    public AssignmentModel() {
    }

    public AssignmentModel(Integer assignmentId, Integer licenseId, String licenseKey, String softwareName, Integer userId, String userName, String userEmail, LocalDate assignedDate, LocalDate unassignedDate, String status) {
        this.assignmentId = assignmentId;
        this.licenseId = licenseId;
        this.licenseKey = licenseKey;
        this.softwareName = softwareName;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.assignedDate = assignedDate;
        this.unassignedDate = unassignedDate;
        this.status = status;
    }

    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Integer getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(Integer licenseId) {
        this.licenseId = licenseId;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDate getUnassignedDate() {
        return unassignedDate;
    }

    public void setUnassignedDate(LocalDate unassignedDate) {
        this.unassignedDate = unassignedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
