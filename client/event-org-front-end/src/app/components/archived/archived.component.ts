import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/internal/Subscription';
import {Event} from '../../models/event';
import {User} from '../../models/user';
import {AuthenticationService} from '../../services/authentication.service';
import {EventsService} from '../../services/events.service';
import {HttpErrorResponse} from '@angular/common/http';
import {AlertService} from '../../services/alert.service';
import {Router} from '@angular/router';

/**
 * Component to handle archived events.
 */
@Component({
  selector: 'app-archived',
  templateUrl: './archived.component.html',
  styleUrls: ['./archived.component.css']
})
export class ArchivedComponent implements OnInit, OnDestroy {
  currentUser: User;
  currentUserSubscription: Subscription;
  events: Event[];
  searched: boolean = false;

  constructor(private authenticationService: AuthenticationService, private eventService: EventsService, private alertService: AlertService
    , private router: Router) {
    this.currentUserSubscription = this.authenticationService.currentUser.subscribe(
      user => {
        this.currentUser = user;
      }
    );

    //Get state passed by different components.
    let state = this.router.getCurrentNavigation().extras.state;
    this.searched = !!(state && state.searchStr);

    //Get archived events.
    this.eventService.getArchivedEvents(state ? state.searchStr : null).subscribe(data => {
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

  //Get length of an js object.
  objLen(obj): number {
    if (!obj) {
      return 0;
    }
    return Object.keys(obj).length;
  }

  //Get truncated representation of a string capped by a size.
  truncatedStr(str: string, size: number): string {
    if (str == null) {
      return '';
    }
    if (str.length > size) {
      return `${str.substring(0, size)}...`;
    }
    return str;
  }

  //Get a personalized message if the passed user is logged in one.
  getCreatedBy(user: User): string {
    if (this.currentUser.username === user.username) {
      return 'You';
    }
    return user.username;
  }

  //Check if the passed user is the logged in one.
  isCreatedByMe(createdBy: User): boolean {
    return this.currentUser.username === createdBy.username;
  }

  //Refresh hack
  refresh() {
    this.router.navigateByUrl('/createEvent', {skipLocationChange: true}).then(
      () => {
        this.router.navigate(['/archived']);
      }
    );
  }

  //Deletes an event.
  deleteEvent(eventId: number, mine: boolean) {
    if (confirm('Are you sure to delete this anEvent')) {
      if (mine) {
        //Delete event created by logged in user.
        this.eventService.deleteEvent(eventId).subscribe(data => {
          this.alertService.success('Event deleted successfully!', true);
          this.refresh();
        }, error => this.alertService.error(error));
      } else {
        //Delete events shared by others. It will not actually delete the event it only deletes the relationship.
        this.eventService.unshareEventWithCurrentUser(eventId).subscribe(data => {
          this.alertService.success('Event unshared successfully!', true);
          this.refresh();
        }, error => this.alertService.error(error));
      }
    }
  }

  unarchiveEvent(eventId: number, mine: boolean) {
    if (confirm('Are you sure to un-archive this Event')) {
      if (mine) {
        //Unarchive event created by logged in user.
        this.eventService.unarchiveEvent(eventId).subscribe(data => {
          this.alertService.success('Event archived successfully!', true);
          this.refresh();
        }, error => this.alertService.error(error));
      } else {
        //Unarchive event shared by others. It will not actually unarchive the original event it only manipulates the relationship.
        this.eventService.unarchiveSharedEvent(eventId).subscribe(data => {
          this.alertService.success('Event unshared successfully!', true);
          this.refresh();
        }, error => this.alertService.error(error));
      }
    }
  }

  //Search for events with the given string in title
  search(searchStr: string) {
    this.router.navigateByUrl('/createEvent', {skipLocationChange: true}).then(
      () => {
        this.router.navigate(['/archived'], {state: {searchStr: searchStr}});
      }
    );
  }
}
