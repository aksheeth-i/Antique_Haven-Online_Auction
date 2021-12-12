
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


public class Server extends Observable {
    //Networking
    private static Server server;
    private static PrintWriter myWriter;

    //Data
    private static ArrayList<ClientHandler> clients;
    private static ArrayList<Item> items;
    private static HashMap<String, String> logins;
    private static ArrayList<String> auctionActivity;

    //Timer
    private static final int GLOBAL_TIMER_START_MINS = 2;
    private static final int GLOBAL_TIMER_START_SEC = 0;
    private static final boolean[] timerDone = new boolean[5];

    //Formatting
    private static final NumberFormat myFormat = NumberFormat.getInstance();
    private static final DecimalFormat dFormat = new DecimalFormat("00");



    //===========================================================================================================
    //Initialize all data fields and start looking for clients

    public static void main(String[] args) {
        try {
            myWriter = new PrintWriter(new FileWriter("AuctionLogs.txt",true),true);
            myWriter.append("\n\n" + "In a prior auction - ");
        } catch (IOException e) {
            System.out.println("An error occurred when writing to log file.");
        }

        server = new Server();
        items = new ArrayList<>();
        logins = new HashMap<>();
        clients = new ArrayList<>();
        for(boolean b: timerDone)
            b=false;
        auctionActivity = new ArrayList<>();
        server.populateItems();
        server.populateLogins();
        server.setUpNetworking();
    }


    //===========================================================================================================
    //Initializing items and accepted logins

    private void populateItems() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("inputItems.txt"));
            String nextLine = reader.readLine();
            while (nextLine != null) {
                parseItem(nextLine);
                nextLine = reader.readLine();
            }
        } catch (IOException s) {
            s.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void parseItem(String nextLine) {
        String[] split = nextLine.split("_");
        Item s = new Item(Integer.parseInt(split[0]),split[1], split[2], Integer.parseInt(split[3]),Integer.parseInt(split[4]),split[5],split[6],0);
        items.add(s);
    }

    private void populateLogins() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("logins.txt"));
            String nextLine = reader.readLine();
            while (nextLine != null) {
                parseLogins(nextLine);
                nextLine = reader.readLine();
            }
        } catch (IOException s) {
            s.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void parseLogins(String nextLine) {
        String[] split = nextLine.split(",");
        logins.put(split[0], split[1]);
    }

    //===========================================================================================================
    // Upon a client startup, create and start a thread to "handle" the new socket

    private void setUpNetworking() {
        int port = 5000;
        try {
            ServerSocket ss = new ServerSocket(port);
            while (true) {
                Socket clientSocket = ss.accept(); //returns a socket obj that corresponds to that particular connection that it has accepted
                ClientHandler x = new ClientHandler(clientSocket);
                addObserver(x);
                clients.add(x);
                Thread t = new Thread(x);
                t.start();

                System.out.println("got a connection");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //===========================================================================================================
    // Synchronized method to interpret all client messages upon logins, bids, etc.

    private synchronized void processRequest(String input, ClientHandler ch) {
        String[] split = input.split("_");
        if (split[0].equals("validateLogin")) {
            if (logins.containsKey(split[1]) && logins.get(split[1]).equals(split[2])) {
                ch.sendToClient("validLogin");
            } else {
                ch.sendToClient("invalidLogin");
            }
        }
        else if(split[0].equals("disconnect"))
        {
            if(split.length==2)
                auctionActivity.add(Character.toUpperCase(split[1].charAt(0)) + split[1].substring(1) + " has left the auction.");
            deleteObserver(ch);
            clients.remove(ch);
            setChanged();
            notifyObservers("left_" + split[1]);
        }
        else if(split[0].equals("setUp"))
        {
            try {

                ObjectOutputStream x = new ObjectOutputStream(ch.clientSocket.getOutputStream());
                x.writeUnshared(items);
                x.writeUnshared(auctionActivity);
                auctionActivity.add(Character.toUpperCase(split[1].charAt(0)) + split[1].substring(1) + " has entered the auction.");
                setChanged();
                notifyObservers("joined_" + split[1]);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else if(split[0].equals("itemBid")) {
            int num = Integer.parseInt(split[1]);
            int amount = Integer.parseInt(split[2]);
            String customer = split[3];

            if (amount > items.get(num - 1).highBid)
                ch.sendToClient("invalidBid_" + num + "_" + "high");
            else if (!(amount > items.get(num - 1).currBid && amount >= items.get(num - 1).startingBid))
                ch.sendToClient("invalidBid_" + num + "_" + "low");
            else {
                if (items.get(num - 1).currBid == 0) {
                    final int[] second = {GLOBAL_TIMER_START_SEC};
                    final int[] minute = {GLOBAL_TIMER_START_MINS};
                    final String[] ddSecond = new String[1];
                    final String[] ddMinute = new String[1];
                    Thread timer1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                if (timerDone[num - 1])
                                    break;
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (timerDone[num - 1])
                                    break;
                                second[0]--;
                                ddSecond[0] = dFormat.format(second[0]);
                                ddMinute[0] = dFormat.format(minute[0]);
                                setChanged();
                                notifyObservers("time_" + num + "_" + ddMinute[0] + ":" + ddSecond[0]);

                                if (second[0] == -1) {
                                    second[0] = 59;
                                    minute[0]--;
                                    ddSecond[0] = dFormat.format(second[0]);
                                    ddMinute[0] = dFormat.format(minute[0]);
                                    setChanged();
                                    notifyObservers("time_" + num + "_" + ddMinute[0] + ":" + ddSecond[0]);
                                }
                                if (minute[0] == 0 && second[0] == 0) {
                                    if(num==3 || num==4)
                                        auctionActivity.add(items.get(num-1).name + " has been sold to " + customer + " for $" + myFormat.format(items.get(num-1).currBid) + "!");
                                    else
                                        auctionActivity.add("The " + items.get(num-1).name + " has been sold to " + customer + " for $" + myFormat.format(items.get(num-1).currBid)  + "!");

                                    items.get(num - 1).sold = true;
                                    items.get(num-1).history.add("SOLD!");
                                    myWriter.append("\nItem ").append(String.valueOf(num)).append(", ").append(items.get(num - 1).name).append(",").append(" was sold to ").append(customer).append(" for $").append(myFormat.format(items.get(num - 1).currBid)).append("!");
                                    for(int i =0;i<items.size();i++)
                                    {
                                        if(!items.get(i).sold)
                                            break;
                                        if(i==items.size()-1)
                                            myWriter.close();
                                    }

                                    setChanged();
                                    notifyObservers("timerDone_" + num + "_" + items.get(num - 1).currBid + "_" + items.get(num - 1).currOwner);
                                    break;
                                }
                            }

                        }
                    });
                    timer1.start();
                }
                items.get(num - 1).currBid = amount;
                items.get(num - 1).currOwner = customer;
                items.get(num - 1).history.add(customer + " bid " + myFormat.format(amount) + "!");
                if (num == 3 || num == 4)
                    auctionActivity.add(Character.toUpperCase(customer.charAt(0)) + customer.substring(1) + " bid $" + myFormat.format(amount) + " on " + items.get(num - 1).name + "!");
                else
                    auctionActivity.add(Character.toUpperCase(customer.charAt(0)) + customer.substring(1) + " bid $" + myFormat.format(amount) + " on the " + items.get(num - 1).name + "!");

                setChanged();
                notifyObservers("bidOn_" + num + "_" + amount + "_" + customer);
            }
        }
        else if(split[0].equals("purchase"))
        {
            int num = Integer.parseInt(split[1]);
            String customer = split[2];

            items.get(num-1).sold = true;
            timerDone[num-1] = true;
            items.get(num-1).currOwner = customer;
            items.get(num-1).currBid = items.get(num-1).highBid;
            items.get(num-1).history.add(Character.toUpperCase(customer.charAt(0)) + customer.substring(1) + " bid " + myFormat.format(items.get(num-1).currBid) + "!");
            items.get(num-1).history.add("SOLD!");

            myWriter.append("\nItem ").append(String.valueOf(num)).append(", ").append(items.get(num - 1).name).append(",").append(" was sold to ").append(customer).append(" for $").append(myFormat.format(items.get(num - 1).currBid)).append("!");
            for(int i =0;i<items.size();i++)
            {
                if(!items.get(i).sold)
                    break;
                if(i==items.size()-1)
                    myWriter.close();
            }

            if(num==3 || num==4)
                auctionActivity.add(items.get(num-1).name + " has been sold to " + customer + " for $" + myFormat.format(items.get(num-1).currBid) + "!");
            else
                auctionActivity.add("The " + items.get(num-1).name + " has been sold to " + customer + " for $" + myFormat.format(items.get(num-1).currBid)  + "!");

            setChanged();
            notifyObservers("sold_" + num + "_" + items.get(num-1).currBid + "_" + customer);
            ch.sendToClient("soldToYou_" + num + "_" + items.get(num-1).currBid);

        }
        else
        {
            System.out.println("WRONG MESSAGE WAS SENT");
        }
    }


    //===========================================================================================================
    //Nested class to set up a client connection that sets up all IO streams and establishes server-client communication via Observer interface

    class ClientHandler implements Runnable, Observer {
        Socket clientSocket;
        private ObjectInputStream reader;
        private ObjectOutputStream sender;
        private BufferedReader in;
        private PrintWriter out,timeOut;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                out = new PrintWriter(clientSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void run() {

            String input;
            try {
                while ((input = in.readLine()) != null) {
                    System.out.println("From client: " + input);
                    server.processRequest(input, this);
                }
                System.out.println("A client shut down.");
            } catch (IOException e) {
                System.out.println(clientSocket + " forgot to logout.");
            }

        }

        @Override
        public void update(Observable o, Object arg) {
            sendToClient((String)arg);
        }

        protected void sendToClient(String commandString) {
            System.out.println("Sending to client: " + commandString);
            out.println(commandString);
            out.flush();
        }

    }
}
