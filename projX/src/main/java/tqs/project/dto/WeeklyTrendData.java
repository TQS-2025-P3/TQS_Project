package tqs.project.dto;

public class WeeklyTrendData {
    private String date;
    private int reservations;
    private int completed;

    public WeeklyTrendData() {}

    public WeeklyTrendData(String date, int reservations, int completed) {
        this.date = date;
        this.reservations = reservations;
        this.completed = completed;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getReservations() { return reservations; }
    public void setReservations(int reservations) { this.reservations = reservations; }

    public int getCompleted() { return completed; }
    public void setCompleted(int completed) { this.completed = completed; }
}