package tqs.project.dto;

public class RushHourData {
    private String hour;
    private int reservations;

    public RushHourData() {}

    public RushHourData(String hour, int reservations) {
        this.hour = hour;
        this.reservations = reservations;
    }

    public String getHour() { return hour; }
    public void setHour(String hour) { this.hour = hour; }

    public int getReservations() { return reservations; }
    public void setReservations(int reservations) { this.reservations = reservations; }
}