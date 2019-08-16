package com.danielbyrne.daftsearch.domain;

import lombok.Data;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Data
@Entity
public class Property {

    @Id
    private Long id;
    private String address;
    private String eircode;
    private int beds;
    private int baths;
    private String propertyType;
    private int price;
    private String priceString;
    private String link;

    private Long distanceInMetres;
    private Long duration;

    private String readableDistance;
    private String readableDuration;

    @Lob
    private String description;

    @Lob
    private Byte[] image;

    public void setImage(String imageUrl) throws IOException {

        BufferedImage bImage = ImageIO.read(new File(imageUrl));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", outputStream);
        int i = 0;
        for (byte b : outputStream.toByteArray()){
            image[i++] = b;
        }
    }

    @Override
    public String toString() {
        return "Property{" +
                "address='" + address + '\'' +
                ", propertyType='" + propertyType + '\'' +
                ", price=" + price +
                ", link='" + link + '\'' +
                ", distanceInMetres=" + distanceInMetres +
                ", duration=" + duration +
                '}';
    }
}