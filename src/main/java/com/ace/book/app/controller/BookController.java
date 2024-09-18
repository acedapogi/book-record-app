package com.ace.book.app.controller;

import com.ace.book.app.data.Book;
import com.ace.book.app.data.BookRepository;
import jakarta.validation.Valid;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="/book")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Book getBookById(@PathVariable(value = "id") Long bookId){
        return bookRepository.findById(bookId).get();
    }

    @PostMapping
    public Book createBook(@RequestBody @Valid Book book){
        return bookRepository.save(book);
    }

    @PutMapping
    public Book updateBook(@RequestBody @Valid Book book) throws NotFoundException {
        if(book == null || book.getId() ==null){
            throw new NotFoundException("Book or bookId must not be null");
        }
        Optional<Book> optionalBook = bookRepository.findById(book.getId());

        if(optionalBook.isEmpty()){
            throw new NotFoundException("Book with booId"+book.getId()+"does not exist");
        }

        Book existingBook = optionalBook.get();
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setRating(book.getRating());
        return bookRepository.save(existingBook);
    }

    //TO-DO delete API using TDD approach
    @DeleteMapping(value = "/{id}")
    public void deleteBookById(@PathVariable(value = "id") Long id) throws NotFoundException {
        if(bookRepository.findById(id).isEmpty()){
            throw new NotFoundException("The book with is not found: "+id);
        }
        bookRepository.deleteById(id);
    }


}
