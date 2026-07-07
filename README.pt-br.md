# Task Manager API (TodoSimple)
🇺🇸 **English Version:** [README.md](README.md)

Uma API RESTful pronta para o mercado para gerenciamento de tarefas, desenvolvida com **Java 17** e **Spring Boot 2.7**. Este projeto foca na aplicação de arquitetura backend de nível de produção, segurança stateless com JWT, tratamento centralizado de exceções e documentação interativa com OpenAPI.

<div align="center">

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Matheus%20Henrique-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/matheus-henrique-araujo)
[![GitHub](https://img.shields.io/badge/GitHub-OdevMatheus-121011?style=for-the-badge&logo=github&logoColor=white)](https://github.com/OdevMatheus)

</div>

---

## O que é isto?

Este repositório contém uma API de Gerenciamento de Tarefas desenvolvida como estudo de referência para o desenvolvimento de backend profissional em Java. Ele implementa uma arquitetura em camadas, validação rigorosa de dados de entrada e segurança baseada em perfis de acesso, garantindo um ecossistema seguro e containerizado pronto para uso.

---

## Stack Tecnológica

| Camada | Tecnologia | Finalidade |
| :--- | :--- | :--- |
| **Linguagem** | Java 17 | Linguagem principal aproveitando os recursos modernos da sintaxe Java. |
| **Framework** | Spring Boot 2.7.2 | Orquestrador base para os endpoints REST, injeção de dependências e segurança. |
| **Segurança** | Spring Security + JWT | Autenticação stateless e autorização baseada em roles com tokens criptograficamente assinados. |
| **Banco de Dados** | MySQL 5.7 | Persistência relacional em instâncias containerizadas. |
| **Persistência** | Spring Data JPA + Hibernate | Mapeamento Objeto-Relacional (ORM) e padrões limpos de repositório. |
| **Documentação** | OpenAPI 3 / Swagger UI | Explorador interativo da API, playground de testes e especificações de contrato. |
| **Infraestrutura** | Docker / Podman | Replicação consistente do ambiente em produção via Compose. |
| **Build Tool** | Maven | Gerenciador de dependências, controle de ciclos de vida do projeto e automação de build. |

---

## Arquitetura & Decisões de Design

- **Arquitetura em Camadas:** Segue uma separação estrita de responsabilidades através de `Controllers` (camada de apresentação), `Services` (lógica de negócios), `Repositories` (acesso aos dados) e `DTOs` (transferência de dados).
- **Objetos de Transferência de Dados (DTOs):** Emprega DTOs explícitos (`UserCreateDTO`, `UserUpdateDTO`, `TaskCreateDTO`) para isolar os modelos de persistência dos endpoints HTTP. Isso impede ataques de sobre-postagem (over-posting) e melhora a segurança.
- **Credenciais Seguras:** As senhas são salgadas (salted) e criptografadas usando o algoritmo `BCryptPasswordEncoder` antes de serem armazenadas no banco de dados.
- **Tratamento de Exceções Centralizado:** Todas as exceções da aplicação são interceptadas e mapeadas para esquemas JSON padronizados com `ErrorResponse` usando um `GlobalExceptionHandler`.
- **Paginação & Otimização de Leituras:** O endpoint de listagem de tarefas utiliza a paginação nativa do Spring (`Pageable`) e projeções personalizadas do JPA (`TaskProjection`) para consultas rápidas e eficientes em memória.

---

## Como Rodar o Projeto

### 📋 Pré-requisitos

Antes de iniciar, certifique-se de que você possui as seguintes ferramentas instaladas:
* **Java 17 JDK** e **Maven** (se for rodar o projeto localmente na sua máquina fora de containers)
* Um motor de container: **Docker** (com Docker Compose) OU **Podman** (com `podman-compose` ou provedor compose padrão)

---

### 🚀 Inicialização Rápida (Ambiente Containerizado)

Oferecemos uma orquestração de containers simplificada para subir toda a infraestrutura do projeto com poucos comandos:

#### 1. Configurar o Arquivo de Variáveis de Ambiente (.env)
O ambiente em container depende de variáveis centralizadas no arquivo `.env`. Você **DEVE** clonar o arquivo de exemplo antes de iniciar os serviços:

* **Linux / macOS:**
  ```bash
  cp .env.example .env
  ```
* **Windows (Prompt de Comando):**
  ```cmd
  copy .env.example .env
  ```
* **Windows (PowerShell):**
  ```powershell
  Copy-Item .env.example .env
  ```

> 💡 **Evitando Conflito de Portas:** Por padrão, a aplicação roda na porta `8080` e o MySQL na porta `3306`. Se alguma dessas portas já estiver em uso no seu sistema operacional, abra o arquivo `.env` gerado e mude os valores de `SPRING_LOCAL_PORT` (ex: para `8081`) e `MYSQLDB_LOCAL_PORT` (ex: para `3307`).

#### 2. Subir os Serviços
Execute o comando correspondente ao seu gerenciador de containers:

* **Usando Docker:**
  ```bash
  docker compose up --build
  ```
* **Usando Podman (Totalmente Compatível!):**
  ```bash
  podman compose up --build
  ```

> ⚠️ **Nota Importante sobre o Healthcheck do Banco:** O container da aplicação possui dependência de saúde (`depends_on.mysqldb.condition: service_healthy`) configurada no compose. Isso significa que o servidor Tomcat do Spring aguardará o MySQL inicializar completamente e estar saudável antes de iniciar, evitando falhas prematuras de conexão com o banco de dados.

#### 3. Derrubando o Ambiente
Para parar a execução e remover os containers e volumes persistentes de dados:
```bash
# Docker
docker compose down -v

# Podman
podman compose down -v
```

---

## Testando e Fluxo de Autenticação

### 1. Conta Padrão Pré-Criada
Ao iniciar o banco de dados, os scripts em `schema.sql` e `data.sql` populam automaticamente uma conta administrativa padrão:
* **Username:** `admin`
* **Password:** `admin`

### 2. Documentação Interativa do Swagger UI
Com o projeto rodando, abra o seu navegador de preferência e acesse:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### 3. Passo a Passo de Login e Autenticação
A segurança da API é stateless baseada em token JWT. Para acessar os endpoints protegidos, faça o seguinte:

1. **Gere o Token JWT:** Envie uma chamada de login no Swagger UI ou use o comando `curl` no terminal:
   ```bash
   curl -i -X POST http://localhost:8080/user/login \
     -H "Content-Type: application/json" \
     -d '{"username": "admin", "password": "admin"}'
   ```
2. **Copie o Token:** Na resposta, localize o cabeçalho `Authorization` e copie a string de token retornada (sem incluir a palavra `Bearer `).
   * Exemplo de resposta: `Authorization: Bearer <seu-token-jwt-gerado>`
3. **Autorize no Swagger:**
   - No canto superior direito da página do Swagger UI, clique no botão verde **Authorize**.
   - Cole o token copiado diretamente no campo de texto.
   - Clique em **Authorize** e depois em **Close**.
4. **Interaja:** Pronto! Agora você pode criar, ler, atualizar e deletar usuários ou tarefas de forma autenticada.

---

## Estrutura do Projeto

```text
src/main/java/com/matheushenrique/todosimple
├── configs/       # Configurações globais (Segurança, CORS, OpenAPI/Swagger)
├── controllers/   # Controllers REST (Exposição de rotas de usuários e tarefas)
├── exceptions/    # Manipulador global de erros e padronização de respostas
├── models/        # Entidades de banco, DTOs, projeções e enums
│   ├── DTOs/      # Classes DTO de validação (UserCreate, UserUpdate, TaskCreate)
│   ├── enums/     # ProfileEnum (Níveis de privilégio ADMIN e USER)
│   └── projection/# TaskProjection para otimização de consultas JPA
├── repositories/  # Repositórios JPA para interação direta com o banco de dados
├── Security/      # Filtros de autenticação, autorização e utilitários JWT
└── services/      # Camada de lógica de negócio e exceções personalizadas
```

---

## Endpoints Principais

| Método | Endpoint | Autorização | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/user` | Público | Cadastrar um novo usuário |
| **POST** | `/user/login` | Público | Autenticar e obter o Token JWT |
| **GET** | `/user/{id}` | Autenticado | Buscar detalhes de um usuário por ID |
| **PUT** | `/user/{id}` | Autenticado | Atualizar dados cadastrais (como a senha) |
| **PATCH**| `/user/{id}/profiles` | **Apenas Admin** | Alterar os perfis/privilégios do usuário (ex: promover a Admin) |
| **GET** | `/task/{id}` | Autenticado | Buscar uma tarefa específica por ID |
| **GET** | `/task/user` | Autenticado | Listar todas as tarefas vinculadas ao usuário logado (Paginado) |
| **POST** | `/task/{userId}` | Autenticado | Criar uma nova tarefa para um usuário específico |
| **PUT** | `/task/{id}` | Autenticado | Atualizar a descrição de uma tarefa |
| **DELETE**| `/task/{id}` | Autenticado | Excluir definitivamente uma tarefa |

---

## Autor

**Matheus Henrique de Araujo**

* [LinkedIn](https://www.linkedin.com/in/matheus-henrique-araujo/)
* [GitHub](https://github.com/OdevMatheus)
