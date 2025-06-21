package in.indupriya.authify.repository;

import in.indupriya.authify.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//UserEntity: Your custom class that maps to the users table in the database.
//JpaRepository: A built-in Spring Data interface that gives you ready-made methods to work with databases \
//(like save, findAll, deleteById, etc.)
//Optional: Used to handle cases when a user might not exist (prevents null pointer exceptions).

//extends JpaRepository, meaning:
//It inherits a bunch of built-in methods like:
//save(), findById(), findAll(), deleteById() etc.

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Boolean existsByEmail(String email);
//    Optional<UserEntity> findByUserId(String email);
}
