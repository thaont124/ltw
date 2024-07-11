package shop.api.configuration.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shop.api.models.User;
import shop.api.service.UserService;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private UserService userService;
    @Autowired
    private PasswordEncoder bcryptEncoder;

    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

//    public User save(UserSignupDTO user) {
//        User newUser = new User();
//        newUser.setUsername(user.getUsername());
//        newUser.setFullname(user.getFullName());
//        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
//        return userService.save(newUser);
//    }
    public shop.api.models.User ChangePassword(String username, UserChangePassword user) {

        user.setPasswordNew(bcryptEncoder.encode(user.getPasswordNew()));
        return userService.ChangePassword(username,user);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return JwtUserFactory.create(user);
        }
    }
}
