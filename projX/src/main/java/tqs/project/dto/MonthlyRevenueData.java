package tqs.project.dto;

public class MonthlyRevenueData {
    private String month;
    private double revenue;
    private int sessions;

    public MonthlyRevenueData() {}

    public MonthlyRevenueData(String month, double revenue, int sessions) {
        this.month = month;
        this.revenue = revenue;
        this.sessions = sessions;
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public double getRevenue() { return revenue; }
    public void setRevenue(double revenue) { this.revenue = revenue; }

    public int getSessions() { return sessions; }
    public void setSessions(int sessions) { this.sessions = sessions; }
}