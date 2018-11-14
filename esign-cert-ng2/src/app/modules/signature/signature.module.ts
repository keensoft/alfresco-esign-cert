import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';

import { SignatureComponent } from './signature.component';
import { SignatureService } from './signature.service';

export { SignatureComponent };

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule
  ],
  providers: [
    HttpClient,
    SignatureService
  ],
  declarations: [
    SignatureComponent
  ],
  exports: [
    SignatureComponent
  ]
})
export class SignatureModule { }
