# 🏢 Sistema ERP Elite - Gerenciador de Estoque

**Versão:** 1.2 (Abril de 2026)

**Plataforma:** Java (Cross-Platform)

**Sistema recomendado:** Linux (Ubuntu)

---

## 📌 Sobre o Projeto

O **Sistema ERP Elite** é uma aplicação desktop desenvolvida em Java com interface gráfica (JavaFX), focada no gerenciamento de estoque de forma simples, eficiente e visual.

O sistema foi projetado seguindo conceitos de **Programação Orientada a Objetos (POO)** e separação em camadas, proporcionando organização, escalabilidade e facilidade de manutenção.

---

## 🎯 Objetivo

Este projeto foi desenvolvido com foco em:

* Praticar arquitetura de software (camadas)
* Aplicar conceitos de POO
* Criar uma interface gráfica moderna
* Simular um sistema real de gestão de estoque

---

## 🚀 Funcionalidades

* 📦 Cadastro, listagem e remoção de produtos
* 🔍 Busca dinâmica em tempo real
* 📊 Dashboard com métricas do estoque
* 💰 Cálculo automático do valor total
* ⚠️ Alerta de estoque baixo
* 💾 Persistência de dados em JSON
* 🎨 Interface estilizada com CSS

---

## 🧱 Arquitetura do Projeto

O sistema segue uma estrutura em camadas:

```
src/
 ├── model/        → Entidades (Produto)
 ├── repository/   → Acesso a dados (JSON)
 ├── service/      → Regras de negócio
 ├── ui/           → Interface gráfica (JavaFX)
```

Esse padrão melhora a organização e facilita futuras evoluções do sistema.

---

## 🛠️ Tecnologias Utilizadas

* Java 17+
* JavaFX (Interface gráfica)
* Gson (Manipulação de JSON)
* CSS (Estilização da interface)

---

## ▶️ Como Executar

### 🔹 Via Script (Linux)

```bash
chmod +x run.sh
./run.sh
```

---

### 🔹 Manual (Terminal)

```bash
javac --module-path ~/IdeaProjects/javafx-sdk-26/lib \
--add-modules javafx.controls \
-cp gson-2.10.1.jar \
-d bin \
src/**/*.java

java --module-path ~/IdeaProjects/javafx-sdk-26/lib \
--add-modules javafx.controls \
-Dprism.order=sw \
-cp "bin:gson-2.10.1.jar" \
ui.Main
```

---

## 📂 Persistência de Dados

Os dados são armazenados localmente no arquivo:

```
produtos.json
```

Isso permite que o sistema funcione **offline**, sem necessidade de banco de dados externo.

---

## 📸 Interface

> Adicione aqui prints do sistema rodando (isso aumenta MUITO o impacto do projeto)

---

## 🔐 Segurança

Algumas ações críticas podem exigir autenticação.

* 🔑 Senha padrão: `admin123`

---

## 📈 Possíveis Melhorias

* Integração com banco de dados (SQLite/MySQL)
* Sistema de login completo
* Relatórios em PDF
* Exportação de dados
* Interface com FXML (Scene Builder)
* Dashboard com gráficos avançados

---

## 📄 Licença

Este projeto é de uso educacional e livre para estudo.

---

## 👨‍💻 Autor

**Victor**

Projeto desenvolvido com foco em aprendizado e evolução técnica 🚀
