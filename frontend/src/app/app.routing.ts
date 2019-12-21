import {Routes, RouterModule} from '@angular/router';
import {RegisterComponent} from './components/register/register.component';
import {LoginComponent} from './components/login/login.component';
import {HomeComponent} from './components/home/home.component';
import {AuthGuard} from './auth.guard';
import {AddEventComponent} from './components/add-event/add-event.component';
import {ViewEventComponent} from './components/view-event/view-event.component';
import {ArchivedComponent} from './components/archived/archived.component';

/**
 * All the routesof the application.
 */
const appRoutes: Routes = [
  {path: '', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'createEvent', component: AddEventComponent, canActivate: [AuthGuard]},
  {path: 'archived', component: ArchivedComponent, canActivate: [AuthGuard]},
  {path: 'viewEvent', component: ViewEventComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},

  // otherwise redirect to home
  {path: '**', redirectTo: ''}
];

export const routing = RouterModule.forRoot(appRoutes);
