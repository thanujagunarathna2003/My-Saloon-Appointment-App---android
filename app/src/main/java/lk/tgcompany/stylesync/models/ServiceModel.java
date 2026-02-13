package lk.tgcompany.stylesync.models;

public class ServiceModel {
    private String serviceId;
    private String name;
    private double price;
    private String imageUrl;

    // Default constructor for Firebase
    public ServiceModel() {}

    public ServiceModel(String serviceId, String name, double price, String imageUrl) {
        this.serviceId = serviceId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getServiceId() { return serviceId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}