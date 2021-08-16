package com.appisoft.iperkz.util;

import java.util.ArrayList;
import java.util.List;

public class Wrapper {
    private List items = new ArrayList();
    private List<Object> categories = null;

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public List<Object> getCategories() {
        return categories;
    }

    public void setCategories(List<Object> categories) {
        this.categories = categories;
    }
}
