import {Component, Input} from '@angular/core';
import {Color, WHITE} from '@lubert/chess.ts';

@Component({
  selector: 'app-bishop',
  templateUrl: './bishop.svg',
  styleUrls: ['../pieces.scss']
})
export class BishopComponent {

  @Input()
  side: Color = WHITE;
}
