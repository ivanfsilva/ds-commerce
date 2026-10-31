<div align="center">

[![Português](https://img.shields.io/badge/Idioma-Portugu%C3%Aas-green.svg)](./README.md)
[![English](https://img.shields.io/badge/Language-English-blue.svg)](./README.en.md)
[![Español](https://img.shields.io/badge/Idioma-Espa%C3%B1ol-yellow.svg)](./README.es.md)

[![release](https://img.shields.io/badge/release-v1.0.0--beta-yellow.svg)](https://github.com/ivanfsilva/dscommerce/releases)
[![status](https://img.shields.io/badge/status-em_desenvolvimento-blue.svg)](https://github.com/ivanfsilva/dscommerce)
[![license](https://img.shields.io/badge/license-MIT-green.svg)](./LICENSE)

</div>

# 🛒 DSCommerce - Plataforma de E-Commerce

O **DSCommerce** é uma solução completa de comércio eletrônico (E-Commerce) desenvolvida como parte da formação corporativa backend em Java e Spring Boot. O sistema fornece uma arquitetura escalável e bem delimitada para gerenciar operações de compra e venda online, atendendo desde a navegação pública de produtos até a gestão administrativa de catálogo e fluxo financeiro de pedidos.

---

## 📌 Visão Geral & Conceitos-Chave

O **DSCommerce** resolve a complexidade do fluxo de vendas online separando claramente a experiência do cliente final da gestão administrativa da loja virtual.

### 💡 Funcionamento Geral do Sistema

1. **Navegação e Carrinho (Público):** Qualquer usuário anônimo pode consultar o catálogo de produtos com paginação e busca por nome, visualizar detalhes dos itens e montar seu carrinho de compras dinâmico (adicionando, alterando quantidades ou removendo produtos).
2. **Checkout e Pedido (Cliente Autenticado):** Ao decidir finalizar a compra, o usuário precisa estar autenticado no sistema. O pedido é registrado com o status inicial de `WAITING_PAYMENT` (Aguardando Pagamento), consolidando o _snapshot_ dos preços dos produtos no momento da transação.
3. **Gestão Administrativa (Admin):** Usuários administradores possuem privilégios para realizar a manutenção completa (CRUD) do cadastro de produtos, categorias e usuários, além de registrar pagamentos e emitir relatórios de vendas por período.

### 🏛️ Padrões Arquiteturais e Boas Práticas Aplicadas

- **Arquitetura em Camadas (Layered Architecture):** Rigorosa separação de responsabilidades entre as camadas de Apresentação/Controladores REST (`Controllers`), Regras de Negócio (`Services`) e Acesso a Dados (`Repositories`).
- **Transferência de Dados Segura (DTO Pattern):** Uso intensivo de _Data Transfer Objects_ para proteger as entidades de domínio JPA e encapsular a entrada e saída das requisições HTTP.
- **Modelo de Domínio Rico e Consistência Histórica:** Mapeamento de entidades com relacionamentos complexos ($1:N$, $N:N$). O preço unitário do item é persistido dentro da tabela de associação `OrderItem`, garantindo que eventuais alterações futuras no valor de tabela de um produto não alterem o histórico financeiro de pedidos já consolidados.
- **Segurança e Autorização Granular (RBAC - Role-Based Access Control):** Controle rigoroso de acesso com Spring Security e OAuth2/JWT, diferenciando perfis `ROLE_CLIENT` e `ROLE_ADMIN`.
- **Tratamento Centralizado de Exceções:** Uso de `@ControllerAdvice` para capturar exceções de negócio, validações de integridade relacional e erros de campo (_Bean Validation_), retornando respostas amigáveis e padronizadas no formato JSON.

---

## 🎨 Protótipos de Tela (Figma)

A concepção da interface do usuário e dos fluxos de navegação do **DSCommerce** foi desenhada previamente no Figma, englobando a área pública do e-commerce, o fluxo de carrinho/checkout e o painel administrativo.

- **Link para o Projeto no Figma:** [DSCommerce - Figma Prototypes](https://www.figma.com/file/ZrGNVNG0kZL6txDv4G8P6s/DSCommerce)

### Escopo das Telas Mapeadas:

- **Área Pública & Cliente:** Vitrine/Catálogo de Produtos, Detalhes do Produto, Carrinho de Compras, Tela de Login e Confirmação de Pedido.
- **Área Administrativa:** Listagem de Produtos (CRUD), Formulário de Cadastro/Edição de Produtos e Gestão de Categorias.

---

## 📋 Casos de Uso & Regras de Negócio

Abaixo está o detalhamento estruturado das funcionalidades e regras operacionais do sistema:

### 1. Área Pública (Usuário Anônimo / Cliente / Admin)

- **Consultar Catálogo:** Listagem paginada de produtos (padrão de 12 itens por página) ordenada por nome, permitindo filtro opcional por parte do nome.
- **Gerenciar Carrinho:** Permite adicionar produtos, remover itens e alterar quantidades. O sistema calcula dinamicamente os subtotais por item e o valor total do carrinho.
- **Login / Sign Up:** Permite o autocadastro de novos clientes (todo usuário cadastrado por padrão recebe o perfil `ROLE_CLIENT`) e a autenticação com geração de token de acesso.

### 2. Área do Cliente (Exige Autenticação)

- **Registrar Pedido:** Converte os itens do carrinho em um pedido salvo no sistema com status `WAITING_PAYMENT`.
  - _Regra de Negócio:_ O carrinho não pode estar vazio no momento do envio.
- **Visualizar Pedidos & Perfil:** Permite ao cliente consultar o histórico e detalhes dos seus próprios pedidos e atualizar seus dados cadastrais.

### 3. Área Administrativa (Exige Perfil `ROLE_ADMIN`)

- **Manter Produtos (CRUD):** Inserção, atualização e remoção de produtos no catálogo.
  - _Regras de Validação:_
    - **Nome:** Obrigatório, entre 3 e 80 caracteres.
    - **Preço:** Obrigatório e estritamente positivo ($> 0$).
    - **Descrição:** No mínimo 10 caracteres.
    - **Categorias:** O produto deve possuir pelo menos 1 categoria vinculada.
  - _Tratamento de Exceções:_ Impedimento de deleção caso o produto possua vínculo com pedidos (violação de integridade referencial).
- **Manter Categorias & Usuários:** Gestão cadastral de apoio às operações do e-commerce.
- **Registrar Pagamento:** Atualização do status do pedido para `PAID` (Pago) e registro do instante exato da confirmação de pagamento.
- **Reportar Vendas:** Emissão de relatórios consolidados de pedidos filtrados por intervalo de datas.

---

## 📐 Arquitetura & Diagramas

### Diagrama de Casos de Uso

```mermaid
graph TD
    subgraph Atores
        UA[Usuário Anônimo]
        CLI[Cliente]
        ADM[Admin]
    end

    CLI -->|Herança| ADM

    subgraph "Área Pública & Autenticação"
        UC_Cat[Consultar Catálogo]
        UC_Car[Gerenciar Carrinho]
        UC_SU[Sign Up]
        UC_Log[Login]
    end

    subgraph "Área do Cliente"
        UC_Ped[Registrar Pedido]
        UC_Prof[Atualizar Perfil]
        UC_VPed[Visualizar Pedidos]
    end

    subgraph "Área Administrativa"
        UC_MProd[Manter Produtos]
        UC_MCat[Manter Categorias]
        UC_MUsu[Manter Usuários]
        UC_RPag[Registrar Pagamento]
        UC_RVen[Reportar Vendas]
    end

    UA --> UC_Cat
    UA --> UC_Car
    UA --> UC_SU
    UA --> UC_Log

    CLI --> UC_Ped
    CLI --> UC_Prof
    CLI --> UC_VPed

    ADM --> UC_MProd
    ADM --> UC_MCat
    ADM --> UC_MUsu
    ADM --> UC_RPag
    ADM --> UC_RVen

    UC_MProd -.->|includes| UC_Cat
    UC_Car -.->|includes| UC_Cat
```

### Modelo Conceitual / Diagrama de Classes

```mermaid
classDiagram
    class User {
        +Long id
        +String name
        +String email
        +String phone
        +LocalDate birthDate
        +String password
        +String[] roles
    }

    class Order {
        +Long id
        +Instant moment
        +OrderStatus status
    }

    class Payment {
        +Long id
        +Instant moment
    }

    class OrderItem {
        +Integer quantity
        +Double price
    }

    class Product {
        +Long id
        +String name
        +String description
        +Double price
        +String imgUrl
    }

    class Category {
        +Long id
        +String name
    }

    class OrderStatus {
        <<enumeration>>
        WAITING_PAYMENT
        PAID
        SHIPPED
        DELIVERED
        CANCELED
    }

    User "1" --> "*" Order : orders / client
    Order "1" --> "0..1" Payment : payment / order
    Order "1" --> "*" OrderItem : items / order
    Product "1" --> "*" OrderItem : items / product
    Product "*" --> "*" Category : categories / products
    Order .. OrderStatus
```

---

## 📋 Especificação de Entrada e Saída (Exemplos Práticos)

# Consultar Catálogo

## Exemplo de entrada e saída

### Entrada

`GET /products?name=Computer&page=0&size=12`

### Saída

```json
{
  "content": [
    {
      "id": 1,
      "name": "Computer",
      "price": 1100.0,
      "imgUrl": "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.png"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 12
  },
  "totalPages": 1,
  "totalElements": 1
}
```

---

# Manter Produtos (Inserir)

## Exemplo de entrada e saída

### Entrada

`POST /products`

```json
{
  "name": "Smart TV",
  "description": "Smart TV 55 polegadas 4K Ultra HD com comandos de voz",
  "price": 2500.0,
  "imgUrl": "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/tv.png",
  "categories": [
    {
      "id": 2
    }
  ]
}
```

### Saída

`Status: 201 Created`

```json
{
  "id": 15,
  "name": "Smart TV",
  "description": "Smart TV 55 polegadas 4K Ultra HD com comandos de voz",
  "price": 2500.0,
  "imgUrl": "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/tv.png",
  "categories": [
    {
      "id": 2,
      "name": "Eletrônicos"
    }
  ]
}
```

---

# Registrar Pedido

## Exemplo de entrada e saída

### Entrada

`POST /orders`

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

### Saída

`Status: 201 Created`

```json
{
  "id": 102,
  "moment": "2026-10-31T11:05:00Z",
  "status": "WAITING_PAYMENT",
  "client": {
    "id": 1,
    "name": "Maria Brown",
    "email": "maria@gmail.com"
  },
  "payment": null,
  "items": [
    {
      "productId": 1,
      "name": "Computer",
      "price": 1100.0,
      "quantity": 2,
      "subTotal": 2200.0
    },
    {
      "productId": 3,
      "name": "Macbook Pro",
      "price": 1250.0,
      "quantity": 1,
      "subTotal": 1250.0
    }
  ],
  "total": 3450.0
}
```

---

## 🛠️ Tecnologias e Ferramentas

- **Linguagem & Framework:** Java 17 LTS, Spring Boot 3.x, Spring MVC, Spring Security (OAuth2 / JWT)
- **Build & Gerenciamento:** Apache Maven
- **Persistência & Banco de Dados:** Jakarta Persistence (JPA), Hibernate, H2 Database (Dev/Testes), PostgreSQL (Produção)
- **Validação & Testes:** Jakarta Bean Validation, JUnit 5, Mockito
- **Ferramentas de Produtividade:** Postman, Git, Figma

<br>

<div align="center">
  <img src="https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot 3" />
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white" alt="Spring Security" />
  <img src="https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Apache Maven" />
  <br>
  <img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Data JPA" />
  <img src="https://img.shields.io/badge/H2_Database-003B5C?style=for-the-badge&logo=database&logoColor=white" alt="H2 Database" />
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <br>
  <img src="https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5" />
  <img src="https://img.shields.io/badge/Mockito-C50000?style=for-the-badge&logo=testing-library&logoColor=white" alt="Mockito" />
  <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white" alt="Figma" />
  <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white" alt="Postman" />
</div>

---

## 📂 Estrutura de Pastas

```text
dscommerce/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── devsuperior/
│   │   │           └── dscommerce/
│   │   │               ├── controllers/
│   │   │               │   ├── CategoryController.java
│   │   │               │   ├── OrderController.java
│   │   │               │   ├── ProductController.java
│   │   │               │   └── UserController.java
│   │   │               ├── dtos/
│   │   │               ├── entities/
│   │   │               │   ├── Category.java
│   │   │               │   ├── Order.java
│   │   │               │   ├── OrderItem.java
│   │   │               │   ├── OrderItemPK.java
│   │   │               │   ├── OrderStatus.java
│   │   │               │   ├── Payment.java
│   │   │               │   ├── Product.java
│   │   │               │   └── User.java
│   │   │               ├── repositories/
│   │   │               ├── services/
│   │   │               └── exceptions/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── import.sql
│   └── test/
├── pom.xml
└── README.md
```

---

## 🚀 Como Executar o Projeto

1. **Clonar o repositório:**

   ```bash
   git clone https://github.com/ivanfsilva/dscommerce.git
   ```

2. **Navegar até a pasta raiz do projeto:**

   ```bash
   cd dscommerce
   ```

3. **Executar a aplicação via Maven:**

   ```bash
   mvn spring-boot:run
   ```

4. **Acessar os endpoints da API:**
   - A aplicação estará rodando em `http://localhost:8080`.
   - O console do banco H2 pode ser acessado em `http://localhost:8080/h2-console`.

---

## 📌 Próximos Passos (Roadmap)

> 💡 **Nota sobre o escopo:** A versão **v1.0.0-beta** consolida o _baseline_ backend (API RESTful) desenvolvido durante o treinamento da **DevSuperior**. A partir da versão **v2.0.0**, todas as _releases_ e evoluções representam **iniciativas 100% pessoais da minha autoria**, criadas para praticar e aprofundar meus conhecimentos em engenharia Full Stack — incluindo a construção de uma interface web dinâmica em React baseada nos protótipos do Figma.

| Versão                                                                                                                            | Descrição                                                                                                                                                                                                     |
| :-------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| [![v1.0.0-beta](https://img.shields.io/badge/github-v1.0.0--beta-blue.svg?logo=github)](https://github.com/ivanfsilva/dscommerce) | **[Baseline DevSuperior]** Entrega da API RESTful backend completa: CRUDs, validações com Bean Validation, segurança OAuth2/JWT e regras de negócio de pedidos.                                               |
| [![v2.0.0](https://img.shields.io/badge/v2.0.0-Futuro-gray.svg)](#)                                                               | **[Prática Extra Autoral - Front-end React]** Construção da interface Single Page Application (SPA) em **React** consumindo a API backend, cobrindo telas de catálogo, carrinho e checkout baseadas no Figma. |
| [![v3.0.0](https://img.shields.io/badge/v3.0.0-Futuro-gray.svg)](#)                                                               | **[Prática Extra Autoral - Testes & Qualidade]** Ampliação da suíte automatizada de testes com JUnit 5, Mockito e RestAssured, com verificação de cobertura via JaCoCo.                                       |
| [![v4.0.0](https://img.shields.io/badge/v4.0.0-Futuro-gray.svg)](#)                                                               | **[Prática Extra Autoral - Documentação]** Integração e disponibilização de documentação interativa das APIs utilizando OpenAPI 3 / Swagger UI.                                                               |

---

## 👤 Autor

Desenvolvido por **Ivan Ferreira**.

Copyright © 2026 Ivan Ferreira. Todos os direitos reservados.

---

## ☕ Sobre

Aplicação prática construída no contexto do treinamento corporativo da plataforma ⚡**DevSuperior**, ministrada pelo Prof. Dr. Nélio Alves.
