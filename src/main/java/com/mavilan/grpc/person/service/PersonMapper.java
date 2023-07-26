package com.mavilan.grpc.person.service;

import com.mavilan.grpc.person.Person;
import com.mavilan.grpc.person.Physical;
import org.bson.Document;
import org.bson.types.ObjectId;

public class PersonMapper {

    private PersonMapper(){}

    public static Person documentToPerson(Document document){

        return Person.newBuilder()
                .setId(document.getObjectId("_id").toString())
                .setFirstName(document.getString("firstName"))
                .setLastName(document.getString("secondName"))
                .setAge(document.getInteger("age"))
                .setGender(Person.Gender.forNumber(document.getInteger("gender")))
                .setWorker(document.getBoolean("worker"))
                .setPhysical(Physical.newBuilder()
                        .setHeight(document.getInteger("physical.height"))
                        .setWeght(document.getInteger("physical.weght"))
                        .build())
                .build();
    }

    public static Document personToDocument(Person person){
        return new Document("_id", new ObjectId(person.getId()))
                .append("firstName", person.getFirstName())
                .append("lastMame", person.getLastName())
                .append("age", person.getAge())
                .append("gender", person.getGenderValue())
                .append("worker", person.getWorker())
                .append("physical", documentToPhysical(person.getPhysical()));
    }

    private static Document documentToPhysical(Physical physical) {
        return new Document("height", physical.getHeight())
                .append("weght", physical.getWeght());
    }
}
