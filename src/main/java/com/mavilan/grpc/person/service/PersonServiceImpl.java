package com.mavilan.grpc.person.service;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.mavilan.grpc.person.ManagePersonGrpc;
import com.mavilan.grpc.person.Person;
import com.mavilan.grpc.person.PersonId;
import com.mavilan.grpc.person.PersonList;
import com.mavilan.grpc.person.PersonResponse;
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
import static com.mavilan.grpc.person.util.ErrorManage.onError;
import static com.mavilan.grpc.person.util.MyConstant.ELEM_NEED;
import static com.mavilan.grpc.person.util.MyConstant.ERROR_BASE;
import static com.mavilan.grpc.person.util.MyConstant.ID_NEED;
import static com.mavilan.grpc.person.util.MyConstant.IMPL_ERROR_BASE;
import static com.mavilan.grpc.person.util.MyConstant.NO_DELETE;
import static com.mavilan.grpc.person.util.MyConstant.NO_ELEM;
import static com.mavilan.grpc.person.util.MyConstant.NO_ELEM_ID;
import static com.mavilan.grpc.person.util.MyConstant.NO_INSERT;
import static com.mavilan.grpc.person.util.MyConstant.NO_UPDATE;
import static com.mongodb.client.model.Filters.eq;

public class PersonServiceImpl extends ManagePersonGrpc.ManagePersonImplBase {

    MongoCollection<Document> personCollection;

    public PersonServiceImpl(MongoClient mongoClient){
        MongoDatabase personDB = mongoClient.getDatabase("personDB");
        personCollection = personDB.getCollection("personCollection");
    }

    @Override
    public void findOnePerson(PersonId personId, StreamObserver<Person> responseObserver) {
        if (personId.getId().isEmpty()) onError(responseObserver, Status.INVALID_ARGUMENT, ID_NEED);

        Document person = personCollection.find(eq("_id", new ObjectId(personId.getId()))).first();
        if (person == null) onError(responseObserver, Status.NOT_FOUND, NO_ELEM_ID, "Id: ".concat(personId.getId()));

        System.out.println("[IMPL][INF] Persona obtenida: " + person);
        responseObserver.onNext(documentToPerson(person));
        responseObserver.onCompleted();
    }

    @Override
    public void findManyPerson(Empty empty, StreamObserver<PersonList> responseObserver) {
        FindIterable<Document> documents = personCollection.find();
        if (documents.first() == null)  onError(responseObserver, Status.NOT_FOUND, NO_ELEM);

        System.out.println("[IMPL][INF] Personas obtenidas: " + documents);
        responseObserver.onNext(PersonList.newBuilder()
                        .addAllPeople(documents.map(PersonMapper::documentToPerson))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void insertOnePerson(Person person, StreamObserver<PersonResponse> responseObserver) {
        if (person.getId().isEmpty())  onError(responseObserver, Status.INVALID_ARGUMENT, ID_NEED);

        InsertOneResult insertOneResult = null;
        try {
            insertOneResult = personCollection.insertOne(personToDocument(person));
        } catch (MongoException me) {
            System.out.println(IMPL_ERROR_BASE + me.getMessage());
            onError(responseObserver, Status.INTERNAL, ERROR_BASE, me.getMessage());
        }

        if (!insertOneResult.wasAcknowledged() || insertOneResult.getInsertedId() == null){
            onError(responseObserver, Status.INTERNAL, NO_INSERT);
        }

        responseObserver.onNext(PersonResponse.newBuilder()
                .setPersonId(PersonId.newBuilder()
                        .setId(insertOneResult.getInsertedId().asObjectId().getValue().toString())
                        .build())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateOnePerson(Person person, StreamObserver<PersonResponse> responseObserver) {
        boolean response = false;
        if (person.getId().isEmpty()) onError(responseObserver, Status.INVALID_ARGUMENT, ELEM_NEED);

        Document document = null;
        try {
            document = personCollection.findOneAndUpdate(eq("_id", new ObjectId(person.getId())), personToDocument(person));
            response = true;
        } catch (MongoException me) {
            System.out.println(IMPL_ERROR_BASE + me.getMessage());
            onError(responseObserver, Status.INTERNAL, ERROR_BASE, me.getMessage());
        }

        if (document.isEmpty()) onError(responseObserver, Status.FAILED_PRECONDITION, NO_UPDATE);

        responseObserver.onNext(PersonResponse.newBuilder()
                .setValue(BoolValue.newBuilder()
                        .setValue(response)
                        .build())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteOnePerson(PersonId personId, StreamObserver<PersonResponse> responseObserver) {
        boolean response = false;
        if (personId.getId().isEmpty()) onError(responseObserver, Status.INVALID_ARGUMENT, ID_NEED);

        DeleteResult deleteResult = null;
        try {
            deleteResult = personCollection.deleteOne(eq("_id", new ObjectId(personId.getId())));
            response = true;
        } catch (MongoException me) {
            System.out.println(IMPL_ERROR_BASE + me.getMessage());
            onError(responseObserver, Status.INTERNAL, ERROR_BASE, me.getMessage());
        }

        if (!deleteResult.wasAcknowledged()) onError(responseObserver, Status.FAILED_PRECONDITION, NO_DELETE);

        responseObserver.onNext(PersonResponse.newBuilder()
                .setValue(BoolValue.newBuilder()
                        .setValue(response)
                        .build())
                .build());
        responseObserver.onCompleted();
    }
}
