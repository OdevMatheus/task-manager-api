package com.matheushenrique.todosimple.controllers;

import com.matheushenrique.todosimple.models.DTOs.LoginRequestDTO;
import com.matheushenrique.todosimple.models.DTOs.UserCreateDTO;
import com.matheushenrique.todosimple.models.DTOs.UserUpdateDTO;
import com.matheushenrique.todosimple.models.User;
import com.matheushenrique.todosimple.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;

@Tag(name = "Usuários", description = "Gerenciamento de usuários e autenticação")
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Token inválido ou permissão insuficiente)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User obj = this.userService.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @Operation(summary = "Criar novo usuário", description = "Endpoint público para cadastro de novos usuários no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (ex: username já existente ou senha curta)")
    })
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserCreateDTO obj) {
        User user = this.userService.fromDTO(obj);
        User newUser = this.userService.create(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Atualizar usuário", description = "Permite que o usuário autenticado altere suas informações (como a senha).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody UserUpdateDTO obj, @PathVariable Long id) {
        obj.setId(id);
        User user = this.userService.fromDTO(obj);
        this.userService.update(user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Excluir usuário", description = "Remove um usuário (Não pode ter tarefas registradas no sistema).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Você não pode excluir outros usuários)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.userService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Realizar Login", description = "Endpoint para autenticação. Retorna o Token JWT no Header 'Authorization'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso. O token está no Header da resposta."),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas (usuário ou senha incorretos)")
    })
    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDTO loginRequest) {
    }

    @Operation(summary = "Promover usuário", description = "Endpoint para conceder novos perfis de acesso a um usuário.")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/{id}/profiles")
    public ResponseEntity<Void> updateProfiles(@PathVariable Long id, @RequestBody Set<Integer> profileCodes) {
        this.userService.updateProfiles(id, profileCodes);
        return ResponseEntity.noContent().build();
    }

}
