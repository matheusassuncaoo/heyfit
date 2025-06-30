package heyfit;

import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * CLASSE MENU - CORAÇÃO DO SISTEMA (INTERFACE COM USUÁRIO)
 * Esta é a classe mais importante! Ela controla TODA a interação com o usuário
 * Aqui estão todos os menus, validações e operações do sistema
 */
public class Menu {
    // SCANNER GLOBAL - para ler dados do teclado em todos os métodos
    private Scanner leia = new Scanner(System.in);
    // INSTÂNCIA DO BANCO - para acessar métodos de conexão
    private BancoDados bancoDados = new BancoDados();

    /**
     * MÉTODO PRINCIPAL - PRIMEIRA TELA QUE O USUÁRIO VÊ
     * Menu inicial com opções: Login, Registrar, Sair
     * LOOP INFINITO até usuário escolher sair (opcao 3)
     */
    public void ExibirMenuInicial() {
        boolean continuar = true; // variável de controle do loop
        while (continuar) {
            // EXIBIÇÃO DO MENU PRINCIPAL
            System.out.println("=== Sistema HeyFit ===");
            System.out.println("1. Login");
            System.out.println("2. Registrar");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            
            int opcao = leia.nextInt(); // lê a opção do usuário
            leia.nextLine(); // IMPORTANTE: limpa o buffer do teclado

            // ESTRUTURA SWITCH - direciona para a funcionalidade escolhida
            switch (opcao) {
                case 1:
                    ExibirLogin(); // vai para tela de login
                    break;
                case 2:
                    ExibirRegistro(); // vai para tela de registro
                    break;
                case 3:
                    continuar = false; // sai do loop
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("Opção inválida!"); // tratamento de erro
            }
        }
    }

    /**
     * TELA DE REGISTRO - CADASTRO DE NOVOS USUÁRIOS
     * Permite criar conta nova no sistema
     * VALIDAÇÃO: Tipo de usuário por números (1-4) em vez de texto
     */
    public void ExibirRegistro() {
        System.out.println("=== Registro de Novo Usuário ===");
        
        // COLETA DE DADOS DO USUÁRIO
        System.out.print("Nome: ");
        String nome = leia.nextLine();
        System.out.print("Email: ");
        String email = leia.nextLine();
        System.out.print("Senha: ");
        String senha = leia.nextLine();
        
        // MENU NUMÉRICO PARA TIPO DE USUÁRIO (evita erros de digitação)
        System.out.println("Tipo de usuário:");
        System.out.println("1 - Aluno");
        System.out.println("2 - Personal");
        System.out.println("3 - Recepcionista");
        System.out.println("4 - Administrador");
        System.out.print("Escolha uma opção (1-4): ");
        
        int opcaoTipo = leia.nextInt();
        leia.nextLine(); // limpa buffer
        
        // CONVERSÃO NÚMERO → TEXTO (para o enum)
        String tipoStr;
        switch (opcaoTipo) {
            case 1: tipoStr = "Aluno"; break;
            case 2: tipoStr = "Personal"; break;
            case 3: tipoStr = "Recepcionista"; break;
            case 4: tipoStr = "Administrador"; break;
            default:
                System.out.println("Opção inválida! Tente novamente.");
                return; // sai do método se opção inválida
        }

        // INSERÇÃO NO BANCO DE DADOS
        try {
            BancoDados.CriarTabelas(); // garante que tabelas existem
            Usuario.tipo tipo = Usuario.tipo.valueOf(tipoStr); // converte String para enum
            
            // PREPARED STATEMENT - previne SQL Injection
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("INSERT INTO usuarios (nome, email, senha, tipoUsuario, estaAtivo) VALUES (?, ?, ?, ?, TRUE)");
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, tipo.toString());
            stmt.executeUpdate(); // executa o INSERT
            
            System.out.println("Usuário registrado com sucesso! Faça login para continuar.");
        } catch (IllegalArgumentException e) {
            System.out.println("Tipo de usuário inválido!");
        } catch (SQLException e) {
            System.out.println("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    /**
     * TELA DE LOGIN - AUTENTICAÇÃO DE USUÁRIOS
     * Verifica email, senha E se está ativo
     * Se login OK, redireciona para menu específico do tipo de usuário
     */
    public void ExibirLogin() {
        System.out.println("=== Sistema HeyFit - Login ===");
        System.out.print("Email: ");
        String email = leia.nextLine();
        System.out.print("Senha: ");
        String senha = leia.nextLine();

        // VERIFICAÇÃO NO BANCO DE DADOS
        try {
            // CONSULTA COM VALIDAÇÃO: email, senha E estaAtivo = TRUE
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("SELECT * FROM usuarios WHERE email = ? AND senha = ? AND estaAtivo = TRUE");
            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // LOGIN SUCESSO - cria objeto Usuario com dados do banco
                Usuario usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha"),
                    Usuario.tipo.valueOf(rs.getString("tipoUsuario")),
                    rs.getBoolean("estaAtivo")
                );
                ExibirMenu(usuario); // vai para menu específico do usuário
            } else {
                System.out.println("Email ou senha incorretos, ou usuário desativado!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao realizar login: " + e.getMessage());
        }
    }

    /**
     * CENTRAL DE MENUS - MOSTRA MENU ESPECÍFICO BASEADO NO TIPO DE USUÁRIO
     * Cada tipo de usuário vê opções diferentes (controle de acesso)
     * POLIMORFISMO: mesmo método, comportamentos diferentes por tipo
     */
    public void ExibirMenu(Usuario usuario) {
        boolean continuar = true;
        while (continuar) {
            // SWITCH POR TIPO DE USUÁRIO - mostra menu específico
            switch (usuario.getTipoUsuario()) {
                case Aluno:
                    System.out.println("=== Menu Aluno ===");
                    System.out.println("1. Ver Plano de Treino");
                    System.out.println("2. Ver Desempenho");
                    System.out.println("3. Registrar Treino de Hoje"); // NOVA funcionalidade
                    System.out.println("4. Sair");
                    break;
                case Recepcionista:
                    System.out.println("=== Menu Recepcionista ===");
                    System.out.println("1. Cadastrar Aluno");
                    System.out.println("2. Listar Personais Disponíveis");
                    System.out.println("3. Vincular Aluno a Personal"); // NOVA funcionalidade
                    System.out.println("4. Sair");
                    break;
                case Personal:
                    System.out.println("=== Menu Personal ===");
                    System.out.println("1. Ver Meus Alunos"); // NOVA funcionalidade
                    System.out.println("2. Criar/Editar Plano de Treino");
                    System.out.println("3. Acompanhar Progresso de Aluno");
                    System.out.println("4. Sair");
                    break;
                case Administrador:
                    System.out.println("=== Menu Administrador ===");
                    System.out.println("1. Cadastrar Personal");
                    System.out.println("2. Cadastrar Recepcionista");
                    System.out.println("3. Cadastrar Administrador");
                    System.out.println("4. Listar Usuários");
                    System.out.println("5. Desativar/Ativar Usuário"); // NOVA funcionalidade
                    System.out.println("6. Editar Usuário"); // NOVA funcionalidade
                    System.out.println("7. Sair");
                    break;
                default:
                    System.out.println("Tipo de usuário não reconhecido! Tipo = " + usuario.getTipoUsuario());
                    return;
            }

            System.out.print("Escolha uma opção: ");
            int opcao = leia.nextInt();
            leia.nextLine(); // limpa buffer
            continuar = PegarOpcao(usuario, opcao); // processa a opção escolhida
        }
    }

    /**
     * PROCESSADOR DE OPÇÕES - EXECUTA A FUNCIONALIDADE ESCOLHIDA
     * Recebe usuário e opção, chama o método correto
     * RETURN: true = continua no menu, false = sai do menu
     */
    public boolean PegarOpcao(Usuario usuario, int opcao) {
        // SWITCH ANINHADO - primeiro por tipo de usuário, depois por opção
        switch (usuario.getTipoUsuario()) {
            case Aluno:
                switch (opcao) {
                    case 1: ExibirMenuPlanoAluno(usuario); break;
                    case 2: ExibirMenuDesempenhoAluno(usuario); break;
                    case 3: ExibirMenuRegistrarTreino(usuario); break; 
                    case 4: return false; // sai do menu
                    default: System.out.println("Opção inválida!");
                }
                break;
            case Recepcionista:
                switch (opcao) {
                    case 1: ExibirMenuCadastrarAluno(); break;
                    case 2: ExibirMenuPersonaisDisponiveis(); break;
                    case 3: ExibirMenuVincularAlunoPersonal(); break; 
                    case 4: return false;
                    default: System.out.println("Opção inválida!");
                }
                break;
            case Personal:
                switch (opcao) {
                    case 1: ExibirMenuVerMeusAlunos(usuario); break;
                    case 2: ExibirMenuCadastroPlano(); break;
                    case 3: ExibirMenuAcompanharAluno(); break;
                    case 4: return false;
                    default: System.out.println("Opção inválida!");
                }
                break;
            case Administrador:
                switch (opcao) {
                    case 1: ExibirMenuCadastrarPersonal(); break;
                    case 2: ExibirMenuCadastrarRecepcao(); break;
                    case 3: ExibirMenuCadastrarAdmin(); break;
                    case 4: ExibirMenuListarUsuarios(); break;
                    case 5: ExibirMenuDesativarAtivarUsuario(usuario); break; 
                    case 6: ExibirMenuEditarUsuario(); break;                    case 7: return false;
                    default: System.out.println("Opção inválida!");
                }
                break;
            default:
                System.out.println("Tipo de usuário não reconhecido!");
                return false;
        }
        return true; // continua no menu
    }
    
    // ============================================================================
    // SEÇÃO ALUNO - FUNCIONALIDADES QUE SÓ ALUNOS PODEM ACESSAR
    // ============================================================================

    /**
     * FUNCIONALIDADE ALUNO: Visualizar plano de treino
     * Busca o plano criado pelo personal para este aluno específico
     * VALIDAÇÃO: Só mostra se existir plano cadastrado
     */
    public void ExibirMenuPlanoAluno(Usuario aluno) {
        System.out.println("=== Plano de Treino do Aluno ===");
        try {
            // BUSCA O PLANO ESPECÍFICO DESTE ALUNO
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("SELECT descricao FROM planos_treino WHERE id_aluno = ?");
            stmt.setInt(1, aluno.getId()); // usa o ID do aluno logado
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // SE ENCONTROU O PLANO, EXIBE
                System.out.println("Plano: " + rs.getString("descricao") + "\n");
            } else {
                // SE NÃO TEM PLANO, AVISA
                System.out.println("Nenhum plano de treino encontrado para este aluno.\n");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao exibir plano de treino: " + e.getMessage());
        }
    }
    
    /**
     * FUNCIONALIDADE ALUNO: Ver histórico de desempenho
     * Mostra os últimos 5 registros de progresso/treinos
     * Dados vêm da tabela 'desempenho' alimentada pelos treinos registrados
     */
    public void ExibirMenuDesempenhoAluno(Usuario aluno) {
        System.out.println("=== Desempenho do Aluno ===");
        try {
            // BUSCA ÚLTIMOS 5 REGISTROS DE DESEMPENHO (ORDER BY data DESC LIMIT 5)
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
    
    /**
     * FUNCIONALIDADE NOVA ALUNO: Registrar treino diário
     * Permite ao aluno registrar que fez treino hoje
     * VALIDAÇÃO IMPORTANTE: Só permite se tiver plano de treino
     * NOVA VALIDAÇÃO: Verifica se exercícios estão no plano do personal
     * DUPLA INSERÇÃO: tabela treinos_realizados + tabela desempenho
     */
    public void ExibirMenuRegistrarTreino(Usuario aluno) {
        System.out.println("=== Registrar Treino de Hoje ===");
        
        // PASSO 1: VERIFICAR SE O ALUNO TEM PLANO DE TREINO E BUSCAR A DESCRIÇÃO
        String planoTreino = "";
        try {
            PreparedStatement stmtPlano = BancoDados.getConexao()
                .prepareStatement("SELECT descricao FROM planos_treino WHERE id_aluno = ?");
            stmtPlano.setInt(1, aluno.getId());
            ResultSet rsPlano = stmtPlano.executeQuery();
            
            if (!rsPlano.next()) {
                // SE NÃO TEM PLANO, NÃO PODE REGISTRAR TREINO
                System.out.println("❌ AVISO: Você ainda não possui um plano de treino. Procure seu personal para criar um!");
                System.out.println();
                return; // SAI DO MÉTODO
            }
            
            planoTreino = rsPlano.getString("descricao");
            
            // MOSTRA O PLANO DO PERSONAL
            System.out.println("📋 SEU PLANO DE TREINO (criado pelo personal):");
            System.out.println("=" .repeat(50));
            System.out.println(planoTreino);
            System.out.println("=" .repeat(50));
            System.out.println();
            
        } catch (SQLException e) {
            System.out.println("❌ Erro ao verificar plano de treino: " + e.getMessage());
            return;
        }
        
        // PASSO 2: COLETAR DADOS DO TREINO REALIZADO COM VALIDAÇÃO
        System.out.println("📝 REGISTRAR TREINO REALIZADO:");
        System.out.println("⚠️  IMPORTANTE: Descreva apenas os exercícios do seu plano!");
        System.out.print("Descreva os exercícios realizados hoje: ");
        String exerciciosRealizados = leia.nextLine();
        
        // VALIDAÇÃO: Verificar se exercícios digitados estão no plano
        if (exerciciosRealizados.trim().isEmpty()) {
            System.out.println("❌ ERRO: Você deve descrever os exercícios realizados!");
            return;
        }
        
        // VALIDAÇÃO INTELIGENTE: Verificar se pelo menos algumas palavras do treino constam no plano
        String[] palavrasPlano = planoTreino.toLowerCase().split("[\\s,.-]+");
        String[] palavrasExercicio = exerciciosRealizados.toLowerCase().split("[\\s,.-]+");
        
        boolean exercicioValido = false;
        for (String palavraExercicio : palavrasExercicio) {
            if (palavraExercicio.length() > 2) { // ignora palavras muito pequenas
                for (String palavraPlano : palavrasPlano) {
                    if (palavraPlano.contains(palavraExercicio) || palavraExercicio.contains(palavraPlano)) {
                        exercicioValido = true;
                        break;
                    }
                }
                if (exercicioValido) break;
            }
        }
        
        // SE NÃO ENCONTROU CORRESPONDÊNCIA, AVISAR E DAR OPÇÃO
        if (!exercicioValido) {
            System.out.println("\n⚠️  ATENÇÃO: Os exercícios digitados não parecem estar no seu plano!");
            System.out.println("Seu plano: " + planoTreino);
            System.out.println("Você digitou: " + exerciciosRealizados);
            System.out.print("\nTem certeza que quer registrar mesmo assim? (s/n): ");
            String confirmacao = leia.nextLine();
            
            if (!confirmacao.equalsIgnoreCase("s")) {
                System.out.println("❌ Registro cancelado. Siga seu plano de treino!\n");
                return;
            }
        }
        
        System.out.print("Tempo de treino (em minutos): ");
        int tempoTreino = 0;
        try {
            tempoTreino = leia.nextInt();
            leia.nextLine(); // Limpa o buffer
            
            if (tempoTreino <= 0) {
                System.out.println("❌ ERRO: Tempo de treino deve ser maior que 0!");
                return;
            }
            
        } catch (Exception e) {
            System.out.println("❌ ERRO: Digite um número válido para o tempo!");
            leia.nextLine(); // limpa buffer
            return;
        }
        
        System.out.print("Observações sobre o treino (opcional): ");
        String observacoes = leia.nextLine();

        // PASSO 3: SALVAR NO BANCO (DUAS TABELAS)
        try {
            // INSERÇÃO 1: Tabela treinos_realizados (registro detalhado)
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("INSERT INTO treinos_realizados (id_aluno, data_treino, exercicios_realizados, tempo_treino, observacoes_aluno) VALUES (?, CURDATE(), ?, ?, ?)");
            stmt.setInt(1, aluno.getId());
            stmt.setString(2, exerciciosRealizados);
            stmt.setInt(3, tempoTreino);
            stmt.setString(4, observacoes.isEmpty() ? null : observacoes);
            stmt.executeUpdate();
            
            // INSERÇÃO 2: Tabela desempenho (para histórico do personal) - MELHORADA
            String progressoDetalhado = String.format("✅ Treino realizado: %s | Tempo: %d min | Plano: %s", 
                                                     exerciciosRealizados, 
                                                     tempoTreino,
                                                     exercicioValido ? "SEGUIDO" : "MODIFICADO");
            
            PreparedStatement stmtDesempenho = BancoDados.getConexao()
                .prepareStatement("INSERT INTO desempenho (id_aluno, data, progresso) VALUES (?, CURDATE(), ?)");
            stmtDesempenho.setInt(1, aluno.getId());
            stmtDesempenho.setString(2, progressoDetalhado);
            stmtDesempenho.executeUpdate();
            
            System.out.println("\n✅ TREINO REGISTRADO COM SUCESSO!");
            if (exercicioValido) {
                System.out.println("👏 Parabéns! Você seguiu seu plano de treino corretamente!");
            } else {
                System.out.println("⚠️  Registro salvo, mas lembre-se de seguir o plano do seu personal!");
            }
            System.out.println();
            
        } catch (SQLException e) {
            System.out.println("❌ Erro ao registrar treino: " + e.getMessage());
        }
    }
    
    // ============================================================================
    // SEÇÃO RECEPCIONISTA - FUNCIONALIDADES DE CADASTRO E VINCULAÇÃO
    // ============================================================================
    
    /**
     * FUNCIONALIDADE RECEPCIONISTA: Cadastrar novos alunos
     * Cria usuários do tipo 'Aluno' no sistema
     * AUTOMATICO: define tipoUsuario = 'Aluno' e estaAtivo = TRUE
     */
    public void ExibirMenuCadastrarAluno() {
        System.out.println("=== Cadastrar Novo Aluno ===");
        System.out.print("Nome: ");
        String nome = leia.nextLine();
        System.out.print("Email: ");
        String email = leia.nextLine();
        System.out.print("Senha: ");
        String senha = leia.nextLine();

        try {
            // INSERT ESPECÍFICO PARA ALUNO (tipo fixo = 'Aluno')
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("INSERT INTO usuarios (nome, email, senha, tipoUsuario, estaAtivo) VALUES (?, ?, ?, 'Aluno', TRUE)");
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.executeUpdate();
            System.out.println("Aluno cadastrado com sucesso!\n");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar aluno: " + e.getMessage());
        }
    }
    
    /**
     * FUNCIONALIDADE RECEPCIONISTA: Listar personals disponíveis
     * Mostra todos os personals ativos para referência
     * USADO ANTES de vincular aluno a personal (para ver os IDs)
     */
    public void ExibirMenuPersonaisDisponiveis() {
        System.out.println("=== Personais Disponíveis ===");
        try {
            // BUSCA APENAS PERSONALS ATIVOS
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("SELECT id, nome FROM usuarios WHERE tipoUsuario = 'Personal' AND estaAtivo = TRUE");
            ResultSet rs = stmt.executeQuery();

            boolean encontrou = false;
            while (rs.next()) {
                encontrou = true;
                // MOSTRA ID E NOME (ID é necessário para vinculação)
                System.out.println("ID: " + rs.getInt("id") + " - Nome: " + rs.getString("nome"));
            }
            if (!encontrou) {
                System.out.println("Nenhum personal ativo encontrado.");
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println("Erro ao listar personais: " + e.getMessage());
        }
    }
    
    /**
     * FUNCIONALIDADE NOVA RECEPCIONISTA: Vincular aluno a personal
     * MELHORADO: Agora mostra listas antes de pedir IDs
     * Cria relacionamento entre aluno e personal
     * VALIDAÇÃO DUPLA: verifica se ambos existem e estão ativos
     * FUNCIONAMENTO: atualiza campo id_personal na tabela usuarios
     */
    public void ExibirMenuVincularAlunoPersonal() {
        System.out.println("=== Vincular Aluno a Personal ===");
        
        // PASSO 1: MOSTRAR TODOS OS ALUNOS DISPONÍVEIS (sem personal ou que podem trocar)
        System.out.println("\n📋 ALUNOS CADASTRADOS NO SISTEMA:");
        System.out.println("=" .repeat(50));
        try {
            PreparedStatement stmtAlunos = BancoDados.getConexao()
                .prepareStatement("SELECT u.id, u.nome, u.email, " +
                                "CASE WHEN u.id_personal IS NULL THEN 'SEM PERSONAL' " +
                                "     ELSE CONCAT('Personal ID: ', u.id_personal, ' - ', p.nome) END as status_personal " +
                                "FROM usuarios u " +
                                "LEFT JOIN usuarios p ON u.id_personal = p.id " +
                                "WHERE u.tipoUsuario = 'Aluno' AND u.estaAtivo = TRUE " +
                                "ORDER BY u.nome");
            ResultSet rsAlunos = stmtAlunos.executeQuery();

            boolean temAlunos = false;
            while (rsAlunos.next()) {
                temAlunos = true;
                System.out.println("ID: " + rsAlunos.getInt("id") + 
                                 " | Nome: " + rsAlunos.getString("nome") + 
                                 " | Email: " + rsAlunos.getString("email") +
                                 " | Status: " + rsAlunos.getString("status_personal"));
            }
            if (!temAlunos) {
                System.out.println("❌ Nenhum aluno cadastrado no sistema.");
                System.out.println("   Cadastre alunos primeiro na opção 1 do menu.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erro ao listar alunos: " + e.getMessage());
            return;
        }

        // PASSO 2: MOSTRAR TODOS OS PERSONALS DISPONÍVEIS
        System.out.println("\n🏃‍♂️ PERSONALS DISPONÍVEIS NO SISTEMA:");
        System.out.println("=" .repeat(50));
        try {
            PreparedStatement stmtPersonals = BancoDados.getConexao()
                .prepareStatement("SELECT p.id, p.nome, p.email, " +
                                "COUNT(a.id) as total_alunos " +
                                "FROM usuarios p " +
                                "LEFT JOIN usuarios a ON p.id = a.id_personal AND a.estaAtivo = TRUE " +
                                "WHERE p.tipoUsuario = 'Personal' AND p.estaAtivo = TRUE " +
                                "GROUP BY p.id, p.nome, p.email " +
                                "ORDER BY p.nome");
            ResultSet rsPersonals = stmtPersonals.executeQuery();

            boolean temPersonals = false;
            while (rsPersonals.next()) {
                temPersonals = true;
                System.out.println("ID: " + rsPersonals.getInt("id") + 
                                 " | Nome: " + rsPersonals.getString("nome") + 
                                 " | Email: " + rsPersonals.getString("email") +
                                 " | Alunos vinculados: " + rsPersonals.getInt("total_alunos"));
            }
            if (!temPersonals) {
                System.out.println("❌ Nenhum personal cadastrado no sistema.");
                System.out.println("   Solicite ao administrador para cadastrar personals.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("❌ Erro ao listar personals: " + e.getMessage());
            return;
        }

        // PASSO 3: AGORA PEDIR OS IDs PARA VINCULAÇÃO
        System.out.println("\n🔗 REALIZAR VINCULAÇÃO:");
        System.out.println("=" .repeat(30));
        System.out.print("Digite o ID do Aluno: ");
        int idAluno = leia.nextInt();
        leia.nextLine();
        System.out.print("Digite o ID do Personal: ");
        int idPersonal = leia.nextInt();
        leia.nextLine();

        // PASSO 4: VALIDAÇÕES E VINCULAÇÃO
        try {
            // VALIDAÇÃO CRÍTICA: Verificar se aluno e personal existem e estão ativos
            if (!BancoDados.usuarioEhDoTipo(idAluno, "Aluno") || !BancoDados.usuarioEhDoTipo(idPersonal, "Personal")) {
                System.out.println("❌ ERRO: Aluno ou Personal não encontrado ou não está ativo!");
                System.out.println("   Verifique os IDs nas listas acima e tente novamente.");
                return;
            }

            // BUSCAR NOMES PARA CONFIRMAÇÃO
            PreparedStatement stmtNomes = BancoDados.getConexao()
                .prepareStatement("SELECT " +
                                "(SELECT nome FROM usuarios WHERE id = ?) as nome_aluno, " +
                                "(SELECT nome FROM usuarios WHERE id = ?) as nome_personal");
            stmtNomes.setInt(1, idAluno);
            stmtNomes.setInt(2, idPersonal);
            ResultSet rsNomes = stmtNomes.executeQuery();
            
            if (rsNomes.next()) {
                String nomeAluno = rsNomes.getString("nome_aluno");
                String nomePersonal = rsNomes.getString("nome_personal");
                
                // CONFIRMAÇÃO ANTES DE VINCULAR
                System.out.println("\n📋 CONFIRMAÇÃO DA VINCULAÇÃO:");
                System.out.println("Aluno: " + nomeAluno + " (ID: " + idAluno + ")");
                System.out.println("Personal: " + nomePersonal + " (ID: " + idPersonal + ")");
                System.out.print("\nConfirma esta vinculação? (s/n): ");
                String confirmacao = leia.nextLine();
                
                if (confirmacao.equalsIgnoreCase("s")) {
                    // VINCULAÇÃO: chama método utilitário do BancoDados
                    if (BancoDados.vincularAlunoPersonal(idAluno, idPersonal)) {
                        System.out.println("\n✅ VINCULAÇÃO REALIZADA COM SUCESSO!");
                        System.out.println("   " + nomeAluno + " agora está vinculado(a) ao personal " + nomePersonal);
                        System.out.println("   O personal já pode criar planos de treino para este aluno.\n");
                    } else {
                        System.out.println("❌ Erro ao realizar vinculação!\n");
                    }
                } else {
                    System.out.println("❌ Vinculação cancelada pelo usuário.\n");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao vincular aluno a personal: " + e.getMessage());
        }
    }

    // ============================================================================
    // SEÇÃO PERSONAL - FUNCIONALIDADES DE TREINO E ACOMPANHAMENTO
    // ============================================================================
    
    /**
     * FUNCIONALIDADE PERSONAL: Ver alunos vinculados
     * Lista apenas os alunos que foram vinculados a este personal pela recepção
     * IMPORTANTE: Só mostra alunos que têm id_personal = ID do personal logado
     */
    public void ExibirMenuVerMeusAlunos(Usuario personal) {
        System.out.println("=== Meus Alunos ===");
        try {
            // BUSCA ALUNOS ONDE id_personal = ID do personal logado
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("SELECT id, nome, email FROM usuarios WHERE tipoUsuario = 'Aluno' AND id_personal = ? AND estaAtivo = TRUE ORDER BY nome");
            stmt.setInt(1, personal.getId());
            ResultSet rs = stmt.executeQuery();

            boolean encontrou = false;
            while (rs.next()) {
                encontrou = true;
                System.out.println("ID: " + rs.getInt("id") + " - Nome: " + rs.getString("nome") + " - Email: " + rs.getString("email"));
            }
            if (!encontrou) {
                System.out.println("Nenhum aluno vinculado encontrado.");
                System.out.println("Solicite à recepção para vincular alunos ao seu perfil.");
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println("Erro ao listar alunos: " + e.getMessage());
        }
    }
    
    /**
     * FUNCIONALIDADE PERSONAL: Criar/editar planos de treino
     * Personal cria plano personalizado para um aluno específico
     * VALIDAÇÃO: Verifica se aluno existe antes de criar plano
     * ON DUPLICATE KEY UPDATE: Se já existe plano, atualiza; senão, cria novo
     */
    public void ExibirMenuCadastroPlano() {
        System.out.println("=== Criar ou Editar Plano de Treino ===");
        System.out.print("ID do Aluno: ");
        int idAluno = leia.nextInt();
        leia.nextLine();
        
        // VALIDAÇÃO CRÍTICA: Verificar se o aluno existe e está ativo
        if (!BancoDados.usuarioEhDoTipo(idAluno, "Aluno")) {
            System.out.println("ERRO: Aluno não encontrado ou não está ativo!");
            return;
        }
        
        System.out.print("Descrição do Plano: ");
        String plano = leia.nextLine();

        try {
            // INSERT + UPDATE em uma query (MySQL specific)
            // Se já existe plano para este aluno, atualiza; senão, cria novo
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("INSERT INTO planos_treino (id_aluno, descricao) VALUES (?, ?) ON DUPLICATE KEY UPDATE descricao = ?");
            stmt.setInt(1, idAluno);
            stmt.setString(2, plano);
            stmt.setString(3, plano); // valor para o UPDATE
            stmt.executeUpdate();
            System.out.println("Plano de treino atualizado com sucesso!\n");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar plano: " + e.getMessage());
        }
    }

    /**
     * FUNCIONALIDADE PERSONAL: Acompanhar progresso de aluno
     * Mostra histórico de desempenho/treinos de um aluno específico
     * USADO PELO PERSONAL para monitorar evolução dos alunos
     */
    public void ExibirMenuAcompanharAluno() {
        System.out.println("=== Acompanhar Progresso do Aluno ===");
        System.out.print("ID do Aluno: ");
        int idAluno = leia.nextInt();
        leia.nextLine();
        
        // VALIDAÇÃO: Verificar se o aluno existe e está ativo
        if (!BancoDados.usuarioEhDoTipo(idAluno, "Aluno")) {
            System.out.println("ERRO: Aluno não encontrado ou não está ativo!");
            return;
        }
        
        try {
            // BUSCA HISTÓRICO DE DESEMPENHO (últimos 5 registros)
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("SELECT data, progresso FROM desempenho WHERE id_aluno = ? ORDER BY data DESC LIMIT 5");
            stmt.setInt(1, idAluno);
            ResultSet rs = stmt.executeQuery();

            boolean temDados = false;
            System.out.println("=== Histórico de Desempenho do Aluno ID " + idAluno + " ===");
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
            System.out.println("Erro ao exibir progresso: " + e.getMessage());
        }
    }

    // ============================================================================
    // SEÇÃO ADMINISTRADOR - FUNCIONALIDADES COMPLETAS DE GESTÃO (CRUD)
    // ============================================================================

    /**
     * FUNCIONALIDADE ADMIN: Cadastrar Personal
     * Chama método genérico passando tipo "Personal"
     */
    public void ExibirMenuCadastrarPersonal() {
        System.out.println("=== Cadastrar Personal ===");
        CadastrarUsuarioComTipo("Personal");
    }

    /**
     * FUNCIONALIDADE ADMIN: Cadastrar Recepcionista
     * Chama método genérico passando tipo "Recepcionista"
     */
    public void ExibirMenuCadastrarRecepcao() {
        System.out.println("=== Cadastrar Recepcionista ===");
        CadastrarUsuarioComTipo("Recepcionista");
    }

    /**
     * FUNCIONALIDADE ADMIN: Cadastrar outro Administrador
     * Chama método genérico passando tipo "Administrador"
     */
    public void ExibirMenuCadastrarAdmin() {
        System.out.println("=== Cadastrar Administrador ===");
        CadastrarUsuarioComTipo("Administrador");
    }

    /**
     * MÉTODO AUXILIAR: Cadastra usuário de qualquer tipo
     * Usado pelas 3 funções de cadastro do admin (DRY principle - Don't Repeat Yourself)
     * REUTILIZAÇÃO DE CÓDIGO: mesmo código serve para Personal, Recepcionista e Admin
     * @param tipo - qual tipo de usuário será criado
     */
    private void CadastrarUsuarioComTipo(String tipo) {
        System.out.print("Nome: ");
        String nome = leia.nextLine();
        System.out.print("Email: ");
        String email = leia.nextLine();
        System.out.print("Senha: ");
        String senha = leia.nextLine();

        try {
            // INSERT GENÉRICO - tipo é passado como parâmetro
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("INSERT INTO usuarios (nome, email, senha, tipoUsuario, estaAtivo) VALUES (?, ?, ?, ?, TRUE)");
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, tipo); // tipo vem do parâmetro
            stmt.executeUpdate();
            System.out.println(tipo + " cadastrado com sucesso!\n");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar " + tipo.toLowerCase() + ": " + e.getMessage());
        }
    }

    /**
     * FUNCIONALIDADE NOVA ADMIN: Listar todos os usuários do sistema
     * Mostra ID, nome, email, tipo e status (ativo/inativo) de TODOS os usuários
     * ORDENAÇÃO: por ID crescente (1, 2, 3, 4...) - mais organizado e lógico
     * USADO PARA: admin ver quem está no sistema antes de editar/desativar
     */
    public void ExibirMenuListarUsuarios() {
        System.out.println("=== Lista de Usuários ===");
        try {
            // SELECT DE TODOS OS USUÁRIOS ORDENADOS POR ID CRESCENTE
            PreparedStatement stmt = BancoDados.getConexao()
                .prepareStatement("SELECT id, nome, email, tipoUsuario, estaAtivo FROM usuarios ORDER BY id ASC");
            ResultSet rs = stmt.executeQuery();

            // LOOP PARA MOSTRAR CADA USUÁRIO EM ORDEM CRESCENTE DE ID
            while (rs.next()) {
                // CONVERSÃO boolean → String para exibição
                String status = rs.getBoolean("estaAtivo") ? "ATIVO" : "INATIVO";
                System.out.println("ID: " + rs.getInt("id") + 
                                 " | Nome: " + rs.getString("nome") + 
                                 " | Email: " + rs.getString("email") + 
                                 " | Tipo: " + rs.getString("tipoUsuario") + 
                                 " | Status: " + status);
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }
    }

    /**
     * FUNCIONALIDADE NOVA ADMIN: Desativar/Ativar usuários (soft delete)
     * Em vez de DELETE (que remove permanentemente), usamos UPDATE estaAtivo
     * REGRA DE NEGÓCIO IMPORTANTE: Admin NÃO pode se auto-desativar!
     * VANTAGEM: usuário "deletado" pode ser reativado depois
     * @param adminLogado - usuario admin que está fazendo a operação
     */
    public void ExibirMenuDesativarAtivarUsuario(Usuario adminLogado) {
        System.out.println("=== Desativar/Ativar Usuário ===");
        System.out.print("ID do usuário: ");
        int idUsuario = leia.nextInt();
        leia.nextLine();

        // PROTEÇÃO CRÍTICA: Impede auto-desativação
        if (idUsuario == adminLogado.getId()) {
            System.out.println("ERRO: Você não pode desativar sua própria conta!");
            return; // SAI DO MÉTODO
        }

        try {
            // PASSO 1: Buscar dados do usuário para confirmação
            PreparedStatement stmtVerifica = BancoDados.getConexao()
                .prepareStatement("SELECT nome, estaAtivo FROM usuarios WHERE id = ?");
            stmtVerifica.setInt(1, idUsuario);
            ResultSet rs = stmtVerifica.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                boolean estaAtivo = rs.getBoolean("estaAtivo");
                String acao = estaAtivo ? "desativar" : "ativar"; // define ação baseada no status atual
                
                // PASSO 2: Mostrar dados e pedir confirmação
                System.out.println("Usuário: " + nome + " (Status atual: " + (estaAtivo ? "ATIVO" : "INATIVO") + ")");
                System.out.print("Confirma " + acao + " este usuário? (s/n): ");
                String confirmacao = leia.nextLine();
                
                // PASSO 3: Se confirmou, executa a operação
                if (confirmacao.equalsIgnoreCase("s")) {
                    PreparedStatement stmtUpdate = BancoDados.getConexao()
                        .prepareStatement("UPDATE usuarios SET estaAtivo = ? WHERE id = ?");
                    stmtUpdate.setBoolean(1, !estaAtivo); // inverte o status (true vira false, false vira true)
                    stmtUpdate.setInt(2, idUsuario);
                    stmtUpdate.executeUpdate();
                    
                    System.out.println("Usuário " + (estaAtivo ? "desativado" : "ativado") + " com sucesso!\n");
                } else {
                    System.out.println("Operação cancelada.\n");
                }
            } else {
                System.out.println("Usuário não encontrado!\n");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao alterar status do usuário: " + e.getMessage());
        }
    }

    /**
     * FUNCIONALIDADE NOVA ADMIN: Editar dados de usuários
     * Permite alterar nome, email e senha de qualquer usuário
     * FLEXIBILIDADE: campos opcionais (ENTER para manter valor atual)
     * MÚLTIPLOS UPDATES: cada campo alterado gera um UPDATE separado
     */
    public void ExibirMenuEditarUsuario() {
        System.out.println("=== Editar Usuário ===");
        System.out.print("ID do usuário: ");
        int idUsuario = leia.nextInt();
        leia.nextLine();

        try {
            // PASSO 1: Buscar dados atuais do usuário
            PreparedStatement stmtVerifica = BancoDados.getConexao()
                .prepareStatement("SELECT nome, email, tipoUsuario FROM usuarios WHERE id = ?");
            stmtVerifica.setInt(1, idUsuario);
            ResultSet rs = stmtVerifica.executeQuery();

            if (rs.next()) {
                // PASSO 2: Mostrar dados atuais
                System.out.println("Dados atuais:");
                System.out.println("Nome: " + rs.getString("nome"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Tipo: " + rs.getString("tipoUsuario"));
                
                // PASSO 3: Coletar novos dados (opcionais)
                System.out.print("Novo nome (ou ENTER para manter): ");
                String novoNome = leia.nextLine();
                System.out.print("Novo email (ou ENTER para manter): ");
                String novoEmail = leia.nextLine();
                System.out.print("Nova senha (ou ENTER para manter): ");
                String novaSenha = leia.nextLine();
                
                // PASSO 4: Executar UPDATEs apenas para campos preenchidos
                if (!novoNome.trim().isEmpty()) {
                    PreparedStatement stmtNome = BancoDados.getConexao()
                        .prepareStatement("UPDATE usuarios SET nome = ? WHERE id = ?");
                    stmtNome.setString(1, novoNome);
                    stmtNome.setInt(2, idUsuario);
                    stmtNome.executeUpdate();
                }
                
                if (!novoEmail.trim().isEmpty()) {
                    PreparedStatement stmtEmail = BancoDados.getConexao()
                        .prepareStatement("UPDATE usuarios SET email = ? WHERE id = ?");
                    stmtEmail.setString(1, novoEmail);
                    stmtEmail.setInt(2, idUsuario);
                    stmtEmail.executeUpdate();
                }
                
                if (!novaSenha.trim().isEmpty()) {
                    PreparedStatement stmtSenha = BancoDados.getConexao()
                        .prepareStatement("UPDATE usuarios SET senha = ? WHERE id = ?");
                    stmtSenha.setString(1, novaSenha);
                    stmtSenha.setInt(2, idUsuario);
                    stmtSenha.executeUpdate();
                }
                
                System.out.println("Usuário editado com sucesso!\n");
            } else {
                System.out.println("Usuário não encontrado!\n");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao editar usuário: " + e.getMessage());
        }
    }
}
