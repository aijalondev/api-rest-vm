# API Rest VM Tecnologia a Nayax Company

![Logo da VM Tecnologia](https://static.wixstatic.com/media/31e6f4_c384784ebd564c69bd34872c89331c55~mv2.png/v1/fill/w_241,h_55,al_c,q_85,usm_0.66_1.00_0.01,enc_avif,quality_auto/logo%20inteira%201.png)

Este projeto é uma API Rest desenvolvida para teste na VM Tecnologia a Nayax Company.

## Visão Geral

A API fornece endpoints para gerenciar usuários e recursos da VM Tecnologia.

## Tecnologias Utilizadas

* **Linguagem de Programação:** Java 17
* **Framework:** Spring Boot 3.4.4
* **Banco de Dados:** PostgreSQL (Configurado para rodar via Docker)
* **Gerenciamento de Dependências:** Maven
* **Containerização:** Docker

## Pré-requisitos

Antes de começar, você precisará ter instalado em sua máquina:

* **Docker:** Certifique-se de ter o Docker instalado. Você pode baixá-lo em [https://www.docker.com/get-started](https://www.docker.com/get-started).
* **Docker Compose:** Geralmente vem instalado com o Docker Desktop. Se não, você pode encontrar instruções de instalação em [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/).
* **Git:** Necessário para clonar o repositório do projeto. Você pode baixá-lo em [https://git-scm.com/downloads](https://git-scm.com/downloads).

## Como Rodar a Aplicação com Docker

A maneira mais fácil e recomendada de rodar esta aplicação é utilizando o Docker e o Docker Compose. Siga os passos abaixo:

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/aijalondev/api-rest-vm.git
    cd api-rest-vm
    ```
    **Observação:** Certifique-se de que o Git esteja instalado e configurado corretamente em seu sistema para que o comando `git clone` funcione.

2.  **Construa a imagem Docker da API:**
    ```bash
    docker build -t back-vm-app .
    ```
    Este comando irá construir a imagem Docker para a API Rest com base no Dockerfile presente no diretório do projeto.

3.  **Suba a aplicação e o banco de dados com Docker Compose:**
    ```bash
    docker-compose up -d
    ```
    Este comando irá ler o arquivo `docker-compose.yml` e iniciar os containers definidos nele, que incluem a API e o banco de dados PostgreSQL. A flag `-d` indica que os containers serão executados em segundo plano (detached mode).

4.  **Acesse a API:**
    Após a execução dos comandos acima, a API Rest estará disponível na porta configurada no `docker-compose.yml` (geralmente `localhost:2803` ou a porta que for especificado).

## Configuração do Banco de Dados

O banco de dados PostgreSQL é configurado para rodar em um container Docker separado, definido no arquivo `docker-compose.yml`. As configurações do banco de dados (nome, usuário, senha) são definidas nas variáveis de ambiente dentro do arquivo `.env`.

**Importante:** Certifique-se de que as configurações do banco de dados no seu arquivo `docker-compose.yml` correspondam às configurações de conexão definidas na sua aplicação Spring Boot (geralmente no arquivo `application.yml` ou `application.properties`).

## Manipulação do Banco de Dados

Para facilitar a administração do PostgreSQL, há um container com o pgAdmin disponível.

Para acessar o pgAdmin, siga os passos:

Abra um navegador e acesse `http://localhost:5431`

Use as credenciais definidas no `docker-compose.yml` para login

Configure a conexão com o PostgreSQL utilizando as credenciais do banco

## Endpoints da API

**Observação:** Para realizar testes nos endpoints abaixo, o banco de dados deve estar populado com alguns usuários. O arquivo `init_users.sql` presente na raiz do projeto contém um script SQL com uma base de usuários para este propósito. Certifique-se de executar este script no banco de dados após a inicialização dos containers Docker.

Para acessar a documentação completa da API, acesse o link abaixo:

[http://localhost:2803/doc/api-rest-vm](http://localhost:2803/doc/api-rest-vm).

A documentação fornece detalhes sobre os endpoints disponíveis, métodos suportados, parâmetros e exemplos de requisição/resposta.

## Possibilidades para frontend
* **Angular:** seria uma boa escolha se o projeto for grande, complexo, precisa de uma estrutura bem definida desde o início e a equipe tiver experiência ou estiver disposta a investir na curva de aprendizado. Pode levar a um maior acoplamento entre diferentes partes da aplicação, dificultando a substituição ou atualização de componentes isoladamente.

* **Vue:** seria uma boa escolha se o projeto for menor, precisar de uma integração mais fácil com código existente, a equipe precisar de uma curva de aprendizado mais suave ou se a prioridade for um desenvolvimento mais rápido e flexível. A flexibilidade, embora uma vantagem, pode levar a inconsistências arquiteturais em projetos maiores se não houver padrões e diretrizes bem definidos pela equipe.

* A decisão final vai depender das necessidades específicas do projeto, do tamanho da equipe e da familiaridade da equipe com cada tecnologia.

* Escolhi o Angular como linguagem pois tenho mais familiaridade e especialmente considerando a tendência da VM Tecnologia em lidar com projetos de grande escala. Olhe o meu projeto em: [https://github.com/aijalondev/frontend-vm-app](https://github.com/aijalondev/frontend-vm-app)
