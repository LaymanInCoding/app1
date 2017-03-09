package com.xiaomabao.weidian.models;

import com.flyco.tablayout.listener.CustomTabEntity;

/**
 * Created by de on 2017/3/6.
 */
public class TabEntity implements CustomTabEntity {

    private String title;
    private int unChooseIcon;
    private int chooseIcon;

    public TabEntity(String title, int unChooseIcon, int chooseIcon) {
        this.title = title;
        this.unChooseIcon = unChooseIcon;
        this.chooseIcon = chooseIcon;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return chooseIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unChooseIcon;
    }
}
