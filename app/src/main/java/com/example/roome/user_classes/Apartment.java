package com.example.roome.user_classes;

/**
 * A class representing an Apartment. Contains all relevant data for an apartment described by the
 * roommate searcher users
 */
public class Apartment {

    /* apartment related info */
    private Boolean hasMainImage;
    private String neighborhood;
    private int entryDate;
    private int numberOfRoommates;
    private double rent;

    public Apartment(){}

    /**
     * creates a new at object
     * @param hasMainImage does the apt has an image?
     * @param neighborhood the neighborhood of the apt
     * @param entryDate the starting date for entering the apt
     * @param numberOfRoommates number of roommates in total
     * @param rent rent price per month
     */
    public Apartment(Boolean hasMainImage, String neighborhood, int entryDate, int numberOfRoommates, double rent) {
        this.hasMainImage = hasMainImage;
        this.neighborhood = neighborhood;
        this.entryDate = entryDate;
        this.numberOfRoommates = numberOfRoommates;
        this.rent = rent;
    }


    //------------------------------------------Getters---------------------------------------------

    public boolean getHasMainImage() {return hasMainImage;}

    public String getNeighborhood() {
        return neighborhood;
    }


    public int getEntryDate() {
        return entryDate;
    }

    public int getNumberOfRoommates() {
        return numberOfRoommates;
    }


    public double getRent() {
        return rent;
    }


    //------------------------------------------Setters---------------------------------------------


    public void setHasMainImage(Boolean flag) { this.hasMainImage = flag; }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setEntryDate(int entryDate) {
        this.entryDate = entryDate;
    }

    public void setNumberOfRoommates(int numberOfRoommates) {
        this.numberOfRoommates = numberOfRoommates;
    }

    public void setRent(double rent) {
        this.rent = rent;
    }


}
