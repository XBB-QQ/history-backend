package com.history.repository;

import com.history.entity.PersonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    Optional<PersonEntity> findByUid(String uid);

    Optional<PersonEntity> findByName(String name);

    Page<PersonEntity> findByGender(String gender, Pageable pageable);

    Page<PersonEntity> findByDynasty_Name(String dynastyName, Pageable pageable);

    Page<PersonEntity> findByRolesContaining(String role, Pageable pageable);

    @Query("SELECT p FROM PersonEntity p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.bio) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.quote) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<PersonEntity> search(@Param("keyword") String keyword, Pageable pageable);
}
