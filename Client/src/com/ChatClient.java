package com;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                System.out.println("You got a message from " + fromLogin + " ===>" + msgBody);
            }
        });

        if (!client.connect()) {
            System.err.println("Connect failed.");
        } else {
            System.out.println("Connect successful");

            if (client.login("driver", "driver")) {
                System.out.println("Login successful");

                client.msg("client", "Hello World!");
            } else {
                System.err.println("Login failed");
            }

            //client.logoff();
        }
    }

    public void msg(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }
    public void start(String sendTo, String msgBody) throws IOException {
        String cmd = "start " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }
    public void drivertoclient(String sendTo, String msgBody) throws IOException {
        String cmd = "drivertoclient " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response Line:" + response);

        if ("ok login".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokensMsg);
                    }
                    else if ("start".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleStart(tokensMsg);
                        secondWindow();

                    }
                    else if ("drivertoclient".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleStart(tokensMsg);
                        drivertoclientwindow();

                    }
                    else if ("firsterror".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleStart(tokensMsg);
                        firsterrorwindow();

                    }
                    else if ("seconderror".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleStart(tokensMsg);
                        seconderrorwindow();

                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void seconderrorwindow() {
        try {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("DealErrorMsgDriverScreen.fxml"));
                    Parent firstscreenparent = null;
                    try {
                        firstscreenparent = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Scene firstscreensecene = new Scene(firstscreenparent);
                    Stage window2= new Stage();
                    window2.setScene(firstscreensecene);
                    window2.show();
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void firsterrorwindow() {
        try {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("DealErrorMsgClientScreen.fxml"));
                    Parent firstscreenparent = null;
                    try {
                        firstscreenparent = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Scene firstscreensecene = new Scene(firstscreenparent);
                    Stage window2= new Stage();
                    window2.setScene(firstscreensecene);
                    window2.show();
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void drivertoclientwindow() throws IOException {
        try {
            /*
           Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
        primaryStage.setTitle("TrackTruck");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

            */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("PaymentScreen.fxml"));
                    Parent firstscreenparent = null;
                    try {
                        firstscreenparent = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Scene firstscreensecene = new Scene(firstscreenparent);
                    Stage window2= new Stage();
                    window2.setScene(firstscreensecene);
                    window2.show();
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void secondWindow() throws IOException {
        try {
            /*
           Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
        primaryStage.setTitle("TrackTruck");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

            */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("ShowReqDetailsScreen.fxml"));
                    Parent firstscreenparent = null;
                    try {
                        firstscreenparent = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Scene firstscreensecene = new Scene(firstscreenparent);
                    Stage window2= new Stage();
                    window2.setScene(firstscreensecene);
                    window2.show();
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleStart(String[] tokensMsg) {
        String start = tokensMsg[1];
        String msgBody = tokensMsg[2];

        for(MessageListener listener : messageListeners) {
            listener.onMessage(start, msgBody);
        }
    }

    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];

        for(MessageListener listener : messageListeners) {
            listener.onMessage(login, msgBody);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

}
