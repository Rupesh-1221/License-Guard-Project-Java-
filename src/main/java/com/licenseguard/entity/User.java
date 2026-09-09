package com.licenseguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "role", length = 30, nullable = false)
    private String role;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_user_department"))
    private Department department;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LicenseAssignment> licenseAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "renewedByUser", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Renewal> renewals = new ArrayList<>();

    public User() {
    }

    public User(String name, String email, String password, String role, String status, Department department) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status != null ? status : "ACTIVE";
        this.department = department;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<LicenseAssignment> getLicenseAssignments() {
        return licenseAssignments;
    }

    public void setLicenseAssignments(List<LicenseAssignment> licenseAssignments) {
        this.licenseAssignments = licenseAssignments;
    }

    public List<Renewal> getRenewals() {
        return renewals;
    }

    public void setRenewals(List<Renewal> renewals) {
        this.renewals = renewals;
    }
}
