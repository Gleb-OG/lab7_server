package main.model;

import main.utils.IDGenerator;

import java.io.Serializable;
import java.time.LocalDate;
import static java.lang.CharSequence.compare;


/**
 * Класс, представляющий структуру организации.
 */
public class Organization implements Comparable<Organization>, Serializable {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private final LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long annualTurnover; //Значение поля должно быть больше 0
    private OrganizationType type; //Поле может быть null
    private Address officialAddress; //Поле может быть null
    private String username;

    public Organization(String organizationName, Coordinates coordinates,
                        long annualTurnover, OrganizationType type, Address address, String username) {
        this.id = IDGenerator.generateID();
        this.name = organizationName;
        this.creationDate = LocalDate.now();
        this.coordinates = coordinates;
        this.officialAddress = address;
        this.annualTurnover = annualTurnover;
        this.type = type;
        this.username = username;
    }

    // Для считывания из бд (id уже существовавшего объекта не нуждается в пересоздании)
    public Organization(int id, String organizationName, LocalDate creationDate, Coordinates coordinates,
                        long annualTurnover, OrganizationType type, Address address, String username) {
        this.id = id;
        this.name = organizationName;
        this.creationDate = creationDate;
        this.coordinates = coordinates;
        this.officialAddress = address;
        this.annualTurnover = annualTurnover;
        this.type = type;
        this.username = username;
    }

    public Organization() {
        this.id = IDGenerator.generateID();
        this.creationDate = LocalDate.now();
        this.username = null;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public String getName() { return name; }

    public Coordinates getCoordinates() { return coordinates; }

    public long getAnnualTurnover() { return annualTurnover; }

    public main.model.OrganizationType getType() { return type; }

    public Address getOfficialAddress() { return officialAddress; }

    public void setName(String orgName) { this.name = orgName.trim(); }

    public void setAnnualTurnover(long annualTurnover) {
        this.annualTurnover = annualTurnover;
    }

    public void setType(main.model.OrganizationType orgType) {
        this.type = orgType;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setOfficialAddress(Address address) {
        this.officialAddress = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int compareTo(Organization other) {
        return compare(this.name, other.name);
    }

    @Override
    public String toString() {
        return "id = " + id +
                "\nname = " + name +
                "\ncreationDate = " + creationDate +
                "\ncoordinates = " + coordinates +
                "\nannual turnover = " + annualTurnover +
                "\norganization type = " + type +
                "\naddress = " + officialAddress +
                "\nusername = " + username +
                "\n------------------------------";
    }
}