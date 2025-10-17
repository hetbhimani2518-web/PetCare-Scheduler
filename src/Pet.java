import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Pet {
    private UUID petId;
    private String name;
    private String breed;
    private int age;
    private String ownerName;
    private String contactInfo;
    private LocalDate registrationDate;
    private List<Appointment> appointments;

    public Pet(String name, String breed, int age, String ownerName, String contactInfo) {
        this.petId = UUID.randomUUID();
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.ownerName = ownerName;
        this.contactInfo = contactInfo;
        this.registrationDate = LocalDate.now();
        this.appointments = new ArrayList<>();
    }

    public UUID getPetId() {
        return petId;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public int getAge() {
        return age;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public List<Appointment> getAppointments() {
        return new ArrayList<>(appointments);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    public void removeAppointment(Appointment appointment) {
        this.appointments.remove(appointment);
    }

    @Override
    public String toString() {
        return String.format("Pet{id=%s, name='%s', breed='%s', age=%d, owner='%s', " +
                           "contact='%s', registered=%s, appointments=%d}",
            petId, name, breed, age, ownerName, contactInfo, 
            registrationDate, appointments.size());
    }
}
