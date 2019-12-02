import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {RegisterComponent} from './components/register/register.component';
import {LoginComponent} from './components/login/login.component';
import {ActiveEventsComponent} from './components/active-events/active-events.component';
import {SharedEventsComponent} from './components/shared-events/shared-events.component';
import {ArchivedEventsComponent} from './components/archived-events/archived-events.component';
import {SharedArchivedEventsComponent} from './components/shared-archived-events/shared-archived-events.component';
import {ReactiveFormsModule} from '@angular/forms';
import {HomeComponent} from './components/home/home.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {routing} from './app.routing';
import {AlertComponent} from './components/alert/alert.component';
import {ErrorInterceptor} from './error.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    RegisterComponent,
    LoginComponent,
    ActiveEventsComponent,
    SharedEventsComponent,
    ArchivedEventsComponent,
    SharedArchivedEventsComponent,
    HomeComponent,
    AlertComponent
  ],
  imports: [
    BrowserModule,
    // We have to add this to start using template based forms.
    ReactiveFormsModule,
    // AppRoutingModule,
    HttpClientModule,
    routing
  ],
  providers: [{provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
