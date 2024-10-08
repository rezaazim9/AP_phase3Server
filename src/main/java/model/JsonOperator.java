package model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static controller.constants.FilePaths.SAVE_FILES_FOLDER_PATH;
import static controller.constants.FilePaths.STATS_FILES_FOLDER_PATH;
import static controller.constants.UIMessageConstants.*;

public abstract class JsonOperator {
    private JsonOperator() {
    }


    private static File saveFile = null;

    public static void JsonInitiate() {
        try {
            saveState();
            loadState();
        } catch (IOException ex) {
            throw new UnsupportedOperationException("Failed to synchronize game instance with the save file");
        }
    }

    public static void saveStats(String stats) {
        Stats stats1 = new Gson().fromJson(stats, Stats.class);
        File saveFilesFolder = new File(STATS_FILES_FOLDER_PATH.getValue());
        if (saveFilesFolder.exists()) {
            try (FileWriter writer = new FileWriter(STATS_FILES_FOLDER_PATH.getValue() + stats1.getProfileId() + stats1.getTimeSurvived() + stats1.getXp() + SAVE_FILE_EXTENSION.getValue())) {
                writer.write(stats);
            } catch (IOException e) {
                throw new UnsupportedOperationException("Failed to save game state");
            }
        }

    }

    public static boolean isProfileExist(String id) {
        String hashId = String.valueOf(id.hashCode());
        File saveFilesFolder = new File(SAVE_FILES_FOLDER_PATH.getValue());
        if (saveFilesFolder.exists()) for (File file : Objects.requireNonNull(saveFilesFolder.listFiles()))
            if (file.getName().equals(hashId + SAVE_FILE_EXTENSION.getValue())) {
                saveFile = file;
                return true;
            }
        return false;
    }

    public static void saveState() {
        Profile.getCurrent().updateINSTANCE();
        if (Profile.getCurrent() != null) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            String jsonWrite = gson.toJson(Profile.getCurrent(), Profile.class);
            try (FileWriter writer = new FileWriter(getFilePath("" + Profile.getCurrent().getProfileId().hashCode()))) {
                writer.write(jsonWrite);
            } catch (IOException e) {
                throw new UnsupportedOperationException("Failed to save game state");
            }
        }
    }

    public static void loadState(String id) throws IOException {
        if (isProfileExist(id)) {
            Profile.setCurrent(new ObjectMapper().readValue(saveFile, Profile.class));
        } else {
            System.out.println("Creating new profile");
            Profile.setCurrent(new Profile(id));
            saveFile = new File(getFilePath("" + Profile.getCurrent().getProfileId().hashCode()));
        }
    }

    public static void loadState() throws IOException {
        loadState(Profile.getCurrent().getProfileId());
    }

    public static String getFilePath(String fileName) {
        return SAVE_FILES_FOLDER_PATH.getValue() + fileName + SAVE_FILE_EXTENSION.getValue();
    }
}

