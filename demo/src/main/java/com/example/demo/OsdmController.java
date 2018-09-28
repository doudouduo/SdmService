package com.example.demo;

import com.fasterxml.jackson.databind.util.JSONPObject;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
public class OsdmController {
    @Autowired
    private OsdmRepository osdmRepository;

    @Autowired
    private CsdmMergeAndValidateUtil csdmMergeAndValidateUtil;

    @PostMapping(value="/getOsdm")
    public Osdm GetOSDM(@RequestParam(value="country")String country,
                        @RequestParam(value="version")String version
    ){
        String country_version=country+"_"+version;

        return osdmRepository.findOne(country_version);
    }

    @PostMapping(value="/xml_parsing")
    public String xml_parsing(@RequestParam(value="Csdm")String csdm_xml,
                            @RequestParam(value="Sdm")String sdm_xml,
                            @RequestParam(value="country")String country,
                            @RequestParam(value="version")String version){
        String country_version = country + "_" + version;
        String osdm_xml="";
        try {
            Osdm osdm = osdmRepository.findOne(country_version);
            if (osdm != null) {
                String osdm_url = osdm.getXml();
                File f = new File(this.getClass().getResource(osdm_url).getPath());
                SAXReader reader = new SAXReader();
                osdm_xml = reader.read(f).asXML();
                osdm_xml=csdmMergeAndValidateUtil.MergeCdsmHomeAddressToOsdm(country,osdm_xml,csdm_xml);
                osdm_xml=csdmMergeAndValidateUtil.MergeCdsmJobInfoToOsdm(country,osdm_xml,csdm_xml);
                osdm_xml=csdmMergeAndValidateUtil.ValidateOsdmAndSdm(country,sdm_xml,osdm_xml);
            }
        }catch (Exception e){
            e.printStackTrace();

            System.out.println(e.getMessage());
        }
        return osdm_xml;
    }

    @PutMapping(value="/addOsdm")
    public Osdm AddOSDM(@RequestParam(value="Osdm")String xml,
                        @RequestParam(value="country")String country,
                        @RequestParam(value="version")String version){
        Osdm osdm=new Osdm();
        osdm.setCountry_version(country+"_"+version);
        osdm.setXml(xml);
        return osdmRepository.save(osdm);
    }

}
