package com.agrovet.pos.utils;

public class Constants {
    // Dominios Públicos de Railway
    public static final String DOMAIN_USUARIOS = "api-usuarios-production-bd11.up.railway.app";
    public static final String DOMAIN_INVENTARIO = "api-inventario-production-3c43.up.railway.app";
    public static final String DOMAIN_VENTAS = "api-ventas-production.up.railway.app";
    public static final String DOMAIN_REPORTES = "api-reportes-production.up.railway.app";

    // URLs Base (Usando HTTPS para dominios públicos)
    public static final String URL_USUARIOS = "https://" + DOMAIN_USUARIOS + "/api/";
    public static final String URL_INVENTARIO = "https://" + DOMAIN_INVENTARIO + "/";
    public static final String URL_VENTAS = "https://" + DOMAIN_VENTAS + "/";
    public static final String URL_REPORTES = "https://" + DOMAIN_REPORTES + "/";
}
