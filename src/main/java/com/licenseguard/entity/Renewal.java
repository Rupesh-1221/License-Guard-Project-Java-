package com.licenseguard.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "renewals")
public class Renewal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "renewal_id")
    private Integer renewalId;

    @Column(name = "old_expiry_date", nullable = false)
    private LocalDate oldExpiryDate;

    @Column(name = "new_expiry_date", nullable = false)
    private LocalDate newExpiryDate;

    @Column(name = "renewal_date", nullable = false)
    private LocalDate renewalDate;

    @Column(name = "renewal_cost", precision = 10, scale = 2)
    private BigDecimal renewalCost;

    @Column(name = "remarks", length = 255)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id", nullable = false, foreignKey = @ForeignKey(name = "fk_renewal_license"))
    private License license;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "renewed_by", foreignKey = @ForeignKey(name = "fk_renewal_user"))
    private User renewedByUser;

    public Renewal() {
    }

    public Renewal(LocalDate oldExpiryDate, LocalDate newExpiryDate, LocalDate renewalDate,
                   BigDecimal renewalCost, String remarks, License license, User renewedByUser) {
        this.oldExpiryDate = oldExpiryDate;
        this.newExpiryDate = newExpiryDate;
        this.renewalDate = renewalDate;
        this.renewalCost = renewalCost;
        this.remarks = remarks;
        this.license = license;
        this.renewedByUser = renewedByUser;
    }

    public Integer getRenewalId() {
        return renewalId;
    }

    public void setRenewalId(Integer renewalId) {
        this.renewalId = renewalId;
    }

    public LocalDate getOldExpiryDate() {
        return oldExpiryDate;
    }

    public void setOldExpiryDate(LocalDate oldExpiryDate) {
        this.oldExpiryDate = oldExpiryDate;
    }

    public LocalDate getNewExpiryDate() {
        return newExpiryDate;
    }

    public void setNewExpiryDate(LocalDate newExpiryDate) {
        this.newExpiryDate = newExpiryDate;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(LocalDate renewalDate) {
        this.renewalDate = renewalDate;
    }

    public BigDecimal getRenewalCost() {
        return renewalCost;
    }

    public void setRenewalCost(BigDecimal renewalCost) {
        this.renewalCost = renewalCost;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public User getRenewedByUser() {
        return renewedByUser;
    }

    public void setRenewedByUser(User renewedByUser) {
        this.renewedByUser = renewedByUser;
    }
}
