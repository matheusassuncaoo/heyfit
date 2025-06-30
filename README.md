# 🏋️‍♂️ HeyFit

**HeyFit** é um sistema de gerenciamento para academias desenvolvido em **Java**, com o objetivo de facilitar o controle de alunos, treinos, planos e pagamentos. Este projeto visa atender tanto administradores quanto instrutores, proporcionando uma gestão mais eficiente do dia a dia da academia.

---

## 📋 Funcionalidades

- Cadastro e gerenciamento de **alunos**
- Criação e edição de **planos de treino**
- Gerenciamento de **instrutores** e **usuários do sistema**
- Relatórios de desempenho e inadimplência

---

## 🛠️ Tecnologias Utilizadas

- **Java** (versão 24+)
- **MySQL**
- **JDBC** para integração com banco de dados
- **Git** e **GitHub** para controle de versão

---

## 🚀 Como executar o projeto

### 1. **Clone este repositório:**
```bash
git clone https://github.com/matheusassuncaoo/heyfit.git
cd heyfit
```

### 2. **Configuração do Java:**
Baixe e instale o JDK 24:
- **Executável (Installer):** [JDK 24 Installer](https://download.oracle.com/java/24/latest/jdk-24_windows-x64_bin.exe)
- **Arquivo ZIP:** [JDK 24 ZIP](https://download.oracle.com/java/24/latest/jdk-24_windows-x64_bin.zip)

### 3. **Configuração do MySQL:**
- Instale o MySQL Server
- **MySQL Connector/J (Driver JDBC):** [Download JDBC Driver](https://dev.mysql.com/downloads/file/?id=538917)
- Configure o banco de dados conforme necessário

### 4. **Execute o projeto:**
```bash
# Compile o projeto
javac -cp "lib/mysql-connector-j-*.jar" src/*.java

# Execute o projeto
java -cp "lib/mysql-connector-j-*.jar:src" Main
```

---

## 📦 Estrutura do Projeto

```
heyfit/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── lib/
│   └── mysql-connector-j-*.jar
├── database/
├── docs/
└── README.md
```

---

## ⚙️ Dependências

### Driver JDBC MySQL
O projeto utiliza o MySQL Connector/J para conectar com o banco de dados:
- **Download:** [MySQL Connector/J](https://dev.mysql.com/downloads/file/?id=538917)
- Extraia o arquivo `.jar` na pasta `lib/` do projeto

---

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença [MIT](LICENSE).

---

## 📞 Contato

**Desenvolvedor:** Matheus Assunção
- GitHub: [@matheusassuncaoo](https://github.com/matheusassuncaoo)

---

## 🔗 Links Úteis

### Downloads Necessários:
- **JDK 24 (Executável):** [Windows Installer](https://download.oracle.com/java/24/latest/jdk-24_windows-x64_bin.exe)
- **JDK 24 (ZIP):** [Windows ZIP](https://download.oracle.com/java/24/latest/jdk-24_windows-x64_bin.zip)
- **MySQL Connector/J (Driver JDBC):** [Download](https://dev.mysql.com/downloads/file/?id=538917)
- **MySQL Server:** [Download](https://dev.mysql.com/downloads/mysql/)

### Documentação:
- [Documentação Java](https://docs.oracle.com/en/java/)
- [MySQL Connector/J Documentation](https://dev.mysql.com/doc/connector-j/en/)
