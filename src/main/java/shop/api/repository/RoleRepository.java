package shop.api.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.api.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.RoleName = :roleName")
    List<Role> findByRoleName(@Param("roleName") String roleName);


    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE r.RoleName = :roleName")
    boolean existsRoleByRoleName(@Param("roleName") String roleName);
}