
package computerrepairshopservicesystem;

public class Customer {

    private String customerID;
    private String name;
    private String contactNumber;

    public Customer() {
    }

    public Customer(String customerID, String name, String contactNumber) {
        this.customerID = customerID;
        this.name = name;
        this.contactNumber = contactNumber;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
