package com.amz.wms.repository;

import com.amz.wms.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT coalesce(lpad(cast(max(cast(userid as integer)) + 1 as string ),8,'0'),'00000001') as maxid FROM Users")
    String getMaxId();

    Integer countBy();

    Optional<Users> findByUserid(String userId);

    Optional<Users> findByUsername(String username);

    boolean existsByEmailAndUseridNot(String email, String userid);

    boolean existsByUsernameAndUseridNot(String username, String userid);

}
