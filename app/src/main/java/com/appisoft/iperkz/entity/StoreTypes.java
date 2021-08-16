package com.appisoft.iperkz.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreTypes {
    private List<Item> items = null;
    private List<Object> categories = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Object> getCategories() {
        return categories;
    }

    public void setCategories(List<Object> categories) {
        this.categories = categories;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}