package com.mavilan.grpc.person.util;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class ErrorManage {

    private ErrorManage(){}

    public static void onError(StreamObserver streamObserver, Status status, String message){
        streamObserver.onError(status
                .withDescription(message)
                .asRuntimeException());
    }

    public static void onError(StreamObserver streamObserver, Status status, String message, String exMessage){
        streamObserver.onError(status
                .withDescription(message)
                .augmentDescription(exMessage)
                .asRuntimeException());
    }
}
