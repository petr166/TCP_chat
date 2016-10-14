import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Petru on 12-Sep-16.
 */
public class TCPClient {
    //fields
    private static InetAddress serverAddress; //object to store the server InetAddress
    private static Socket socket; //client socket
    private static String userName; //client userName
    private static MessageListener messageListener;

    //communication objects
    private static Scanner chatInput;
    private static PrintWriter chatOutput;

    //getter
    public static Scanner getChatInput() {
        return chatInput;
    }

    //MAIN
    public static void initialize(ClientController client) {
        //initialize and start the ALIVE sender thread
        Thread alive = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try { //sends the ALIVE message every 60sec
                        Thread.sleep(60000);
                        chatOutput.println("ALVE i am " + userName + ".");
                        System.out.println("-->ALVE message sent.");
                        System.out.println("------------------------------------------------->");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        alive.start();

        //initialize and start the listener thread
        messageListener = new MessageListener(client);
        messageListener.start();

    }


    //method to connect to the server
    public static boolean connectToServer(String serverIP, int serverPort, String user, IntroController introController) {
        boolean connectedSuccessful = false;

        try {//print the welcome message
            System.out.printf("client running(%s)...\n\n", InetAddress.getLocalHost());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-->unable to get the local host.");
            System.out.println("------------------------------------------------->");
        }

        try {
            //initialize the server address
            serverAddress = InetAddress.getByName(serverIP);
            socket = new Socket(serverAddress, serverPort); //initialize the client socket

            //initialize the communication objects
            chatOutput = new PrintWriter(socket.getOutputStream(), true);
            chatInput = new Scanner(socket.getInputStream());

            //check for correct userName input
            boolean isUserNameOK;

            do { //try until getting valid input
                isUserNameOK = checkUserName(user);

                if (isUserNameOK) { //userName meets the requirements

                    //send the JOIN message
                    chatOutput.printf("JOIN %s, %s:%s\n", userName, serverAddress, serverPort);
                    String response = chatInput.nextLine(); //get the server response

                    //handle the server response
                    switch (response) {

                        case "J_OK": //server accepted the new client
                            System.out.println("-->server connection success.");
                            System.out.println("------------------------------------------------->");
                            connectedSuccessful = true;
                            break;

                        case "J_ERR": //the username is already used
                            introController.showWarningAlert("Username taken!", "This username is already in use.\n" +
                                    "Try another username.");
                            System.out.println("-->the username is already in use.");
                            System.out.println("------------------------------------------------->");
                            break;
                    }
                    return connectedSuccessful;

                } else {
                    introController.showWarningAlert("Wrong input!", "Your username should be max 12 character long and should only contain " +
                            "chars, digits, '-' and '_'");
                    return connectedSuccessful;
                }

            } while (!isUserNameOK);

        } catch (Exception e) { //in case of bad server address
            try {
                introController.showWarningAlert("Bad address!", "Server address not found.\n" +
                        "Please try again.");
                System.out.println("-->server address not found.");
                System.out.println("------------------------------------------------->");
                return connectedSuccessful;

            } catch (Exception f) {
                System.out.println(f);
                System.out.println("------------------------------------------------->");
                return connectedSuccessful;
            }
        }
    }


    public static void exit() {
        //send the quit message to the server
        chatOutput.println("QUIT i am " + userName + ".");

        //print the closing messages
        System.out.println("-->client exit.");
        System.out.println("-->closing connection..");
        System.out.println("------------------------------------------------->");

        System.exit(1);
    }

    //method to get the username
    private static boolean checkUserName(String user) {
            userName = user;
            if (userName.length() < 13 && userName.matches("^[a-zA-Z0-9_-]+$")) { //username matches the standard
                return true;

            } else { //wrong input
                System.out.println("-->wrong username input.");
                System.out.println("------------------------------------------------->");
                return false;
            }
    }

    public static void sendButton(String message) {
        String dataMessage;

        //create the DATA message
        dataMessage = "DATA " + userName + ": " + message;
        //send the message to the server
        chatOutput.println(dataMessage);
    }
}


//defining the thread class to take care of the incoming messages
class MessageListener extends Thread {
    private String response, key;
    private Scanner keyScanner;
    private ClientController clientController;
    private volatile boolean running = true;

    public MessageListener(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void run() {
        while (running) {
            try {
                //get the response from the server
                response = TCPClient.getChatInput().nextLine();

                //get the key to know how to handle the message
                keyScanner = new Scanner(response);
                key = keyScanner.next();

                //handling the message by the key
                switch (key) {
                    case "DATA":
                        clientController.handleChatField(response.substring(response.indexOf(" ") + 1));
                        //print the message
                        System.out.println("-->data message received.");
                        System.out.println("--<<" + response.substring(response.indexOf(" ") + 1));
                        System.out.println("------------------------------------------------->");
                        break;

                    case "LIST":
                        clientController.handleChatField(response.substring(response.indexOf(" ") + 1));
                        //print the message
                        System.out.println("-->list message received.");
                        System.out.println("--<<" + response.substring(response.indexOf(" ") + 1));
                        System.out.println("------------------------------------------------->");
                        break;

                    case "J_ERR":
                        clientController.handleChatField("\nThe server thinks you're shit..");
                        System.out.println("-->J_ERR message received.");
                        System.out.println("-->closing connection..");
                        System.out.println("------------------------------------------------->");

                        stopRunning();

                    default:
                        //in case of a server message (for AleXx)
                        clientController.handleChatField("-->Server system message: " + response);
                        System.out.println("-->#server system message received.");
                        System.out.println("-->#" + response);
                        System.out.println("------------------------------------------------->");
                }

            } catch (Exception e) { // the server is not responding
                stopRunning();

                System.out.println("-->server connection lost.");
                System.out.println("------------------------------------------------->");
            }
        }
    }

    //method to stop the thread
    public void stopRunning() {
        running = false;
    }

}
