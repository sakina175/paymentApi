package com.paymentapi.repositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymentapi.model.PendingEvent;

@Repository
public interface PendingEventRepositry extends JpaRepository<PendingEvent,Long>{
    boolean existsByEventId(String eventId); 
    PendingEvent findByEventId(String eventId);
}
