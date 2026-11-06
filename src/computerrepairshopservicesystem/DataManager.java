package computerrepairshopservicesystem;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DataManager {

    private static final int HASH_ITERATIONS = 600_000;
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    private final ArrayList<Customer> customers = new ArrayList<>();
    private final ArrayList<Device> devices = new ArrayList<>();
    private final ArrayList<Technician> technicians = new ArrayList<>();
    private final ArrayList<RepairJob> repairJobs = new ArrayList<>();
    private final ArrayList<Payment> payments = new ArrayList<>();
    private final ArrayList<SystemUser> systemUsers = new ArrayList<>();

    private final Queue<RepairJob> waitingQueue = new ArrayDeque<>();

    private int customerCounter = 1;
    private int deviceCounter = 1;
    private int technicianCounter = 1;
    private int repairJobCounter = 1001;
    private int paymentCounter = 1;
    private int userCounter = 1;

    private String currentAuthenticatedUserID;

    public DataManager() {
    initializeDefaultTechnicians();
} 

    private void initializeDefaultTechnicians() {
        if (!technicians.isEmpty()) {
            return;
        }

        technicians.add(new Technician(generateTechnicianID(), "Gill", true));
        technicians.add(new Technician(generateTechnicianID(), "Renato", true));
        technicians.add(new Technician(generateTechnicianID(), "Janella", true));
        technicians.add(new Technician(generateTechnicianID(), "Ronald", true));
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be empty.");
        }
    }

    private void requireMoney(double amount, String fieldName) {
        if (Double.isNaN(amount)
                || Double.isInfinite(amount)
                || amount < 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be finite and non-negative.");
        }
    }

    private void requirePositiveScreenSize(double screenSize) {
        if (Double.isNaN(screenSize)
                || Double.isInfinite(screenSize)
                || screenSize <= 0) {
            throw new IllegalArgumentException(
                    "Screen size must be a positive number.");
        }
    }

    private boolean isValidRole(String role) {
        return SystemUser.ADMIN.equals(role)
                || SystemUser.STAFF.equals(role);
    }

    private boolean isActiveJob(RepairJob job) {
        return RepairJob.DIAGNOSING.equals(job.getStatus())
                || RepairJob.AWAITING_APPROVAL.equals(job.getStatus())
                || RepairJob.REPAIRING.equals(job.getStatus());
    }

    // =========================================================
    // SEARCHING 
    // =========================================================

    private Customer findCustomer(String id) {
        for (Customer customer : customers) {
            if (customer.getCustomerID().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    private Device findDevice(String id) {
        for (Device device : devices) {
            if (device.getDeviceID().equals(id)) {
                return device;
            }
        }
        return null;
    }

    private Technician findTechnician(String id) {
        for (Technician technician : technicians) {
            if (technician.getTechnicianID().equals(id)) {
                return technician;
            }
        }
        return null;
    }

    private RepairJob findRepairJob(int id) {
        for (RepairJob job : repairJobs) {
            if (job.getRepairJobID() == id) {
                return job;
            }
        }
        return null;
    }

    private Payment findPayment(String id) {
        for (Payment payment : payments) {
            if (payment.getPaymentID().equals(id)) {
                return payment;
            }
        }
        return null;
    }

    private SystemUser findUserByID(String id) {
        if (id == null) {
            return null;
        }

        for (SystemUser user : systemUsers) {
            if (user.getUserID().equals(id)) {
                return user;
            }
        }
        return null;
    }

    private SystemUser findUserByUsername(String username) {
        if (username == null) {
            return null;
        }

        for (SystemUser user : systemUsers) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    // =========================================================
    // DEFENSIVE COPIES
    // =========================================================

    private Customer copyCustomer(Customer source) {
        if (source == null) {
            return null;
        }

        return new Customer(
                source.getCustomerID(),
                source.getName(),
                source.getContactNumber());
    }

    private Device copyDevice(Device source) {
        if (source == null) {
            return null;
        }

        if (source instanceof Desktop) {
            Desktop desktop = (Desktop) source;

            return new Desktop(
                    desktop.getDeviceID(),
                    desktop.getBrand(),
                    desktop.getModel(),
                    desktop.getCaseType());
        }

        if (source instanceof Laptop) {
            Laptop laptop = (Laptop) source;

            return new Laptop(
                    laptop.getDeviceID(),
                    laptop.getBrand(),
                    laptop.getModel(),
                    laptop.getScreenSize());
        }

        return null;
    }

    private Technician copyTechnician(Technician source) {
        if (source == null) {
            return null;
        }

        return new Technician(
                source.getTechnicianID(),
                source.getName(),
                source.isAvailable());
    }

    private RepairJob copyRepairJob(RepairJob source) {
        if (source == null) {
            return null;
        }

        return new RepairJob(
                source.getRepairJobID(),
                copyCustomer(source.getCustomer()),
                copyDevice(source.getDevice()),
                copyTechnician(source.getTechnician()),
                source.getReportedProblem(),
                source.getDiagnosis(),
                source.getRepairDescription(),
                source.getStatus(),
                source.getEstimatedCost(),
                source.getFinalCost());
    }

    private Payment copyPayment(Payment source) {
        if (source == null) {
            return null;
        }

        return new Payment(
                source.getPaymentID(),
                copyRepairJob(source.getRepairJob()),
                source.getAmountPaid(),
                source.getPaymentDate(),
                source.getPaymentStatus());
    }

    private SystemUser copyUserWithoutHash(SystemUser source) {
        if (source == null) {
            return null;
        }

        return new SystemUser(
                source.getUserID(),
                source.getUsername(),
                "",
                source.getRole(),
                source.getFailedLoginAttempts(),
                source.isBlocked());
    }

    // =========================================================
    // SESSION AND AUTHORIZATION
    // =========================================================

    private SystemUser requireActor(String actorUserID) {
        SystemUser user = findUserByID(actorUserID);

        if (user == null) {
            if (actorUserID != null
                    && actorUserID.equals(currentAuthenticatedUserID)) {
                currentAuthenticatedUserID = null;
            }

            throw new SecurityException("User account does not exist.");
        }

        if (currentAuthenticatedUserID == null
                || !currentAuthenticatedUserID.equals(actorUserID)) {
            throw new SecurityException(
                    "The supplied user is not the logged-in user.");
        }

        if (user.isBlocked()) {
            currentAuthenticatedUserID = null;
            throw new SecurityException("This account is blocked.");
        }

        if (!isValidRole(user.getRole())) {
            currentAuthenticatedUserID = null;
            throw new SecurityException("This account has an invalid role.");
        }

        return user;
    }

    private SystemUser requireAdmin(String actorUserID) {
        SystemUser user = requireActor(actorUserID);

        if (!SystemUser.ADMIN.equals(user.getRole())) {
            throw new SecurityException(
                    "Administrator permission is required.");
        }

        return user;
    }

    private SystemUser requireStaffOrAdmin(String actorUserID) {
        SystemUser user = requireActor(actorUserID);

        if (!SystemUser.STAFF.equals(user.getRole())
                && !SystemUser.ADMIN.equals(user.getRole())) {
            throw new SecurityException("Permission denied.");
        }

        return user;
    }

    /*
     * Used by public read methods that do not accept an actor ID.
     * The role is read from the stored authenticated account, not
     * from a value supplied by the GUI.
     */
    private SystemUser requireCurrentActor() {
        if (currentAuthenticatedUserID == null) {
            throw new SecurityException("Please log in first.");
        }

        return requireActor(currentAuthenticatedUserID);
    }

    private SystemUser requireCurrentAdmin() {
        SystemUser user = requireCurrentActor();

        if (!SystemUser.ADMIN.equals(user.getRole())) {
            throw new SecurityException(
                    "Administrator permission is required.");
        }

        return user;
    }

    private SystemUser requireSelfOrAdmin(
            String actorUserID, String targetUserID) {

        SystemUser actor = requireActor(actorUserID);

        if (actorUserID.equals(targetUserID)
                || SystemUser.ADMIN.equals(actor.getRole())) {
            return actor;
        }

        throw new SecurityException(
                "You may only modify your own account.");
    }

    public String getCurrentAuthenticatedUserID() {
        if (currentAuthenticatedUserID == null) {
            return null;
        }

        try {
            requireCurrentActor();
            return currentAuthenticatedUserID;
        } catch (SecurityException exception) {
            return null;
        }
    }

    public void logout() {
        currentAuthenticatedUserID = null;
    }

    /**
     * Counts one failed attempt per unsuccessful password verification.
     * A hashing-provider failure propagates as an internal error and
     * does not increment the failed-attempt counter.
     */
    public SystemUser authenticate(String username, String password) {
        // A new login attempt always ends any previous session first.
        currentAuthenticatedUserID = null;

        if (username == null || password == null) {
            return null;
        }

        SystemUser user = findUserByUsername(username);

        if (user == null || user.isBlocked()) {
            return null;
        }

        // Do not catch cryptographic failures here.
        // verifyPassword returns false for an incorrect password,
        // but internal hashing-provider failures propagate as errors.
        boolean passwordMatches =
                verifyPassword(password, user.getPasswordHash());

        if (!passwordMatches) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= 3) {
                user.setBlocked(true);
            }

            return null;
        }

        user.setFailedLoginAttempts(0);
        currentAuthenticatedUserID = user.getUserID();

        return copyUserWithoutHash(user);
    }

    // =========================================================
    // ID GENERATION
    // =========================================================

    private String generateCustomerID() {
        String id;

        do {
            id = String.format("C%03d", customerCounter++);
        } while (findCustomer(id) != null);

        return id;
    }

    private String generateDeviceID() {
        String id;

        do {
            id = String.format("D%03d", deviceCounter++);
        } while (findDevice(id) != null);

        return id;
    }

    private String generateTechnicianID() {
        String id;

        do {
            id = String.format("T%03d", technicianCounter++);
        } while (findTechnician(id) != null);

        return id;
    }

    private int generateRepairJobID() {
        int id;

        do {
            id = repairJobCounter++;
        } while (findRepairJob(id) != null);

        return id;
    }

    private String generatePaymentID() {
        String id;

        do {
            id = String.format("P%03d", paymentCounter++);
        } while (findPayment(id) != null);

        return id;
    }

    private String generateUserID() {
        String id;

        do {
            id = String.format("U%03d", userCounter++);
        } while (findUserByID(id) != null);

        return id;
    }

    // =========================================================
    // PROTECTED PUBLIC READ METHODS
    // =========================================================

    public List<Customer> getCustomers() {
        requireCurrentActor();

        ArrayList<Customer> result = new ArrayList<>();
        for (Customer item : customers) {
            result.add(copyCustomer(item));
        }

        return Collections.unmodifiableList(result);
    }

    public List<Device> getDevices() {
        requireCurrentActor();

        ArrayList<Device> result = new ArrayList<>();
        for (Device item : devices) {
            result.add(copyDevice(item));
        }

        return Collections.unmodifiableList(result);
    }

    public List<Technician> getTechnicians() {
        requireCurrentActor();

        ArrayList<Technician> result = new ArrayList<>();
        for (Technician item : technicians) {
            result.add(copyTechnician(item));
        }

        return Collections.unmodifiableList(result);
    }

    public List<Technician> getAvailableTechnicians() {
        requireCurrentActor();

        ArrayList<Technician> result = new ArrayList<>();
        for (Technician technician : technicians) {
            String technicianID = technician.getTechnicianID();
            if (technician.isAvailable()
                    && !technicianHasActiveJob(technicianID)
                    && !technicianHasQueuedJob(technicianID)) {
                result.add(copyTechnician(technician));
            }
        }

        return Collections.unmodifiableList(result);
    }

    public List<RepairJob> getRepairJobs() {
        requireCurrentActor();

        ArrayList<RepairJob> result = new ArrayList<>();
        for (RepairJob item : repairJobs) {
            result.add(copyRepairJob(item));
        }

        return Collections.unmodifiableList(result);
    }

    public List<Payment> getPayments() {
        requireCurrentActor();

        ArrayList<Payment> result = new ArrayList<>();
        for (Payment item : payments) {
            result.add(copyPayment(item));
        }

        return Collections.unmodifiableList(result);
    }

    public List<RepairJob> getWaitingQueue() {
        requireCurrentActor();

        ArrayList<RepairJob> result = new ArrayList<>();
        for (RepairJob item : waitingQueue) {
            result.add(copyRepairJob(item));
        }

        return Collections.unmodifiableList(result);
    }

    // Account listings and account lookups are Admin-only.

    public List<SystemUser> getSystemUsers() {
        requireCurrentAdmin();

        ArrayList<SystemUser> result = new ArrayList<>();
        for (SystemUser item : systemUsers) {
            result.add(copyUserWithoutHash(item));
        }

        return Collections.unmodifiableList(result);
    }

    public Customer getCustomerByID(String id) {
        requireCurrentActor();
        return copyCustomer(findCustomer(id));
    }

    public Device getDeviceByID(String id) {
        requireCurrentActor();
        return copyDevice(findDevice(id));
    }

    public Technician getTechnicianByID(String id) {
        requireCurrentActor();
        return copyTechnician(findTechnician(id));
    }

    public RepairJob getRepairJobByID(int id) {
        requireCurrentActor();
        return copyRepairJob(findRepairJob(id));
    }

    public Payment getPaymentByID(String id) {
        requireCurrentActor();
        return copyPayment(findPayment(id));
    }

    public Payment getPaymentForRepairJob(int repairJobID) {
        requireCurrentActor();

        for (Payment payment : payments) {
            if (payment.getRepairJob() != null
                    && payment.getRepairJob().getRepairJobID()
                            == repairJobID) {
                return copyPayment(payment);
            }
        }

        return null;
    }

    public SystemUser getSystemUserByID(String id) {
        requireCurrentAdmin();
        return copyUserWithoutHash(findUserByID(id));
    }

    public SystemUser getSystemUserByUsername(String username) {
        requireCurrentAdmin();
        return copyUserWithoutHash(findUserByUsername(username));
    }

    // =========================================================
    // CUSTOMER MANAGEMENT
    // =========================================================

    public Customer addCustomer(
            String actorUserID, String name, String contactNumber) {

        requireStaffOrAdmin(actorUserID);
        requireText(name, "Customer name");
        requireText(contactNumber, "Contact number");

        Customer customer = new Customer(
                generateCustomerID(), name.trim(), contactNumber.trim());

        customers.add(customer);
        return copyCustomer(customer);
    }

    public boolean updateCustomer(
            String actorUserID, String customerID,
            String name, String contactNumber) {

        requireStaffOrAdmin(actorUserID);
        requireText(name, "Customer name");
        requireText(contactNumber, "Contact number");

        Customer customer = findCustomer(customerID);

        if (customer == null) {
            return false;
        }

        customer.setName(name.trim());
        customer.setContactNumber(contactNumber.trim());

        return true;
    }

    public boolean deleteCustomer(
            String actorUserID, String customerID) {

        requireStaffOrAdmin(actorUserID);

        Customer customer = findCustomer(customerID);

        if (customer == null) {
            return false;
        }

        for (RepairJob job : repairJobs) {
            if (job.getCustomer() != null
                    && customerID.equals(
                            job.getCustomer().getCustomerID())) {
                throw new IllegalStateException(
                        "Cannot delete a customer referenced by repair history.");
            }
        }

        return customers.remove(customer);
    }

    // =========================================================
    // DEVICE MANAGEMENT
    // =========================================================

    public Desktop addDesktop(
            String actorUserID, String brand,
            String model, String caseType) {

        requireStaffOrAdmin(actorUserID);
        requireText(brand, "Brand");
        requireText(model, "Model");
        requireText(caseType, "Case type");

        Desktop desktop = new Desktop(
                generateDeviceID(),
                brand.trim(),
                model.trim(),
                caseType.trim());

        devices.add(desktop);
        return (Desktop) copyDevice(desktop);
    }

    public Laptop addLaptop(
            String actorUserID, String brand,
            String model, double screenSize) {

        requireStaffOrAdmin(actorUserID);
        requireText(brand, "Brand");
        requireText(model, "Model");
        requirePositiveScreenSize(screenSize);

        Laptop laptop = new Laptop(
                generateDeviceID(),
                brand.trim(),
                model.trim(),
                screenSize);

        devices.add(laptop);
        return (Laptop) copyDevice(laptop);
    }

    public boolean updateDevice(
            String actorUserID, String deviceID,
            String brand, String model) {

        requireStaffOrAdmin(actorUserID);
        requireText(brand, "Brand");
        requireText(model, "Model");

        Device device = findDevice(deviceID);

        if (device == null) {
            return false;
        }

        device.setBrand(brand.trim());
        device.setModel(model.trim());

        return true;
    }

    public boolean updateDesktopCaseType(
            String actorUserID, String deviceID, String caseType) {

        requireStaffOrAdmin(actorUserID);
        requireText(caseType, "Case type");

        Device device = findDevice(deviceID);

        if (!(device instanceof Desktop)) {
            return false;
        }

        ((Desktop) device).setCaseType(caseType.trim());
        return true;
    }

    public boolean updateLaptopScreenSize(
            String actorUserID, String deviceID, double screenSize) {

        requireStaffOrAdmin(actorUserID);
        requirePositiveScreenSize(screenSize);

        Device device = findDevice(deviceID);

        if (!(device instanceof Laptop)) {
            return false;
        }

        ((Laptop) device).setScreenSize(screenSize);
        return true;
    }

    public boolean deleteDevice(
            String actorUserID, String deviceID) {

        requireStaffOrAdmin(actorUserID);

        Device device = findDevice(deviceID);

        if (device == null) {
            return false;
        }

        for (RepairJob job : repairJobs) {
            if (job.getDevice() != null
                    && deviceID.equals(job.getDevice().getDeviceID())) {
                throw new IllegalStateException(
                        "Cannot delete a device referenced by repair history.");
            }
        }

        return devices.remove(device);
    }

    // =========================================================
    // TECHNICIAN MANAGEMENT (ADMIN ONLY)
    // =========================================================

    public Technician addTechnician(
            String actorUserID, String name) {

        requireAdmin(actorUserID);
        requireText(name, "Technician name");

        Technician technician = new Technician(
                generateTechnicianID(), name.trim(), true);

        technicians.add(technician);
        return copyTechnician(technician);
    }

    public boolean updateTechnician(
            String actorUserID, String technicianID, String name) {

        requireAdmin(actorUserID);
        requireText(name, "Technician name");

        Technician technician = findTechnician(technicianID);

        if (technician == null) {
            return false;
        }

        technician.setName(name.trim());
        return true;
    }

    public boolean deleteTechnician(
            String actorUserID, String technicianID) {

        requireAdmin(actorUserID);

        Technician technician = findTechnician(technicianID);

        if (technician == null) {
            return false;
        }

        for (RepairJob job : repairJobs) {
            if (job.getTechnician() != null
                    && technicianID.equals(
                            job.getTechnician().getTechnicianID())) {
                throw new IllegalStateException(
                        "Cannot delete a technician referenced by repair history.");
            }
        }

        return technicians.remove(technician);
    }

    // =========================================================
    // REPAIR-JOB CREATION AND QUEUE
    // =========================================================

    public RepairJob createRepairJob(
            String actorUserID, String customerID,
            String deviceID, String reportedProblem) {

        requireStaffOrAdmin(actorUserID);
        requireText(reportedProblem, "Reported problem");

        Customer customer = findCustomer(customerID);
        Device device = findDevice(deviceID);

        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        if (device == null) {
            throw new IllegalArgumentException("Device not found.");
        }

        // A new job waits without reserving a technician.
        RepairJob job = new RepairJob(
                generateRepairJobID(), customer, device, null,
                reportedProblem.trim(), "", "", RepairJob.PENDING, 0.0, 0.0);

        repairJobs.add(job);
        waitingQueue.add(job);
        return copyRepairJob(job);
    }

    /**
     * Compatibility overload for older callers. Technician selection now
     * happens when the next FIFO job is started, not during job creation.
     */
    public RepairJob createRepairJob(
            String actorUserID, String customerID,
            String deviceID, String technicianID, String reportedProblem) {
        requireStaffOrAdmin(actorUserID);
        requireText(technicianID, "Technician");
        return createRepairJob(actorUserID, customerID, deviceID, reportedProblem);
    }

    private boolean technicianHasQueuedJob(String technicianID) {
        for (RepairJob job : waitingQueue) {
            if (job.getTechnician() != null
                    && technicianID.equals(job.getTechnician().getTechnicianID())
                    && RepairJob.PENDING.equals(job.getStatus())) {
                return true;
            }
        }
        return false;
    }

    private boolean technicianHasActiveJob(String technicianID) {
        for (RepairJob job : repairJobs) {
            if (isActiveJob(job)
                    && job.getTechnician() != null
                    && technicianID.equals(
                            job.getTechnician().getTechnicianID())) {
                return true;
            }
        }

        return false;
    }

    public RepairJob assignNextWaitingJob(String actorUserID) {
        requireStaffOrAdmin(actorUserID);

        RepairJob job = waitingQueue.peek();
        if (job == null) {
            return null;
        }
        if (!RepairJob.PENDING.equals(job.getStatus())) {
            throw new IllegalStateException(
                    "The next job in the queue is not pending.");
        }

        // FIFO is strict: do not skip the oldest job if no technician is free.
        Technician selected = null;
        for (Technician technician : technicians) {
            if (technician.isAvailable()
                    && !technicianHasActiveJob(technician.getTechnicianID())
                    && !technicianHasQueuedJob(technician.getTechnicianID())) {
                selected = technician;
                break;
            }
        }

        if (selected == null) {
            return null;
        }

        job.setTechnician(selected);
        job.setStatus(RepairJob.DIAGNOSING);
        selected.setAvailable(false);
        waitingQueue.remove();
        return copyRepairJob(job);
    }

    private void validateAssignedActiveJob(RepairJob job) {
        if (job == null || !repairJobs.contains(job)) {
            throw new IllegalArgumentException("Repair job not found.");
        }

        if (job.getTechnician() == null) {
            throw new IllegalStateException(
                    "The repair job has no assigned technician.");
        }

        String technicianID = job.getTechnician().getTechnicianID();
        Technician technician = findTechnician(technicianID);

        if (technician == null || technician.isAvailable()) {
            throw new IllegalStateException(
                    "The technician assignment is invalid.");
        }

        int activeJobs = 0;

        for (RepairJob other : repairJobs) {
            if (isActiveJob(other)
                    && other.getTechnician() != null
                    && technicianID.equals(
                            other.getTechnician().getTechnicianID())) {
                activeJobs++;
            }
        }

        if (activeJobs != 1) {
            throw new IllegalStateException(
                    "Technician assignment is inconsistent.");
        }
    }

    private void releaseTechnicianIfIdle(RepairJob job) {
        if (job == null || job.getTechnician() == null) {
            return;
        }

        String technicianID = job.getTechnician().getTechnicianID();
        Technician technician = findTechnician(technicianID);

        if (technician != null
                && !technicianHasActiveJob(technicianID)) {
            technician.setAvailable(true);
        }
    }

    // =========================================================
    // DIAGNOSIS, ESTIMATE, APPROVAL, AND REPAIR
    // =========================================================

    public boolean submitDiagnosisAndEstimate(
            String actorUserID, int repairJobID,
            String diagnosis, String repairDescription,
            double estimatedCost) {

        requireStaffOrAdmin(actorUserID);
        requireText(diagnosis, "Diagnosis");
        requireText(repairDescription, "Repair description");
        requireMoney(estimatedCost, "Estimated cost");

        RepairJob job = findRepairJob(repairJobID);

        if (job == null) {
            return false;
        }

        if (!RepairJob.DIAGNOSING.equals(job.getStatus())) {
            throw new IllegalStateException(
                    "Only jobs being diagnosed can receive an estimate.");
        }

        validateAssignedActiveJob(job);

        job.setDiagnosis(diagnosis.trim());
        job.setRepairDescription(repairDescription.trim());
        job.setEstimatedCost(estimatedCost);
        job.setStatus(RepairJob.AWAITING_APPROVAL);

        return true;
    }

    public boolean decideEstimate(
            String actorUserID, int repairJobID, boolean approved) {

        requireStaffOrAdmin(actorUserID);

        RepairJob job = findRepairJob(repairJobID);

        if (job == null) {
            return false;
        }

        if (!RepairJob.AWAITING_APPROVAL.equals(job.getStatus())) {
            throw new IllegalStateException(
                    "The job is not awaiting estimate approval.");
        }

        validateAssignedActiveJob(job);

        if (job.getDiagnosis() == null
                || job.getDiagnosis().trim().isEmpty()
                || job.getRepairDescription() == null
                || job.getRepairDescription().trim().isEmpty()) {
            throw new IllegalStateException(
                    "Diagnosis and repair description are required.");
        }

        requireMoney(job.getEstimatedCost(), "Estimated cost");

        if (approved) {
            job.setStatus(RepairJob.REPAIRING);
        } else {
            job.setStatus(RepairJob.REJECTED);
            releaseTechnicianIfIdle(job);
        }

        return true;
    }

    public boolean completeRepair(
            String actorUserID, int repairJobID, double finalCost) {

        requireStaffOrAdmin(actorUserID);
        requireMoney(finalCost, "Final cost");

        RepairJob job = findRepairJob(repairJobID);

        if (job == null) {
            return false;
        }

        if (!RepairJob.REPAIRING.equals(job.getStatus())) {
            throw new IllegalStateException(
                    "Only jobs currently being repaired can be completed.");
        }

        validateAssignedActiveJob(job);

        if (finalCost > job.getEstimatedCost()) {
            throw new IllegalArgumentException(
                    "Final cost cannot exceed the approved estimate.");
        }

        job.setFinalCost(finalCost);
        job.setStatus(RepairJob.COMPLETED);
        releaseTechnicianIfIdle(job);

        return true;
    }

    // =========================================================
    // REPAIR-JOB DELETION
    // =========================================================

    public boolean deleteRepairJob(
            String actorUserID, int repairJobID) {

        requireStaffOrAdmin(actorUserID);

        RepairJob job = findRepairJob(repairJobID);

        if (job == null) {
            return false;
        }

        boolean hasDiagnosis = job.getDiagnosis() != null
                && !job.getDiagnosis().trim().isEmpty();

        boolean hasDescription = job.getRepairDescription() != null
                && !job.getRepairDescription().trim().isEmpty();

        if (!RepairJob.PENDING.equals(job.getStatus())
                || job.getTechnician() != null
                || !waitingQueue.contains(job)
                || hasDiagnosis
                || hasDescription
                || job.getEstimatedCost() != 0.0
                || job.getFinalCost() != 0.0) {
            throw new IllegalStateException(
                    "Only untouched, unassigned pending jobs can be deleted.");
        }

        for (Payment payment : payments) {
            if (payment.getRepairJob() != null
                    && payment.getRepairJob().getRepairJobID()
                            == repairJobID) {
                throw new IllegalStateException(
                        "Cannot delete a repair job that has a payment.");
            }
        }

        waitingQueue.remove(job);
        repairJobs.remove(job);

        return true;
    }

    // =========================================================
    // PAYMENTS
    // =========================================================

    public Payment createPayment(
            String actorUserID, int repairJobID,
            double amountPaid, LocalDate paymentDate) {

        requireStaffOrAdmin(actorUserID);
        requireMoney(amountPaid, "Payment amount");

        if (paymentDate == null) {
            throw new IllegalArgumentException(
                    "Payment date cannot be null.");
        }

        RepairJob job = findRepairJob(repairJobID);

        if (job == null) {
            throw new IllegalArgumentException("Repair job not found.");
        }

        if (!RepairJob.COMPLETED.equals(job.getStatus())) {
            throw new IllegalStateException(
                    "Payment can only be recorded after repair completion.");
        }

        for (Payment payment : payments) {
            if (payment.getRepairJob() != null
                    && payment.getRepairJob().getRepairJobID()
                            == repairJobID) {
                throw new IllegalStateException(
                        "A payment already exists for this repair job.");
            }
        }

        if (Double.compare(amountPaid, job.getFinalCost()) != 0) {
            throw new IllegalArgumentException(
                    "Payment must equal the full final repair cost.");
        }

        Payment payment = new Payment(
                generatePaymentID(),
                job,
                amountPaid,
                paymentDate,
                Payment.PENDING);

        payments.add(payment);

        return copyPayment(payment);
    }

    public boolean markPaymentPaid(
            String actorUserID, String paymentID) {

        requireStaffOrAdmin(actorUserID);

        Payment payment = findPayment(paymentID);

        if (payment == null) {
            return false;
        }

        if (!Payment.PENDING.equals(payment.getPaymentStatus())) {
            throw new IllegalStateException(
                    "Only pending payments can be marked paid.");
        }

        RepairJob job = payment.getRepairJob();

        if (job == null || findRepairJob(job.getRepairJobID()) == null) {
            throw new IllegalStateException(
                    "The payment's repair job no longer exists.");
        }

        if (!RepairJob.COMPLETED.equals(job.getStatus())) {
            throw new IllegalStateException(
                    "The repair job must be completed before payment.");
        }

        if (Double.compare(
                payment.getAmountPaid(), job.getFinalCost()) != 0) {
            throw new IllegalStateException(
                    "Payment amount does not match the final cost.");
        }

        payment.setPaymentStatus(Payment.PAID);

        return true;
    }

    public boolean claimRepairJob(
            String actorUserID, int repairJobID) {

        requireStaffOrAdmin(actorUserID);

        RepairJob job = findRepairJob(repairJobID);

        if (job == null) {
            return false;
        }

        if (!RepairJob.COMPLETED.equals(job.getStatus())) {
            throw new IllegalStateException(
                    "Only completed repair jobs can be claimed.");
        }

        Payment matchingPayment = null;

        for (Payment payment : payments) {
            if (payment.getRepairJob() != null
                    && payment.getRepairJob().getRepairJobID()
                            == repairJobID) {
                matchingPayment = payment;
                break;
            }
        }

        if (matchingPayment == null
                || !Payment.PAID.equals(
                        matchingPayment.getPaymentStatus())
                || Double.compare(
                        matchingPayment.getAmountPaid(),
                        job.getFinalCost()) != 0) {
            throw new IllegalStateException(
                    "Full payment must be confirmed before handover.");
        }

        job.setStatus(RepairJob.CLAIMED);

        return true;
    }

    // =========================================================
    // PASSWORD HASHING
    // =========================================================

    private String hashPassword(String password) {
        requireText(password, "Password");

        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] derivedHash = derivePasswordHash(
                password.toCharArray(), salt, HASH_ITERATIONS);

        return "PBKDF2$"
                + HASH_ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(derivedHash);
    }

    private byte[] derivePasswordHash(
            char[] password, byte[] salt, int iterations) {

        PBEKeySpec spec = new PBEKeySpec(
                password, salt, iterations, HASH_LENGTH * 8);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();

        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                    "Password hashing is unavailable.", exception);
        } finally {
            spec.clearPassword();
        }
    }

    private boolean verifyPassword(
            String password, String storedHash) {

        if (password == null || storedHash == null) {
            return false;
        }

        String[] parts = storedHash.split("\\$");

        if (parts.length != 4 || !"PBKDF2".equals(parts[0])) {
            return false;
        }

        final int iterations;
        final byte[] salt;
        final byte[] expectedHash;

        try {
            iterations = Integer.parseInt(parts[1]);
            salt = Base64.getDecoder().decode(parts[2]);
            expectedHash = Base64.getDecoder().decode(parts[3]);
        } catch (IllegalArgumentException exception) {
            // Malformed stored hash, not a provider failure.
            return false;
        }

        if (iterations < 1 || iterations > 2_000_000
                || salt.length < 8
                || expectedHash.length != HASH_LENGTH) {
            return false;
        }

        // Provider failures from this call are intentionally not
        // converted to "wrong password" results.
        byte[] actualHash = derivePasswordHash(
                password.toCharArray(), salt, iterations);

        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    // =========================================================
    // INITIAL DEMONSTRATION ACCOUNTS
    // =========================================================

    /**
     * Run once after creating a fresh DataManager.
     * The two initial accounts are created directly because no
     * authenticated Admin session exists during initial setup.
     */
    public boolean initializeDefaultAccounts() {
        if (!systemUsers.isEmpty()) {
            return false;
        }

        try {
            SystemUser admin = new SystemUser(
                    generateUserID(),
                    "Admin",
                    hashPassword("Admin2424"),
                    SystemUser.ADMIN,
                    0,
                    false);

            SystemUser staff = new SystemUser(
                    generateUserID(),
                    "Staff",
                    hashPassword("Staff2424"),
                    SystemUser.STAFF,
                    0,
                    false);

            systemUsers.add(admin);
            systemUsers.add(staff);

            return true;

        } catch (RuntimeException exception) {
            systemUsers.clear();
            return false;
        }
    }

    // =========================================================
    // USER-ACCOUNT MANAGEMENT
    // =========================================================

    public SystemUser createStaffAccount(
            String adminUserID, String username, String password) {

        requireAdmin(adminUserID);
        requireText(username, "Username");
        requireText(password, "Password");

        if (findUserByUsername(username.trim()) != null) {
            throw new IllegalArgumentException(
                    "That username is already in use.");
        }

        SystemUser user = new SystemUser(
                generateUserID(),
                username.trim(),
                hashPassword(password),
                SystemUser.STAFF,
                0,
                false);

        systemUsers.add(user);

        return copyUserWithoutHash(user);
    }

    public boolean updateUsername(
            String actorUserID, String targetUserID,
            String newUsername) {

        requireSelfOrAdmin(actorUserID, targetUserID);
        requireText(newUsername, "Username");

        SystemUser target = findUserByID(targetUserID);

        if (target == null) {
            return false;
        }

        SystemUser duplicate = findUserByUsername(newUsername.trim());

        if (duplicate != null
                && !duplicate.getUserID().equals(targetUserID)) {
            throw new IllegalArgumentException(
                    "That username is already in use.");
        }

        target.setUsername(newUsername.trim());

        return true;
    }

    public boolean deleteSystemUser(
            String adminUserID, String targetUserID) {

        requireAdmin(adminUserID);

        SystemUser target = findUserByID(targetUserID);

        if (target == null) {
            return false;
        }

        if (targetUserID.equals(currentAuthenticatedUserID)) {
            throw new IllegalStateException(
                    "Log out before deleting your own account.");
        }

        if (SystemUser.ADMIN.equals(target.getRole())
                && !target.isBlocked()) {

            int unblockedAdmins = 0;

            for (SystemUser user : systemUsers) {
                if (SystemUser.ADMIN.equals(user.getRole())
                        && !user.isBlocked()) {
                    unblockedAdmins++;
                }
            }

            if (unblockedAdmins <= 1) {
                throw new IllegalStateException(
                        "Cannot delete the last unblocked administrator.");
            }
        }

        return systemUsers.remove(target);
    }

    public boolean unblockUser(
            String adminUserID, String targetUserID) {

        requireAdmin(adminUserID);

        SystemUser target = findUserByID(targetUserID);

        if (target == null) {
            return false;
        }

        target.setFailedLoginAttempts(0);
        target.setBlocked(false);

        return true;
    }

    /**
     * Successful password change ends the current session.
     * The user must authenticate again using the new password.
     */
    public boolean changePassword(
            String actorUserID,
            String currentPassword,
            String newPassword) {

        SystemUser user = requireActor(actorUserID);

        requireText(currentPassword, "Current password");
        requireText(newPassword, "New password");

        if (!verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new SecurityException(
                    "Current password is incorrect.");
        }

        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException(
                    "The new password must differ from the current password.");
        }

        String newHash = hashPassword(newPassword);
        user.setPasswordHash(newHash);

        // The password was changed successfully. End the session.
        currentAuthenticatedUserID = null;

        return true;
    }
}
