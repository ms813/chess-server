import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatToolbarModule} from '@angular/material/toolbar';
import {HomeComponent} from './home/home.component';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {HttpClientModule} from '@angular/common/http';
import {ChessGameComponent} from './chess-game/chess-game.component';
import {MatGridListModule} from '@angular/material/grid-list';
import {PiecesModule} from './pieces/pieces.module';
import {MatListModule} from '@angular/material/list';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    ChessGameComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    HttpClientModule,
    MatGridListModule,
    PiecesModule,
    MatListModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
