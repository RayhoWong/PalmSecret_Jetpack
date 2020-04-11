package com.example.oldlib.old;


public class AgeBean {
    private int iconId;
    private int age;
    private int ageingTextureId;
    private HairTextureBean hairBean;
    private float hairIntensity;
    private float ageIntensity;

    public AgeBean(int iconId, int age, int ageingTextureId, HairTextureBean hairBean, float hairIntensity, float ageIntensity) {
        this.iconId = iconId;
        this.age = age;
        this.ageingTextureId = ageingTextureId;
        this.hairBean = hairBean;
        this.hairIntensity = hairIntensity;
        this.ageIntensity = ageIntensity;
    }

    public int getAgeingTextureId() {
        return ageingTextureId;
    }

    public void setAgeingTextureId(int ageingTextureId) {
        this.ageingTextureId = ageingTextureId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public HairTextureBean getHairBean() {
        return hairBean;
    }

    public void setHairBean(HairTextureBean hairBean) {
        this.hairBean = hairBean;
    }

    public float getHairIntensity() {
        return hairIntensity;
    }

    public void setHairIntensity(float hairIntensity) {
        this.hairIntensity = hairIntensity;
    }

    public float getAgeIntensity() {
        return ageIntensity;
    }

    public void setAgeIntensity(float ageIntensity) {
        this.ageIntensity = ageIntensity;
    }
}
