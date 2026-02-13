package lk.tgcompany.stylesync.models;

public class BookingModel {
    private String serviceName;
    private String date;
    private String time;
    private double price;
    private String status;
    private String transactionId;
    private String userId;

    // Default constructor for Firebase
    public BookingModel() {}

    public BookingModel(String serviceName, String date, String time, double price, String status, String transactionId, String userId) {
        this.serviceName = serviceName;
        this.date = date;
        this.time = time;
        this.price = price;
        this.status = status;
        this.transactionId = transactionId;
        this.userId = userId;
    }

    // Getters
    public String getServiceName() { return serviceName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public String getUserId() { return userId; }
}