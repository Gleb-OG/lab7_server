package lab.utils;

import lab.model.OrganizationType;
import lab.exceptions.InvalidDataException;

/**
 * Проверяет соответствие строкового ввода соответствующему ему полю класса Organization.
 */
public class Validator {
    public static void validateOrganizationName(String name) throws InvalidDataException  {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Название организации не может быть пустым.");
        }
        if (name.contains(";")) {
            throw new InvalidDataException("В названии организации не может присутствовать символ \";\"");
        }
    }

    public static Double parseXCoordinates(String input) throws InvalidDataException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidDataException("Координата X не может быть пустой.");
        }
        try {
            Double value = Double.parseDouble(input);
            if (value <= -922) {
                throw new InvalidDataException("Число должно быть больше -922.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Координата X должна быть числом.");
        }
    }

    public static long parseYCoordinates(String input) throws InvalidDataException  {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidDataException("Координата Y не может быть пустой.");
        }
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Координата Y должна быть числом.");
        }
    }

    public static float parseXLocation(String input) throws InvalidDataException  {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidDataException("Координата X не может быть пустой.");
        }
        try {
            return Float.parseFloat(input);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Координата X должна быть числом.");
        }
    }

    public static double parseYLocation(String input) throws InvalidDataException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidDataException("Координата Y не может быть пустой.");
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Координата Y должна быть числом.");
        }
    }

    public static Long parseZLocation(String input) throws InvalidDataException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidDataException("Координата Z не может быть пустой.");
        }
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Координата Z должна быть числом.");
        }
    }

    public static long parseAnnualTurnover(String input) throws InvalidDataException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidDataException("Это поле не может быть пустым.");
        }
        try {
            long annualTurnover = Long.parseLong(input);
            if (annualTurnover <= 0) {
                throw new InvalidDataException("Годовой оборот должен быть больше нуля.");
            }
            return annualTurnover;
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Годовой оборот должен быть числом.");
        }
    }

    public static OrganizationType parseOrganizationType(String input) throws InvalidDataException {
        if (input == null || input.trim().isEmpty()) {
           return null;
        }
        try {
            return OrganizationType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("Неизвестный тип организации. Введите тип организации из списка.");
        }
    }

    public static String validateStreetName(String streetName) throws InvalidDataException {
        if (streetName == null || streetName.trim().isEmpty()) {
            throw new InvalidDataException("Название улицы не может быть пустым.");
        }
        if (streetName.length() > 103) {
            throw new InvalidDataException("Длина названия улицы не может превышать 103 символа.");
        }
        if (streetName.contains(";")) {
            throw new InvalidDataException("В названии улицы не может присутствовать символ \";\"");
        }
        return streetName;
    }

    public static int validateInt(String input) throws InvalidDataException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidDataException("id не может быть пустым.");
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new InvalidDataException("id должно быть числом.");
        }
    }
}
