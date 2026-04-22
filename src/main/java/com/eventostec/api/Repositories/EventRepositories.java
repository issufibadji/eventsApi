package com.eventostec.api.Repositories;

import com.eventostec.api.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepositories extends JpaRepository <Event, UUID> {

    
}
