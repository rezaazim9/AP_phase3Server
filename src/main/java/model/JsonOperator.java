package model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static controller.constants.FilePaths.SAVE_FILES_FOLDER_PATH;
import static controller.constants.UIMessageConstants.*;

public abstract class JsonOperator {
    private JsonOperator(){}
    private static boolean proceedToSaveLoad = true;
    private static File saveFile=null;

    public static void JsonInitiate() {
        Timer jsonTimer = new Timer(10, null);
        jsonTimer.addActionListener(e -> {
            if (proceedToSaveLoad) {
                try {
                    saveState();
                    loadState();
                } catch (IOException ex) {throw new UnsupportedOperationException("Failed to synchronize game instance with the save file");}
            } else jsonTimer.stop();
        });
        jsonTimer.setCoalesce(true);
        jsonTimer.start();
    }

    public static void saveState() {
        Profile.getCurrent().updateINSTANCE();
        if (Profile.getCurrent() != null) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            String jsonWrite = gson.toJson(Profile.getCurrent(), Profile.class);
            try(FileWriter writer = new FileWriter(getFilePath(Profile.getCurrent().getProfileId()))){writer.write(jsonWrite);
            } catch (IOException e) {throw new UnsupportedOperationException("Failed to save game state");}
        }
    }

    public static boolean loadState(String id) throws IOException {
        boolean login;
        String hashId= String.valueOf(id.hashCode());
        if (saveFile==null){
            File saveFilesFolder=new File(SAVE_FILES_FOLDER_PATH.getValue());
            if (saveFilesFolder.exists()) for (File file: Objects.requireNonNull(saveFilesFolder.listFiles())) if (file.getName().equals(hashId+SAVE_FILE_EXTENSION.getValue())) {
                saveFile=file;
                break;
            }
        }
        if (saveFile!=null) {
            Profile.setCurrent(new ObjectMapper().readValue(saveFile,Profile.class));
            login=true;
        }
        else {
            Profile.setCurrent(new Profile(id));
            saveFile=new File(getFilePath(Profile.getCurrent().getProfileId()));
            login=false;
        }
        return login;
    }
    public static void loadState() throws IOException {
        loadState(Profile.getCurrent().getProfileId());
    }

    public static String getFilePath(String fileName) {
        return SAVE_FILES_FOLDER_PATH.getValue() + fileName + SAVE_FILE_EXTENSION.getValue();
    }

    public static void setProceedToSaveLoad(boolean proceedToSaveLoad) {
        JsonOperator.proceedToSaveLoad = proceedToSaveLoad;
    }
}

