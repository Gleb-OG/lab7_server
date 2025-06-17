package lab.model;

import lab.exceptions.InvalidDataException;
import lab.utils.Validator;


public class Address {
    private String street; //Длина строки не должна быть больше 103, Поле не может быть null
    private lab.model.Location town; //Поле может быть null

    public Address(String inputStreetName, lab.model.Location inputTown) {
        this.street = inputStreetName;
        this.town = inputTown;
    }

    public Address(String inputStreetName) {
        this.street = inputStreetName;
        this.town = null;
    }

    public Address() {
    }

    public String getStreet() {
        return street;
    }

    public lab.model.Location getTown() {
        return town;
    }

    public void setStreet(String inputStreetName) throws InvalidDataException {
        this.street = Validator.validateStreetName(inputStreetName);
    }

    public void setLocation(lab.model.Location location) {
        this.town = location;
    }

    @Override
    public String toString() {
        if (town != null) {
            return " {" +
                    "\n  Street = " + street +
                    "\n  x = " + town.getX() +
                    "\n  y = " + town.getY() +
                    "\n  z = " + town.getZ() + "\n}";

        }
        return " {" +
                "\n  Street = " + street +
                "\n  Town coordinates = null" + "\n}";
    }
}
