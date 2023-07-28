package com.mavilan.grpc.person.service;

import com.mavilan.grpc.person.Person;
import com.mavilan.grpc.person.Physical;
import org.bson.Document;

public class PersonMapper {

    private PersonMapper(){}

    private static Physical documentToPhysical(Document document){
        return Physical.newBuilder()
                .setHeight(document.getDouble("height"))
                .setWeight(document.getDouble("weight"))
                .build();
    }
    public static Person documentToPerson(Document document){
        return Person.newBuilder()
                .setId(document.getString("id"))
                .setFirstName(document.getString("firstName"))
                .setLastName(document.getString("lastName"))
                .setAge(document.getInteger("age"))
                .setGender(Person.Gender.forNumber(document.getInteger("gender")))
                .setWorker(document.getBoolean("worker"))
                .setPhysical(documentToPhysical(document.get("physical", Document.class)))
                .build();
    }

    private static Document physicalToDocument(Physical physical) {
        return new Document("height", physical.getHeight())
                .append("weight", physical.getWeight());
    }

    public static Document personToDocument(Person person){
        return new Document("id", person.getId())
                .append("firstName", person.getFirstName())
                .append("lastName", person.getLastName())
                .append("age", person.getAge())
                .append("gender", person.getGenderValue())
                .append("worker", person.getWorker())
                .append("physical", physicalToDocument(person.getPhysical()));
    }
}
