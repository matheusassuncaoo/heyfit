package heyfit;

/**
 * CLASSE USUARIO - MODELO DE DADOS DOS USUÁRIOS DO SISTEMA
 * Esta é a classe principal que representa qualquer usuário do HeyFit
 * Pode ser: Aluno, Personal, Recepcionista ou Administrador
 * IMPORTANTE: Todos herdam desta mesma classe (polimorfismo por enum)
 */
public class Usuario {
    // ATRIBUTOS PRIVADOS - ENCAPSULAMENTO (boa prática POO)
    private int id; // ID único do usuário no banco de dados
    private String nome; // nome completo do usuário
    private String email; // email para login (deve ser único)
    private String senha; // senha para login (idealmente deveria ser criptografada)
    private boolean estaAtivo; // Campo para controlar se o usuário está ativo (true) ou desativado (false)
    
    /**
     * ENUM - TIPOS DE USUÁRIO PERMITIDOS NO SISTEMA
     * Enum garante que só esses 4 tipos existam (segurança de tipos)
     * Corresponde exatamente ao ENUM do banco de dados
     */
    public enum tipo {
        Aluno,          // usuário que faz treinos
        Personal,       // usuário que cria planos e acompanha alunos
        Recepcionista,  // usuário que cadastra alunos e vincula a personals
        Administrador   // usuário com acesso total ao sistema
    }
    tipo tipoUsuario; // qual tipo este usuário é (público para facilitar acesso)

    /**
     * CONSTRUTOR PADRÃO - PARA NOVOS USUÁRIOS
     * Usado quando criamos um usuário novo (registro)
     * Por padrão, todo usuário novo é criado ATIVO
     * @param id - ID do usuário (vem do banco após INSERT)
     * @param nome - nome completo
     * @param email - email único
     * @param senha - senha escolhida
     * @param tipoUsuario - que tipo de usuário será
     */
    public Usuario(int id, String nome, String email, String senha, tipo tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha; 
        this.tipoUsuario = tipoUsuario;
        this.estaAtivo = true; // Por padrão, usuário criado está ativo
    }

    /**
     * CONSTRUTOR COMPLETO - PARA USUÁRIOS DO BANCO
     * Usado quando buscamos um usuário que já existe no banco
     * Inclui o campo estaAtivo que vem do banco de dados
     * @param id - ID do usuário
     * @param nome - nome completo
     * @param email - email único  
     * @param senha - senha
     * @param tipoUsuario - tipo do usuário
     * @param estaAtivo - se está ativo ou não
     */
    public Usuario(int id, String nome, String email, String senha, tipo tipoUsuario, boolean estaAtivo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha; 
        this.tipoUsuario = tipoUsuario;
        this.estaAtivo = estaAtivo;
    }

    // ============ MÉTODOS GETTERS E SETTERS (ENCAPSULAMENTO) ============
    // Estes métodos permitem acesso controlado aos atributos privados
    // GET = pegar o valor / SET = definir o valor

    /**
     * GETTER: Retorna o ID do usuário
     * Usado para identificar o usuário nas operações do banco
     */
	public int getId() {
		return id;
	}

    /**
     * SETTER: Define o ID do usuário
     * Normalmente usado após inserir no banco e receber o ID gerado
     */
	public void setId(int id) {
		this.id = id;
	}

    /**
     * GETTER: Retorna o nome do usuário
     * Usado para exibir na tela e relatórios
     */
	public String getNome() {
		return nome;
	}

    /**
     * SETTER: Define/altera o nome do usuário
     * Usado quando admin edita dados do usuário
     */
	public void setNome(String nome) {
		this.nome = nome;
	}

    /**
     * GETTER: Retorna o email do usuário
     * Usado para login e identificação
     */
	public String getEmail() {
		return email;
	}

    /**
     * SETTER: Define/altera o email do usuário
     * CUIDADO: email deve ser único no sistema!
     */
	public void setEmail(String email) {
		this.email = email;
	}

    /**
     * GETTER: Retorna a senha do usuário
     * ATENÇÃO: em sistema real, senha deveria ser criptografada
     */
	public String getSenha() {
		return senha;
	}

    /**
     * SETTER: Define/altera a senha do usuário
     * Usado quando usuário ou admin muda a senha
     */
	public void setSenha(String senha) {
		this.senha = senha;
	}

    /**
     * GETTER: Retorna o tipo do usuário (enum)
     * Usado para determinar qual menu mostrar e quais permissões tem
     */
	public tipo getTipoUsuario() {
		return tipoUsuario;
	}

    /**
     * SETTER: Define/altera o tipo do usuário
     * CUIDADO: mudança de tipo pode quebrar regras de negócio!
     */
	public void setTipoUsuario(tipo tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

    /**
     * GETTER: Verifica se usuário está ativo
     * Usado para validar se pode fazer login e usar o sistema
     */
	public boolean isEstaAtivo() {
		return estaAtivo;
	}

    /**
     * SETTER: Ativa ou desativa o usuário
     * Usado pelo admin para "excluir" sem realmente deletar do banco
     * REGRA: Admin não pode se auto-desativar!
     */
	public void setEstaAtivo(boolean estaAtivo) {
		this.estaAtivo = estaAtivo;
	}
    
    /**
     * MÉTODO OPCIONAL: toString() para debug
     * Facilita na hora de imprimir objeto User para testes
     * Não mostra senha por segurança
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipoUsuario=" + tipoUsuario +
                ", estaAtivo=" + estaAtivo +
                '}';
    }
}
