package com.biblioteca.util;

import com.biblioteca.entities.Book;

public class BookCreator {
	
	public static Book createValidBook() {
		return Book.builder()
				.id(1l)
				.title("o poder da acao")
				.yearPublication(DateConvert.convertData("2022/09/15"))
				.userDomain(UserDomainCreator.createUserDomainWithRoleADMIN())
				.build();
	}
	
	public static Book createBookToBeSaved() {
		return Book.builder()
				.title("o poder da acao")
				.yearPublication(DateConvert.convertData("2022/09/15"))
				.build();
	}
	
	public static Book createUpdatedBook() {
		return Book.builder()
				.id(1l)
				.title("o poder da acao 2")
				.yearPublication(DateConvert.convertData("2024/04/14"))
				.build();
	}
}
