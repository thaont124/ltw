package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import shop.api.models.Role;
import shop.api.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    public Role save(Role role){
        return roleRepository.save(role);
    }

    public boolean existsRoleByRoleName(String roleName){
        return roleRepository.existsRoleByRoleName(roleName);
    }


    public Role findByRoleName(String roleName){
        List<Role> roleAdmin = roleRepository.findByRoleName(roleName);
        return roleAdmin.get(0);
    }
}
