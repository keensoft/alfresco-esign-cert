import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { SignatureModule } from './modules/signature/signature.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    SignatureModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
