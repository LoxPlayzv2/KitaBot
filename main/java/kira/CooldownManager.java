package kira;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {
    private static final Map<String, Long> cooldowns = new ConcurrentHashMap<>(); // Store user ID and last interaction timestamp

    public static boolean isOnCooldown(String userId, long cooldownSeconds) {
        long currentTime = Instant.now().getEpochSecond();
        if (cooldowns.containsKey(userId)) {
            long lastInteraction = cooldowns.get(userId);
            long cooldownEndTime = lastInteraction + cooldownSeconds;
            return currentTime < cooldownEndTime;
        }
        return false;
    }

    public static void applyCooldown(String userId) {
        cooldowns.put(userId, Instant.now().getEpochSecond());
    }
}
