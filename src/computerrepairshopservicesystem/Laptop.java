
package computerrepairshopservicesystem;

public class Laptop extends Device {

    private double screenSize;

    public Laptop() {
        super();
    }

    public Laptop(String deviceID, String brand, String model,
                  double screenSize) {
        super(deviceID, brand, model);
        this.screenSize = screenSize;
    }

    public double getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(double screenSize) {
        this.screenSize = screenSize;
    }
}
