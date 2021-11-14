package com.java.lambda;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ServiceHandler implements RequestHandler<S3Event, Object> {
    public Object handleRequest(S3Event input, Context context){
    AmazonS3Client s3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
        S3EventNotification.S3EventNotificationRecord record = input.getRecords().get(0);
        String bucket = record.getS3().getBucket().getName();
        String bucketTo = "resized";
        System.out.println("BUCKET: " + bucket);
        String key = record.getS3().getObject().getKey();
        System.out.println("KEY: " + key);
        S3Object object = s3Client.getObject(new GetObjectRequest(bucket, key));
        InputStream objectData = object.getObjectContent();
        System.out.println("OBJECT: " + objectData.toString());

        BufferedImage bf = null;

        try {
            bf = ImageIO.read(objectData);
            System.out.println("WCZYTAl OBRAZEK");
        } catch (IOException e) {
           System.out.println("NIE CZYTA OBRAZKA!!!!");
        }

        BufferedImage newBF = null;

        try {
            newBF = Thumbnails.of(bf).size(50, 50).asBufferedImage();
            System.out.println("Nowy obrazek");
        } catch (IOException e) {
            System.out.println("nie zrobi£ nowego obrazka");
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(newBF, "jpeg", os);
            System.out.println("zrobi£ nowy input stream");
        } catch (IOException e) {
            System.out.println("przerobka na inputstream sie zesra£a");
        }
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        s3Client.putObject(new PutObjectRequest(bucketTo, key, is, new ObjectMetadata()));
        System.out.println("koniec kurwa");

        return 1;
    }
}
