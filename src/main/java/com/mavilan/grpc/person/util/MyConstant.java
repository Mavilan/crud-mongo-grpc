package com.mavilan.grpc.person.util;

public class MyConstant {

    private MyConstant(){}

    public static final int PORT = 50050;
    public static final String HOSTNAME = "localhost";
    public static final String ERROR_BASE = "Ocurrio un error en la comuniación a la base...";
    public static final String IMPL_ERROR_BASE = "[IMPL][ERR] Ocurrio un error en la comuniación a la base: ";
    public static final String NO_ELEM_ID = "No se encontraron elementos con ese id...";
    public static final String ID_NEED = "Id es requerido para la accion...";
    public static final String NO_ELEM = "No se encontraron elementos en la base...";
    public static final String NO_INSERT = "No se pudo hacer insert en la base...";
    public static final String ELEM_NEED = "Elemento necesario para la actualizacion...";
    public static final String NO_UPDATE = "No se pudo actualizar en la base...";
    public static final String NO_DELETE = "No se pudo borrar en la base...";
}
