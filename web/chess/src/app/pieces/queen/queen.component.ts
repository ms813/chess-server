import {Component, Input} from '@angular/core';
import {Color, WHITE} from '@lubert/chess.ts';

@Component({
  selector: 'app-queen',
  templateUrl: './queen.svg',
  styleUrls: ['../pieces.scss']
})
export class QueenComponent {

  @Input()
  side: Color = WHITE;
}
