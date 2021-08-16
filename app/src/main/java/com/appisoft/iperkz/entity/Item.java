package com.appisoft.iperkz.entity;

import java.util.HashMap;
import java.util.Map;

public class Item {

    private Integer storeTypeId;
    private String storeTypeName;
    private Object storeTypeDescription;
    private String imageURL;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getStoreTypeId() {
        return storeTypeId;
    }

    public void setStoreTypeId(Integer storeTypeId) {
        this.storeTypeId = storeTypeId;
    }

    public String getStoreTypeName() {
        return storeTypeName;
    }

    public void setStoreTypeName(String storeTypeName) {
        this.storeTypeName = storeTypeName;
    }

    public Object getStoreTypeDescription() {
        return storeTypeDescription;
    }

    public void setStoreTypeDescription(Object storeTypeDescription) {
        this.storeTypeDescription = storeTypeDescription;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
