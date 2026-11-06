
package computerrepairshopservicesystem;

public class Desktop extends Device {

    private String caseType;

    public Desktop() {
        super();
    }

    public Desktop(String deviceID, String brand, String model,
                   String caseType) {
        super(deviceID, brand, model);
        this.caseType = caseType;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }
}
