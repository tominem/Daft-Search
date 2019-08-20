package com.danielbyrne.daftsearch.domain;

import lombok.Data;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Data
@Entity
public class PropertyForSale extends Property {

    public PropertyForSale() {
        super();
    }

    public void setImage(String imageUrl) throws IOException {

        BufferedImage bImage = ImageIO.read(new File(imageUrl));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", outputStream);
        int i = 0;
        for (byte b : outputStream.toByteArray()){
            getImage()[i++] = b;
        }
    }
}