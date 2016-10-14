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
    private static int port; //server port
    private static Socket socket; //client socket
    private static String userName; //client userName
    private static Scanner cin = new Scanner(System.in); //scanner object for user input

    //communication objects
    private static Scanner chatInput;
    private static PrintWriter chatOutput;

    //getter
    public static Scanner getChatInput() {
        return chatInput;
    }

    //MAIN
    public static void initialize(ClientController client) {

        try {//print the welcome message
            System.out.println();
            System.out.printf("client running(%s)...\n\n", InetAddress.getLocalHost());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\nUnable to get the local host.");

            System.exit(1);
        }

        /*//connect to the server
        connectToServer();*/

        //initialize and start the ALIVE sender thread
        Thread alive = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try { //sends the ALIVE message every 60sec
                        Thread.sleep(60000);
                        chatOutput.println("ALVE i am " + userName + ".");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        alive.start();

        //initialize and start the listener thread
        MessageListener messageListener = new MessageListener(client);
        messageListener.start();

        //system message if you connect successfully :D
        System.out.println("Type in your message(\"QUIT\" to exit): \n");

        //communicate with the server
        sendMessages();

    }


    //method to connect to the server
    public static boolean connectToServer(String serverIP, int serverPort, String user, IntroController introController) {
        boolean connectedSuccessful = false;

        try {
            //get the server address
            /*System.out.print("Please enter the server address: ");
            String server = cin.nextLine();
            System.out.print("Please enter the port: ");
            port = Integer.parseInt(cin.nextLine());*/

            //initialize the server address
            serverAddress = InetAddress.getByName(serverIP);
            socket = new Socket(serverAddress, serverPort); //initialize the client socket

            //initialize the communication objects
            chatOutput = new PrintWriter(socket.getOutputStream(), true);
            chatInput = new Scanner(socket.getInputStream());

            //check for correct userName input
            boolean isUserNameOK;

            do { //try until getting valid input
                isUserNameOK = getUserName(user);

                if (isUserNameOK) { //userName meets the requirements

                    //send the JOIN message
                    chatOutput.printf("JOIN %s, %s:%s\n", userName, serverAddress, port);
                    String response = chatInput.nextLine(); //get the server response

                    //handle the server response
                    switch (response) {

                        case "J_OK": //server accepted the new client
                            System.out.println("You are now connected to the server.");
                            connectedSuccessful = true;
                            break;

                        case "J_ERR": //the username is already used
                            introController.showWarningAlert("Username taken!", "This username is already in use.\n" +
                                    "Try another username.");
                            System.out.println("This username is already in use.");
                            System.out.println("Try another username.\n");
                            isUserNameOK = false; //to loop again
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
                /*System.out.println("\nEstablishing connection...\n");
                Thread.sleep(1000);*/

                introController.showWarningAlert("Bad address!", "Server address not found.\n" +
                        "Please try again.");
                System.out.println("Server address not found!");
                System.out.println("Please try again.\n");
                return connectedSuccessful;

            } catch (Exception f) {
                f.printStackTrace();
                return connectedSuccessful;
            } /*finally {
                connectToServer(); //restart the connection process
            }*/
        }
    }


    public static void exit() {
        //print the closing messages
        System.out.println("You left the conversation. See ya!");
        System.out.println("Closing connection...");

        //send the quit message to the server
        chatOutput.println("QUIT i am " + userName + ".");

        /*System.exit(1);*/
    }

    //method to send the messages
    private static void sendMessages() {
        try {
            String message, dataMessage;

            do { //loop until user sends "quit" message
                message = cin.nextLine(); //store the message from the user input

                if (!message.equals("QUIT")) {
                    //create the DATA message
                    dataMessage = "DATA " + userName + ": " + message;
                    //send the message to the server
                    chatOutput.println(dataMessage);
                }

            } while (!message.equals("QUIT"));

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                //print the closing messages
                System.out.println("You left the conversation. See ya!");
                System.out.println("Closing connection...");

                //send the quit message to the server
                chatOutput.println("QUIT i am " + userName + ".");

                System.exit(1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //method to get the username
    private static boolean getUserName(String user) {
        try {
            /*System.out.print("Please enter your user name: ");
            userName = cin.nextLine(); //get username from input
            System.out.println();*/
            userName = user;

            if (userName.length() < 13 && userName.matches("^[a-zA-Z0-9_-]+$")) { //username matches the standard
                return true;

            } else { //wrong input
                System.out.println("Wrong input!\n" +
                        "Your username should be max 12 character long and should only contain " +
                        "chars, digits, '-' and '_'\n");
                return false;
            }

        } catch (Exception e) { //no input
            System.out.println("Wrong input!\n");
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

    public MessageListener(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //get the response from the server
                response = TCPClient.getChatInput().nextLine();

                //get the key to know how to handle the message
                keyScanner = new Scanner(response);
                key = keyScanner.next();

                //handling the message by the key
                switch (key) {
                    case "DATA":
                        //print the message
                        System.out.println(response.substring(response.indexOf(" ") + 1));
                        clientController.handleChatField(response.substring(response.indexOf(" ") + 1));
                        break;

                    case "LIST":
                        //print the message
                        System.out.println(response.substring(response.indexOf(" ") + 1));
                        clientController.handleChatField(response.substring(response.indexOf(" ") + 1));
                        break;

                    case "J_ERR":
                        System.out.println("\nThe server thinks you're shit..");
                        clientController.handleChatField("\nThe server thinks you're shit..");

                        //print the closing messages
                        System.out.println("You left the conversation. See ya!");
                        clientController.handleChatField("You left the conversation. See ya!");
                        System.out.println("Closing connection...");
                        clientController.handleChatField("Closing connection...");

                        System.exit(1);

                    default:
                        //in case of a server message (for AleXx)
                        System.out.print("\nServer system message: ");
                        clientController.handleChatField("\nServer system message: ");
                        System.out.println(response + "\n");
                        clientController.handleChatField(response + "\n");
                }

            } catch (Exception e) { // the server is not responding
                System.out.println("\nThe server is dead..\n");
                clientController.handleChatField("\nThe server is dead..\n");
                System.out.println("Closing connection...");
                clientController.handleChatField("Closing connection...");

                System.exit(1);
            }
        }
    }

}
