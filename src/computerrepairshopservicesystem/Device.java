
package computerrepairshopservicesystem;

public abstract class Device {

    private String deviceID;
    private String brand;
    private String model;

    protected Device() {
    }

    protected Device(String deviceID, String brand, String model) {
        this.deviceID = deviceID;
        this.brand = brand;
        this.model = model;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

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
}
