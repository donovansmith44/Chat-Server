import java.io.*;
import java.net.*;
import java.util.*;

class Server {
    public static void main(String[] args) {
        if(args.length != 1){
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }

        ArrayList<ClientHandler> ThreadList = new ArrayList<>();
        int portNumber = Integer.parseInt(args[0]);
        ServerSocket server = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        String clientName;

        try{
            
            server = new ServerSocket(portNumber);
            server.setReuseAddress(true);

            System.out.println("Host connected");
            while(true){
                
                Socket client = server.accept();
                reader = 
                    new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer =
                    new PrintWriter(client.getOutputStream(), true);

                System.out.println("Client accepted from IP: "
                    + client.getInetAddress()
                        .getHostAddress());

                writer.println("What's your name?");
                clientName = reader.readLine();

                ClientHandler clientHandler =
                    new ClientHandler(client, ThreadList, clientName);
                
                ThreadList.add(clientHandler);
                
                clientHandler.start();
            }
        }
        catch(IOException i)
        {
            i.printStackTrace();
        }
    }

private static class ClientHandler extends Thread {
    private final Socket clientSocket;
    private ArrayList<ClientHandler> threadList;
    PrintWriter thisOutput;
    String clientName;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> ThreadList, String name){
        this.clientSocket = socket;
        this.threadList = ThreadList;
        this.clientName = name;
    }
    
    @Override
    public void run()
    {
        BufferedReader thisInput = null;
        try{
            thisOutput = 
                new PrintWriter(clientSocket.getOutputStream(), true);
            thisInput = 
                new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            String line = "";

            while((line = thisInput.readLine()) != null) { 
                if(line.equalsIgnoreCase("exit")){
                    break;
                }
                printToAllClients(line);
                System.out.println("From client '" + clientName + "' : " + line);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (thisOutput != null) {
                    thisOutput.close();
                }
                if (thisInput != null) {
                    thisInput.close();
                    clientSocket.close();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }        
        }
    }

    private void printToAllClients(String outputLine){
        for(ClientHandler handler: threadList){
            handler.thisOutput.println(clientName + ": " + outputLine);
        }
    }
}
}