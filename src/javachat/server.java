/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javachat;

/**
 *
 * @author zGuindouOS
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class server {

    private class infos_client implements Serializable {

        private String Login;
        private String Password;
        private Socket socket;

        private infos_client(String login, String password, Socket socket) {
            this.Login = login;
            this.Password = password;
            this.socket = socket;
        }
    };

    static private ArrayList<infos_client> clients;

    private void save_clients() throws Exception {
        clients.forEach((clt) -> {
            clt.socket = null;
        });
        try {
            FileOutputStream file = new FileOutputStream("Clients.ser");
            ObjectOutputStream oos = new ObjectOutputStream(file);
            oos.writeObject(clients);
            oos.flush();
            oos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void load_clients() {
        try {
            FileInputStream file = new FileInputStream("Clients.ser");
            ObjectInputStream ois = new ObjectInputStream(file);
            clients = (ArrayList<infos_client>) ois.readObject();
            ois.close();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private boolean log_in(Socket sClient) throws IOException {
        boolean test = false;
        String login, password;
        BufferedReader br = new BufferedReader(new InputStreamReader(sClient.getInputStream()));
        PrintWriter out = new PrintWriter(sClient.getOutputStream());
        out.println("\nLogin : ");
        out.flush();
        login = br.readLine();
        out.println("\nPassword : ");
        out.flush();
        password = br.readLine();

        //load_clients();
        for (infos_client clt : clients) {
            if (clt.Login.equals(login) && clt.Password.equals(password)) {
                clt.socket = sClient;
                test = true;
            }
        }
        return test;
    }

    private void sign_up(Socket sClient) throws IOException, Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(sClient.getInputStream()));
        PrintWriter out = new PrintWriter(sClient.getOutputStream());
        out.println("Login : ");
        out.flush();
        String login = br.readLine();
        out.println("Password : ");
        out.flush();
        String password = br.readLine();
        out.println("\n");
        infos_client clt = new infos_client(login, password, sClient);
        clients.add(clt);
        //save_clients();
    }

    public server() {
        clients = new ArrayList<infos_client>();
    }

    public static void main(String[] args) throws IOException, Exception {

        ServerSocket SerSock = new ServerSocket(8001);
        server srv = new server();
        try {
            srv.load_clients();
            while (true) {

                Socket sClient = SerSock.accept();

                Thread chat_th = new Thread(new Runnable() {

                    private void runChat() throws IOException, Exception {

                        try {
                            //System.out.println("new client with " + ClientNumber + " like a number");

                            BufferedReader br = new BufferedReader(new InputStreamReader(sClient.getInputStream()));
                            PrintWriter out = new PrintWriter(sClient.getOutputStream());

                            out.println("1. log in");
                            out.flush();
                            out.println("2. sign up");
                            out.flush();
                            out.println("Your Coice : ");
                            out.flush();

                            switch (br.readLine()) {
                                case "1":
                                    srv.log_in(sClient);
                                    break;
                                case "2":
                                    srv.sign_up(sClient);
                                    break;
                                default:
                                    out.println("Invalide choix\n");
                                    out.flush();
                            }

                            out.println(clients.get(clients.size() - 1).Login + " : Accepted Conexion\n");
                            out.flush();
                            String str_cl = clients.get(clients.size() - 1).Login + " : ";

                            out.println("Login de votre correspondant");
                            out.flush();

                            String corres = br.readLine();

                            for (infos_client clt : clients) {
                                if (clt.Login.equals(corres)) {
                                    String msg;
                                    String str;
                                    while (true) {

                                        str = br.readLine();
                                        msg = str_cl + str;

                                        PrintWriter outMsg = new PrintWriter(clt.socket.getOutputStream());
                                        outMsg.println(msg);
                                        outMsg.flush();
                                    }
                                }
                            }

                        } finally {
                            sClient.close();
                        }
                    }

                    @Override
                    public void run() {

                        try {
                            runChat();
                        } catch (IOException ex) {
                            System.err.println(ex);
                        } catch (Exception ex) {
                            Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                });
                chat_th.start();
            }
        } finally {
            SerSock.close();
            srv.save_clients();
        }
    }
}
