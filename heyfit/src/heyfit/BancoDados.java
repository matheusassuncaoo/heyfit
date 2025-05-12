package heyfit;

import java.sql.*; //Extremamente necessario está importação

public class BancoDados {
	private static final String url = "jdbc:mysql://localhost:3306/heyfit?createDatabaseIfNotExist=true";
    private static final String user = "root";
    private static final String password = "";
    private static Connection conexao = null;
    
    public static Connection getConexao() {
        if (conexao == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexao = DriverManager.getConnection(url, user, password);
                System.out.println("Conexão realizada com sucesso!");
            } catch (ClassNotFoundException e) {
                System.out.println("Driver do SQL não encontrado: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Erro ao conectar com o banco de dados: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return conexao;
    }

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
	
}