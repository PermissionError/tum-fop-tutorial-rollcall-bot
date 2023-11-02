package dev.rayma.util;

import dev.rayma.Launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RollcallManager {
    private static Map<String, List<String>> ongoingRollcalls = new HashMap<>();

    public static void initiateRollcall(String id) {
        ongoingRollcalls.put(id, new ArrayList<>());
    }

    public static void markAttendance(String id, String studentName) {
        if(ongoingRollcalls.get(id) == null) {
            Launcher.LOGGER.error("Rollcall not found when trying to mark attendance");
            throw new RuntimeException();
        }

        //If student already marked attendance, ignore
        if(ongoingRollcalls.get(id).contains(studentName))
            return;

        ongoingRollcalls.get(id).add(studentName);
    }

    public static List<String> concludeRollcall(String id) {
        if(ongoingRollcalls.get(id) == null) {
            Launcher.LOGGER.error("Rollcall not found when trying to conclude");
            throw new RuntimeException();
        }

        List<String> students = ongoingRollcalls.get(id);
        ongoingRollcalls.remove(id);
        return students;
    }

    public static boolean rollcallExists(String id) {
        return ongoingRollcalls.get(id) != null;
    }
}
