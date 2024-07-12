package com.api.countryinfo.repository;

import com.api.countryinfo.model.CountryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryInformationRepository extends JpaRepository<CountryInfo, Long> {
}
