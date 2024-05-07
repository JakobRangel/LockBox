package com.lockbox.backend.repositories;

import com.lockbox.backend.models.MetaData;
import com.lockbox.backend.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaDataRepository
        extends CrudRepository<MetaData, String> {
    @Query("SELECT m FROM MetaData m WHERE m.fileName = :fileName")
    MetaData getMetaDataByFileName(String fileName);

    @Query("SELECT m FROM MetaData m WHERE m.uuid = :uuid")
    MetaData getMetaDataByUUID(String uuid);
}