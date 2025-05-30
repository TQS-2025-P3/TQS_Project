package tqs.project.dto;




public class CarDTO {
    private String brand;             // Marca do carro, ex: "Tesla"
    private String model;             // Modelo do carro, ex: "Model 3"
    private String plate;             // Matrícula, ex: "AA-00-XX"
    private double batteryCapacity;   // Capacidade da bateria em kWh
    private Long userId;              // ID do utilizador (associação)


    
    // Getters e Setters

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public double getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
