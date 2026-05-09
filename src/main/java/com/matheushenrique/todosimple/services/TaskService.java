package com.matheushenrique.todosimple.services;

import com.matheushenrique.todosimple.Security.UserSS;
import com.matheushenrique.todosimple.models.Task;
import com.matheushenrique.todosimple.models.User;
import com.matheushenrique.todosimple.models.enums.ProfileEnum;
import com.matheushenrique.todosimple.models.projection.TaskProjection;
import com.matheushenrique.todosimple.repositories.TaskRepository;
import com.matheushenrique.todosimple.services.exceptions.AuthorizationException;
import com.matheushenrique.todosimple.services.exceptions.DataBindingViolationException;
import com.matheushenrique.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                "Tarefa não encontrada! Id: " + id + ", Tipo: " + Task.class.getName()));

        UserSS userSS = UserService.authenticated();
        if (Objects.isNull(userSS) || !userSS.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSS, task))
            throw new AuthorizationException("Acesso negado!");
        return task;
    }

    public Page<TaskProjection> findAllByUser(Pageable pageable) {
        UserSS userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");
        return this.taskRepository.findByUser_Id(userSpringSecurity.getId(), pageable);
    }

    @Transactional
    public Task create(Task obj, Long userId) {
        UserSS userSS = UserService.authenticated();
        if (Objects.isNull(userSS))
            throw new AuthorizationException("Acesso negado!");

        Long targetUserId = Objects.nonNull(userId) ? userId : userSS.getId();
        if (!userSS.hasRole(ProfileEnum.ADMIN) && !targetUserId.equals(userSS.getId()))
            throw new AuthorizationException("Acesso negado!");

        User user = this.userService.findById(targetUserId);
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj) {
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir devido a dependências existentes!");
        }
    }

    private boolean userHasTask(UserSS userSS, Task task) {
        return task.getUser().getId().equals(userSS.getId());
    }

}