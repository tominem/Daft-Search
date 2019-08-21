package com.danielbyrne.daftsearch.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Getter
@Setter
@Document
public class PropertyForSale extends Property {

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