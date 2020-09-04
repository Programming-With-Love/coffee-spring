package site.zido.coffee.autoconfigure.security.rest;

public enum SecureStoreType {
    // spring security自带的cookie
    COOKIE,
    //JWT token 标准
    JWT,
    //TODO 传统token支持
    TOKEN
}
