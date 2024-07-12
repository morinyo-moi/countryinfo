package com.api.countryinfo.util;


import com.api.countryinfo.DTO.CountryDTO;
import com.api.countryinfo.DTO.LanguageDTO;
import com.api.countryinfo.model.CountryInfo;
import com.api.countryinfo.model.Language;

import java.util.List;
import java.util.stream.Collectors;
public class CountryConverter {
     public static CountryDTO convertToDTO(CountryInfo countryInfo) {
        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setId(countryInfo.getId());
        countryDTO.setIsoCode(countryInfo.getIsoCode());
        countryDTO.setName(countryInfo.getName());
        countryDTO.setCapitalCity(countryInfo.getCapitalCity());
        countryDTO.setPhoneCode(countryInfo.getPhoneCode());
        countryDTO.setContinentCode(countryInfo.getContinentCode());
        countryDTO.setCurrencyISOCode(countryInfo.getCurrencyISOCode());
        countryDTO.setCountryFlag(countryInfo.getCountryFlag());
        
        List<LanguageDTO> languages = countryInfo.getLanguages().stream()
                .map(CountryConverter::convertToDTO)
                .collect(Collectors.toList());
        countryDTO.setLanguages(languages);
        
        return countryDTO;
    }

    public static List<CountryDTO> convertToDTOList(List<CountryInfo> countryInfoList) {
        return countryInfoList.stream()
                              .map(CountryConverter::convertToDTO)
                              .collect(Collectors.toList());
    }

    public static LanguageDTO convertToDTO(Language language) {
        LanguageDTO languageDTO = new LanguageDTO();
        languageDTO.setId(language.getId());
        languageDTO.setIsoCode(language.getIsoCode());
        languageDTO.setName(language.getName());
        return languageDTO;
    }

    public static CountryInfo convertToEntity(CountryDTO countryDTO) {
        CountryInfo countryInfo = new CountryInfo();
        countryInfo.setId(countryDTO.getId());
        countryInfo.setIsoCode(countryDTO.getIsoCode());
        countryInfo.setName(countryDTO.getName());
        countryInfo.setCapitalCity(countryDTO.getCapitalCity());
        countryInfo.setPhoneCode(countryDTO.getPhoneCode());
        countryInfo.setContinentCode(countryDTO.getContinentCode());
        countryInfo.setCurrencyISOCode(countryDTO.getCurrencyISOCode());
        countryInfo.setCountryFlag(countryDTO.getCountryFlag());
        
        List<Language> languages = countryDTO.getLanguages().stream()
                .map(languageDTO -> {
                    Language language = CountryConverter.convertToEntity(languageDTO);
                    language.setCountryInfo(countryInfo); // Set the countryInfo reference
                    return language;
                })
                .collect(Collectors.toList());
        countryInfo.setLanguages(languages);
        
        return countryInfo;
    }

    public static Language convertToEntity(LanguageDTO languageDTO) {
        Language language = new Language();
        language.setId(languageDTO.getId());
        language.setIsoCode(languageDTO.getIsoCode());
        language.setName(languageDTO.getName());
        return language;
    }
}
