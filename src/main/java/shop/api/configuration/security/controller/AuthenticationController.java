package shop.api.configuration.security.controller;

import org.springframework.security.core.AuthenticationException;
import shop.api.configuration.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import shop.api.exception.InvalidInputException;
import shop.api.exception.NotFoundException;
import shop.api.models.User;

import javax.servlet.http.HttpServletRequest;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class AuthenticationController {

    @Value("${jwt.header}")
    private String tokenHeader;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    @Autowired

    private JwtUserDetailsService userDetailsServicea;


    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                                    @Qualifier("jwtUserDetailsService") UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value = "/Api/v1.0/Login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            final String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticationResponse(token));
        }catch (AuthenticationException e){
            throw  new InvalidInputException("username/password is incorrect");
        }

    }
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PatchMapping("/ApiV1/ChangePassword")

    public ResponseEntity<?> ChangePassword(HttpServletRequest request,@RequestBody UserChangePassword userChangePassword) {
        String token = request.getHeader(tokenHeader).substring(7);
        String username = jwtUtil.getUsernameFromToken(token);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, userChangePassword.getPassword()));
        User userPatchPassword=userDetailsServicea.ChangePassword(username,userChangePassword);
        return ResponseEntity.ok(userPatchPassword);
    }


}
