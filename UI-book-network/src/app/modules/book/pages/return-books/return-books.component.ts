import {Component, OnInit} from '@angular/core';
import {PageResponseBorrowedBookResponse} from "../../../../services/models/page-response-borrowed-book-response";
import {BorrowedBookResponse} from "../../../../services/models/borrowed-book-response";
import {FeedbackRequest} from "../../../../services/models/feedback-request";
import {BookService} from "../../../../services/services/book.service";
import {FeedbackService} from "../../../../services/services/feedback.service";

@Component({
  selector: 'app-return-books',
  templateUrl: './return-books.component.html',
  styleUrls: ['./return-books.component.css']
})
export class ReturnBooksComponent implements OnInit{
  returnedBooks: PageResponseBorrowedBookResponse = {};
  page: number = 0;
  size: number = 10;
  message: string = '';
  level: string = 'success';

  constructor(
    private bookService: BookService,
  ) {}

  ngOnInit(): void {
    this.findAllReturnedBooks();
  }


  private findAllReturnedBooks() {
    this.bookService.findAllReturnedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (response) => {
        this.returnedBooks = response;
      },
      error: (error) => {
        console.error('Error fetching borrowed books:', error);
      }
    })
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllReturnedBooks()
  }

  goToPreviousPage() {
    this.page--;
    this.findAllReturnedBooks();
  }

  goToPage(page: number) {
    this.page = page;
    this.findAllReturnedBooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllReturnedBooks();
  }

  goToLastPage() {
    this.page = this.returnedBooks.totalPages as number - 1;
    this.findAllReturnedBooks();
  }

  get isLastPage(): boolean {
    return this.page === this.returnedBooks.totalPages as number - 1;
  }


  approveBookReturn(book: BorrowedBookResponse) {
    if(!book.returned){
      this.level = 'failure';
      this.message = 'Book is not returned yet';
      return;
    }

    this.bookService.approveBorrowBook({
      'book-id': book.id as number
    }).subscribe({
      next: () => {
        this.level= 'success';
        this.message = 'Book return approved successfully';
        this.findAllReturnedBooks();
      },
      error: (error) => {
        this.level = 'failure';
        console.error('Error approving book return:', error);
      }
    })

  }
}
