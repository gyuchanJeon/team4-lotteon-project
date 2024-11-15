package com.lotte4.entity;

import com.fasterxml.jackson.core.ErrorReportConfiguration;
import com.lotte4.dto.AddressDTO;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.*;

@Builder(toBuilder = true)
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String zipCode;
    private String addr1;
    private String addr2;


}
