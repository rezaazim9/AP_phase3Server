package model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static controller.constants.FilePaths.STATS_FILES_FOLDER_PATH;
import static controller.constants.UIConstants.MINIMUM_PROFILE_ID_LENGTH;
import static controller.constants.UIMessageConstants.PROFILE_ID_REGEX;
import static model.JsonOperator.*;

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
                String entry = (String) packet.getObject();
                valid = entry.matches(PROFILE_ID_REGEX.getValue()) && entry.length() >= MINIMUM_PROFILE_ID_LENGTH.getValue();
                outputStream.writeObject(valid);
            }
            if ((packet.getType().equals("login"))) {
                String entry = (String) packet.getObject();
                boolean login = isProfileExist(entry);
                outputStream.writeObject(login);
            }
            if (packet.getType().equals("profileId")) {
                String entry = (String) packet.getObject();
                loadState(entry);
                outputStream.writeObject(new Gson().toJson(Profile.getCurrent()));
                JsonInitiate();
                Skill.initializeSkills();
            }
            if (packet.getType().equals("profile")) {
                String json = packet.getObject().toString();
                Profile.setCurrent(new Gson().fromJson(json, Profile.class));
                JsonInitiate();
                Skill.initializeSkills();
            }
            if (packet.getType().equals("stats")) {
                String json = packet.getObject().toString();
                saveStats(json);
                File statsFilesFolder = new File(STATS_FILES_FOLDER_PATH.getValue());
                List<String> stats = new ArrayList<>();
                for (File file : Objects.requireNonNull(statsFilesFolder.listFiles())) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Stats stat = objectMapper.readValue(file, Stats.class);
                    stats.add(stat.getProfileId() + " " + stat.getTimeSurvived() + " " + stat.getXp());
                }
                outputStream.writeObject(stats);
            }
            socket.getOutputStream().flush();
            socket.close();
        } catch (IOException | ClassNotFoundException ignored) {

        }
    }
}
