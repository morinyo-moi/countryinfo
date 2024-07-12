package com.api.countryinfo.controller;

import com.api.countryinfo.DTO.CountryDTO;
import com.api.countryinfo.model.CountryInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.api.countryinfo.service.CountryService;
import com.api.countryinfo.util.CountryConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import java.util.List;


@RestController
@RequestMapping("/api")
public class CountryInformationController {
    @Autowired
    CountryService countryService;

    private static final Logger logger = LoggerFactory.getLogger(CountryInformationController.class);



    @PostMapping("/format-country-name")
    public ResponseEntity<?> toSentenceCase(@RequestBody CountryInfo countryName) {
        try{
            String name = countryService.convertToSentenceCase(countryName.getName());
            logger.info("The country name is  "+name);
            Boolean countryInfo = countryService.getCountyIsoByCountryName(name);

            if(countryInfo){
                return ResponseEntity.ok(countryInfo);
            }else{
                return ResponseEntity.ok("No cuntry with such a name");
            }

            
        }catch (Exception e) {
            logger.error("Error while formating country infromations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


    @GetMapping("/get-all-country-info")
    public ResponseEntity<?> getAllCountryInfo() {
        try{
            List<CountryInfo> countryInfoList = countryService.getAllCountryInfo();
            if (!countryInfoList.isEmpty()) {

                List<CountryDTO> responseCountryDTOList = CountryConverter.convertToDTOList(countryInfoList);

                return new ResponseEntity<>(responseCountryDTOList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching all country infromations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
    @GetMapping("/get-country-by-id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try{
            Optional<CountryInfo> countryInfo = countryService.findById(id);
            if (countryInfo.isPresent()) {

                CountryInfo countrydetails =  countryInfo.get();
 
                //convert  to DTO
                CountryDTO responseCountryDTO = CountryConverter.convertToDTO(countrydetails);

                return new ResponseEntity<>(responseCountryDTO, HttpStatus.OK);
 
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot find country with id: "+id);
            }
         } catch (Exception e) {
            logger.error("Error while fetching country with ID " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PutMapping("/update-countryinfo/{id}")
    public ResponseEntity<CountryDTO> updateCountryInfo(@PathVariable Long id, @RequestBody CountryInfo newCountryInfo) {
        Optional<CountryInfo> existingCountryInfo = countryService.findById(id);
        if (existingCountryInfo.isPresent()) {

            //only update name as the rest of the fileds will be 
            //auto updated by callling soap endpint by passing country name
            String name = countryService.convertToSentenceCase(newCountryInfo.getName());
 

            CountryInfo countryInfo = countryService.getCountyIsoByCountryName2(name,id);

            //convert to DTO
            CountryDTO responseCountryDTO = CountryConverter.convertToDTO(countryInfo);

            return new ResponseEntity<>(responseCountryDTO, HttpStatus.OK);


        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-countryinfo/{id}")
    public ResponseEntity<?> deleteCountryInfo(@PathVariable Long id) {
        try{
            Optional<CountryInfo> existingCountryInfo = countryService.findById(id);
            if (existingCountryInfo.isPresent()) {
                countryService.deleteCountryInfo(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error while deleting country with ID " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
