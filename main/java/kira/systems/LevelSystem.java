package kira.systems;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LevelSystem extends ListenerAdapter {
    private static final int BASE_EXP_PER_LEVEL = 100;
    private static final double EXP_MULTIPLIER = 1.2;
    private static final Random random = new Random();
    private static final String DATA_FILE = "user_data.json";

    private Map<Long, UserData> userDataMap;
    private Map<Integer, LevelReward> levelRewards;

    // Probabilities for different experience amounts
    private static final double ZERO_EXP_PROBABILITY = 0.5; // 50% chance of getting 0 experience
    private static final double ONE_EXP_PROBABILITY = 0.2; // 20% chance of getting 1 experience
    private static final double TWO_EXP_PROBABILITY = 0.2; // 20% chance of getting 2 experience
    private static final double THREE_EXP_PROBABILITY = 0.1; // 10% chance of getting 3 experience

    public LevelSystem() {
        userDataMap = new HashMap<>();
        levelRewards = new HashMap<>();
        initializeLevelRewards(); // Initialize level rewards
        loadUserData(); // Load user data from file
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            long userId = event.getAuthor().getIdLong();
            int expGained = getRandomExperience();
            addExperience(userId, expGained);
            saveUserData(); // Save user data to file
        }
    }

    private int getRandomExperience() {
        double randomValue = random.nextDouble();
        if (randomValue < ZERO_EXP_PROBABILITY) {
            return 0;
        } else if (randomValue < ZERO_EXP_PROBABILITY + ONE_EXP_PROBABILITY) {
            return 1;
        } else if (randomValue < ZERO_EXP_PROBABILITY + ONE_EXP_PROBABILITY + TWO_EXP_PROBABILITY) {
            return 2;
        } else {
            return 3;
        }
    }

    public void addExperience(long userId, int expGained) {
        UserData userData = userDataMap.computeIfAbsent(userId, k -> new UserData());
        int currentLevel = userData.getLevel();
        int currentExp = userData.getExperience();
        int expRequired = getExperienceRequired(currentLevel);

        int newExp = currentExp + expGained;
        while (newExp >= expRequired) {
            newExp -= expRequired;
            currentLevel++;
            expRequired = getExperienceRequired(currentLevel);
            grantLevelReward(userId, currentLevel); // Grant level reward
        }

        userData.setLevel(currentLevel);
        userData.setExperience(newExp);
    }

    public int getLevel(long userId) {
        UserData userData = userDataMap.get(userId);
        return userData != null ? userData.getLevel() : 0;
    }

    public int getExperience(long userId) {
        UserData userData = userDataMap.get(userId);
        return userData != null ? userData.getExperience() : 0;
    }

    public int getExperienceRequired(int level) {
        return (int) (BASE_EXP_PER_LEVEL * Math.pow(EXP_MULTIPLIER, level - 1));
    }

    public void resetAllLevels() {
        userDataMap.clear();
    }
    public void resetUserData(long userId) {
        userDataMap.remove(userId);
    }

    public void addLevels(long userId, int levelsToAdd) {
        UserData userData = userDataMap.computeIfAbsent(userId, k -> new UserData());
        int currentLevel = userData.getLevel();
        int newLevel = currentLevel + levelsToAdd;
        int expRequired = getExperienceRequired(newLevel);

        userData.setLevel(newLevel);
        userData.setExperience(expRequired);
    }

    private void grantLevelReward(long userId, int level) {
        LevelReward reward = levelRewards.get(level);
        if (reward != null) {
            reward.grantReward(userId);
        }
    }

    private void initializeLevelRewards() {
        // Example level rewards
        levelRewards.put(5, new LevelReward() {
            @Override
            public void grantReward(long userId) {
                System.out.println("Granting reward for level 5 to user " + userId);
                // Grant reward logic here
            }
        });

        levelRewards.put(10, new LevelReward() {
            @Override
            public void grantReward(long userId) {
                System.out.println("Granting reward for level 10 to user " + userId);
                // Grant reward logic here
            }
        });
    }

    private void loadUserData() {
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                JsonObject jsonObject = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
                Gson gson = new Gson();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    long userId = Long.parseLong(entry.getKey());
                    UserData userData = gson.fromJson(entry.getValue(), UserData.class);
                    userDataMap.put(userId, userData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUserData() {
        try {
            JsonObject jsonObject = new JsonObject();
            Gson gson = new Gson();
            for (Map.Entry<Long, UserData> entry : userDataMap.entrySet()) {
                long userId = entry.getKey();
                UserData userData = entry.getValue();
                jsonObject.add(String.valueOf(userId), gson.toJsonTree(userData));
            }
            FileWriter fileWriter = new FileWriter(DATA_FILE);
            fileWriter.write(jsonObject.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class UserData {
        private int level;
        private int experience;

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }
    }

    private interface LevelReward {
        void grantReward(long userId);
    }
}
