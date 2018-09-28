package com.example.demo;

import javax.persistence.*;

@Entity
public class Osdm {

    @Id
    private String country_version;
    private String xml;

    public String getCountry_version() {
        return country_version;
    }

    public void setCountry_version(String country_version) {
        this.country_version = country_version;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Osdm(){

    }
}
