package com.mavilan.grpc.person.server;

import com.mavilan.grpc.person.service.PersonServiceImpl;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class MiGrpcServer {

    private static final int PORT = 50050;

    public static void main(String[] args) {

        System.out.println("[SERV][DEB] Iniciando el cliente de mongo...");
        MongoClient mongoClient = MongoClients.create("mongodb://root:root@localhost:27017/");

        System.out.println("[SERV][DEB] Inicio del servidor grpc...");
        Server server = ServerBuilder
                .forPort(PORT)
                .addService(new PersonServiceImpl(mongoClient))
                .build();
        try {
            server.start();
            System.out.println("[SERV][INF] Servidor iniciado y escuchando en el puerto: " + PORT);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[SERV][WAR] Apagando el servidor...");
                server.shutdown();
                System.out.println("[SERV][WAR] Servidor apagado, ADIOOOooossss!!!!");
            }));

            server.awaitTermination();

        } catch (IOException e) {
            System.out.println("[SERV][ERR] No se pudo iniciar el servidor por: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("[SERV][ERR] No se puede mantener encendido el servidor por: " + e.getMessage());
        }
    }
}
