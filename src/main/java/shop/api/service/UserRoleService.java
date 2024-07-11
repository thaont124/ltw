package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import shop.api.models.User;
import shop.api.models.UserRole;
import shop.api.repository.UserRoleRepository;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    public boolean existsUserRoleByRole(Long idRole){
        return userRoleRepository.existsUserRoleByRole(idRole);
    }

    public void save(UserRole adminUser) {
        userRoleRepository.save(adminUser);
    }
}
