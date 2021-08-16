package com.appisoft.iperkz.entity;


public class Reward {

    private Integer perkzId;
    private String name;
    private String startdate;
    private String endDate;
    private Double reward;
    private String rewardType;
    private Integer storeId;
    private String description;
    private String perkzType;
    private Double claimedReward =0.0;
    private Double previousClaim =0.0;

    public Integer getPerkzId() {
        return perkzId;
    }

    public void setPerkzId(Integer id) {
        this.perkzId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Double getReward() {
        return reward;
    }

    public void setReward(Double reward) {
        this.reward = reward;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPerkzType() {
        return perkzType;
    }

    public void setPerkzType(String perkzType) {
        this.perkzType = perkzType;
    }

    public Double getClaimedReward() {
        return claimedReward;
    }

    public void setClaimedReward(Double claimedReward) {
        this.claimedReward = claimedReward;
    }

    public Double getPreviousClaim() {
        return previousClaim;
    }
    public void setPreviousClaim(Double previousClaim) {
        this.previousClaim = previousClaim;
    }
}
