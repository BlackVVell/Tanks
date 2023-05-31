package it_school.sumdu.edu.ua.tanks;

import java.io.Serializable;

public class Tank implements Serializable {
    private int id;
    private String name;
    private String description;
    private String nation;
    private int tier;
    private String image;
    private String type;
    private boolean isLiked;

    public Tank(int id, String name, String description, String nation, int tier, String image, String type, boolean isLiked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.nation = nation;
        this.tier = tier;
        this.image = image;
        this.type = type;
        this.isLiked = isLiked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        this.isLiked = liked;
    }
}
