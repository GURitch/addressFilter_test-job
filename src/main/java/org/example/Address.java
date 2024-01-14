package org.example;

import lombok.Data;

import java.util.Date;
@Data
public class Address {
    private String objectId;
    private String name;
    private String typeName;
    private Date startDate;
    private Date endDate;
    private boolean isActual;
    private boolean isActive;
}
