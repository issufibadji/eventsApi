package com.eventostec.api.service;

import com.eventostec.api.domain.event.*;
import com.eventostec.api.mappers.EventMapper;
import com.eventostec.api.repositories.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private AddressService addressService;

    @Mock
    private CouponService couponService;

    @Mock
    private EventRepository repository;

    @Mock
    private EventMapper mapper;

    @InjectMocks
    private EventService eventService;

    private final String adminKey = "test-admin-key";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventService = new EventService(storageService, addressService, couponService, repository);
        ReflectionTestUtils.setField(eventService, "adminKey", adminKey);
        ReflectionTestUtils.setField(eventService, "mapper", mapper);
    }

    @Test
    void test_shouldSaveEvent() {
        EventRequestDTO requestDTO = new EventRequestDTO("Evento Teste", "Descrição do evento", new Date().getTime(), "Cidade Teste", "UF", true, "https://evento.com", null);
        Event event = new Event();

        when(mapper.toEntity(any(EventRequestDTO.class), anyString())).thenReturn(event);
        when(repository.save(any(Event.class))).thenReturn(event);

        Event savedEvent = eventService.createEvent(requestDTO);

        assertNotNull(savedEvent);
        verify(repository, times(1)).save(any(Event.class));
    }

    @Test
    void test_shouldReturnListOfEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventAddressProjection> events = List.of(mock(EventAddressProjection.class));
        Page<EventAddressProjection> eventsPage = new PageImpl<>(events);

        when(repository.findUpcomingEvents(any(Date.class), eq(pageable))).thenReturn(eventsPage);

        List<EventResponseDTO> result = eventService.getUpcomingEvents(0, 10);

        assertFalse(result.isEmpty());
        verify(repository, times(1)).findUpcomingEvents(any(Date.class), eq(pageable));
    }

    @Test
    void test_shouldReturnEventDetails() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Teste de evento");
        event.setDescription("Descrição do evento");
        event.setDate(new Date());
        event.setEventUrl("https://evento.com");

        when(repository.findById(eventId)).thenReturn(Optional.of(event));
        when(addressService.findByEventId(eventId)).thenReturn(Optional.empty());
        when(couponService.consultCoupons(eventId, new Date())).thenReturn(Collections.emptyList());

        EventDetailsDTO result = eventService.getEventDetails(eventId);

        assertNotNull(result);
        assertEquals(eventId, result.id());
        verify(repository, times(1)).findById(eventId);
    }

    @Test
    void test_shouldDeleteEvent() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId);

        when(repository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.deleteEvent(eventId, adminKey);

        verify(repository, times(1)).delete(event);
    }

    @Test
    void test_shouldReturnMatchingEvents() {
        String title = "Evento Teste";
        List<EventAddressProjection> events = List.of(mock(EventAddressProjection.class));

        when(repository.findEventsByTitle(title)).thenReturn(events);

        List<EventResponseDTO> result = eventService.searchEvents(title);

        assertFalse(result.isEmpty());
        verify(repository, times(1)).findEventsByTitle(title);
    }

    @Test
    void test_shouldReturnFilteredEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventAddressProjection> events = List.of(mock(EventAddressProjection.class));
        Page<EventAddressProjection> eventsPage = new PageImpl<>(events);

        when(repository.findFilteredEvents(anyString(), anyString(), any(Date.class), any(Date.class), eq(pageable))).thenReturn(eventsPage);

        List<EventResponseDTO> result = eventService.getFilteredEvents(0, 10, "Cidade Teste", "UF", new Date(), new Date());

        assertFalse(result.isEmpty());
        verify(repository, times(1)).findFilteredEvents(anyString(), anyString(), any(Date.class), any(Date.class), eq(pageable));
    }

    @Test
    void test_shouldDelegateUploadToStorageService() {
        EventRequestDTO requestDTO = new EventRequestDTO("Evento Teste", "Descrição", new Date().getTime(), "Cidade", "UF", true, "https://evento.com", mock(org.springframework.web.multipart.MultipartFile.class));
        Event event = new Event();
        String fakeUrl = "https://storage.example.com/imagem.jpg";

        when(storageService.uploadFile(any())).thenReturn(fakeUrl);
        when(mapper.toEntity(any(EventRequestDTO.class), eq(fakeUrl))).thenReturn(event);
        when(repository.save(any(Event.class))).thenReturn(event);

        Event savedEvent = eventService.createEvent(requestDTO);

        assertNotNull(savedEvent);
        verify(storageService, times(1)).uploadFile(any());
        verify(mapper, times(1)).toEntity(any(), eq(fakeUrl));
    }
}
