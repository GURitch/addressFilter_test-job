package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddressResolver {

    private static final String ADDR_OBJ_FILE = "file/AS_ADDR_OBJ.csv";
    private static final String HIERARCHY_FILE = "file/AS_ADM_HIERARCHY.csv";

    public static void main(String[] args) {
        try {
            // Задача №1
            String dateStr = "2012-01-01";
            List<String> objectIds = Arrays.asList("1422396", "1450759", "1449192", "1536554");
            printAddressesOnDate(dateStr, objectIds, ADDR_OBJ_FILE);

            // Задача №2
            String type = "проезд";
//            findAddressesWithType(ADDR_OBJ_FILE, HIERARCHY_FILE, type);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void printAddressesOnDate(String dateStr, List<String> objectIds, String fileAddress) throws IOException, ParseException {
        Map<String, String> addresses = getAddressOnDate(dateStr, fileAddress);
        for (String objectId : objectIds) {

            if (addresses.containsKey(objectId)) {
                System.out.println(objectId + ": " + addresses.get(objectId));
            }
        }
    }

    private static Map<String, String> getAddressOnDate(String dateStr, String fileAddress) throws IOException, ParseException {
        Map<String, String> addresses = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateStr);
        try (BufferedReader reader = new BufferedReader(new FileReader(fileAddress))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Date startDate = dateFormat.parse(parts[3]);
                Date finishDate = dateFormat.parse(parts[4]);
                if (date.after(startDate) && date.before(finishDate)) {
                    addresses.put(parts[0], parts[2] + " " + parts[1]); // objectId -> type + name
                }
            }
        }
        return addresses;
    }

    private static Map<String, String> getAllAddress(String fileAddress) throws IOException{
        Map<String, String> addresses = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileAddress))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                    addresses.put(parts[0], parts[2] + " " + parts[1]); // objectId -> type + name
            }
        }
        return addresses;
    }

}
