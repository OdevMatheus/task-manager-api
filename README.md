# Task Manager API
Nota: Este projeto foi desenvolvido sob o codinome interno TodoSimple.

A TodoSimpleAPI é uma API RESTful para gerenciamento de tarefas desenvolvida com Java 17 e Spring Boot 2.7.2, projeto focado na aplicação de padrões de arquitetura backend e segurança stateless com JWT, tratamento centralizado de erros e documentação interativa com OpenAPI 3 / Swagger UI.

O projeto foi estruturado para seguir padrões utilizados em ambientes produtivos, com separação clara de responsabilidades, persistência com JPA/Hibernate, controle de acesso por perfis e execução isolada por Docker Compose. A proposta é servir como base de estudo para boas práticas de desenvolvimento backend profissional.

## Sumario
- [Stack tecnológica](#stack-tecnológica)
- [Arquitetura e Implementação](#arquitetura-e-implementação)
- [Guia rápido de teste](#guia-rápido-de-teste)
- [Fluxo de autenticacao e autorizacao](#fluxo-de-autenticação-e-autorização)
- [Documentacao da API](#documentação-da-api)
- [Estrutura simplificada do projeto](#estrutura-simplificada-do-projeto)
- [Configuração de ambiente](#configuração-de-ambiente)
- [Endpoints principais para validação](#endpoints-principais-para-validação)
- [Decisões de Implementação](#decisões-de-implementação)
- [Autor](#autor)
- [Contato](#contato)

## Stack tecnológica

| Camada | Tecnologia                  | Finalidade |
| --- |-----------------------------| --- |
| Linguagem | Java 17                     | Base da aplicação, com suporte a recursos modernos da plataforma Java. |
| Framework | Spring Boot 2.7.2           | Estrutura principal da API REST e orquestração do ecossistema Spring. |
| Segurança | Spring Security + JWT       | Autenticação e autorização stateless com validação por token. |
| Persistência | Spring Data JPA + Hibernate | Mapeamento objeto-relacional e acesso ao banco de dados. |
| Banco de dados | MySQL 5.7                   | Persistência relacional em ambiente containerizado. |
| Documentação | OpenAPI 3 / Swagger UI      | Catálogo interativo de endpoints e testes da API. |
| Infraestrutura | Docker + Docker Compose     | Provisionamento consistente do ambiente de execução. |
| Produtividade | Maven + Lombok              | Build, dependências e redução de boilerplate. |

## Arquitetura e Implementação

### Arquitetura em camadas

O código segue uma organização clássica e sustentável em camadas, com responsabilidades bem delimitadas entre `Controller`, `Service`, `Repository` e `DTO`. Essa abordagem facilita manutenção, testabilidade e evolução do domínio sem acoplamento excessivo entre entrada HTTP e entidades persistentes.

### DTOs para proteção e validação de entrada

As operações de cadastro e atualização de usuários utilizam `UserCreateDTO` e `UserUpdateDTO` para desacoplar a API da entidade de domínio. Isso reduz exposição indevida de atributos internos, melhora a consistência da validação e reforça o controle sobre os dados aceitos pela camada de aplicação.

### Segurança e autenticação com JWT

A autenticação e a autorização são implementadas com Spring Security e JWT, utilizando filtros customizados no ciclo de vida da requisição:

- `JWTAuthenticationFilter`: responsável por processar as credenciais e emitir o token após autenticação bem-sucedida.
- `JWTAuthorizationFilter`: responsável por validar o token nas requisições subsequentes e reconstruir o contexto de segurança.

O resultado é uma API stateless, apropriada para integração com clientes web, ferramentas de teste e arquiteturas desacopladas.

### Controle de acesso por perfis

O projeto adota hierarquia de acesso baseada em roles com suporte a `ROLE_ADMIN` e `ROLE_USER`, representadas por `ProfileEnum` com códigos explícitos. Esse desenho favorece rastreabilidade, leitura de domínio e expansão futura de permissões sem perda de clareza.

### Persistência segura de credenciais

As senhas são persistidas com `BCryptPasswordEncoder`, garantindo armazenamento com hash forte e alinhado às recomendações de segurança para aplicações backend.

### Resiliência e tratamento centralizado de exceções

Erros são tratados de forma consistente por meio de `GlobalExceptionHandler`, que padroniza respostas em JSON com `ErrorResponse`. Essa estratégia melhora a previsibilidade da API, simplifica o consumo pelo cliente e reduz divergências entre cenários de validação, autorização e falhas de domínio.

### Otimização de leitura com JPA Projections

Consultas específicas utilizam `TaskProjection` para retornar apenas os campos necessários em determinadas operações, reduzindo tráfego de dados e favorecendo performance em cenários de leitura.

### Paginação de Resultados

Os endpoints de listagem de tarefas utilizam a interface Pageable do Spring Data. Isso permite que grandes volumes de dados sejam processados de forma eficiente, retornando metadados sobre o total de elementos e páginas disponíveis, reduzindo o tráfego de rede e a carga no servidor.

## Guia rápido de teste

### 1. Subir o ambiente

O fluxo recomendado para execução local é via Docker Compose:

```powershell
docker-compose up --build
```

Esse comando provisiona a aplicação e o banco de dados em containers isolados.

### 2. Considerar o arquivo `.env`

As variáveis sensíveis e de ambiente devem ser centralizadas em um arquivo `.env`, o que melhora portabilidade e evita hardcoding de credenciais no repositório.

Exemplo de variáveis esperadas pelo ambiente:

```env
MYSQLDB_USER=root
MYSQLDB_ROOT_PASSWORD=root
MYSQLDB_DATABASE=todosimple
MYSQLDB_LOCAL_PORT=3306
MYSQLDB_DOCKER_PORT=3306

SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080
```

### 3. Abrir a documentação da API

Com os containers ativos, a documentação interativa fica disponível em:

```text
http://localhost:8080/swagger-ui.html
```

### 4. Utilizar as credenciais padrão

O banco é inicializado automaticamente pelo script `src/main/resources/import.sql`, que cria um usuário administrativo padrão.

Credenciais iniciais:

```text
username: admin
password: admin
```

### 5. Fazer login e obter o token

Execute o endpoint de autenticação no Swagger:

```http
POST /user/login
```

Envie o payload:

```json
{
  "username": "admin",
  "password": "admin"
}
```

O token JWT será retornado no cabeçalho `Authorization` da resposta, utilizando o formato `Bearer <token>`.

### 6. Autorizar as próximas requisições no Swagger

No Swagger UI, acesse o botão `Authorize` e informe o valor completo recebido no header `Authorization`, incluindo o prefixo `Bearer `. Após essa etapa, os endpoints protegidos passarão a ser executados com a identidade autenticada.

### 7. Validar os principais fluxos

Fluxo recomendado de exploração inicial:

```text
1. Login com admin/admin
2. Autorizar o Swagger com Bearer Token
3. Consultar endpoints de usuário e tarefa
4. Criar, atualizar e excluir registros autenticado
5. Validar o comportamento de erros e permissões
```

### 8. Para parar os containers e apagar volumes persistidos (banco de dados)
```powershell
docker-compose down -v
```

## Fluxo de autenticação e autorização

1. O cliente envia as credenciais ao endpoint `POST /user/login`.
2. `JWTAuthenticationFilter` intercepta a requisição e valida usuário e senha.
3. Após autenticação bem-sucedida, a aplicação emite um JWT assinado.
4. O token é enviado pelo cliente nas requisições seguintes no header `Authorization`.
5. `JWTAuthorizationFilter` valida a assinatura, a expiração e o contexto do usuário.
6. O Spring Security aplica as regras de acesso conforme a role atribuída ao usuário autenticado.

## Documentação da API

A documentação foi configurada com OpenAPI 3 e Swagger UI, incluindo esquema de segurança `bearerAuth` para facilitar a execução dos endpoints protegidos e a validação da jornada de autenticação.

## Estrutura simplificada do projeto

```text
src/main/java/com/matheushenrique/todosimple
├── configs
├── controllers
├── exceptions
├── models
│   ├── DTOs
│   ├── enums
│   └── projection
├── repositories
├── Security
└── services
```

## Configuração de ambiente

### Variáveis e perfis

O projeto utiliza variáveis de ambiente para desacoplar a configuração da aplicação do código-fonte. Essa prática facilita execução local, pipelines de integração contínua e promoção entre ambientes.

Além disso, há separação de perfis de execução via Spring Profiles, com suporte a ajustes específicos para desenvolvimento e produção.

### Banco de dados e inicialização

O ambiente é preparado com banco MySQL em container e inicialização automática de dados por meio de `import.sql`. Isso permite que a aplicação esteja pronta para teste imediatamente após a subida dos containers.

## Endpoints principais para validação

| Método | Endpoint               | Finalidade |
|--------|------------------------| --- |
| POST   | `/user`                | Criar usuário. |
| POST   | `/user/login`          | Autenticar e obter JWT. |
| GET    | `/user/{id}`           | Consultar usuário autenticado ou autorizado. |
| PUT    | `/user/{id}`           | Atualizar dados do usuário. |
| PATCH  |  `/user/{id}/profiles` |  Promover cargo/perfil (Acesso exclusivo ADMIN). |
| GET    | `/task/{id}`           | Consultar tarefa específica. |
| GET    | `/task/user`           | Listar tarefas do usuário autenticado. |
| POST   | `/task/{userId}`       | Criar tarefa vinculada a um usuário. |
| PUT    | `/task/{id}`           | Atualizar tarefa. |
| DELETE | `/task/{id}`           | Remover tarefa. |
## Decisões de Implementação

- Uso de autenticação e autorização com JWT para garantir um fluxo de segurança stateless em conformidade com padrões de produção.
- Desacoplamento entre a camada de entrada HTTP e o modelo de persistência através de DTOs, visando integridade e segurança dos dados.
- Centralização do tratamento de exceções para fornecer respostas padronizadas e previsíveis ao cliente da API.
- Implementação de controle de acesso baseado em perfis (Roles) para segmentação de permissões administrativas e de usuário comum.
- Documentação automatizada para facilitar a integração, validação e testes dos endpoints.
- Provisionamento via containerização para assegurar a paridade do ambiente entre desenvolvimento e execução.

## Autor

Matheus Henrique de Araujo.

## Contato

[![LinkedIn](https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/matheus-henrique-araujo/)
[![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/OdevMatheus)