package com.api.countryinfo.model;


import jakarta.persistence.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class CountryInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String isoCode;
    private String name;
    private String capitalCity;
    private String phoneCode;
    private String continentCode;
    private String currencyISOCode;
    private String countryFlag;


    @OneToMany(mappedBy = "countryInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference

    private List<Language> languages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapitalCity() {
        return capitalCity;
    }

    public void setCapitalCity(String capitalCity) {
        this.capitalCity = capitalCity;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public void setContinentCode(String continentCode) {
        this.continentCode = continentCode;
    }

    public String getCurrencyISOCode() {
        return currencyISOCode;
    }

    public void setCurrencyISOCode(String currencyISOCode) {
        this.currencyISOCode = currencyISOCode;
    }

    public String getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    @Override
    public String toString() {
        return "CountryInfo{" +
                "id=" + id +
                ", isoCode='" + isoCode + '\'' +
                ", name='" + name + '\'' +
                ", capitalCity='" + capitalCity + '\'' +
                ", phoneCode='" + phoneCode + '\'' +
                ", continentCode='" + continentCode + '\'' +
                ", currencyISOCode='" + currencyISOCode + '\'' +
                ", countryFlag='" + countryFlag + '\'' +
                ", languages=" + languages +
                '}';
    }
}
