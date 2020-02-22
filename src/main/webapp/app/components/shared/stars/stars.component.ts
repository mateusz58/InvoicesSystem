import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-stars',
  templateUrl: './stars.component.html',
  styles: []
})
export class StarsComponent implements OnChanges {

  @Input() rating: number =4;
  starWidth: number;

  constructor() { }

  ngOnChanges(changes: SimpleChanges): void {
    this.starWidth = this.rating*75/5;
  }

}
