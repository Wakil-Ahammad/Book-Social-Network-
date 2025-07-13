import {Component, OnInit} from '@angular/core';
import {BookService} from "../../../../services/services/book.service";
import {Router} from "@angular/router";
import {PageResponseBookResponse} from "../../../../services/models/page-response-book-response";
import {borrowBook} from "../../../../services/fn/book/borrow-book";
import {BookResponse} from "../../../../services/models/book-response";

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css']
})
export class BookListComponent implements OnInit{

   bookResponse: PageResponseBookResponse = {};
   page:number =  0;
   size:number =  2;
   message = '';
   level  = 'failure';

  constructor(
    private bookService: BookService,
    private router: Router
  ) { }

  ngOnInit(): void {
       this.findAllBooks();
    }

  private findAllBooks() {
    this.bookService.findAllBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next:(books) =>{
           this.bookResponse = books;
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  goToFirstPage() {
      this.page = 0;
      this.findAllBooks()
  }

  goToPreviousPage() {
      this.page--;
      this.findAllBooks();
  }

  goToPage(page: number) {
     this.page = page;
     this.findAllBooks();
  }

  goToNextPage() {
      this.page++;
      this.findAllBooks();
  }

  goToLastPage() {
       this.page = this.bookResponse.totalPages as number - 1;
       this.findAllBooks();
  }

  get isLastPage(): boolean {
    return this.page === this.bookResponse.totalPages as number - 1;
  }

  protected readonly Array = Array;
  protected readonly borrowBook = borrowBook;

  borrow_Book(book: BookResponse) {
     this.message = '';
     this.bookService.borrowBook({
        'book-id': book.id as number
     }).subscribe({
       next:() =>{
         this.level = 'success'
         this.message = 'Book borrowed successfully';
       },
        error: (err) => {
         this.level = 'failure';
          this.message = err.error.error;
        }
     });
  }
}
