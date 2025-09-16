package main.utils;

import main.model.*;
import main.exceptions.InvalidDataException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Реализует парсинг csv-файлов, запись в них.
 */
public class CSVProcessor {
//    public static List<Organization> loadFromCSV(String filename) throws IOException, InvalidDataException {
//        List<Organization> organizations = new ArrayList<>();
//
//        File file = new File(filename);
//        if (file.createNewFile()) {
//            return organizations;
//        }
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
//            String line;
//            reader.readLine();
//            reader.readLine();
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split(";");
//                if (parts.length == 9) {
//                    String name = parts[0];
//                    try {
//                        Double xCoordinates = Validator.parseXCoordinates(parts[1]);
//                        long yCoordinates = Validator.parseYCoordinates(parts[2]);
//                        Coordinates coordinates = new Coordinates(xCoordinates, yCoordinates);
//                        long annualTurnover = Validator.parseAnnualTurnover(parts[3]);
//                        OrganizationType type = Validator.parseOrganizationType(parts[4]);
//
//                        if (!parts[5].equals(" ")) {
//                            if (parts[6].equals(" ") && parts[7].equals(" ") && parts[8].equals(" ")) {
//                                String streetName = Validator.validateStreetName(parts[5]);
//                                Address address = new Address(streetName);
//
//                                Organization org = new Organization(name, coordinates, annualTurnover, type, address);
//                                organizations.add(org);
//                            } else if (!parts[6].equals(" ") && !parts[7].equals(" ") && !parts[8].equals(" ")) {
//                                String streetName = Validator.validateStreetName(parts[5]);
//                                float xLocation = Validator.parseXLocation(parts[6]);
//                                double yLocation = Validator.parseYLocation(parts[7]);
//                                Long zLocation = Validator.parseZLocation(parts[8]);
//
//                                Location location = new Location(xLocation, yLocation, zLocation);
//                                Address address = new Address(streetName, location);
//
//                                Organization org = new Organization(name, coordinates, annualTurnover, type, address);
//                                organizations.add(org);
//                            } else {
//                                throw new InvalidDataException("Неверные координаты локации организации: " + line);
//                            }
//                        } else if (parts[6].equals(" ") && parts[7].equals(" ") &&
//                                parts[8].equals(" ") && parts[5].equals(" ")) {
//                            Organization org = new Organization(name, coordinates, annualTurnover, type, null);
//                            organizations.add(org);
//                        } else {
//                            throw new InvalidDataException("Название улицы не может быть пустым: " + line);
//                        }
//                    } catch (InvalidDataException e) {
//                        throw new InvalidDataException(e.getMessage());
//                    }
//                } else {
//                    throw new InvalidDataException("Неверное количество аргументов: " + line);
//                }
//            }
//        }
//        return organizations;
//    }

//    public static void saveToCSV(String filename, TreeMap<Integer, Organization> collection) throws IOException {
//        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
//            writer.println("OrganizationName;xCoordinates;yCoordinates;AnnualTurnover;OrganizationType;Street;xLocation;yLocation;zLocation");
//            writer.println("---------------------------------------------------------------------------------------------------------------");
//            for (Organization org : collection.values()) {
//                if (org == null) {
//                    continue;
//                }
//                String line;
//                line = String.format("%s;%s;%d;%d;%s;%s",
//                        org.getName(),
//                        org.getCoordinates().getX(),
//                        org.getCoordinates().getY(),
//                        org.getAnnualTurnover(),
//                        org.getType() != null ? org.getType().name() : " ",
//                        org.getOfficialAddress() != null ? org.getOfficialAddress().getStreet() : " ");
//                if (org.getOfficialAddress() != null && org.getOfficialAddress().getTown() != null) {
//                    line += String.format(";%s;%s;%d", org.getOfficialAddress().getTown().getX(),
//                            org.getOfficialAddress().getTown().getY(),
//                            org.getOfficialAddress().getTown().getZ());
//                } else {
//                    line += String.format(";%s;%s;%s", " ", " ", " ");
//                }
//                writer.println(line);
//            }
//        }
//    }

    public static Organization parseOrganizationFromString(String line) throws InvalidDataException {
        String[] parts = line.split(";");
        Organization output = null;
        if (parts.length == 9) {
            String name = parts[0];
            try {
                Double xCoordinates = Validator.parseXCoordinates(parts[1]);
                long yCoordinates = Validator.parseYCoordinates(parts[2]);
                Coordinates coordinates = new Coordinates(xCoordinates, yCoordinates);
                long annualTurnover = Validator.parseAnnualTurnover(parts[3]);
                OrganizationType type = parts[4].equals("_") ? null : Validator.parseOrganizationType(parts[4]);

                if (!parts[5].equals("_")) {
                    if (parts[6].equals("_") && parts[7].equals("_") && parts[8].equals("_")) {
                        String streetName = Validator.validateStreetName(parts[5]);
                        Address address = new Address(streetName);
                        return new Organization(name, coordinates, annualTurnover, type, address, "server");
                    } else if (!parts[6].equals("_") && !parts[7].equals("_") && !parts[8].equals("_")) {
                        String streetName = Validator.validateStreetName(parts[5]);
                        float xLocation = Validator.parseXLocation(parts[6]);
                        double yLocation = Validator.parseYLocation(parts[7]);
                        Long zLocation = Validator.parseZLocation(parts[8]);

                        Location location = new Location(xLocation, yLocation, zLocation);
                        Address address = new Address(streetName, location);

                        output = new Organization(name, coordinates, annualTurnover, type, address, "server");
                    } else {
                        throw new InvalidDataException("Неверные координаты локации организации: " + line);
                    }
                } else if (parts[6].equals("_") && parts[7].equals("_") &&
                        parts[8].equals("_") && parts[5].equals("_")) {
                    output = new Organization(name, coordinates, annualTurnover, type, null, "server");
                } else {
                    throw new InvalidDataException("Название улицы не может быть пустым: " + line);
                }
            } catch (InvalidDataException e) {
                throw new InvalidDataException(e.getMessage());
            }
            return output;
        } else {
            throw new InvalidDataException("Неверное количество аргументов: " + line);
        }
    }
}
