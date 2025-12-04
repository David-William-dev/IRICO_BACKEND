package com.irico.backend.user;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {
    Optional<UserModel> findByUserName(String userName);

    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);

}
