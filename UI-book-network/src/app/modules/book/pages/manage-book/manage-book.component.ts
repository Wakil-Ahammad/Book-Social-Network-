import {Component, OnInit} from '@angular/core';
import {BookRequest} from "../../../../services/models/book-request";
import {saveBook} from "../../../../services/fn/book/save-book";
import {BookService} from "../../../../services/services/book.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-manage-book',
  templateUrl: './manage-book.component.html',
  styleUrls: ['./manage-book.component.css']
})
export class ManageBookComponent implements OnInit{
   errorMsg: Array<string> = [];
   selectedPicture: string | undefined;
   selectedBookCover: any;
   bookRequest: BookRequest = {authorName: "", isbn: "", synopsis: "", title: "", shareable: false};

   constructor(
     private bookService: BookService,
     private router: Router,
     private activatedRoute: ActivatedRoute
   ) {
   }

  ngOnInit(): void {
      const bookId = this.activatedRoute.snapshot.params['bookId'];
      console.log(bookId)

      if(bookId){
        this.bookService.findBookById({
          'book-id': bookId as number
        }).subscribe({
          next: (book) => {
            this.bookRequest = {
              id: book.id,
              authorName: book.authorName as string,
              isbn: book.isbn as string,
              synopsis: book.synopsis as string,
              title: book.title as string,
              shareable: book.shareable
            }
            if(book.cover){
              this.selectedPicture = 'data:image/jpeg;base64,' + book.cover;
            }
          },
          error: (err) => {
            console.error(err);
          }
        })
      }
   }

  onFileSelected(event: any) {
       this.selectedBookCover = event.target.files[0];
       console.log(this.selectedBookCover)
       if(this.selectedBookCover){
         const reader: FileReader = new FileReader();
         reader.onload = () =>{
           this.selectedPicture = reader.result as string
         }
         reader.readAsDataURL(this.selectedBookCover);
       }
  }

  savedBook() {
    this.bookService.saveBook({
      body: this.bookRequest
    }).subscribe({

     next:(bookId) =>{
        this.bookService.uploadBookCoverPicture({
          'book-id': bookId,
          body:{
            file: this.selectedBookCover
          }
        }).subscribe({
          next: () => {
            this.router.navigate(['/books/my-books']);
          }
        })
     },
      error: (err) => {
          this.errorMsg = err.error.validationErrors;
      }
    });
  }
}
