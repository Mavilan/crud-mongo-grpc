package com.mavilan.grpc.person.service;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.mavilan.grpc.person.ManagePersonGrpc;
import com.mavilan.grpc.person.Person;
import com.mavilan.grpc.person.PersonId;
import com.mavilan.grpc.person.PersonList;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mavilan.grpc.person.service.PersonMapper.documentToPerson;
import static com.mavilan.grpc.person.service.PersonMapper.personToDocument;
import static com.mongodb.client.model.Filters.eq;

public class PersonServiceImpl extends ManagePersonGrpc.ManagePersonImplBase {

    MongoCollection<Document> personCollection;

    public PersonServiceImpl(MongoClient mongoClient){
        MongoDatabase personDB = mongoClient.getDatabase("personDB");
        personCollection = personDB.getCollection("personCollection");
    }

    @Override
    public void findOnePerson(PersonId request, StreamObserver<Person> responseObserver) {
        if (request.getId().isEmpty()){
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Id es requerido para la busqueda...")
                    .asRuntimeException());
        }

        Document document = personCollection.find(eq("_id", new ObjectId(request.getId()))).first();
        if (document == null){
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No se encontraron elementos con ese id")
                    .augmentDescription("Id : " + request.getId())
                    .asRuntimeException());
        }

        System.out.println("[IMPL][INF] Objeto obtenido document: " + document);

        responseObserver.onNext(documentToPerson(document));
        responseObserver.onCompleted();
    }

    @Override
    public void findManyPerson(Empty request, StreamObserver<PersonList> responseObserver) {

        FindIterable<Document> documents = personCollection.find();
        if (documents == null){
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No se encontraron elementos en la base")
                    .asRuntimeException());
        }

        System.out.println("[IMPL][INF] Objeto obtenido document: " + documents);
        responseObserver.onNext(PersonList.newBuilder()
                        .addAllPeople(documents.map(document -> documentToPerson(document)))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void insertOnePerson(Person request, StreamObserver<PersonId> responseObserver) {
        if (request.getId().isEmpty()){
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Id es requerido para la busqueda...")
                    .asRuntimeException());
        }

        InsertOneResult insertOneResult = null;
        try {
            insertOneResult = personCollection.insertOne(personToDocument(request));
        } catch (MongoException me) {
            System.out.println("[IMPL][ERR] Ocurrio un error en la comuniación a la base: " + me.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Ocurrio un error en la comuniación a la base")
                    .augmentDescription(me.getMessage())
                    .asRuntimeException());
        }

        if (!insertOneResult.wasAcknowledged() || insertOneResult.getInsertedId() == null){
            responseObserver.onError(Status.INTERNAL
                    .withDescription("No se pudo hacer insert en la base...")
                    .asRuntimeException());
        }

        responseObserver.onNext(PersonId.newBuilder().setId(String.valueOf(insertOneResult.getInsertedId())).build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateOnePerson(Person request, StreamObserver<BoolValue> responseObserver) {
        boolean response = false;

        if (request.getId().isEmpty()){
            onError(responseObserver, "Elemento necesario para la actualizacion...");
        }

        Document document = null;
        try {
            document = personCollection.findOneAndUpdate(eq("_id", new ObjectId(request.getId())), personToDocument(request));
            response = true;
        } catch (MongoException me) {
            manejaException(responseObserver, me);
        }

        if (document.isEmpty()){
            onError(responseObserver, "No se pudo actualizar en la base...");
        }

        responseObserver.onNext(BoolValue.newBuilder()
                .setValue(response)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteOnePerson(Person request, StreamObserver<BoolValue> responseObserver) {
        boolean response = false;

        if (request.getId().isEmpty()){
            onError(responseObserver, "Elemento necesario para borrar...");
        }

        DeleteResult deleteResult = null;
        try {
            deleteResult = personCollection.deleteOne(eq("_id", new ObjectId(request.getId())));
            response = true;
        } catch (MongoException me) {
            manejaException(responseObserver, me);
        }

        if (!deleteResult.wasAcknowledged()){
            onError(responseObserver, "No se pudo actualizar en la base...");
        }

        responseObserver.onNext(BoolValue.newBuilder()
                .setValue(response)
                .build());
        responseObserver.onCompleted();
    }

    private void onError(StreamObserver<BoolValue> responseObserver, String message) {
        responseObserver.onError(Status.INTERNAL
                .withDescription(message)
                .asRuntimeException());
    }


    private void manejaException(StreamObserver<BoolValue> responseObserver, MongoException me) {
        System.out.println("[IMPL][ERR] Ocurrio un error en la comuniación a la base: " + me.getMessage());
        responseObserver.onError(Status.INTERNAL
                .withDescription("Ocurrio un error en la comuniación a la base")
                .augmentDescription(me.getMessage())
                .asRuntimeException());
    }
}
