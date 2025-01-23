package com.tpe.security;

import com.tpe.domain.Role;
import com.tpe.domain.User;
import com.tpe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    //1-UserDetailsService->implemente ederek yaptık
    //2-UserDetails->loadUserByUsername
    //3-GrantedAuthority->buildGrantedAuthorities

    //bu metod ile DB deki kendi userımızı bulup securitye UserDetails vereceğiz

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //bizim userimiz : security bilmez
        User user = userRepository.findByUserName(username).
                orElseThrow(()-> new UsernameNotFoundException("User not found by username :" + username));

        return new org.springframework.security.core.userdetails.
                User(user.getUserName(),user.getPassword(),buildGrantedAuthorities(user.getRoles()));

        //kendi userımızın fieldlarını kullanarak UserDetailsi implemente eden
        //securitynin userını oluşturduk
    }

    //userımızın rolleri var-->GrantedAuthority
    private List<SimpleGrantedAuthority> buildGrantedAuthorities(Set<Role> roles) {

        List<SimpleGrantedAuthority> authorities=new ArrayList<>();

        for (Role role:roles){
            authorities.add(new SimpleGrantedAuthority(role.getType().name()));
            //new SimpleGrantedAuthority("ROLE_ADMIN")
        }

        return authorities;

    }

}
