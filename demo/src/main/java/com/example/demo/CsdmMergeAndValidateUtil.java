package com.example.demo;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



import java.io.File;
import java.util.Iterator;
import java.util.List;

@Component
public class CsdmMergeAndValidateUtil {

    @Autowired
    OsdmRepository osdmRepository;

    public String MergeCsdmToSdm(String OsdmXml,String CsdmXml,String country,String label){
        Document CsdmDoc=null;
        Document OsdmDoc=null;
        try {
            OsdmDoc = DocumentHelper.parseText(OsdmXml);
            CsdmDoc = DocumentHelper.parseText(CsdmXml);

            String CsdmPattern = "//country[@id='" + country + "']/hris-element[@id='"+label+"']";
            Node CsdmNode = CsdmDoc.selectSingleNode(CsdmPattern);
            Element CsdmEle = (Element) CsdmNode;
            Iterator CsdmIter = CsdmEle.elementIterator();
            while (CsdmIter.hasNext()) {
                Element Csdm = (Element) CsdmIter.next();
                String id = Csdm.attributeValue("id");
                String OsdmPattern = "//country[@id='" + country + "']/hris-element[@id='"+label+"']/hris-field[@id='" + id + "']";
                Node OsdmNode = OsdmDoc.selectSingleNode(OsdmPattern);
                if (OsdmNode == null) {
                    //Add Csdm to Osdm
                    OsdmPattern = "//country[@id='" + country + "']/hris-element[@id='"+label+"']";
                    OsdmNode = OsdmDoc.selectSingleNode(OsdmPattern);
                    List<Element> list = ((Element) OsdmNode).elements();
                    list.add((Element) Csdm.clone());
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();

            System.out.println(e.getMessage());
        }
        return OsdmDoc.asXML();
    }

    public Document RemoveSdmFromOsdm(Document OsdmDoc,String country,String label,String id){
        String OsdmPattern = "//country[@id='" + country + "']/hris-element[@id='"+label+"']/hris-field[@id='" + id + "']";
        Node OsdmNode = OsdmDoc.selectSingleNode(OsdmPattern);
        if (OsdmNode != null) {
            //Remove duplicate part in Osdm
            Element parent=OsdmNode.getParent();
            parent.remove(OsdmNode);
        }
        return OsdmDoc;
    }

    public String MergeCdsmHomeAddressToOsdm(String country,String OsdmXml,String CsdmXml){
        return MergeCsdmToSdm(OsdmXml,CsdmXml,country,"homeAddress");
    }

    public String MergeCdsmJobInfoToOsdm(String country,String OsdmXml,String CsdmXml){
        return MergeCsdmToSdm(OsdmXml,CsdmXml,country,"jobInfo");
    }

    public String ValidateOsdmAndSdm(String country,String SdmXml,String OsdmXml){
        Document SdmDoc=null;
        Document OsdmDoc=null;
        try {
            SdmDoc = DocumentHelper.parseText(SdmXml);
            OsdmDoc = DocumentHelper.parseText(OsdmXml);

            String SdmPattern = "//succession-data-model";
            Node SdmNode = SdmDoc.selectSingleNode(SdmPattern);
            Element SdmEle = (Element) SdmNode;
            Iterator SdmIter = SdmEle.elementIterator();
            while (SdmIter.hasNext()) {
                Element Sdm = (Element) SdmIter.next();
                String id = Sdm.attributeValue("id");

                //ValidatejobInfo
                OsdmDoc=RemoveSdmFromOsdm(OsdmDoc,country,"jobInfo",id);

                //Validate HomeAddress
                OsdmDoc=RemoveSdmFromOsdm(OsdmDoc,country,"homeAddress",id);
            }
        }
        catch (Exception e){
            e.printStackTrace();

            System.out.println(e.getMessage());
        }
        return OsdmDoc.asXML();
    }
}
