import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.*;
import java.nio.file.*;

public class PetCareScheduler {

    private static final String PETS_FILE = "pets.dat";
    private static final String APPOINTMENTS_FILE = "appointments.dat";
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Scanner scanner;

    public PetCareScheduler() {
        this.scanner = new Scanner(System.in);
        this.pets = new HashMap<>();
        this.appointments = new HashMap<>();
    }

    private Map<UUID, Pet> pets;
    private Map<LocalDateTime, List<Appointment>> appointments;

    public static void main(String[] args) {
        PetCareScheduler scheduler = new PetCareScheduler();
        scheduler.loadData();  // Load existing data if available
        
        boolean running = true;
        while (running) {
            try {
                scheduler.displayMenu();
                String choice = scheduler.scanner.nextLine();
                
                switch (choice) {
                    case "1":
                        scheduler.registerPet();
                        break;
                    case "2":
                        scheduler.scheduleAppointment();
                        break;
                    case "3":
                        scheduler.displayRecords();
                        break;
                    case "4":
                        scheduler.generateReports();
                        break;
                    case "5":
                        scheduler.saveData();
                        break;
                    case "6":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Please enter a valid number!");
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
        
        scheduler.saveData();  // Save data before exiting
        scheduler.scanner.close();
        System.out.println("Thank you for using Pet Care Scheduler!");
    }

    @SuppressWarnings("unchecked")
    public void loadData() {
        try {            
            if (Files.exists(Paths.get(PETS_FILE))) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PETS_FILE))) {
                    pets = (Map<UUID, Pet>) ois.readObject();
                }
            }

            // Load appointments
            if (Files.exists(Paths.get(APPOINTMENTS_FILE))) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(APPOINTMENTS_FILE))) {
                    appointments = (Map<LocalDateTime, List<Appointment>>) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private void displayMenu() {
        System.out.println("\n=== Pet Care Scheduler Menu ===");
        System.out.println("1. Register New Pet");
        System.out.println("2. Schedule Appointment");
        System.out.println("3. Display Records");
        System.out.println("4. Generate Reports");
        System.out.println("5. Save Data");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private void registerPet() {
        try {
            System.out.println("\n====>> Enter Pet Details <<====");
            System.out.print("Pet Name: ");
            String name = scanner.nextLine();
            
            System.out.print("Pet's Breed: ");
            String breed = scanner.nextLine();
            
            System.out.print("Pet's Age: ");
            int age = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Owner Name: ");
            String ownerName = scanner.nextLine();
            
            System.out.print("Contact Info: ");
            String contactInfo = scanner.nextLine();

            Pet pet = new Pet(name, breed, age, ownerName, contactInfo);
            addPet(pet);
            System.out.println("Pet registered successfully! Pet ID: " + pet.getPetId());
        } catch (NumberFormatException e) {
            System.err.println("Invalid age format. Please enter a number.");
        } catch (Exception e) {
            System.err.println("Error registering pet: " + e.getMessage());
        }
    }
    
    public void addPet(Pet pet) {
        pets.put(pet.getPetId(), pet);
    }
    
    private void scheduleAppointment() {
        try {
            System.out.println("\n====>> Schedule Appointment <<====");
            System.out.print("Enter Pet ID: ");
            UUID petId = UUID.fromString(scanner.nextLine());
            
            Pet pet = getPet(petId);
            if (pet == null) {
                System.out.println("Pet not found!");
                return;
            }

            System.out.print("Appointment Type (e.g., checkup, grooming , vet visits, vaccinations): ");
            String type = scanner.nextLine();

            System.out.print("Date and Time (yyyy-MM-dd HH:mm): ");
            String dateTimeStr = scanner.nextLine();
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);

            System.out.print("Notes: ");
            String notes = scanner.nextLine();

            Appointment appointment = new Appointment();
            appointment.setAppointmentType(type);
            appointment.setDateTime(dateTime);
            appointment.setNotes(notes);

            addAppointment(appointment, pet);
            System.out.println("Appointment scheduled successfully!");
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format. Please use yyyy-MM-dd HH:mm");
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Pet ID format.");
        } catch (Exception e) {
            System.err.println("Error scheduling appointment: " + e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void addAppointment(Appointment appointment, Pet pet) {
        // Add to the general appointments collection
        appointments.computeIfAbsent(appointment.getDateTime(), k -> new ArrayList<>())
                   .add(appointment);
        // Add to the pet's appointments
        pet.addAppointment(appointment);
    }

    public Pet getPet(UUID petId) {
        return pets.get(petId);
    }

    private void displayRecords() {
        System.out.println("\n=== Pet Records ===");
        if (pets.isEmpty()) {
            System.out.println("No pets registered.");
        } else {
            for (Pet pet : getAllPets()) {
                System.out.println("\n" + pet);
                List<Appointment> petAppointments = pet.getAppointments();
                if (!petAppointments.isEmpty()) {
                    System.out.println("Appointments:");
                    for (Appointment apt : petAppointments) {
                        System.out.println("  | " + apt);
                    }
                }
            }
        }
    }

    public List<Pet> getAllPets() {
        return new ArrayList<>(pets.values());
    }

    private void generateReports() {
        System.out.println("\n=== Reports Menu ===");
        System.out.println("1. Today's Appointments");
        System.out.println("2. Upcoming Appointments");
        System.out.println("3. Pet Statistics");
        System.out.print("Select report type: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    displayTodaysAppointments();
                    break;
                case 2:
                    displayUpcomingAppointments();
                    break;
                case 3:
                    displayPetStatistics();
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (NumberFormatException e) {
            System.err.println("Please enter a valid number.");
        }
    }

    private void displayTodaysAppointments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        System.out.println("\n=== Today's Appointments ===");
        boolean found = false;
        for (Map.Entry<LocalDateTime, List<Appointment>> entry : appointments.entrySet()) {
            if (entry.getKey().isAfter(startOfDay) && entry.getKey().isBefore(endOfDay)) {
                System.out.println("\nTime: " + entry.getKey().format(DATE_FORMATTER));
                for (Appointment apt : entry.getValue()) {
                    System.out.println("  - " + apt);
                }
                found = true;
            }
        }
        if (!found) {
            System.out.println("No appointments scheduled for today.");
        }
    }

    private void displayUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekLater = now.plusWeeks(1);

        System.out.println("\n=== Upcoming Appointments (Next 7 Days) ===");
        boolean found = false;
        for (Map.Entry<LocalDateTime, List<Appointment>> entry : appointments.entrySet()) {
            if (entry.getKey().isAfter(now) && entry.getKey().isBefore(oneWeekLater)) {
                System.out.println("\nDate/Time: " + entry.getKey().format(DATE_FORMATTER));
                for (Appointment apt : entry.getValue()) {
                    System.out.println("  - " + apt);
                }
                found = true;
            }
        }
        if (!found) {
            System.out.println("No upcoming appointments in the next 7 days.");
        }
    }
    
    private void displayPetStatistics() {
        System.out.println("\n=== Pet Statistics ===");
        System.out.println("Total number of pets: " + pets.size());
        
        // Breed statistics
        Map<String, Integer> breedCount = new HashMap<>();
        for (Pet pet : pets.values()) {
            breedCount.merge(pet.getBreed(), 1, Integer::sum);
        }
        
        System.out.println("\nPets by Breed:");
        breedCount.forEach((breed, count) -> 
            System.out.println(breed + ": " + count)
        );
    }

    public void saveData() {
        try {
            // Save pets
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PETS_FILE))) {
                oos.writeObject(pets);
            }
            
            // Save appointments
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(APPOINTMENTS_FILE))) {
                oos.writeObject(appointments);
            }
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    


    // public void removePet(UUID petId) {
    //     pets.remove(petId);
    // }
    
    // public void removeAppointment(Appointment appointment, Pet pet) {
    //     // Remove from the general appointments collection
    //     List<Appointment> timeSlotAppointments = appointments.get(appointment.getDateTime());
    //     if (timeSlotAppointments != null) {
    //         timeSlotAppointments.remove(appointment);
    //         if (timeSlotAppointments.isEmpty()) {
    //             appointments.remove(appointment.getDateTime());
    //         }
    //     }
    //     // Remove from the pet's appointments
    //     pet.removeAppointment(appointment);
    // }
    
    // public List<Appointment> getAppointmentsForDate(LocalDateTime dateTime) {
    //     return appointments.getOrDefault(dateTime, new ArrayList<>());
    // }
    
    // public List<Appointment> getAllAppointments() {
    //     List<Appointment> allAppointments = new ArrayList<>();
    //     appointments.values().forEach(allAppointments::addAll);
    //     return allAppointments;
    // }
}