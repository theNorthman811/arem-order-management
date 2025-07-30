package com.arem.core.model;

import java.time.LocalDateTime;

public class Payment {

    private  long identifier;
    private long userIdentifier;
    private long factureIdentifier;
    private double amount;
    private String personWhoPayed;
    private LocalDateTime paymentDate;


}
