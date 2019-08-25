package pl.coderstrust.database;

import pl.coderstrust.exception.DatabaseOperationException;
import pl.coderstrust.model.Invoice;

import java.util.Collection;
import java.util.Optional;

public interface Database {

    Invoice save(Invoice invoice) throws DatabaseOperationException;

    void delete(Long id) throws DatabaseOperationException;

    Optional<Invoice> getById(Long id) throws DatabaseOperationException;

    Optional<Invoice> getByNumber(String number) throws DatabaseOperationException;

    Collection<Invoice> getAll() throws DatabaseOperationException;

    void deleteAll() throws DatabaseOperationException;

    boolean exists(Long id) throws DatabaseOperationException;

    long count() throws DatabaseOperationException;
}
