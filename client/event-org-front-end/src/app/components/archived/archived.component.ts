import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/internal/Subscription';
import {Event} from '../../models/event';
import {User} from '../../models/user';
import {AuthenticationService} from '../../services/authentication.service';
import {EventsService} from '../../services/events.service';
import {HttpErrorResponse} from '@angular/common/http';
import {AlertService} from '../../services/alert.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-archived',
  templateUrl: './archived.component.html',
  styleUrls: ['./archived.component.css']
})
export class ArchivedComponent implements OnInit, OnDestroy {
  currentUser: User;
  currentUserSubscription: Subscription;
  events: Event[];

  constructor(private authenticationService: AuthenticationService, private eventService: EventsService, private alertService: AlertService
    , private router: Router) {
    this.currentUserSubscription = this.authenticationService.currentUser.subscribe(
      user => {
        this.currentUser = user;
      }
    );
    this.eventService.getArchivedEvents().subscribe(data => {
      this.events = <Event[]>data;
      this.events.map(event => {
        event.startTime = new Date(event.startTime);
        event.endTime = new Date(event.endTime);
      });
    }, (error: HttpErrorResponse) => {
      this.alertService.error(error);
    });
  }

  ngOnInit() {
  }

  ngOnDestroy(): void {
    this.currentUserSubscription.unsubscribe();
  }

  truncatedStr(str: string, size: number): string {
    if (str == null) {
      return '';
    }
    if (str.length > size) {
      return `${str.substring(0, size)}...`;
    }
    return str;
  }

  isCreatedByMe(createdBy: User): boolean {
    return this.currentUser.username === createdBy.username;
  }

  deleteEvent(eventId: number, mine: boolean) {
    if (confirm('Are you sure to delete this anEvent')) {
      if (mine) {
        this.eventService.deleteEvent(eventId).subscribe(data => {
          this.alertService.success('Event deleted successfully!', true);
          //Refresh hack
          this.router.navigateByUrl('/createEvent', {skipLocationChange: true}).then(() => {
            this.router.navigate(['/archived']);
          });
        }, error => this.alertService.error(error));
      } else {
        this.eventService.unshareEventWithCurrentUser(eventId).subscribe(data => {
          this.alertService.success('Event unshared successfully!', true);
          //Refresh hack
          this.router.navigateByUrl('/createEvent', {skipLocationChange: true}).then(() => {
            this.router.navigate(['/archived']);
          });
        }, error => this.alertService.error(error));
      }
    }
  }

  unarchiveEvent(eventId: number, mine: boolean) {
    console.log('unarchive:' + eventId);
  }
}
