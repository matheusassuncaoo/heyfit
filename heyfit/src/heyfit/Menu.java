package heyfit;

import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Menu {
    private Scanner leia = new Scanner(System.in);
    private BancoDados bancoDados = new BancoDados();

    // Exibe o menu inicial do sistema
    public void ExibirMenuInicial() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("=== Sistema HeyFit ===");
            System.out.println("1. Login");
            System.out.println("2. Registrar");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            
            int opcao = leia.nextInt();
            leia.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1:
                    ExibirLogin();
                    break;
                case 2:
                    ExibirRegistro();
                    break;
                case 3:
                    continuar = false;
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    // Exibe a tela de registro de novo usuário
    public void ExibirRegistro() {
        System.out.println("=== Registro de Novo Usuário ===");
        System.out.print("Nome: ");
        String nome = leia.nextLine();
        System.out.print("Email: ");
        String email = leia.nextLine();
        System.out.print("Senha: ");
        String senha = leia.nextLine();
        System.out.print("Tipo (Aluno/Personal/Recepcionista/Administrador): ");
        String tipoStr = leia.nextLine();

        try {
        	BancoDados.CriarTabelas();
            Usuario.tipo tipo = Usuario.tipo.valueOf(tipoStr);
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("INSERT INTO usuarios (nome, email, senha, tipoUsuario) VALUES (?, ?, ?, ?)");
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, tipo.toString());
            stmt.executeUpdate();
            System.out.println("Usuário registrado com sucesso! Faça login para continuar.");
        } catch (IllegalArgumentException e) {
            System.out.println("Tipo de usuário inválido! Use: Aluno, Personal, Recepcionista ou Administrador.");
        } catch (SQLException e) {
            System.out.println("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    // Exibe e gerencia a tela de login
    public void ExibirLogin() {
        System.out.println("=== Sistema HeyFit - Login ===");
        System.out.print("Email: ");
        String email = leia.nextLine();
        System.out.print("Senha: ");
        String senha = leia.nextLine();

        // Verifica credenciais no banco de dados
        try {
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("SELECT * FROM usuarios WHERE email = ? AND senha = ?");
            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Cria usuário com base nos dados do banco
                Usuario usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha"),
                    Usuario.tipo.valueOf(rs.getString("tipoUsuario"))
                );
                ExibirMenu(usuario);
            } else {
                System.out.println("Email ou senha incorretos!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao realizar login: " + e.getMessage());
        }
    }

    // Exibe o menu principal baseado no tipo de usuário
    public void ExibirMenu(Usuario usuario) {
        boolean continuar = true;
        while (continuar) {
            switch (usuario.getTipoUsuario()) {
                case Aluno:
                    System.out.println("=== Menu Aluno ===");
                    System.out.println("1. Ver Plano de Treino");
                    System.out.println("2. Ver Desempenho");
                    System.out.println("3. Sair");
                    break;
                case Recepcionista:
                    System.out.println("=== Menu Recepcionista ===");
                    System.out.println("1. Cadastrar Aluno");
                    System.out.println("2. Listar Personais Disponíveis");
                    System.out.println("3. Sair");
                    break;
                case Personal:
                    System.out.println("=== Menu Personal ===");
                    System.out.println("1. Criar/Editar Plano de Treino");
                    System.out.println("2. Acompanhar Aluno");
                    System.out.println("3. Sair");
                    break;
                case Administrador:
                    System.out.println("=== Menu Administrador ===");
                    System.out.println("1. Cadastrar Personal");
                    System.out.println("2. Cadastrar Recepcionista");
                    System.out.println("3. Cadastrar Adm");
                    System.out.println("4. Sair");
                    break;
                default:
                    System.out.println("Tipo de usuário não reconhecido! Tipo = " + usuario.getTipoUsuario());
                    return;
            }

            System.out.print("Escolha uma opção: ");
            int opcao = leia.nextInt();
            leia.nextLine(); // Limpa o buffer
            continuar = PegarOpcao(usuario, opcao);
        }
    }

    // Processa a opção escolhida pelo usuário
    public boolean PegarOpcao(Usuario usuario, int opcao) {
        switch (usuario.getTipoUsuario()) {
            case Aluno:
                switch (opcao) {
                    case 1:
                        ExibirMenuPlanoAluno(usuario);
                        break;
                    case 2:
                        ExibirMenuDesempenhoAluno(usuario);
                        break;
                    case 3:
                        return false; // Sai do menu
                    default:
                        System.out.println("Opção inválida!");
                }
                break;
            case Recepcionista:
                switch (opcao) {
                    case 1:
                        ExibirMenuCadastrarAluno();
                        break;
                    case 2:
                       ExibirMenuPersonaisDisponiveis();
                        break;
                    case 3:
                        return false;
                    default:
                        System.out.println("Opção inválida!");
                }
                break;
            case Personal:
                switch (opcao) {
                    case 1:
                        ExibirMenuCadastroPlano();
                        break;
                    case 2:
                       ExibirMenuAcompanharAluno();
                        break;
                    case 3:
                        return false;
                    default:
                        System.out.println("Opção inválida!");
                }
                break;
            case Administrador:
                switch (opcao) {
                    case 1:
                    	ExibirMenuCadastrarPersonal();
                    	break;
                    case 2:
                    	ExibirMenuCadastrarRecepcao();
                    	break;
                    case 3:
                        ExibirMenuCadastrarAdmin();   
                        break;
                    case 4:
                        return false;
				default:
                        System.out.println("Opção inválida!");
                        break;
                }
                break;
            default:
                System.out.println("Tipo de usuário não reconhecido!");
                return false;
        }
        return true;
    }
    
 // === ALUNO ===
    public void ExibirMenuPlanoAluno(Usuario aluno) {
        System.out.println("=== Plano de Treino do Aluno ===");
        try {
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("SELECT descricao FROM planos_treino WHERE id_aluno = ?");
            stmt.setInt(1, aluno.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Plano: " + rs.getString("descricao") + "\n");
            } else {
                System.out.println("Nenhum plano de treino encontrado para este aluno.\n");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao exibir plano de treino: " + e.getMessage());
        }
    }
    
    public void ExibirMenuDesempenhoAluno(Usuario aluno) {
        System.out.println("=== Desempenho do Aluno ===");
        try {
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("SELECT data, progresso FROM desempenho WHERE id_aluno = ? ORDER BY data DESC LIMIT 5");
            stmt.setInt(1, aluno.getId());
            ResultSet rs = stmt.executeQuery();

            boolean temDados = false;
            while (rs.next()) {
                temDados = true;
                System.out.println("Data: " + rs.getDate("data") + " - Progresso: " + rs.getString("progresso"));
            }
            if (!temDados) {
                System.out.println("Nenhum dado de desempenho encontrado.\n");
            } else {
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao exibir desempenho: " + e.getMessage());
        }
    }
    
 // === RECEPCIONISTA ===
    public void ExibirMenuCadastrarAluno() {
        System.out.println("=== Cadastrar Novo Aluno ===");
        System.out.print("Nome: ");
        String nome = leia.nextLine();
        System.out.print("Email: ");
        String email = leia.nextLine();
        System.out.print("Senha: ");
        String senha = leia.nextLine();

        try {
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("INSERT INTO usuarios (nome, email, senha, tipoUsuario) VALUES (?, ?, ?, 'Aluno')");
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.executeUpdate();
            System.out.println("Aluno cadastrado com sucesso!\n");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar aluno: " + e.getMessage());
        }
    }
    
    public void ExibirMenuPersonaisDisponiveis() {
        System.out.println("=== Personais Disponíveis ===");
        try {
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("SELECT id, nome FROM usuarios WHERE tipoUsuario = 'Personal'");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " - Nome: " + rs.getString("nome"));
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println("Erro ao listar personais: " + e.getMessage());
        }
    }

 // === PERSONAL ===
    public void ExibirMenuCadastroPlano() {
        System.out.println("=== Criar ou Editar Plano de Treino ===");
        System.out.print("ID do Aluno: ");
        int idAluno = leia.nextInt();
        leia.nextLine();
        System.out.print("Descrição do Plano: ");
        String plano = leia.nextLine();

        try {
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("INSERT INTO planos_treino (id_aluno, descricao) VALUES (?, ?) ON DUPLICATE KEY UPDATE descricao = ?");
            stmt.setInt(1, idAluno);
            stmt.setString(2, plano);
            stmt.setString(3, plano);
            stmt.executeUpdate();
            System.out.println("Plano de treino atualizado com sucesso!\n");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar plano: " + e.getMessage());
        }
    }

    public void ExibirMenuAcompanharAluno() {
        System.out.println("=== Acompanhar Progresso do Aluno ===");
        System.out.print("ID do Aluno: ");
        int idAluno = leia.nextInt();
        leia.nextLine();
        // Simulação: buscar dados de desempenho
        System.out.println("Exibindo progresso do aluno ID " + idAluno + " (dados fictícios).\n");
    }

 // === ADMINISTRADOR ===
    public void ExibirMenuCadastrarPersonal() {
        System.out.println("=== Cadastrar Personal ===");
        CadastrarUsuarioComTipo("Personal");
    }

    public void ExibirMenuCadastrarRecepcao() {
        System.out.println("=== Cadastrar Recepcionista ===");
        CadastrarUsuarioComTipo("Recepcionista");
    }

    public void ExibirMenuCadastrarAdmin() {
        System.out.println("=== Cadastrar Administrador ===");
        CadastrarUsuarioComTipo("Administrador");
    }

    private void CadastrarUsuarioComTipo(String tipo) {
        System.out.print("Nome: ");
        String nome = leia.nextLine();
        System.out.print("Email: ");
        String email = leia.nextLine();
        System.out.print("Senha: ");
        String senha = leia.nextLine();

        try {
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("INSERT INTO usuarios (nome, email, senha, tipoUsuario) VALUES (?, ?, ?, ?)");
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, tipo);
            stmt.executeUpdate();
            System.out.println(tipo + " cadastrado com sucesso!\n");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar " + tipo.toLowerCase() + ": " + e.getMessage());
        }
    }
}
