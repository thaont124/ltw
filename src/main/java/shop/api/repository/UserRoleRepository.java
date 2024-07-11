package shop.api.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.api.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT COUNT(u) > 0 FROM User u " +
            "JOIN UserRole ur ON u.Id = ur.User.Id " +
            "JOIN Role r ON ur.Role.Id = r.Id " +
            "WHERE r.Id = :roleId ")

    boolean existsUserRoleByRole(@Param("roleId") Long roleId);
}