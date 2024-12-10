package com.name.carregistry;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class propertytest {

    @Value("${property.propertytest}")
    public String Holi;

    @PostConstruct
    public void postConstruct(){
        System.out.println("The value of the property property.propertytest is -> " +Holi);
    }
}
