import java.io.*;
import java.net.*;
import java.util.*;

class Client{
    public static void main(String args[]){
        if(args.length != 2){
            System.err.println("Usage: java socket <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try(Socket socket = new Socket(hostName, portNumber))
        {  
            PrintWriter clientOutputPrinter = 
                new PrintWriter(socket.getOutputStream(), true);
            BufferedReader clientInputReader =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner userInputScanner =
                new Scanner(System.in);
            String clientName = "";
            String line = "";
            ClientThread clientThread = new ClientThread(socket);
            clientThread.start();

            while(!line.equalsIgnoreCase("exit")){
                line = userInputScanner.nextLine();
                clientOutputPrinter.println(line);
            }

            clientInputReader.close();
            socket.close();
            userInputScanner.close();
        }
        catch(IOException i){
            i.printStackTrace();
            System.exit(1);
        }
    }

    private static class ClientThread extends Thread {
        private Socket clientSocket;
        private BufferedReader clientInput;

        public ClientThread(Socket s) throws IOException {
            this.clientSocket = s;
            this.clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String response = "";
                while((response = clientInput.readLine()) != null) {
                    System.out.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientInput.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}