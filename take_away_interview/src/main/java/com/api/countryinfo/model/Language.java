package com.api.countryinfo.model;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;


@Entity
public class Language{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String isoCode;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "country_info_id")
    @JsonBackReference
    private CountryInfo countryInfo;

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

    public CountryInfo getCountryInfo() {
        return countryInfo;
    }

    public void setCountryInfo(CountryInfo countryInfo) {
        this.countryInfo = countryInfo;
    }

    @Override
        public String toString() {
            return "Language{" +
                    "id=" + id +
                    ", isoCode='" + isoCode + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
}
