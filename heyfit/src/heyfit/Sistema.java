package heyfit;

public class Sistema{
	public static void main (String args[])
	{
		Menu menu = new Menu();
		BancoDados bancoDados = new BancoDados();
		BancoDados.getConexao();
		
		//ao iniciar exiba o primeiro menu
		menu.ExibirMenuInicial();
		

	}
}
