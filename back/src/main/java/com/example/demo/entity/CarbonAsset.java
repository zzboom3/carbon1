package com.example.demo.entity;

import java.math.BigDecimal;

public class CarbonAsset {
    private Integer id;
    private Integer uid;        // 用户ID
    private String projectName; // 项目名称
    private String assetType;   // 资产类型
    private BigDecimal amount;  // 金额

    // Getter & Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUid() { return uid; }
    public void setUid(Integer uid) { this.uid = uid; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}