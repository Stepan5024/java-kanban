package service;

public interface GenerateIdService<T> {

    T generateId();

    Long getId();
}