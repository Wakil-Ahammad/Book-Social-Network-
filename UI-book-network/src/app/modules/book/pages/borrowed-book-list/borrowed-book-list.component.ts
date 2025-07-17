import {Component, OnInit} from '@angular/core';
import {BorrowedBookResponse} from "../../../../services/models/borrowed-book-response";
import {PageResponseBorrowedBookResponse} from "../../../../services/models/page-response-borrowed-book-response";
import {BookService} from "../../../../services/services/book.service";
import {FeedbackRequest} from "../../../../services/models/feedback-request";
import {FeedbackService} from "../../../../services/services/feedback.service";

@Component({
  selector: 'app-borrowed-book-list',
  templateUrl: './borrowed-book-list.component.html',
  styleUrls: ['./borrowed-book-list.component.css']
})
export class BorrowedBookListComponent implements OnInit{

  borrowedBooks: PageResponseBorrowedBookResponse = {};
  page: number = 0;
  size: number = 10;
  selectedBook: BorrowedBookResponse | undefined = undefined;
  feedBackRequest: FeedbackRequest = {bookId: 0, comment: "", note: 0};

  constructor(
    private bookService: BookService,
    private feedbackService: FeedbackService
  ) {}

  ngOnInit(): void {
      this.findAllBorrowedBooks();
  }



  returnBorrowedBook(book : BorrowedBookResponse) {
     this.selectedBook = book;
     this.feedBackRequest.bookId = book.id as number;
  }

  private findAllBorrowedBooks() {
    this.bookService.findAllBorrowedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (response) => {
        this.borrowedBooks = response;
      },
      error: (error) => {
        console.error('Error fetching borrowed books:', error);
      }
    })
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBorrowedBooks()
  }

  goToPreviousPage() {
    this.page--;
    this.findAllBorrowedBooks();
  }

  goToPage(page: number) {
    this.page = page;
    this.findAllBorrowedBooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllBorrowedBooks();
  }

  goToLastPage() {
    this.page = this.borrowedBooks.totalPages as number - 1;
    this.findAllBorrowedBooks();
  }

  get isLastPage(): boolean {
    return this.page === this.borrowedBooks.totalPages as number - 1;
  }

  returnBook(withFeedback: boolean) {
     this.bookService.returnBorrowBook({
      'book-id': this.selectedBook?.id as number,
     }).subscribe({
       next: ()=>{
         if(withFeedback){
           this.giveFeedback();
         }

         this.selectedBook = undefined;
         this.findAllBorrowedBooks();
      }
     });


  }

  private giveFeedback() {
     this.feedbackService.saveFeedback({
       body: this.feedBackRequest
     }).subscribe({
        next:() =>{
           console.log("Feedback saved successfully");
        }
     })
  }
}
