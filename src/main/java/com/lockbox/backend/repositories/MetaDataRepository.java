package com.lockbox.backend.repositories;

import com.lockbox.backend.models.MetaData;
import com.lockbox.backend.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaDataRepository
        extends CrudRepository<MetaData, String> {
}