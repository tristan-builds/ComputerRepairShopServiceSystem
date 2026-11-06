package computerrepairshopservicesystem;

public class RepairJob {

    public static final String PENDING = "PENDING";
    public static final String DIAGNOSING = "DIAGNOSING";
    public static final String AWAITING_APPROVAL = "AWAITING_APPROVAL";
    public static final String REPAIRING = "REPAIRING";
    public static final String COMPLETED = "COMPLETED";
    public static final String CLAIMED = "CLAIMED";
    public static final String REJECTED = "REJECTED";

    private int repairJobID;
    private Customer customer;
    private Device device;
    private Technician technician;
    private String reportedProblem;
    private String diagnosis;
    private String repairDescription;
    private String status = PENDING;
    private double estimatedCost;
    private double finalCost;

    public RepairJob() {
    }

    public RepairJob(int repairJobID, Customer customer, Device device,
                     Technician technician, String reportedProblem,
                     String diagnosis, String repairDescription,
                     String status, double estimatedCost, double finalCost) {

        this.repairJobID = repairJobID;
        this.customer = customer;
        this.device = device;
        this.technician = technician;
        this.reportedProblem = reportedProblem;
        this.diagnosis = diagnosis;
        this.repairDescription = repairDescription;
        setStatus(status);
        this.estimatedCost = estimatedCost;
        this.finalCost = finalCost;
    }

    public int getRepairJobID() {
        return repairJobID;
    }

    public void setRepairJobID(int repairJobID) {
        this.repairJobID = repairJobID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }

    public String getReportedProblem() {
        return reportedProblem;
    }

    public void setReportedProblem(String reportedProblem) {
        this.reportedProblem = reportedProblem;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getRepairDescription() {
        return repairDescription;
    }

    public void setRepairDescription(String repairDescription) {
        this.repairDescription = repairDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException(
                "Invalid repair status: " + status
            );
        }

        this.status = status;
    }

    private boolean isValidStatus(String status) {
        return PENDING.equals(status)
                || DIAGNOSING.equals(status)
                || AWAITING_APPROVAL.equals(status)
                || REPAIRING.equals(status)
                || COMPLETED.equals(status)
                || CLAIMED.equals(status)
                || REJECTED.equals(status);
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(double finalCost) {
        this.finalCost = finalCost;
    }
} 
