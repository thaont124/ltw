package shop.api.configuration.security.controller;

import shop.api.configuration.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import shop.api.service.UserService;

import javax.servlet.http.HttpServletRequest;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class UserController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired

    private JwtUserDetailsService userDetailsServicea;

    @Autowired
    private UserService userService;
    @RequestMapping(value = "user", method = RequestMethod.GET)

    @Secured({"ROLE_ADMIN"})
    public JwtUser getAuthenticatedUser(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader).substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        return (JwtUser) userDetailsService.loadUserByUsername(username);
    }

//    @RequestMapping(value = "/register", method = RequestMethod.POST)
//    public ResponseEntity<?> saveUser(@RequestBody UserSignupDTO user) throws Exception {
//        return new ResponseEntity<>(userDetailsServicea.save(user),HttpStatus.CREATED);
//    }
//    @Secured({"ROLE_ADMIN", "ROLE_USER"})
//    @GetMapping("/ApiV1/UserByLogin")
//    public ResponseEntity<?> getUserByIdLogin(HttpServletRequest request){
//        String token = request.getHeader(tokenHeader).substring(7);
//        String username = jwtUtil.getUsernameFromToken(token);
//        Long userID = historyService.getUserID(username);
//        User user = userService.findById(userID);
//        UserDTO userDTO = new UserDTO(user);
//        return new ResponseEntity<>(userDTO, HttpStatus.OK);
//    }
//    @Secured({"ROLE_ADMIN", "ROLE_USER"})
//    @PatchMapping("/ApiV1/UserByLogin")
//    public ResponseEntity<?> changeFullName(HttpServletRequest request,@RequestBody UserDTO userDTO){
//        String token = request.getHeader(tokenHeader).substring(7);
//        String username = jwtUtil.getUsernameFromToken(token);
//        Long userID = historyService.getUserID(username);
//        User user = userService.findById(userID);
//        user.setFullname(userDTO.getFullName());
//        User userUpdate = userService.updateUser(user);
//        UserDTO userUpdateDTO = new UserDTO(userUpdate);
//        return new ResponseEntity<>(userUpdateDTO, HttpStatus.OK);
//    }


}