package com.licenseguard.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "license_assignments")
public class LicenseAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Integer assignmentId;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    @Column(name = "unassigned_date")
    private LocalDate unassignedDate;

    @Column(name = "status", length = 30)
    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assignment_license"))
    private License license;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assignment_user"))
    private User user;

    public LicenseAssignment() {
    }

    public LicenseAssignment(LocalDate assignedDate, LocalDate unassignedDate, String status, License license, User user) {
        this.assignedDate = assignedDate;
        this.unassignedDate = unassignedDate;
        this.status = status != null ? status : "ACTIVE";
        this.license = license;
        this.user = user;
    }

    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
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

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
