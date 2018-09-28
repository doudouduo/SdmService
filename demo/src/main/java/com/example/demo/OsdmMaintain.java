package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;

public class OsdmMaintain {
    @Autowired
    private OsdmRepository osdmRepository;

    public boolean OsdmSave(Osdm osdm){
        Osdm result=osdmRepository.save(osdm);
        if (result!=null)return true;
        else return false;
    }
}
