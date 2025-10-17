import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private String appointmentType;
    private LocalDateTime dateTime;
    private String notes;

    public String getAppointmentType() {
        return appointmentType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = dateTime != null ? dateTime.format(formatter) : "Not scheduled";
        String noteText = notes != null && !notes.isEmpty() ? notes : "No notes";
        
        return String.format("Appointment{type='%s', datetime=%s, notes='%s'}", 
            appointmentType != null ? appointmentType : "Not specified",
            formattedDateTime,
            noteText);
    }
}
