package com.api.countryinfo.serviceImpl;


import com.api.countryinfo.repository.CountryInformationRepository;
 
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;

import com.api.countryinfo.controller.CountryInformationController;
import com.api.countryinfo.exception.InvalidCountryNameException;
import com.api.countryinfo.model.CountryInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.api.countryinfo.service.CountryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import com.api.countryinfo.model.Language;


import org.w3c.dom.Document;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Service
public class CountryServiceImpl implements CountryService {

    private static final Logger logger = LoggerFactory.getLogger(CountryServiceImpl.class);


    private final CountryInformationRepository countryInfoRepository;

    public CountryServiceImpl(CountryInformationRepository countryInfoRepository) {
        this.countryInfoRepository = countryInfoRepository;
    }

    @Override
    public CountryInfo saveCountryInfo(CountryInfo countryInfo) {
        return null;
    }

    @Override
    public List<CountryInfo> getAllCountryInfo() {
        return countryInfoRepository.findAll();
    }

    @Override
    public CountryInfo getCountyIsoByCountryName2(String countryName, Long id) {
        RestTemplate restTemplate = new RestTemplate(createFactory());
        CountryInfo countryInfo= null;;
        String url = "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";
        String request = createSoapRequest(countryName);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/xml");

        HttpEntity<String> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        logger.info("Response: " + response.getBody());

        String responseBody = response.getBody();

        if (responseBody != null) {
            String isoCode = parseSoapResponse(responseBody);

            logger.info("Country ISO Code: " + isoCode);

             countryInfo = this.getFullCountyInfoByIsoCode(isoCode);


            // update languages with new language from countryInfo 

            Optional<CountryInfo> existingCountryInfo = countryInfoRepository.findById(id);

            if (existingCountryInfo.isPresent()) {
                CountryInfo existingCountryDetails = existingCountryInfo.get();
                
                // Clear existing languages
                existingCountryDetails.getLanguages().clear();

                for (Language language : countryInfo.getLanguages()) {
                    language.setCountryInfo(existingCountryDetails);
                    existingCountryDetails.getLanguages().clear();
                    existingCountryDetails.getLanguages().add(language);
                }
                existingCountryDetails.setIsoCode(countryInfo.getIsoCode());
                existingCountryDetails.setName(countryInfo.getName());
                existingCountryDetails.setCapitalCity(countryInfo.getCapitalCity());
                existingCountryDetails.setPhoneCode(countryInfo.getPhoneCode());
                existingCountryDetails.setContinentCode(countryInfo.getContinentCode());
                existingCountryDetails.setCurrencyISOCode(countryInfo.getCurrencyISOCode());
                existingCountryDetails.setCountryFlag(countryInfo.getCountryFlag());

                // Save the updated CountryInfo
                countryInfo = countryInfoRepository.save(existingCountryDetails);

            } 

            
            countryInfoRepository.save(countryInfo);
        }
        return countryInfo;
    }



    @Override
    public Boolean getCountyIsoByCountryName(String countryName) {
        RestTemplate restTemplate = new RestTemplate(createFactory());
        String url = "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";
        String request = createSoapRequest(countryName);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/xml");

        HttpEntity<String> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        
        logger.info("Response: " + response.getBody());


        String responseBody = response.getBody();

        if (responseBody != null) {
            String isoCode = parseSoapResponse(responseBody);

            logger.info("Country ISO Code: " + isoCode);

            CountryInfo countryInfo = this.getFullCountyInfoByIsoCode(isoCode);
 
            countryInfoRepository.save(countryInfo);
            return isoCode != null && isoCode.matches("^[A-Z]{2}$");
        }
        return false;
    }


    

    @Override
    public Optional<CountryInfo> findById(Long countryId) {
        return countryInfoRepository.findById(countryId);
    }

    @Override
    public CountryInfo updateCountryInfo(CountryInfo countryInfo) {
        return countryInfoRepository.save(countryInfo);
    }

    @Override
    public void deleteCountryInfo(Long countryId) {
        countryInfoRepository.deleteById(countryId);
    }

    public String createSoapRequest(String countryName) {

        logger.info("CountryName: " + countryName);

        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://www.oorsprong.org/websamples.countryinfo\">" +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <web:CountryISOCode>" +
                "         <web:sCountryName>"+ countryName +"</web:sCountryName>" +
                "      </web:CountryISOCode>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";
    }

    private static ClientHttpRequestFactory createFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return factory;
    }

    private String parseSoapResponse(String response) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(response)));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            xPath.setNamespaceContext(new NamespaceContext() {
                @Override
                public String getNamespaceURI(String prefix) {
                    if ("m".equals(prefix)) {
                        return "http://www.oorsprong.org/websamples.countryinfo";
                    }
                    return null;
                }

                @Override
                public String getPrefix(String namespaceURI) {
                    if ("http://www.oorsprong.org/websamples.countryinfo".equals(namespaceURI)) {
                        return "m";
                    }
                    return null;
                }

                @Override
                public Iterator<String> getPrefixes(String namespaceURI) {
                    return null;
                }
            });
            XPathExpression expression = xPath.compile("//m:CountryISOCodeResult");
            String result = (String) expression.evaluate(doc, XPathConstants.STRING);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CountryInfo getFullCountyInfoByIsoCode(String isoCode) {

        CountryInfo countryInfo = new CountryInfo();

        RestTemplate restTemplate = new RestTemplate(createFactory());
        String url = "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";
        String request = createSoapRequest2(isoCode);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/xml");

        HttpEntity<String> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        logger.info("Response: " + response.getBody());


        String responseBody = response.getBody();

        if (responseBody != null) {
            countryInfo = this.parseSoapResponse2(responseBody);
        }
        return countryInfo;

    }

    public String createSoapRequest2(String isoCode) {

        logger.info("isoCode: " + isoCode);

        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://www.oorsprong.org/websamples.countryinfo\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <web:FullCountryInfo>\n" +
                "         <web:sCountryISOCode>"+isoCode+"</web:sCountryISOCode>\n" +
                "      </web:FullCountryInfo>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }


    private CountryInfo parseSoapResponse2(String response) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(response)));
    
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            xPath.setNamespaceContext(new NamespaceContext() {
                @Override
                public String getNamespaceURI(String prefix) {
                    if ("m".equals(prefix)) {
                        return "http://www.oorsprong.org/websamples.countryinfo";
                    }
                    return null;
                }
    
                @Override
                public String getPrefix(String namespaceURI) {
                    if ("http://www.oorsprong.org/websamples.countryinfo".equals(namespaceURI)) {
                        return "m";
                    }
                    return null;
                }
    
                @Override
                public Iterator<String> getPrefixes(String namespaceURI) {
                    return null;
                }
            });
    
            CountryInfo countryInfo = new CountryInfo();
            countryInfo.setIsoCode(xPath.evaluate("//m:FullCountryInfoResult/m:sISOCode", doc));
            countryInfo.setName(xPath.evaluate("//m:FullCountryInfoResult/m:sName", doc));
            countryInfo.setCapitalCity(xPath.evaluate("//m:FullCountryInfoResult/m:sCapitalCity", doc));
            countryInfo.setPhoneCode(xPath.evaluate("//m:FullCountryInfoResult/m:sPhoneCode", doc));
            countryInfo.setContinentCode(xPath.evaluate("//m:FullCountryInfoResult/m:sContinentCode", doc));
            countryInfo.setCurrencyISOCode(xPath.evaluate("//m:FullCountryInfoResult/m:sCurrencyISOCode", doc));
            countryInfo.setCountryFlag(xPath.evaluate("//m:FullCountryInfoResult/m:sCountryFlag", doc));
    
            NodeList languageNodes = (NodeList) xPath.evaluate("//m:FullCountryInfoResult/m:Languages/m:tLanguage", doc, XPathConstants.NODESET);
            List<Language> languages = new ArrayList<>();
            for (int i = 0; i < languageNodes.getLength(); i++) {
                Language language = new Language();
                language.setIsoCode(xPath.evaluate("m:sISOCode", languageNodes.item(i)));
                language.setName(xPath.evaluate("m:sName", languageNodes.item(i)));
                language.setCountryInfo(countryInfo); // Set the countryInfo field
                languages.add(language);
            }
    
            countryInfo.setLanguages(languages);

            logger.info("The country info >>" + countryInfo.toString());

    
    
            return countryInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

    @Override
    public String convertToSentenceCase(String name) {
        if (name == null || name.isEmpty()) {
            throw new InvalidCountryNameException("Country name cannot be null or empty");
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
     }
}
