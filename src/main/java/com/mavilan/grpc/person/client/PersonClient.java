package com.mavilan.grpc.person.client;

import com.mavilan.grpc.person.ManagePersonGrpc;
import com.mavilan.grpc.person.Person;
import com.mavilan.grpc.person.PersonId;
import com.mavilan.grpc.person.PersonResponse;
import com.mavilan.grpc.person.Physical;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static com.mavilan.grpc.person.util.MyConstant.HOSTNAME;
import static com.mavilan.grpc.person.util.MyConstant.PORT;

public class PersonClient {

    public static void main(String[] args) {

        System.out.println("[CLIE][DEB] Creando el canal de comunicación...");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOSTNAME, PORT).usePlaintext().build();

        run(channel);

        System.out.println("[CLIE][INF] Cerrando canal...");
        channel.shutdown();
    }

    private static void run(ManagedChannel channel) {
        ManagePersonGrpc.ManagePersonBlockingStub blockingStub = ManagePersonGrpc.newBlockingStub(channel);

        // acciones para probar todos los rpc's
        PersonId personId = insertPerson(blockingStub);

        Person person = findPerson(blockingStub, personId);

        updatePerson(blockingStub, person);

        deletePerson(blockingStub, personId);
        System.out.println("[CLIE][INF] Termiann acciones de cliente...");
    }

    private static PersonId insertPerson(ManagePersonGrpc.ManagePersonBlockingStub blockingStub) {
        PersonResponse personResponse = PersonResponse.getDefaultInstance();
        try {
            personResponse = blockingStub.insertOnePerson(Person.newBuilder()
                    .setId("10001")
                    .setFirstName("Marco")
                    .setLastName("Avila")
                    .setAge(30)
                    .setGender(Person.Gender.GENDER_MALE)
                    .setWorker(true)
                    .setPhysical(Physical.newBuilder()
                            .setWeght(65.5f)
                            .setHeight(1.74f)
                            .build())
                    .build());
            System.out.println("[CLIE][INF] Cliente creado con id: ".concat(personResponse.getPersonId().getId()));
        } catch (RuntimeException rte) {
            System.out.println("[CLIE][ERR] No se pudo conectar con el servidor: ".concat(rte.getMessage()));
        }

        return personResponse.getPersonId();
    }

    private static Person findPerson(ManagePersonGrpc.ManagePersonBlockingStub blockingStub, PersonId personId) {

        Person onePerson = null;
        try {
            onePerson = blockingStub.findOnePerson(personId);
            System.out.println("[CLIE][INF] Cliente encontrado: ".concat(onePerson.toString()));
        } catch (RuntimeException rte) {
            System.out.println("[CLIE][ERR] No se pudo conectar con el servidor: ".concat(rte.getMessage()));
        }

        return onePerson;
    }
    private static void updatePerson(ManagePersonGrpc.ManagePersonBlockingStub blockingStub, Person person) {
        person = person.toBuilder()
                .setLastName("Perez")
                .setAge(40)
                .build();
        try {
            PersonResponse personResponse = blockingStub.updateOnePerson(person);
            if (personResponse.getValue().getValue()) System.out.println("[CLIE][INF] Cliente actualizado...");
        } catch (RuntimeException rte){
            System.out.println("[CLIE][ERR] No se pudo conectar con el servidor: ".concat(rte.getMessage()));
        }
    }

    private static void deletePerson(ManagePersonGrpc.ManagePersonBlockingStub blockingStub, PersonId personId) {
        try {
            PersonResponse personResponse = blockingStub.deleteOnePerson(personId);
            if (personResponse.getValue().getValue()) System.out.println("[CLIE][INF] Cliente borrado...");
        } catch (RuntimeException rte){
            System.out.println("[CLIE][ERR] No se pudo conectar con el servidor: ".concat(rte.getMessage()));
        }
    }
}
