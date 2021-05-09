import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {KingComponent} from './king/king.component';
import {QueenComponent} from './queen/queen.component';
import {BishopComponent} from './bishop/bishop.component';
import {KnightComponent} from './knight/knight.component';
import {RookComponent} from './rook/rook.component';
import {PawnComponent} from './pawn/pawn.component';


@NgModule({
  declarations: [
    KingComponent,
    QueenComponent,
    BishopComponent,
    KnightComponent,
    RookComponent,
    PawnComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [
    KingComponent,
    QueenComponent,
    BishopComponent,
    KnightComponent,
    RookComponent,
    PawnComponent
  ]
})
export class PiecesModule {
}
