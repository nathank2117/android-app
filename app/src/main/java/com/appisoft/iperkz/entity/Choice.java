package com.appisoft.iperkz.entity;

import java.util.List;

public class Choice {

    private Integer menuId;
    private Integer additionsId;
    private String name;
    private String description;
    private Double price;
    private boolean isSelected = false;
    private Integer parentId;
    private String selectionType;
    private Integer childCount;
    private List<SubItem> subItems = null;
    private boolean defaultSelection = false;

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Integer getAdditionsId() {
        return additionsId;
    }

    public void setAdditionsId(Integer additionsId) {
        this.additionsId = additionsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(String selectionType) {
        this.selectionType = selectionType;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public List<SubItem> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<SubItem> subItems) {
        this.subItems = subItems;
    }

    public boolean isDefaultSelection() {
        return defaultSelection;
    }

    public void setDefaultSelection(boolean defaultSelection) {
        this.defaultSelection = defaultSelection;
    }
}

