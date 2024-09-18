package com.ace.book.app;

import com.ace.book.app.controller.BookController;
import com.ace.book.app.data.Book;
import com.ace.book.app.data.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(MockitoJUnitRunner.class)
public class BookControllerTest {

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookController bookController;

    //test data
    Book BOOK_RECORD_1 = new Book(101L, "Book 101", "Author 101", 3);
    Book BOOK_RECORD_2 = new Book(102L, "Book 102", "Author 102", 8);
    Book BOOK_RECORD_3= new Book(103L, "Book 103", "Author 103", 5);


    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        this.mockMvc = standaloneSetup(bookController).build();
    }

    @Test
    public void getAllBooks_success() throws Exception{
        List<Book> books = new ArrayList<>(Arrays.asList(BOOK_RECORD_1,BOOK_RECORD_2,BOOK_RECORD_3));

        Mockito.when(bookRepository.findAll()).thenReturn(books);

        mockMvc.perform(get("/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title", is("Book 101")))
                .andExpect(jsonPath("$[1].title", is("Book 102")))
                .andExpect(jsonPath("$[2].title", is("Book 103")));

    }

    @Test
    public void getBookById_success() throws Exception{

        Mockito.when(bookRepository.findById(BOOK_RECORD_1.getId())).thenReturn(Optional.of(BOOK_RECORD_1));

        mockMvc.perform(get("/book/101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.title", is("Book 101")));

    }

    @Test
    public void createBook_success() throws Exception{

        //build new record to create
        Book book = Book.builder()
                .id(1004L)
                .title("Book 104")
                .author("Author 104")
                .rating(7).build();

        Mockito.when(bookRepository.save(book)).thenReturn(book);

        String body = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book 104"));

    }

    @Test
    public void updateBook_success() throws Exception{

        //build new record to create
        Book updatedBook = Book.builder()
                .id(101L)
                .title("Book 101 - Updated Edition")
                .author("Author 1")
                .rating(10).build();

        Mockito.when(bookRepository.findById(BOOK_RECORD_1.getId())).thenReturn(Optional.ofNullable(BOOK_RECORD_1));
        Mockito.when(bookRepository.save(updatedBook)).thenReturn(updatedBook);

        String body = objectMapper.writeValueAsString(updatedBook);

        mockMvc.perform(MockMvcRequestBuilders.put("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book 101 - Updated Edition"));

    }

}
