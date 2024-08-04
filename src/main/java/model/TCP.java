package model;

import model.entities.Skill;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static controller.constants.UIConstants.MINIMUM_PROFILE_ID_LENGTH;
import static controller.constants.UIMessageConstants.PROFILE_ID_REGEX;
import static model.JsonOperator.loadState;

public class TCP extends Thread {
    private final Socket socket;

    public TCP(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            boolean valid;
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            Object o = inputStream.readObject();
            Packet packet = (Packet) o;
            if (packet.getType().equals("loginCheck")) {
                String entry=(String) packet.getObject();
                valid = entry.matches(PROFILE_ID_REGEX.getValue()) && entry.length() >= MINIMUM_PROFILE_ID_LENGTH.getValue();
                outputStream.writeObject(valid);
            }
             if ((packet.getType().equals("login"))){
                String entry=(String) packet.getObject();
                boolean login=loadState(entry);
                if (!login){
                    JsonOperator.JsonInitiate();
                    Skill.initializeSkills();
                }
                outputStream.writeObject(login);
            }
             if ((packet.getType().equals("exit"))){
                JsonOperator.setProceedToSaveLoad((boolean)packet.getObject());
            }
            socket.getOutputStream().flush();
            socket.close();
        } catch (IOException | ClassNotFoundException ignored) {

        }
    }
}
