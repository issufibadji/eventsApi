package com.eventostec.api.domain.Address;

import com.eventostec.api.domain.event.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table (name = "Address")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Address {
    @Id
    @GeneratedValue

    private UUID Id;

    private String uf;

    private  String City;

    @ManyToOne
    @JoinColumn(name = "event_id)")
    private Event event;

}
