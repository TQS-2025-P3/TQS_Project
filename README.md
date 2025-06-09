# Projeto TQS: Sistema de Carregamento de Veículos Elétricos

O objetivo deste projeto é desenvolver uma aplicação que permita aos utilizadores encontrar estações de carregamento para veículos elétricos ou híbridos, efetuar reservas por um período de tempo e realizar pagamentos de forma simples e eficiente.

---

## Equipa e Funções

- **Pedro Rei** – Team Coordinator & Product Owner  
- **João Morais** – DevOps Master 
- **Gonçalo Abrantes** – QA Engineer 

---

## Recursos

- **SonarQube Cloud**: [https://sonarcloud.io/project/overview?id=TQS-2025-P3_TQS_Project](https://sonarcloud.io/project/overview?id=TQS-2025-P3_TQS_Project)  
- **Jira Board**: [https://tqs-project-18.atlassian.net/jira/software/projects/TQSPROJECT/boards/1](https://tqs-project-18.atlassian.net/jira/software/projects/TQSPROJECT/boards/1)  
- **Deploy da Aplicação**: *[inserir link]*

### Credenciais de Acesso

- **MySQL**: senha1234  
- **Página de Admin**: admin123

- 

---

## Alterações desde a Apresentação

### Funcionalidades e Melhorias

#### Gestão de Carros
- Botão de delete corrigido: agora é possível eliminar carros mesmo com reservas associadas. As reservas antigas são eliminadas automaticamente antes da remoção do carro.
- Validação de matrícula: impedida a adição de veículos com matrículas duplicadas (chave primária).

#### Gestão de Estações
- Botão de delete corrigido: estações com reservas antigas agora podem ser eliminadas. As reservas são removidas automaticamente antes da eliminação da estação.
- Validação de coordenadas: não é permitido adicionar estações com o mesmo par de coordenadas geográficas.

#### Gestão de Utilizadores
- Validação de email: prevenido o registo de utilizadores com e-mails duplicados (chave primária).

---

## Testes

- **Cobertura de Código**: A cobertura de testes foi significativamente aumentada, estando agora bem acima dos 80% no SonarQube.
- **Testes End-to-End com Cypress**: Implementados testes automatizados utilizando [Cypress](https://www.cypress.io/), ferramenta moderna para testes de interface diretamente no navegador. Mais atual e simples de utilizar em comparação com Selenium.

---

