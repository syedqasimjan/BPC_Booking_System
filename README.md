# Boost Physio Clinic Booking System

A Java-based application for managing physiotherapy appointments at Boost Physio Clinic in Toronto, Canada. The system supports booking, canceling, marking appointments as attended, generating reports, and creating UML diagrams.

## Running the Program

- **Main.java**: Loads availability time ranges from `timetable.txt`, generates a report, and creates a UML diagram.
    - Run with: `java -cp target/classes;path/to/plantuml.jar com.bpc.booking.Main`
- **MenuBasedMain.java**: Provides an interactive menu for managing appointments, including viewing the availability timetable, adding/removing patients, and changing appointments.
    - Run with: `java -cp target/classes;path/to/plantuml.jar com.bpc.booking.MenuBasedMain`

## Data Files

The system uses the following data files located in `src/main/resources/data/`:

- **patients.txt**: Contains patient data.
    - Example: `ID101,Emily Carter,123 Yonge St, Toronto, ON M5V 2T6,416-555-0101`
- **physiotherapists.txt**: Contains physiotherapist data.
    - Example: `ID001,Dr. Michael Harper,101 College St, Toronto, ON M5G 1L7,416-555-0201,Physiotherapy;Sports Medicine`
- **treatments.txt**: Contains treatment data, including duration (minutes) and cost.
    - Example: `ID001,Deep Tissue Massage,Physiotherapy,60,90.0`
- **timetable.txt**: Contains availability time ranges for each physiotherapist for each day over the 4-week term (April 1 to April 28, 2025).
    - Example: `ID001,2025-04-01,09:00,12:00`
- **appointments.txt**: Stores appointment data (generated at runtime), including all details of the appointment.
    - Example: `ID101,Emily Carter,123 Yonge St, Toronto, ON M5V 2T6,416-555-0101,ID001,Dr. Michael Harper,Deep Tissue Massage,Physiotherapy,60,90.0,2025-04-01 10:00,BOOKED,416-555-0201`

**Note:** To avoid booking conflicts when re-running the program, clear `appointments.txt` before starting.

## Features

- **Booking:** Schedule appointments with conflict checking (Monday-Friday, 9 AM-5 PM) based on physiotherapist availability defined in `timetable.txt`. Two methods:
    - By expertise: View available physiotherapists, treatments, and times, then book.
    - By physiotherapist name: View available treatments and times, then book.
- **Availability Timetable:** Load a 4-week availability timetable from `timetable.txt` (Main.java and MenuBasedMain.java).
- **View Timetable:** Display the 4-week availability timetable, scheduled appointments, and available time slots, grouped by physiotherapist and week (MenuBasedMain.java).
- **Patient Management:** Add or remove patients, with persistence to `patients.txt`.
- **Change Appointment:** Cancel an existing appointment and book a new one in one step.
- **Reporting:** Generate a detailed report with appointment summaries, patient details, and revenue, sorted by attended appointments.
- **Persistence:** Appointments and patients are saved to `appointments.txt` and `patients.txt` for persistence across runs.
- **Menu Interface:** Interactive menu for managing appointments (MenuBasedMain.java).
- **UML Diagram:** Generate a class diagram using PlantUML, saved as `class_diagram.png`.

## Sample Usage (MenuBasedMain.java)

1. Run the program: `java -cp target/classes;path/to/plantuml.jar com.bpc.booking.MenuBasedMain`
2. Choose option 7 to view the availability timetable and available time slots.
3. Choose option 1 to book a new appointment by expertise:
    - Expertise: `Physiotherapy`
    - Patient ID: `ID101` (Emily Carter)
    - Select an available option from the list (e.g., Dr. Michael Harper - Deep Tissue Massage at 2025-04-01 10:00).
4. Choose option 8 to add a new patient:
    - ID: `ID113`
    - Name: `John Doe`
    - Address: `123 Main St, Toronto, ON M1M 1M1`
    - Phone: `416-555-0113`
5. Choose option 5 to generate a report and view the appointment details.
6. Choose option 11 to exit.