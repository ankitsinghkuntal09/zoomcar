package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Main class
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Creating users
        User u1 = new User(1, "Bhadruuuuu", 576896);
        User u2 = new User(2, "Bedhparkash", 576896);
        User u3 = new User(3, "Ankit", 576896);
        User u4 = new User(4, "Maya", 987654);

        // Creating ZoomCar instance
        ZoomCar zc = new ZoomCar();
        Store st = new Store("London");

        zc.addStore(st); // Add store to ZoomCar
        zc.addUser(u1);
        zc.addUser(u2);
        zc.addUser(u3);
        zc.addUser(u4);

        // Adding vehicles to the store
        Car car1 = new Car(5, "CAR123", "Toyota", "V6", 1000, u1);
        Car car2 = new Car(2, "CAR124", "Honda", "V4", 1500, u1);
        Car car3 = new Car(4, "CAR125", "Ford", "V8", 2000, u2);
        Bike bike1 = new Bike("BIKE123", "Yamaha", "Single Cylinder", 500, u2);
        Bike bike2 = new Bike("BIKE124", "Kawasaki", "Twin Cylinder", 600, u3);
        st.addVehicle(car1);
        st.addVehicle(car2);
        st.addVehicle(car3);
        st.addVehicle(bike1);
        st.addVehicle(bike2);

        // Example 1: User selects vehicle
        handleUserReservation(scanner, zc, st, u1);

        // Example 2: Another user selects vehicle
        handleUserReservation(scanner, zc, st, u2);

        // Example 3: User selects bike
        handleUserReservation(scanner, zc, st, u3);

        // Example 4: User attempts to select a car with insufficient seats
        handleUserReservation(scanner, zc, st, u4);

        scanner.close();
    }

    private static void handleUserReservation(Scanner scanner, ZoomCar zc, Store st, User user) {
        System.out.println("\nUser: " + user.email);
        System.out.println("Select the type of vehicle you want to rent:");
        System.out.println("1. Car");
        System.out.println("2. Bike");
        int vehicleChoice = scanner.nextInt();
    
        Vehicle rentedVehicle = null;
    
        if (vehicleChoice == 1) {
            System.out.print("Enter the number of seats you need: ");
            int seatsNeeded = scanner.nextInt();
    
            // Show available vehicles based on seats
            List<Vehicle> availableCars = new ArrayList<>();
            for (Vehicle v : st.carInventoryObj.vehicleList) {
                if (v instanceof Car && ((Car) v).seats >= seatsNeeded && v.isAvailable) {
                    availableCars.add(v);
                }
            }
    
            // Display available cars with serial numbers
            System.out.println("Available Cars:");
            for (int i = 0; i < availableCars.size(); i++) {
                System.out.println((i + 1) + ". " + availableCars.get(i).model + " (" + availableCars.get(i).engine + ")");
            }
    
            if (availableCars.isEmpty()) {
                System.out.println("No cars available matching your criteria.");
                return;
            }
    
            // Select a car by serial number
            System.out.print("Select a car by number: ");
            int carChoice = scanner.nextInt();
            if (carChoice < 1 || carChoice > availableCars.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            rentedVehicle = availableCars.get(carChoice - 1);
    
            // Show available engine options for the selected car
            List<String> availableEngines = Car.getAvailableEnginesForSeats(seatsNeeded);
            System.out.println("Available engine options: " + availableEngines);
            System.out.print("Enter the type of engine you prefer (or press Enter to skip): ");
            scanner.nextLine(); // Consume newline
            String enginePreference = scanner.nextLine(); // Engine preference
    
            // Check for engine preference
            if (!enginePreference.isEmpty() && !availableEngines.contains(enginePreference)) {
                System.out.println("Selected engine is not available for this vehicle.");
                return;
            }
        } else if (vehicleChoice == 2) {
            rentedVehicle = st.bikeInventoryObj.getVehicle();
        }
    
        if (rentedVehicle != null) {
            System.out.print("Enter the start time for the reservation: ");
            int startTime = scanner.nextInt();
            Reservation reservation = zc.makeReservation(rentedVehicle, startTime, user);
            System.out.println("Reserved Vehicle: " + rentedVehicle.licence);
            System.out.print("Enter the end time for billing calculation: ");
            int endTime = scanner.nextInt();
            System.out.println("Total Bill: " + zc.payBill(reservation, endTime));
        } else {
            System.out.println("No vehicle available matching your criteria.");
        }
    }
    
}

// Enum for Vehicle Types
enum VehicleType {
    CAR,
    BIKE;
}

// Abstract class for Vehicle
class Vehicle {
    public Store parkingStore; // Store where the vehicle is parked
    public VehicleType type; // Type of the vehicle
    public User owner; // Owner of the vehicle
    public String licence; // Licence plate
    public String model; // Model of the vehicle
    public String engine; // Engine details
    public int kms; // Kilometers driven
    public boolean isAvailable; // Availability status

    Vehicle(String licence, String model, String engine, int kms, VehicleType type, User owner) {
        this.licence = licence;
        this.model = model;
        this.engine = engine;
        this.kms = kms;
        this.type = type;
        this.owner = owner;
        this.isAvailable = true; // Default to available
    }
}

// Class for Car, extending Vehicle
class Car extends Vehicle {
    public int seats; // Number of seats in the car

    Car(int seats, String licence, String model, String engine, int kms, User owner) {
        super(licence, model, engine, kms, VehicleType.CAR, owner);
        this.seats = seats;
    }

    // Static method to return available engine options for given seats
    public static List<String> getAvailableEnginesForSeats(int seatsNeeded) {
        if (seatsNeeded <= 2) {
            return List.of("V4", "V6");
        } else if (seatsNeeded <= 5) {
            return List.of("V6", "V8", "Hybrid");
        }
        return List.of("V8", "Hybrid", "Electric");
    }

    // Static method to return all available engine options
    public static List<String> getAvailableEngines() {
        return List.of("V4", "V6", "V8", "Hybrid", "Electric");
    }
}

// Class for Bike, extending Vehicle
class Bike extends Vehicle {
    Bike(String licence, String model, String engine, int kms, User owner) {
        super(licence, model, engine, kms, VehicleType.BIKE, owner);
    }
}

// Interface for Inventory
interface Inventory {
    List<Vehicle> vehicleList = new ArrayList<>(); // List of vehicles in inventory

    void addVehicle(Vehicle v);
    void rentVehicle(Vehicle v);
    void removeVehicle(Vehicle v);
    void unrentVehicle(Vehicle v);
    Vehicle getVehicle();
}

// Class for Bike Inventory
class BikeInventory implements Inventory {
    public void addVehicle(Vehicle v) {
        vehicleList.add(v);
    }

    public void rentVehicle(Vehicle v) {
        v.isAvailable = false;
    }

    public void removeVehicle(Vehicle v) {
        vehicleList.remove(v);
    }

    public void unrentVehicle(Vehicle v) {
        v.isAvailable = true;
    }

    public Vehicle getVehicle() {
        for (Vehicle v : vehicleList) {
            if (v.isAvailable) return v;
        }
        return null; // No available vehicle found
    }
}

// Class for Car Inventory
class CarInventory implements Inventory {
    public void addVehicle(Vehicle v) {
        vehicleList.add(v);
    }

    public void rentVehicle(Vehicle v) {
        v.isAvailable = false;
    }

    public void removeVehicle(Vehicle v) {
        vehicleList.remove(v);
    }

    public void unrentVehicle(Vehicle v) {
        v.isAvailable = true;
    }

    public Vehicle getVehicle() {
        for (Vehicle v : vehicleList) {
            if (v.isAvailable) return v;
        }
        return null; // No available vehicle found
    }
}

// Class for Store
class Store {
    String city;
    BikeInventory bikeInventoryObj;
    CarInventory carInventoryObj;

    Store(String city) {
        this.city = city;
        bikeInventoryObj = new BikeInventory();
        carInventoryObj = new CarInventory();
    }

    public void addVehicle(Vehicle v) {
        if (v.type == VehicleType.CAR) {
            carInventoryObj.addVehicle(v);
        } else {
            bikeInventoryObj.addVehicle(v);
        }
    }

    public void removeVehicle(Vehicle v) {
        if (v.type == VehicleType.CAR) {
            carInventoryObj.removeVehicle(v);
        } else {
            bikeInventoryObj.removeVehicle(v);
        }
    }

    public Vehicle getVehicle(VehicleType vt) {
        if (vt == VehicleType.CAR) {
            return carInventoryObj.getVehicle();
        } else {
            return bikeInventoryObj.getVehicle();
        }
    }
}

// Class for Reservation
class Reservation {
    Vehicle reservedVehicle;
    int startTime;
    int bill;
    int endTime;
    User renter;

    Reservation(Vehicle reservedVehicle, int startTime, User renter) {
        this.reservedVehicle = reservedVehicle;
        this.startTime = startTime;
        this.renter = renter;
    }

    public int makeBill(int edt) {
        this.endTime = edt;
        if (reservedVehicle.type == VehicleType.CAR) {
            return (edt - this.startTime) * 3800;
        } else {
            return (edt - this.startTime) * 1800;
        }
    }
}

// Class for User
class User {
    public int userId;
    public String email;
    public long mobile;

    User(int userId, String email, long mobile) {
        this.userId = userId;
        this.email = email;
        this.mobile = mobile;
    }
}

// Class for ZoomCar
class ZoomCar {
    public List<Store> storeList;
    public List<User> userList;
    public List<Reservation> reservationList;

    ZoomCar() {
        storeList = new ArrayList<>();
        userList = new ArrayList<>();
        reservationList = new ArrayList<>();
    }

    public void addUser(User u) {
        userList.add(u);
    }

    public void removeUser(User u) {
        userList.remove(u);
    }

    public void addStore(Store st) {
        storeList.add(st);
    }

    public Vehicle getVehicle(VehicleType vt, String location) {
        for (Store s : storeList) {
            if (s.city.equals(location)) {
                return s.getVehicle(vt);
            }
        }
        return null; // No vehicle found in the specified location
    }

    public Reservation makeReservation(Vehicle v, int startTime, User u) {
        Reservation rs = new Reservation(v, startTime, u);
        reservationList.add(rs);
        return rs; // Return the created reservation
    }

    public int payBill(Reservation rs, int edt) {
        return rs.makeBill(edt);
    }
}
