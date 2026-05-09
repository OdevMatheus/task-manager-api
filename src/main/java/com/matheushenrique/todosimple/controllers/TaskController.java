package com.matheushenrique.todosimple.controllers;

import com.matheushenrique.todosimple.models.DTOs.TaskCreateDTO;
import com.matheushenrique.todosimple.models.Task;
import com.matheushenrique.todosimple.models.User;
import com.matheushenrique.todosimple.models.projection.TaskProjection;
import com.matheushenrique.todosimple.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Tag(name = "Tarefas", description = "Operações relacionadas às tarefas dos usuários")
@RestController
@RequestMapping("/task")
@Validated
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Operation(summary = "Buscar tarefa por ID", description = "Retorna uma tarefa específica baseada no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        Task obj = this.taskService.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @Operation(summary = "Listar tarefas do usuário", description = "Retorna todas as tarefas vinculadas ao usuário autenticado via Token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarefas recuperada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token ausente ou inválido")
    })

    @GetMapping("/user")
    public ResponseEntity<Page<TaskProjection>> findAllByUser(Pageable pageable) {
        Page<TaskProjection> objs = this.taskService.findAllByUser(pageable);
        return ResponseEntity.ok().body(objs);
    }

    @Operation(summary = "Criar nova tarefa", description = "Cria uma tarefa e a vincula a um usuário específico através do ID na URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping("/{userId}")
    @Validated
    public ResponseEntity<Void> create(@Valid @RequestBody TaskCreateDTO objDto, @PathVariable Long userId) {
        Task obj = new Task();
        obj.setDescription(objDto.getDescription());

        Task saved = this.taskService.create(obj, userId);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/task/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Atualizar tarefa", description = "Atualiza a descrição de uma tarefa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "403", description = "Você não tem permissão para alterar esta tarefa")
    })
    @PutMapping("/{id}")
    @Validated
    public ResponseEntity<Void> update(@Valid @RequestBody TaskCreateDTO objDto, @PathVariable Long id) {
        Task obj = new Task();
        obj.setId(id);
        obj.setDescription(objDto.getDescription());

        this.taskService.update(obj);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Excluir tarefa", description = "Remove permanentemente uma tarefa do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
