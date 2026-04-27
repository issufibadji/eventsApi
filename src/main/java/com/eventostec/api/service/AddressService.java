package com.eventostec.api.service;

import com.eventostec.api.domain.address.Address;
import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventRequestDTO;
import com.eventostec.api.domain.event.EventUpdateRequestDTO;
import com.eventostec.api.repositories.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public void createAddress(EventRequestDTO data, Event event) {
        Address address = new Address();
        address.setCity(data.city());
        address.setUf(data.state());
        address.setEvent(event);
        addressRepository.save(address);
    }

    public void updateAddress(EventUpdateRequestDTO data, Event event) {
        Address address = addressRepository.findByEventId(event.getId())
                .orElse(new Address());
        if (data.city() != null) address.setCity(data.city());
        if (data.state() != null) address.setUf(data.state());
        address.setEvent(event);
        addressRepository.save(address);
    }

    public void deleteByEvent(Event event) {
        addressRepository.findByEventId(event.getId())
                .ifPresent(addressRepository::delete);
    }

    public Optional<Address> findByEventId(UUID eventId) {
        return addressRepository.findByEventId(eventId);
    }
}
