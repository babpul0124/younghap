package network;

public class ClientMain {
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
        client.stop();
    }
}
