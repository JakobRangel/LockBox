package com.lockbox.backend.repositories;

import com.lockbox.backend.models.MetaData;
import com.lockbox.backend.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaDataRepository
        extends CrudRepository<MetaData, String> {
    MetaData findByFileName(String fileName);
    MetaData findByUuid(String uuid);
    List<MetaData> findByFileNameContaining(String name);

}