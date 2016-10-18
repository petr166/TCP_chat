import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Petru on 12-Sep-16.
 */
public class TCPServer {
    //fields
    private static final int PORT = 7777;
    private static ServerSocket serverSocket;
    private static ArrayList<ClientThread> activeClients = new ArrayList<ClientThread>();

    //get the activeClients list
    public static ArrayList<ClientThread> getActiveClients() {
        return activeClients;
    }


    //MAIN
    public static void main(String args[]) {
        openPort(PORT); //starts the server at the given port
        handleEntry(); //receives and sends messages from/to the client

    }


    //method to start the server on desired port
    static void openPort(int port) {
        try {
            //initialize the server socket
            serverSocket = new ServerSocket(port);
            System.out.println();
            System.out.println(InetAddress.getLocalHost());
            System.out.printf("server listening on port %s...\n\n", port); //everything fine

        } catch (Exception e) { //bad port
            System.out.println("unable to set up port!!");
            System.out.println("server closed.");

            System.exit(1);
        }
    }


    //method to handle the connecting users
    static void handleEntry() {
        ListHandler listHandler = new ListHandler();
        listHandler.start();

        try {
            while (true) {
                //wait for client...
                Socket clientSocket = serverSocket.accept();
                System.out.println("new client connected: " + clientSocket.getInetAddress());

                activeClients.add(new ClientThread(clientSocket));
                activeClients.get(activeClients.size() - 1).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("unable to connect to the client.");
        }
    }

}


//defining the thread created for each user
class ClientThread extends Thread {
    //fields
    private Socket clientSocket;
    private Scanner input;
    private PrintWriter output;
    private String userName = "";
    private Instant lastBeat;
    private volatile boolean running = true;


    //constructor passing the clientSocket
    public ClientThread(Socket socket) {
        //Set up reference to associated socket...
        clientSocket = socket;
        lastBeat = Instant.now();

        try { //initialize the i/o objects
            input = new Scanner(clientSocket.getInputStream());
            output = new PrintWriter(clientSocket.getOutputStream(), true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //RUN
    public void run() {
        String received; //will store the message from client

        while (running) {
            try {
                //Accept message from client on the socket's input stream...
                received = input.nextLine();
                System.out.printf("received from (%s): %s\n", clientSocket.getInetAddress(), received);

                //handles messages according to their meaning
                handleMessage(received);

                System.out.println("------------------------------------------------->");

            } catch (Exception e) {
                //if the connection is lost
                stopRunning();
            }
        }
    }


    //method to add/decline client for conversation
    private boolean checkUsername(String name) {
        //loop through user threads list
        for (ClientThread clientThread : TCPServer.getActiveClients()) {
            if (clientThread.getUserName().equalsIgnoreCase(name)) //the name exists
                return false;
        }

        return true; //the username is valid
    }


    //method to handle the messages according to their meaning
    private void handleMessage(String message) {
        Scanner messageScanner = new Scanner(message);
        String key = messageScanner.next();

        switch (key) { //behavior according to the key word

            case "JOIN": {
                //store the userName
                String name = message.substring(message.indexOf(" ") + 1, message.indexOf(","));

                if (checkUsername(name)) { //the username is valid
                    output.println("J_OK"); //send the J_OK to THIS user
                    this.setLastBeat(Instant.now());

                    String users = ""; //string to store active users names

                    //loop the activeClients list to get the userNames
                    for (ClientThread clientThread : TCPServer.getActiveClients()) {
                        if (!clientThread.getUserName().equals("")) {
                            users += clientThread.getUserName() + " ";
                        }
                    }

                    users += name; //add the new user

                    //loop again and send the list with active clients
                    for (ClientThread clientThread : TCPServer.getActiveClients()) {
                        clientThread.getOutput().printf("LIST %s\n", users);
                    }

                    //set the userName for this object(thread)
                    this.setUserName(name);

                    System.out.println(name + " has joined successfully.");

                } else {
                    output.println("J_ERR"); //send the J_ERR to the user, invalid name

                    System.out.println(name + " was rejected.");
                }
                break;
            }

            case "DATA": {
                if (message.length() > 250) {
                    output.println("The message is too long.");
                }

                else {
                    //loop the activeClients list and send the DATA message
                    for (ClientThread clientThread : TCPServer.getActiveClients()) {
                        if (!clientThread.getUserName().equals("")) {
                            clientThread.getOutput().println(message);
                        }
                    }
                    System.out.println("data sent to all active clients.");
                }
                break;
            }

            case "QUIT": {
                System.out.println(this.getUserName() + " left the conversation.");
                ListHandler.removeThread(this);
                break;
            }

            case "ALVE": {
                System.out.println(this.getUserName() + " is beating.");
                this.setLastBeat(Instant.now());
                break;
            }

            default: {
                System.out.println(this.getUserName() + " sent a weird message");
                output.println("J_ERR"); //send the fuck off message
                break;
            }
        }
    }


    //method to stop the thread
    public void stopRunning() {
        running = false;
    }


    //getters
    public Socket getClientSocket() {
        return clientSocket;
    }

    public PrintWriter getOutput() {
        return output;
    }

    public String getUserName() {
        return userName;
    }

    //setters
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Instant getLastBeat() {
        return lastBeat;
    }

    public void setLastBeat(Instant lastBeat) {
        this.lastBeat = lastBeat;
    }

}


//thread to handle the activeUsers list
class ListHandler extends Thread {

    //method to erase the user and all related
    public static void removeThread(ClientThread client) { //reason: left/dropped
        try {
            TCPServer.getActiveClients().remove(client);
            client.getClientSocket().close();
            client.stopRunning(); //stop the thread

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TCPServer.getActiveClients().isEmpty()) {
            String users = "";
            //loop the activeClients list to get the userNames
            for (ClientThread clientThread : TCPServer.getActiveClients()) {
                if (!clientThread.getUserName().equals("")) {
                    users += clientThread.getUserName() + " ";
                }
            }

            //send the LIST message
            for (ClientThread clientThread : TCPServer.getActiveClients()) {
                clientThread.getOutput().printf("LIST %s\n", users);
            }
        }

        client = null; //nulled object will be disposed by the garbage collector
    }

    //RUN
    public void run() {
        long duration;

        while (true) {
            if (!TCPServer.getActiveClients().isEmpty()) {
                try {
                    for (ClientThread clientThread : new ArrayList<ClientThread>(TCPServer.getActiveClients())) {
                        //store the time since his last beat
                        duration = Duration.between(clientThread.getLastBeat(), Instant.now()).getSeconds();

                        if (duration > 60) {  //more than the 1 minute timeout
                            System.out.println(clientThread.getClientSocket().getInetAddress() +
                                    "(" + clientThread.getUserName() + ") is no longer active, closing connection..");

                            System.out.println(clientThread.getUserName() + " dropped due to timeout.");
                            removeThread(clientThread);
                            System.out.println("------------------------------------------------->");
                        }
                    }
                    Thread.sleep(20000);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    Thread.sleep(30000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

