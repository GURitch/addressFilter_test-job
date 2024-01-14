package org.example;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddressResolver {

    private static final String ADDR_OBJ_FILE = "file/AS_ADDR_OBJ.csv";
    private static final String HIERARCHY_FILE = "file/AS_ADM_HIERARCHY.csv";

    public static void main(String[] args) {
        try {
            // Задача №1
            System.out.println("Задача №1");
            String dateStr = "2012-01-01";
            List<String> objectIds = Arrays.asList("1422396", "1450759", "1449192", "1536554");
            printAddressesOnDate(dateStr, objectIds);

            // Задача №2
            System.out.println("Задача №2");
            String typeAddress = "проезд";
            printAddressesWithTypeInHierarchy(typeAddress);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Выводит в консоль описание адресов соответсвующих переданному набору идентификаторов (subjectsIds) и дате (dateStr)
     *
     * @param dateStr     интересующая дата
     * @param subjectsIds набор идентификаторов
     * @throws IOException
     * @throws ParseException
     */
    private static void printAddressesOnDate(String dateStr, List<String> subjectsIds) throws IOException, ParseException {
        for (Address address : getAddressOnDate(dateStr)) {
            if (subjectsIds.contains(address.getObjectId())) {
                System.out.println(address.getObjectId() + ": " + address.getTypeName() + " " + address.getName());
            }
        }
    }

    /**
     * Выводит в консоль адреса с указанным типом из файла иерархии и адресов.
     *
     * @param typeAddress Тип адреса для фильтрации
     * @throws IOException    Исключение в случае ошибок ввода-вывода
     * @throws ParseException Исключение в случае ошибок парсинга дат
     */
    private static void printAddressesWithTypeInHierarchy(String typeAddress) throws IOException, ParseException {
        List<Address> addresses = readAddressFromFile(ADDR_OBJ_FILE);
        List<AddressHierarchy> hierarchies = readAddressHierarchyFromFile();

        for (Address address : addresses) {
            if (address.getTypeName().equalsIgnoreCase(typeAddress)) {
                String fullAddress = getFullAddress(address.getObjectId(), hierarchies, addresses);
                if (fullAddress != null) {
                    System.out.println(fullAddress);
                }
            }
        }
    }

    /**
     * Метод считывает информацию о субъектах РФ из файла,
     * преобразует в Set с фильтрацией по дате.
     *
     * @param dateStr интересующая дата
     * @return - множество Set состоящее из Address, соответствующих переданной дате
     * @throws IOException
     * @throws ParseException
     */
    private static Set<Address> getAddressOnDate(String dateStr) throws IOException, ParseException {
        Set<Address> addresses = new HashSet<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateStr);
        try (BufferedReader reader = new BufferedReader(new FileReader(AddressResolver.ADDR_OBJ_FILE))) {
            // Пропускаем строку заголовков
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Date startDate = dateFormat.parse(parts[3]);
                Date finishDate = dateFormat.parse(parts[4]);
                if (date.after(startDate) && date.before(finishDate)) {
                    addresses.add(createAddress(parts));
                }
            }
        }
        return addresses;
    }

    /**
     * Метод считывает информацию о субъектах РФ из файла, преобразует в Set
     *
     * @param fileAddress файл с адресами
     * @return множество Set состоящее из Address
     * @throws IOException
     * @throws ParseException
     */
    public static List<Address> readAddressFromFile(String fileAddress) throws IOException, ParseException {
        List<Address> addresses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileAddress))) {
            // Пропускаем строку заголовков
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                addresses.add(createAddress(parts));
            }
        }
        return addresses;
    }

    /**
     * создает список с информацией о иерархии адресов из файла
     *
     * @return Список List с объектами AddressHierarchy
     * @throws IOException
     * @throws ParseException
     */
    private static List<AddressHierarchy> readAddressHierarchyFromFile() throws IOException, ParseException {
        List<AddressHierarchy> subjectHierarchies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(AddressResolver.HIERARCHY_FILE))) {
            // Пропускаем строку заголовков
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                subjectHierarchies.add(createAddressHierarchy(parts));
            }
        }
        return subjectHierarchies;
    }

    private static Address createAddress(String[] parts) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Address address = new Address();
        address.setObjectId(parts[0]);
        address.setName(parts[1]);
        address.setTypeName(parts[2]);
        address.setStartDate(dateFormat.parse(parts[3]));
        address.setEndDate(dateFormat.parse(parts[4]));
        address.setActual(Boolean.parseBoolean(parts[5]));
        address.setActive(Boolean.parseBoolean(parts[6]));
        return address;
    }

    /**
     *Создает объект иерархии адресов
     * @param parts массив со значениями полей из файла иерархии адресов
     * @return объект иерархии адресов
     * @throws ParseException
     */
    private static AddressHierarchy createAddressHierarchy(String[] parts) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AddressHierarchy addressHierarchy = new AddressHierarchy();
        addressHierarchy.setObjectId(parts[0]);
        addressHierarchy.setParentObjectId(parts[1]);
        addressHierarchy.setStartDate(dateFormat.parse(parts[2]));
        addressHierarchy.setEndDate(dateFormat.parse(parts[3]));
        addressHierarchy.setActive(Boolean.parseBoolean(parts[4]));
        return addressHierarchy;
    }

    /**
     * Возвращает полный адрес для заданного идентификатора объекта.
     *
     * @param objectId      Идентификатор объекта
     * @param hierarchies   Список объектов иерархии адресов
     * @param addresses     Список объектов адресов
     * @return Полный адрес в виде строки или null, если иерархия не найдена или адреса отсутствуют
     */
    private static String getFullAddress(String objectId, List<AddressHierarchy> hierarchies, List<Address> addresses) {
        AddressHierarchy hierarchy = findHierarchyForObjectId(objectId, hierarchies);
        if (hierarchy != null) {
            List<String> addressNames = new ArrayList<>();
            while (hierarchy != null) {
                Address address = findAddressById(hierarchy.getObjectId(), addresses);
                if (address != null) {
                    addressNames.add(address.getTypeName() + " " + address.getName());
                }
                hierarchy = findHierarchyForObjectId(hierarchy.getParentObjectId(), hierarchies);
            }
            Collections.reverse(addressNames);
            return String.join(", ", addressNames);
        }
        return null;
    }

    /**
     * Находит объект иерархии адресов для заданного идентификатора объекта.
     *
     * @param objectId    Идентификатор объекта
     * @param hierarchies Список объектов иерархии адресов
     * @return Объект иерархии адресов или null, если объект не найден
     */
    private static AddressHierarchy findHierarchyForObjectId(String objectId, List<AddressHierarchy> hierarchies) {
        for (AddressHierarchy hierarchy : hierarchies) {
            if (hierarchy.getObjectId().equals(objectId)) {
                return hierarchy;
            }
        }
        return null;
    }

    /**
     * Находит объект адреса для заданного идентификатора объекта.
     *
     * @param objectId  Идентификатор объекта
     * @param addresses Список объектов адресов
     * @return Объект адреса или null, если объект не найден
     */
    private static Address findAddressById(String objectId, List<Address> addresses) {
        for (Address address : addresses) {
            if (address.getObjectId().equals(objectId)) {
                return address;
            }
        }
        return null;
    }
}
