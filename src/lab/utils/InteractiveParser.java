package lab.utils;

import lab.model.*;
import lab.exceptions.InvalidDataException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Считывает из пользовательского ввода информацию об организации.
 */
public class InteractiveParser {
    private final Scanner scanner = new Scanner(System.in);

    public String readOrganizationName() {
        while (true) {
            try {
                System.out.print("Введите название организации: ");
                String input = scanner.nextLine().trim();
                try {
                    Validator.validateOrganizationName(input);
                    return input;
                } catch (InvalidDataException e) {
                    System.out.println(e.getMessage());
                }
            } catch (NoSuchElementException e) {
                System.out.println("Завершение работы консоли.");
                System.exit(0);
            }
        }
    }

    public Coordinates readCoordinates() {
        while (true) {
            try {
                System.out.print("Введите координату Х: ");
                String inputX = scanner.nextLine().trim();
                System.out.print("Введите координату Y: ");
                String inputY = scanner.nextLine().trim();
                try {
                    Double x = Validator.parseXCoordinates(inputX);
                    long y = Validator.parseYCoordinates(inputY);
                    return new Coordinates(x, y);
                } catch (InvalidDataException e) {
                    System.out.println("Неверный формат ввода числа: оба поля должны быть числами, причем Х > -922.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("Завершение работы консоли.");
                System.exit(0);
            }
        }
    }


    public long readAnnualTurnover() {
        while (true) {
            try {
                System.out.print("Введите годовой оборот: ");
                String input = scanner.nextLine().trim();
                try {
                    return Validator.parseAnnualTurnover(input);
                } catch (InvalidDataException e) {
                    System.out.println("Годовой оборот должен быть строго положительным числом.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("Завершение работы консоли.");
                System.exit(0);
            }
        }
    }

    public OrganizationType readOrganizationType() {
        while (true) {
            try {
                System.out.print("Введите тип организации (COMMERCIAL, PUBLIC, GOVERNMENT, " +
                        "PRIVATE_LIMITED_COMPANY или нажмите Enter, если тип отсутствует): ");
                String input = scanner.nextLine().trim();
                try {
                    return Validator.parseOrganizationType(input);
                } catch (InvalidDataException e) {
                    System.out.println("Введите тип организации строго из приведенного списка.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("Завершение работы консоли.");
                System.exit(0);
            }
        }
    }

    public Location readLocation() {
        while (true) {
            try {
                System.out.print("Введите координату X адреса (или нажмите Enter, если координаты отсутствуют): ");
                String inputX = scanner.nextLine().trim();
                if (inputX.isEmpty()) return null;
                float x = Validator.parseXLocation(inputX);

                System.out.print("Введите координату Y адреса: ");
                String inputY = scanner.nextLine().trim();
                double y = Validator.parseYLocation(inputY);

                System.out.print("Введите координату Z адреса: ");
                String inputZ = scanner.nextLine().trim();
                Long z = Validator.parseZLocation(inputZ);

                return new Location(x, y, z);
            } catch (InvalidDataException e) {
                System.out.println("Неверный формат ввода числа: все координаты должны быть числами.");
            } catch (NoSuchElementException e) {
                System.out.println("Завершение работы консоли.");
                System.exit(0);
            }
        }
    }

    public Address readAddress() {
        while (true) {
            try {
                System.out.print("Введите название улицы (или нажмите Enter, если адрес отсутствует): ");
                String streetName = scanner.nextLine().trim();
                if (streetName.isEmpty()) return null;
                Location location = readLocation();
                try {
                    streetName = Validator.validateStreetName(streetName);
                    if (location == null) {
                        return new Address(streetName);
                    }
                    return new Address(streetName, location);
                } catch (InvalidDataException e) {
                    System.out.println("Длина названия улицы не может превышать 103 символа.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("Завершение работы консоли.");
                System.exit(0);
            }
        }
    }

    public Organization parseOrganization() throws InvalidDataException {
        String name = readOrganizationName();
        Coordinates coordinates = readCoordinates();
        long annualTurnover = readAnnualTurnover();
        OrganizationType type = readOrganizationType();
        Address address = readAddress();

        return new Organization(name, coordinates, annualTurnover, type, address);
    }
}