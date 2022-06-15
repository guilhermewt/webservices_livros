package com.biblioteca.config;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.biblioteca.entities.Emprestimo;
import com.biblioteca.entities.Livro;
import com.biblioteca.entities.Usuario;
import com.biblioteca.repository.RepositorioEmprestimo;
import com.biblioteca.repository.RepositorioLivro;
import com.biblioteca.repository.RepositorioUsuario;

@Configuration
public class configuracoes implements CommandLineRunner{

	@Autowired
	private RepositorioUsuario repositorioUsuario;
	
	@Autowired
	private RepositorioLivro repositorioLivro;
	
	@Autowired
	private RepositorioEmprestimo repositorioEmprestimo;
	
	@Override
	public void run(String... args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Usuario user1 = new Usuario(1l,"rafael silva", "rafa@gmail","rafa silva","123");
		Usuario user2 = new Usuario(2l,"marcos", "marcos@gmail","marcos","1234567");
		Usuario user3 = new Usuario(3l,"tony", "tony@gmail","tony","1234567");
		
		repositorioUsuario.saveAll(Arrays.asList(user1,user2,user3));
		
		Livro lv1 = new Livro(1l,"the lord of the kings" , sdf.parse("2009/05/26"));
		Livro lv2 = new Livro(2l,"o poder da acao" , sdf.parse("2012/04/01"));
		lv1.setUsuario(user1);
		lv2.setUsuario(user2); 
		
		repositorioLivro.saveAll(Arrays.asList(lv1,lv2));
		
		Emprestimo emp1 = new Emprestimo(1l, sdf.parse("2022/05/22"), sdf.parse("2022/06/26"));
		emp1.setUsuario(user1);
		emp1.getLivros().add(lv1);
		
		repositorioEmprestimo.save(emp1);
		
	}

}
