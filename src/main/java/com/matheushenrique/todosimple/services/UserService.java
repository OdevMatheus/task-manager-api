package com.matheushenrique.todosimple.services;

import com.matheushenrique.todosimple.Security.UserSS;
import com.matheushenrique.todosimple.models.DTOs.UserCreateDTO;
import com.matheushenrique.todosimple.models.DTOs.UserUpdateDTO;
import com.matheushenrique.todosimple.models.User;
import com.matheushenrique.todosimple.models.enums.ProfileEnum;
import com.matheushenrique.todosimple.repositories.TaskRepository;
import com.matheushenrique.todosimple.repositories.UserRepository;
import com.matheushenrique.todosimple.services.exceptions.AuthorizationException;
import com.matheushenrique.todosimple.services.exceptions.DataBindingViolationException;
import com.matheushenrique.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    public User findById(Long id) {
        UserSS userSS = authenticated();
        if (Objects.nonNull(userSS) && !userSS.hasRole(ProfileEnum.ADMIN) && !id.equals(userSS.getId()))
            throw new AuthorizationException("Acesso negado!");


        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
                "Usuário não foi encontrado! Id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    @Transactional
    public User create(User obj) {
        obj.setId(null);
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj) {
        User newObj = findById(obj.getId());
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        return this.userRepository.save(newObj);
    }

    public void delete(Long id) {
        UserSS userSpringSecurity = authenticated();
        if (Objects.nonNull(userSpringSecurity) && id.equals(userSpringSecurity.getId())) {
            throw new DataBindingViolationException("Não é possível excluir o próprio usuário autenticado!");
        }

        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas!");
        }
    }

    public static UserSS authenticated() {
        try {
            return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public void updateProfiles(Long id, java.util.Set<Integer> profileCodes) {
        User user = findById(id);
        user.setProfiles(profileCodes);
        this.userRepository.save(user);
    }

    public User fromDTO(@Valid UserCreateDTO obj) {
        User user = new User();
        user.setUsername(obj.getUsername());
        user.setPassword(obj.getPassword());
        return user;
    }

    public User fromDTO(@Valid UserUpdateDTO obj) {
        User user = new User();
        user.setId(obj.getId());
        user.setPassword(obj.getPassword());
        return user;
    }

}