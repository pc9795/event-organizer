import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {RegisterComponent} from './components/register/register.component';
import {LoginComponent} from './components/login/login.component';
import {ReactiveFormsModule} from '@angular/forms';
import {HomeComponent} from './components/home/home.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {routing} from './app.routing';
import {AlertComponent} from './components/alert/alert.component';
import {ErrorInterceptor} from './error.interceptor';
import {AddEventComponent} from './components/add-event/add-event.component';
import {ViewEventComponent} from './components/view-event/view-event.component';
import {ArchivedComponent} from './components/archived/archived.component';
import {AppRoutingModule} from './app-routing.module';

@NgModule({
  declarations: [
    AppComponent,
    RegisterComponent,
    LoginComponent,
    HomeComponent,
    AlertComponent,
    AddEventComponent,
    ViewEventComponent,
    ArchivedComponent
  ],
  imports: [
    BrowserModule,
    // We have to add this to start using template based forms.
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule,
    routing
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
