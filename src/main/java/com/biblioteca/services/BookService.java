package com.biblioteca.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.biblioteca.entities.Book;
import com.biblioteca.entities.UserDomain;
import com.biblioteca.enums.StatusBook;
import com.biblioteca.mapper.BookMapper;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.GenrerRepository;
import com.biblioteca.repository.UserDomainRepository;
import com.biblioteca.requests.BookPostRequestBody;
import com.biblioteca.requests.BookPutRequestBody;
import com.biblioteca.services.exceptions.BadRequestException;
import com.biblioteca.services.utilService.GetUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;

	private final UserDomainRepository userDomainRepository;
	
	private final GenrerRepository genrerRepository;
 
	private final GetUserDetails userAuthenticated;

	public List<Book> findAllNonPageable() {
		return bookRepository.findByUserDomainId(userAuthenticated.userAuthenticated().getId());
	}
	
	public Page<Book> findAll(Pageable pageable) {
		return bookRepository.findByUserDomainId(userAuthenticated.userAuthenticated().getId(), pageable);
	}
	
	public List<Book> findAllBooksByStatusNonPageable(StatusBook statusBook) {
		List<Book> books = bookRepository.findByUserDomainId(userAuthenticated.userAuthenticated().getId());
		
		return books.stream().filter(x -> x.getStatusBook() == statusBook).collect(Collectors.toList());
	}

	public Book findByIdOrElseThrowResourceNotFoundException(Long idBook) {
		return  bookRepository.findAuthenticatedUserBooksById(idBook, userAuthenticated.userAuthenticated().getId())
				.orElseThrow(() -> new BadRequestException("book not found"));
	}
	
	public List<Book> findByTitle(String title){
		return bookRepository.findAuthenticatedUserBooksByTitle(title,userAuthenticated.userAuthenticated().getId());
	}
	
	public List<Book> findByGenrer(String genrer){	
		return bookRepository.findByUserDomainIdAndGenrersNameContainingIgnoreCase(
				userAuthenticated.userAuthenticated().getId(), genrer);
	}
	
	public List<Book> findByAuthors(String author){	
		return bookRepository.findByUserDomainIdAndAuthorsContainingIgnoreCase(
				userAuthenticated.userAuthenticated().getId(), author);
	}

	@Transactional
	public Book save(BookPostRequestBody bookPostRequestBody) {
		UserDomain userDomain = userDomainRepository.findById(userAuthenticated.userAuthenticated().getId()).get();	
		Book book = BookMapper.INSTANCE.toBook(bookPostRequestBody);
		
		book.setUserDomain(userDomain);	

		validationBook(book);
		
		return bookRepository.save(book);
	}
	
	public void validationBook(Book book) {
		boolean bookExist = bookRepository.findAll()
				    .stream()
					.anyMatch((x) -> x.getExternalCode().equals(book.getExternalCode()) && x.getUserDomain() == book.getUserDomain() || x.equals(book));
	
		if(bookExist) {
				throw new BadRequestException("the book already exist");
		}
		
		if(!genrerRepository.findAll().containsAll(book.getGenrers())) {
			throw new BadRequestException("Genrer not found");
	    }

	}
	
	@Transactional
	public void delete(Long idBook) {
		try {
			bookRepository.deleteAuthenticatedUserBookById(findByIdOrElseThrowResourceNotFoundException(idBook)
					.getId(),userAuthenticated.userAuthenticated()
					.getId());
		} catch (DataIntegrityViolationException e) {
			throw new BadRequestException(e.getMessage());
		}
	}
	
	@Transactional
	public void update(BookPutRequestBody bookPutRequestBody) {
		UserDomain userDomain = userDomainRepository.findById(userAuthenticated.userAuthenticated().getId()).get();

		
		Book bookSaved = bookRepository.findAuthenticatedUserBooksById(bookPutRequestBody.getId(), 
				userAuthenticated.userAuthenticated()
				.getId())
				.orElseThrow(() -> new BadRequestException("book not found"));
		
		
		Book book = BookMapper.INSTANCE.toBook(bookPutRequestBody);
		book.setId(bookSaved.getId());
		book.setUserDomain(userDomain);	
		bookRepository.save(book);
	}
}
