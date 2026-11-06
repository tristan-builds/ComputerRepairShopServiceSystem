
package computerrepairshopservicesystem;

public class Technician {

    private String technicianID;
    private String name;
    private boolean isAvailable;

    public Technician() {
    }

    public Technician(String technicianID, String name,
                      boolean isAvailable) {
        this.technicianID = technicianID;
        this.name = name;
        this.isAvailable = isAvailable;
    }

    public String getTechnicianID() {
        return technicianID;
    }

    public void setTechnicianID(String technicianID) {
        this.technicianID = technicianID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
