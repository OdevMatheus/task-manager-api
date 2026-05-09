package com.matheushenrique.todosimple.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .version("v1")

                        .description("### (Codinome: TodoSimple)\n\n" +
                                "Ecossistema RESTful para gestão de tarefas desenvolvido com **Java 17** e **Spring Boot 2.7**. " +
                                "Esta documentação detalha os contratos da API e fornece uma interface interativa para testes de integração.\n\n" +

                                "#### 1. Guia de Autenticação e Teste\n" +
                                "Para validar os endpoints protegidos, siga o fluxo abaixo:\n" +
                                "1. **Autenticação**: Utilize o endpoint `POST /user/login` com as credenciais padrão: `admin` / `admin`.\n" +
                                "2. **Obtenção do Token**: O sistema retornará um JWT no cabeçalho `Authorization` da resposta.\n" +
                                "3. **Autorização**: Clique no botão **Authorize** no topo desta página e insira o valor no formato: `SEU_TOKEN_AQUI` Sem copiar o Bearer.\n" +
                                "4. **Execução**: Com o token configurado, todos os endpoints protegidos por perfis de acesso estarão liberados para teste.\n\n" +

                                "#### 2. Hierarquia de Perfis (Roles)\n" +
                                "O sistema gerencia permissões baseadas em IDs de perfis:\n" +
                                "* **ID 1 (ADMIN)**: Acesso total ao sistema, incluindo a promoção de usuários e exclusão de tarefas.\n" +
                                "* **ID 2 (USER)**: Acesso restrito às próprias tarefas e atualização de informações pessoais.\n\n" +

                                "#### 3. Notas de Implementação\n" +
                                "* **Segurança**: Senhas são protegidas com BCrypt e a comunicação é stateless via JWT.\n" +
                                "* **Promoção de Cargo**: O perfil de um usuário só pode ser alterado via endpoint dedicado `PATCH /user/{id}/profiles`, restrito a administradores.\n" +
                                "* **Resiliência**: Erros globais são padronizados para facilitar o consumo."))

                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}