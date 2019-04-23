public class AddressWithPort {
    private String address;
    private int port;

    public AddressWithPort(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String toString() {
        return "{Address: " + address + ", Port: " + port + "}";
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
