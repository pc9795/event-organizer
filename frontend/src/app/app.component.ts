import {Component} from '@angular/core';
import {User} from './models/user';
import {AuthenticationService} from './services/authentication.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  currentUser: User;

  constructor(private router: Router, private authenticationService: AuthenticationService) {
    this.authenticationService.currentUser.subscribe(user => {
      this.currentUser = user;
    });
  }

  /**
   * Log out
   */
  logout() {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }

  // Methods to bypass angular component reuse.
  getHome() {
    this.router.navigateByUrl('/createEvent', {skipLocationChange: true}).then(
      () => {
        this.router.navigate(['/']);
      }
    );
  }

  // Methods to bypass angular component reuse.
  getArchived() {
    this.router.navigateByUrl('/createEvent', {skipLocationChange: true}).then(
      () => {
        this.router.navigate(['/archived']);
      }
    );
  }
}
