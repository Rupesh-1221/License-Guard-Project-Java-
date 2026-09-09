package com.licenseguard.dto.response;

public class DepartmentResponse {
    private Integer departmentId;
    private String departmentName;
    private String description;
    private Integer userCount;

    public DepartmentResponse() {
    }

    public DepartmentResponse(Integer departmentId, String departmentName, String description, Integer userCount) {
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
}
