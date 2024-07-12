package com.api.countryinfo.service;



import com.api.countryinfo.model.CountryInfo;

import java.util.List;
import java.util.Optional;

public interface CountryService {
    public CountryInfo saveCountryInfo(CountryInfo countryInfo);
    public List<CountryInfo> getAllCountryInfo();
    public Boolean getCountyIsoByCountryName(String countryName);
    public CountryInfo getCountyIsoByCountryName2(String countryName, Long id);
    public Optional<CountryInfo> findById(Long countryId);
    public CountryInfo updateCountryInfo(CountryInfo countryInfo);
    public void deleteCountryInfo(Long countryId);
    public String convertToSentenceCase(String name);

}
