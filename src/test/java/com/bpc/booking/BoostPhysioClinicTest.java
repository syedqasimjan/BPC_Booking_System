package com.bpc.booking;

import com.bpc.booking.model.*;
import com.bpc.booking.service.BookingService;
import com.bpc.booking.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class BoostPhysioClinicTest {
    private Clinic clinic;
    private Patient patient1;
    private Patient patient2;
    private Physiotherapist physio1;
    private Physiotherapist physio2;
    private Treatment treatment1;
    private Treatment treatment2;
    private Availability availability1;
    private Availability availability2;
    private BookingService bookingService;
    private ReportService reportService;

    @BeforeEach
    public void setUp() {
        // Initialize the clinic
        clinic = new Clinic();

        // Clear any existing appointments to ensure a clean state
        clinic.setAppointments(new ArrayList<>());

        // Initialize patients
        patient1 = new Patient("ID101", "Emily Carter", "123 Yonge St, Toronto", "416-555-0101");
        patient2 = new Patient("ID102", "Lucas Bennett", "456 Bloor St W, Toronto", "416-555-0102");
        List<Patient> patients = new ArrayList<>();
        patients.add(patient1);
        patients.add(patient2);
        clinic.setPatients(patients);

        // Initialize physiotherapists
        physio1 = new Physiotherapist("ID001", "Dr. Michael Harper", "789 King St W, Toronto", "416-555-0201");
        physio1.addExpertise("Physiotherapy");
        physio1.addExpertise("Sports Medicine");
        physio2 = new Physiotherapist("ID002", "Dr. Sarah Mitchell", "321 Queen St E, Toronto", "416-555-0202");
        physio2.addExpertise("Rehabilitation");
        List<Physiotherapist> physios = new ArrayList<>();
        physios.add(physio1);
        physios.add(physio2);
        clinic.setPhysiotherapists(physios);

        // Initialize treatments
        treatment1 = new Treatment("Deep Tissue Massage", "Physiotherapy", 60, 90.0);
        treatment2 = new Treatment("Pool Therapy", "Rehabilitation", 60, 150.0);
        physio1.addTreatment(treatment1);
        physio2.addTreatment(treatment2);

        // Initialize availabilities
        availability1 = new Availability("ID001", LocalDate.of(2025, 4, 1), LocalTime.of(9, 0), LocalTime.of(12, 0));
        availability2 = new Availability("ID002", LocalDate.of(2025, 4, 1), LocalTime.of(9, 0), LocalTime.of(11, 0));
        List<Availability> availabilities = new ArrayList<>();
        availabilities.add(availability1);
        availabilities.add(availability2);
        clinic.setAvailabilities(availabilities);

        // Initialize services
        bookingService = new BookingService(clinic);
        reportService = new ReportService(clinic);
    }
    @Test
    public void testAppointmentCreation() {
        Appointment appointment = new Appointment(patient1, physio1, treatment1, "2025-04-01 10:00");
        assertEquals(patient1, appointment.getPatient());
        assertEquals(physio1, appointment.getPhysiotherapist());
        assertEquals(treatment1, appointment.getTreatment());
        assertEquals("2025-04-01 10:00", appointment.getDateTime());
        assertEquals(Status.BOOKED, appointment.getStatus());
    }

    @Test
    public void testChangeStatus() {
        Appointment appointment = new Appointment(patient1, physio1, treatment1, "2025-04-01 10:00");
        appointment.changeStatus(Status.ATTENDED);
        assertEquals(Status.ATTENDED, appointment.getStatus());
        appointment.changeStatus(Status.CANCELLED);
        assertEquals(Status.CANCELLED, appointment.getStatus());
    }

    @Test
    public void testEqualsAndHashCode() {
        Appointment appointment1 = new Appointment(patient1, physio1, treatment1, "2025-04-01 10:00");
        Appointment appointment2 = new Appointment(patient2, physio1, treatment1, "2025-04-01 10:00");
        Appointment appointment3 = new Appointment(patient1, physio2, treatment2, "2025-04-01 10:00");
        Appointment appointment4 = new Appointment(patient1, physio1, treatment1, "2025-04-01 11:00");

        // Same physiotherapist and dateTime
        assertEquals(appointment1, appointment2);
        assertEquals(appointment1.hashCode(), appointment2.hashCode());

        // Different physiotherapist
        assertNotEquals(appointment1, appointment3);
        assertNotEquals(appointment1.hashCode(), appointment3.hashCode());

        // Different dateTime
        assertNotEquals(appointment1, appointment4);
        assertNotEquals(appointment1.hashCode(), appointment4.hashCode());
    }

    @Test
    public void testToString() {
        Appointment appointment = new Appointment(patient1, physio1, treatment1, "2025-04-01 10:00");
        String expected = "ID101,Emily Carter,123 Yonge St, Toronto,416-555-0101,ID001,Dr. Michael Harper,Deep Tissue Massage,Physiotherapy,60,90.0,2025-04-01 10:00,BOOKED,416-555-0201";
        assertEquals(expected, appointment.toString());
    }

    @Test
    public void testAvailabilityCreation() {
        Availability availability = new Availability("ID001", LocalDate.of(2025, 4, 1), LocalTime.of(9, 0), LocalTime.of(12, 0));
        assertEquals("ID001", availability.getPhysioId());
        assertEquals(LocalDate.of(2025, 4, 1), availability.getDate());
        assertEquals(LocalTime.of(9, 0), availability.getStartTime());
        assertEquals(LocalTime.of(12, 0), availability.getEndTime());
    }

    @Test
    public void testPatientCreation() {
        Patient patient = new Patient("ID101", "Emily Carter", "123 Yonge St, Toronto", "416-555-0101");
        assertEquals("ID101", patient.getId());
        assertEquals("Emily Carter", patient.getName());
        assertEquals("123 Yonge St, Toronto", patient.getAddress());
        assertEquals("416-555-0101", patient.getPhoneNumber());
    }

    @Test
    public void testPatientEqualsAndHashCode() {
        Patient patient1 = new Patient("ID101", "Emily Carter", "123 Yonge St, Toronto", "416-555-0101");
        Patient patient2 = new Patient("ID101", "Different Name", "Different Address", "416-555-9999");
        Patient patient3 = new Patient("ID102", "Lucas Bennett", "456 Bloor St W, Toronto", "416-555-0102");

        // Same ID
        assertEquals(patient1, patient2);
        assertEquals(patient1.hashCode(), patient2.hashCode());

        // Different ID
        assertNotEquals(patient1, patient3);
        assertNotEquals(patient1.hashCode(), patient3.hashCode());
    }

    @Test
    public void testPhysiotherapistCreation() {
        Physiotherapist physio = new Physiotherapist("ID001", "Dr. Michael Harper", "789 King St W, Toronto", "416-555-0201");
        assertEquals("ID001", physio.getId());
        assertEquals("Dr. Michael Harper", physio.getName());
        assertEquals("789 King St W, Toronto", physio.getAddress());
        assertEquals("416-555-0201", physio.getPhoneNumber());
        assertTrue(physio.getExpertise().isEmpty());
        assertTrue(physio.getTreatments().isEmpty());
    }

    @Test
    public void testAddExpertiseAndTreatment() {
        Physiotherapist physio = new Physiotherapist("ID001", "Dr. Michael Harper", "789 King St W, Toronto", "416-555-0201");
        physio.addExpertise("Physiotherapy");
        physio.addTreatment(treatment1);

        assertEquals(1, physio.getExpertise().size());
        assertEquals("Physiotherapy", physio.getExpertise().get(0));
        assertEquals(1, physio.getTreatments().size());
        assertEquals(treatment1, physio.getTreatments().get(0));
    }

    @Test
    public void testPhysiotherapistEqualsAndHashCode() {
        Physiotherapist physio1 = new Physiotherapist("ID001", "Dr. Michael Harper", "789 King St W, Toronto", "416-555-0201");
        Physiotherapist physio2 = new Physiotherapist("ID001", "Different Name", "Different Address", "416-555-9999");
        Physiotherapist physio3 = new Physiotherapist("ID002", "Dr. Sarah Mitchell", "321 Queen St E, Toronto", "416-555-0202");

        // Same ID
        assertEquals(physio1, physio2);
        assertEquals(physio1.hashCode(), physio2.hashCode());

        // Different ID
        assertNotEquals(physio1, physio3);
        assertNotEquals(physio1.hashCode(), physio3.hashCode());
    }

    @Test
    public void testTreatmentCreation() {
        Treatment treatment = new Treatment("Deep Tissue Massage", "Physiotherapy", 60, 90.0);
        assertEquals("Deep Tissue Massage", treatment.getName());
        assertEquals("Physiotherapy", treatment.getExpertise());
        assertEquals(60, treatment.getDuration());
        assertEquals(90.0, treatment.getCost(), 0.01);
    }

    @Test
    public void testAddAndRemovePatient() {
        Patient patient3 = new Patient("ID103", "Sophia Nguyen", "789 Queen St E, Toronto", "416-555-0103");
        clinic.addPatient(patient3);
        assertEquals(3, clinic.getPatients().size());
        assertTrue(clinic.getPatients().contains(patient3));

        clinic.removePatient("ID103");
        assertEquals(2, clinic.getPatients().size());
        assertFalse(clinic.getPatients().contains(patient3));
    }

    @Test
    public void testBookAppointmentSuccess() {
        clinic.bookAppointment(patient1, physio1, treatment1, "2025-04-01 10:00");
        assertEquals(1, clinic.getAppointments().size());
        Appointment appointment = clinic.getAppointments().get(0);
        assertEquals(patient1, appointment.getPatient());
        assertEquals(physio1, appointment.getPhysiotherapist());
        assertEquals(treatment1, appointment.getTreatment());
        assertEquals("2025-04-01 10:00", appointment.getDateTime());
        assertEquals(Status.BOOKED, appointment.getStatus());
    }

    @Test
    public void testBookAppointmentPhysioNotAvailable() {
        // Try booking outside of availability (e.g., 13:00 when available only 9:00-12:00)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            clinic.bookAppointment(patient1, physio1, treatment1, "2025-04-01 13:00");
        });
        assertEquals("Physiotherapist is not available at the requested time.", exception.getMessage());
    }

    @Test
    public void testBookAppointmentOverlapping() {
        clinic.bookAppointment(patient1, physio1, treatment1, "2025-04-01 10:00");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            clinic.bookAppointment(patient2, physio1, treatment1, "2025-04-01 10:30");
        });
        assertEquals("Physiotherapist already has an appointment at the requested time.", exception.getMessage());
    }

    @Test
    public void testBookAppointmentWrongExpertise() {
        // Physio1 doesn't have expertise in Rehabilitation
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            clinic.bookAppointment(patient1, physio1, treatment2, "2025-04-01 10:00");
        });
        assertEquals("Physiotherapist does not have the required expertise: Rehabilitation", exception.getMessage());
    }

    @Test
    public void testCancelAppointment() {
        clinic.bookAppointment(patient1, physio1, treatment1, "2025-04-01 10:00");
        clinic.cancelAppointment("2025-04-01 10:00", physio1);
        Appointment appointment = clinic.getAppointments().get(0);
        assertEquals(Status.CANCELLED, appointment.getStatus());
    }

    @Test
    public void testCancelAppointmentNotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            clinic.cancelAppointment("2025-04-01 10:00", physio1);
        });
        assertEquals("Appointment not found.", exception.getMessage());
    }

    @Test
    public void testSearchByPhysioName() {
        Physiotherapist found = clinic.searchByPhysioName("Dr. Michael Harper");
        assertEquals(physio1, found);

        Physiotherapist notFound = clinic.searchByPhysioName("Dr. Nonexistent");
        assertNull(notFound);
    }

    @Test
    public void testSearchByExpertise() {
        List<Physiotherapist> found = clinic.searchByExpertise("Physiotherapy");
        assertEquals(1, found.size());
        assertEquals(physio1, found.get(0));

        List<Physiotherapist> notFound = clinic.searchByExpertise("Neurology");
        assertTrue(notFound.isEmpty());
    }

    @Test
    public void testBookByExpertiseSuccess() {
        bookingService.bookByExpertise("Physiotherapy", "ID101");
        assertEquals(1, clinic.getAppointments().size());
        Appointment appointment = clinic.getAppointments().get(0);
        assertEquals(patient1, appointment.getPatient());
        assertEquals(physio1, appointment.getPhysiotherapist());
        assertEquals("Physiotherapy", appointment.getTreatment().getExpertise());
        assertEquals("2025-04-01 09:00", appointment.getDateTime()); // First available slot
    }

    @Test
    public void testBookByExpertisePatientNotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookByExpertise("Physiotherapy", "ID999");
        });
        assertEquals("Patient not found: ID999", exception.getMessage());
    }

    @Test
    public void testBookByExpertiseNoPhysioWithExpertise() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookByExpertise("Neurology", "ID101");
        });
        assertEquals("No physiotherapist found with expertise: Neurology", exception.getMessage());
    }

    @Test
    public void testBookByPhysioNameSuccess() {
        bookingService.bookByPhysioName("Dr. Michael Harper", "ID101");
        assertEquals(1, clinic.getAppointments().size());
        Appointment appointment = clinic.getAppointments().get(0);
        assertEquals(patient1, appointment.getPatient());
        assertEquals(physio1, appointment.getPhysiotherapist());
        assertEquals("2025-04-01 09:00", appointment.getDateTime()); // First available slot
    }

    @Test
    public void testBookByPhysioNamePhysioNotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookByPhysioName("Dr. Nonexistent", "ID101");
        });
        assertEquals("Physiotherapist not found: Dr. Nonexistent", exception.getMessage());
    }

    @Test
    public void testGenerateReport() {
        // Book some appointments
        clinic.bookAppointment(patient1, physio1, treatment1, "2025-04-01 10:00");
        clinic.bookAppointment(patient2, physio2, treatment2, "2025-04-01 09:00");
        clinic.getAppointments().get(0).changeStatus(Status.ATTENDED);
        clinic.getAppointments().get(1).changeStatus(Status.CANCELLED);

        // Generate report (we can't easily test console output, so we'll check internal calculations)
        Map<Physiotherapist, List<Appointment>> appointmentsByPhysio = clinic.getAppointments().stream()
                .collect(Collectors.groupingBy(Appointment::getPhysiotherapist));

        // Check Dr. Michael Harper's stats
        List<Appointment> physio1Appointments = appointmentsByPhysio.get(physio1);
        assertEquals(1, physio1Appointments.size());
        assertEquals(Status.ATTENDED, physio1Appointments.get(0).getStatus());
        double physio1Revenue = physio1Appointments.stream()
                .filter(a -> a.getStatus() == Status.ATTENDED)
                .mapToDouble(a -> a.getTreatment().getCost())
                .sum();
        assertEquals(90.0, physio1Revenue, 0.01);

        // Check Dr. Sarah Mitchell's stats
        List<Appointment> physio2Appointments = appointmentsByPhysio.get(physio2);
        assertEquals(1, physio2Appointments.size());
        assertEquals(Status.CANCELLED, physio2Appointments.get(0).getStatus());
        double physio2Revenue = physio2Appointments.stream()
                .filter(a -> a.getStatus() == Status.ATTENDED)
                .mapToDouble(a -> a.getTreatment().getCost())
                .sum();
        assertEquals(0.0, physio2Revenue, 0.01);

        // Check overall stats
        long totalAttended = clinic.getAppointments().stream().filter(a -> a.getStatus() == Status.ATTENDED).count();
        long totalBooked = clinic.getAppointments().stream().filter(a -> a.getStatus() == Status.BOOKED).count();
        long totalCancelled = clinic.getAppointments().stream().filter(a -> a.getStatus() == Status.CANCELLED).count();
        double totalRevenue = clinic.getAppointments().stream()
                .filter(a -> a.getStatus() == Status.ATTENDED)
                .mapToDouble(a -> a.getTreatment().getCost())
                .sum();
        assertEquals(1, totalAttended);
        assertEquals(0, totalBooked);
        assertEquals(1, totalCancelled);
        assertEquals(90.0, totalRevenue, 0.01);
    }


}


