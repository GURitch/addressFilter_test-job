package org.example;

import lombok.Data;

import java.util.Date;
@Data
public class AddressHierarchy {
    private String objectId;
    private String parentObjectId;
    private Date startDate;
    private Date endDate;
    private boolean isActive;

}
