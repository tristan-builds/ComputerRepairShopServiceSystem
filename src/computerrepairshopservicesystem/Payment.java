
package computerrepairshopservicesystem;

import java.time.LocalDate;

public class Payment {

    public static final String PENDING = "PENDING";
    public static final String PAID = "PAID";

    private String paymentID;
    private RepairJob repairJob;
    private double amountPaid;
    private LocalDate paymentDate;
    private String paymentStatus;

    public Payment() {
    }

    public Payment(String paymentID, RepairJob repairJob,
                   double amountPaid, LocalDate paymentDate,
                   String paymentStatus) {

        this.paymentID = paymentID;
        this.repairJob = repairJob;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        setPaymentStatus(paymentStatus);
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public RepairJob getRepairJob() {
        return repairJob;
    }

    public void setRepairJob(RepairJob repairJob) {
        this.repairJob = repairJob;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        if (!PENDING.equals(paymentStatus)
                && !PAID.equals(paymentStatus)) {
            throw new IllegalArgumentException(
                "Invalid payment status: " + paymentStatus
            );
        }

        this.paymentStatus = paymentStatus;
    }
} 