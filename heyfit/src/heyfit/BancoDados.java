package heyfit;

import java.sql.*; //Extremamente necessario está importação

/**
 * CLASSE BANCO DE DADOS - RESPONSÁVEL POR TODA CONEXÃO COM MYSQL
 * Esta classe gerencia a conexão com o banco de dados MySQL e criação das tabelas
 * IMPORTANTE: Precisa do XAMPP rodando e banco "heyfit" criado!
 */
public class BancoDados {
    // CONFIGURAÇÕES DO BANCO - ALTERE AQUI SE NECESSÁRIO
	private static final String url = "jdbc:mysql://localhost:3306/heyfit?createDatabaseIfNotExist=true";
    private static final String user = "root"; // usuário padrão do XAMPP
    private static final String password = ""; // senha vazia no XAMPP
    private static Connection conexao = null; // conexão global (singleton)
    
    /**
     * MÉTODO PRINCIPAL DE CONEXÃO - SINGLETON PATTERN
     * Só cria uma conexão durante toda execução do programa
     * Se já existe conexão, retorna a mesma (economia de recursos)
     */
    public static Connection getConexao() {
        // Verifica se ainda não tem conexão criada
        if (conexao == null) {
            try {
                // PASSO 1: Carregar o driver do MySQL (obrigatório!)
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // PASSO 2: Estabelecer conexão com banco
                conexao = DriverManager.getConnection(url, user, password);
                
                // PASSO 3: Criar todas as tabelas necessárias
                CriarTabelas();
                System.out.println("Conexão realizada com sucesso!");
                
            } catch (ClassNotFoundException e) {
                // ERRO: Driver do MySQL não foi encontrado (falta mysql-connector-java.jar)
                System.out.println("Driver do SQL não encontrado: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                // ERRO: Problema na conexão (XAMPP parado, banco inexistente, etc)
                System.out.println("Erro ao conectar com o banco de dados: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return conexao; // retorna a conexão para uso
    }

    /**
     * MÉTODO PARA FECHAR CONEXÃO - BOA PRÁTICA
     * Sempre feche conexões para liberar recursos do sistema
     */
    public static void fecharConexao() {
        if (conexao != null) {
            try {
                conexao.close();
                System.out.println("Conexão fechada com sucesso!");
            } catch (SQLException e) {
                System.out.println("Erro ao fechar a conexão: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * MÉTODO QUE CRIA TODAS AS TABELAS DO SISTEMA
     * É executado automaticamente quando conecta no banco
     * CREATE TABLE IF NOT EXISTS = só cria se não existir (seguro)
     */
    public static void CriarTabelas() {
        // TABELA PRINCIPAL: usuarios
        // Armazena todos os tipos de usuário (Aluno, Personal, Recepcionista, Admin)
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," + // ID único automático
                "nome VARCHAR(100) NOT NULL," + // nome obrigatório
                "email VARCHAR(100) NOT NULL UNIQUE," + // email único (não pode repetir)
                "senha VARCHAR(100) NOT NULL," + // senha obrigatória
                "tipoUsuario ENUM('Aluno', 'Personal', 'Recepcionista', 'Administrador') NOT NULL," + // tipo restrito
                "estaAtivo BOOLEAN DEFAULT TRUE," + // status ativo/inativo (padrão: ativo)
                "id_personal INT DEFAULT NULL," + // ID do personal vinculado (só para alunos)
                "FOREIGN KEY (id_personal) REFERENCES usuarios(id) ON DELETE SET NULL" + // chave estrangeira
                ");";
        
        // TABELA: planos_treino
        // Armazena os planos de treino criados pelos personais para os alunos
        String sqlPlanosTreino = "CREATE TABLE IF NOT EXISTS planos_treino (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "id_aluno INT NOT NULL," + // qual aluno tem esse plano
                "id_personal INT," + // qual personal criou
                "descricao TEXT NOT NULL," + // descrição do plano (pode ser longa)
                "data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," + // quando foi criado
                "FOREIGN KEY (id_aluno) REFERENCES usuarios(id) ON DELETE CASCADE," + // se aluno deletado, plano também
                "FOREIGN KEY (id_personal) REFERENCES usuarios(id) ON DELETE SET NULL," + // se personal deletado, id fica null
                "UNIQUE KEY unique_aluno_plano (id_aluno)" + // um aluno só pode ter um plano
                ");";
        
        // TABELA: desempenho
        // Histórico de progresso dos alunos (alimentada pelos treinos registrados)
        String sqlDesempenho = "CREATE TABLE IF NOT EXISTS desempenho (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "id_aluno INT NOT NULL," + // qual aluno
                "data DATE NOT NULL," + // data do progresso
                "progresso TEXT," + // descrição do progresso
                "observacoes TEXT," + // observações extras
                "FOREIGN KEY (id_aluno) REFERENCES usuarios(id) ON DELETE CASCADE" + // se aluno deletado, progresso também
                ");";
        
        // TABELA: treinos_realizados
        // Registra os treinos que os alunos fazem no dia a dia
        String sqlTreinosRealizados = "CREATE TABLE IF NOT EXISTS treinos_realizados (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "id_aluno INT NOT NULL," + // qual aluno fez o treino
                "data_treino DATE NOT NULL," + // que dia foi o treino
                "exercicios_realizados TEXT," + // quais exercícios fez
                "tempo_treino INT," + // quanto tempo treinou (em minutos)
                "observacoes_aluno TEXT," + // observações do próprio aluno
                "data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP," + // quando foi registrado no sistema
                "FOREIGN KEY (id_aluno) REFERENCES usuarios(id) ON DELETE CASCADE" + // se aluno deletado, treinos também
                ");";
        
        // EXECUTAR CRIAÇÃO DAS TABELAS
        try (Statement stmt = conexao.createStatement()) {
            // Executa cada comando SQL de criação
            stmt.execute(sqlUsuarios);
            System.out.println("Tabela 'usuarios' verificada/criada com sucesso.");
            
            stmt.execute(sqlPlanosTreino);
            System.out.println("Tabela 'planos_treino' verificada/criada com sucesso.");
            
            stmt.execute(sqlDesempenho);
            System.out.println("Tabela 'desempenho' verificada/criada com sucesso.");
            
            stmt.execute(sqlTreinosRealizados);
            System.out.println("Tabela 'treinos_realizados' verificada/criada com sucesso.");
            
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabelas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * MÉTODO UTILITÁRIO: Verifica se usuário existe e está ativo
     * Usado em validações antes de fazer operações
     * @param idUsuario - ID do usuário para verificar
     * @return true se existe e está ativo, false caso contrário
     */
    public static boolean usuarioExisteEAtivo(int idUsuario) {
        try {
            // PreparedStatement previne SQL Injection (boa prática de segurança)
            PreparedStatement stmt = getConexao()
                .prepareStatement("SELECT estaAtivo FROM usuarios WHERE id = ?");
            stmt.setInt(1, idUsuario); // substitui o ? pelo idUsuario
            ResultSet rs = stmt.executeQuery(); // executa a consulta
            
            // Se encontrou o usuário
            if (rs.next()) {
                return rs.getBoolean("estaAtivo"); // retorna se está ativo
            }
            return false; // usuário não existe
        } catch (SQLException e) {
            System.out.println("Erro ao verificar usuário: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * MÉTODO UTILITÁRIO: Verifica se usuário é de um tipo específico E está ativo
     * Usado para validar se um ID realmente corresponde ao tipo esperado
     * @param idUsuario - ID do usuário
     * @param tipo - tipo esperado ("Aluno", "Personal", etc)
     * @return true se é do tipo e está ativo
     */
    public static boolean usuarioEhDoTipo(int idUsuario, String tipo) {
        try {
            PreparedStatement stmt = getConexao()
                .prepareStatement("SELECT tipoUsuario FROM usuarios WHERE id = ? AND estaAtivo = TRUE");
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Compara o tipo do banco com o tipo esperado
                return rs.getString("tipoUsuario").equals(tipo);
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Erro ao verificar tipo de usuário: " + e.getMessage());
            return false;
        }
    }
	
    /**
     * MÉTODO ESPECÍFICO: Busca alunos vinculados a um personal
     * Usado no menu do personal para mostrar "Meus Alunos"
     * @param idPersonal - ID do personal
     * @return ResultSet com os alunos vinculados
     */
    public static ResultSet getAlunosDoPersonal(int idPersonal) {
        try {
            PreparedStatement stmt = getConexao()
                .prepareStatement("SELECT id, nome, email FROM usuarios WHERE tipoUsuario = 'Aluno' AND id_personal = ? AND estaAtivo = TRUE ORDER BY nome");
            stmt.setInt(1, idPersonal);
            return stmt.executeQuery();
        } catch (SQLException e) {
            System.out.println("Erro ao buscar alunos do personal: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * MÉTODO ESPECÍFICO: Vincula um aluno a um personal
     * Usado pela recepção para fazer a associação aluno-personal
     * @param idAluno - ID do aluno
     * @param idPersonal - ID do personal
     * @return true se vinculação foi bem sucedida
     */
    public static boolean vincularAlunoPersonal(int idAluno, int idPersonal) {
        try {
            // UPDATE modifica o campo id_personal do aluno
            PreparedStatement stmt = getConexao()
                .prepareStatement("UPDATE usuarios SET id_personal = ? WHERE id = ? AND tipoUsuario = 'Aluno'");
            stmt.setInt(1, idPersonal); // novo personal
            stmt.setInt(2, idAluno); // qual aluno
            int rowsAffected = stmt.executeUpdate(); // executa o UPDATE
            return rowsAffected > 0; // se modificou alguma linha, deu certo
        } catch (SQLException e) {
            System.out.println("Erro ao vincular aluno ao personal: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * MÉTODO ESPECÍFICO: Conta quantos administradores ativos existem no sistema
     * Usado para validar se um admin pode se auto desativar
     * @return número de administradores ativos
     */
    public static int contarAdministradoresAtivos() {
        try {
            PreparedStatement stmt = getConexao()
                .prepareStatement("SELECT COUNT(*) as total FROM usuarios WHERE tipoUsuario = 'Administrador' AND estaAtivo = TRUE");
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("Erro ao contar administradores ativos: " + e.getMessage());
            return 0;
        }
    }
}
