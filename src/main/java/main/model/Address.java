package main.model;

import main.exceptions.InvalidDataException;
import main.utils.Validator;

import java.io.Serializable;


public class Address implements Serializable {
    private String street; //Длина строки не должна быть больше 103, Поле не может быть null
    private Location town; //Поле может быть null

    public Address(String inputStreetName, Location inputTown) {
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

    public main.model.Location getTown() {
        return town;
    }

    public void setStreet(String inputStreetName) throws InvalidDataException {
        this.street = Validator.validateStreetName(inputStreetName);
    }

    public void setLocation(main.model.Location location) {
        this.town = location;
    }

    @Override
    public String toString() {
        if (town != null) {
            return " {" +
                    "\n  street = " + street +
                    "\n  x = " + town.getX() +
                    "\n  y = " + town.getY() +
                    "\n  z = " + town.getZ() + "\n}";

        }
        return " {" +
                "\n  street = " + street +
                "\n  town coordinates = null" + "\n}";
    }
}
