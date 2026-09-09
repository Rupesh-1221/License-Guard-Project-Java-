package com.licenseguard.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartmentModel {
    private Integer departmentId;
    private String departmentName;
    private String description;
    private Integer userCount;

    public DepartmentModel() {
    }

    public DepartmentModel(String departmentName, String description) {
        this.departmentName = departmentName;
        this.description = description;
    }

    public DepartmentModel(Integer departmentId, String departmentName, String description, Integer userCount) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.description = description;
        this.userCount = userCount;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    @Override
    public String toString() {
        return departmentName != null ? departmentName : "";
    }
}
