package com.eventostec.api.domain.coupon;


import com.eventostec.api.domain.event.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.UUID;

@Table(name ="coupon")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue

    private UUID id;

    private Integer discount;

    private String code;

    private Date valid;

    //Mapeamento de relacionamentro da tabela Envento com Cupon
    @ManyToOne
    @JoinColumn(name ="event_id")

    private Event event;

}
