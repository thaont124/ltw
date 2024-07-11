package shop;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import shop.api.models.Role;
import shop.api.models.User;
import shop.api.models.UserRole;
import shop.api.repository.RoleRepository;
import shop.api.repository.UserRepository;
import shop.api.repository.UserRoleRepository;
import shop.api.service.RoleService;
import shop.api.service.UserRoleService;
import shop.api.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@Configuration
public class BootifulApplication implements CommandLineRunner{

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserRoleService userRoleService;

	public static void main(String[] args) {
		try {
			SpringApplication.run(BootifulApplication.class, args);

		} catch (Exception e) {
			System.out.println("error setting up service, shutting down ...");
		}
	}

	@Override
	public void run(String... args) throws Exception {
		createAdminAccountIfNotExists();
	}
	private void createAdminAccountIfNotExists() {

		//thêm nếu chưa có 2 loại role
		if (!roleService.existsRoleByRoleName("ROLE_ADMIN")){
			Role adminRole = new Role();
			adminRole.setRoleName("ROLE_ADMIN");
			roleService.save(adminRole);
		}

		if (!roleService.existsRoleByRoleName("ROLE_USER")){
			Role userRole = new Role();
			userRole.setRoleName("ROLE_USER");
			roleService.save(userRole);
		}


		//kiểm tra xem có tài khoản nào là admin chưa

		Role roleIsAdmin = roleService.findByRoleName("ROLE_ADMIN");
		if (!userRoleService.existsUserRoleByRole(roleIsAdmin.getId())) {

			// Create the admin user
			User user = new User();
			user.setUsername("admin");
			user.setPassword("$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi");
			user.setFullname("Admin");
			userService.save(user);

			// Assign the admin role to the admin user
			UserRole userWithRole = new UserRole();
			userWithRole.setRole(roleIsAdmin);
			userWithRole.setUser(user);
			userRoleService.save(userWithRole);
		}
	}

}
