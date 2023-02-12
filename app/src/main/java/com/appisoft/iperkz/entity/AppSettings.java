package com.appisoft.iperkz.entity;

import java.util.List;

public class AppSettings {

    private List<Setting> settings = null;

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    public String getSettingValueByKey(String key) {
        if (settings == null) {
            return null;
        }
        for(Setting setting : settings){
            if(setting.getKey().equals(key)){
              return setting.getValue();
            }
        }
        return null;
    }

}
