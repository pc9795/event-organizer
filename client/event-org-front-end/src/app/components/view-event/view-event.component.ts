import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EventsService} from '../../services/events.service';
import {AlertService} from '../../services/alert.service';
import {Event} from '../../models/event';
import {formatDate} from '@angular/common';
import {Subscription} from 'rxjs/internal/Subscription';
import {User} from '../../models/user';
import {AuthenticationService} from '../../services/authentication.service';
import {HttpErrorResponse} from '@angular/common/http';


/**
 * Handles the page which has details about an event.
 */
@Component({
  selector: 'app-view-event',
  templateUrl: './view-event.component.html',
  styleUrls: ['./view-event.component.css']
})
export class ViewEventComponent implements OnInit {
  anEvent: Event; //Passed data
  archived: boolean = true; //Passed data
  viewEventForm: FormGroup;
  submitted = false;
  loading = false;
  currentUser: User;
  currentUserSubscription: Subscription;

  constructor(private router: Router, private formBuilder: FormBuilder, private eventService: EventsService,
              private alertService: AlertService, private authenticationService: AuthenticationService) {
    //Receive information from other component.
    let state = this.router.getCurrentNavigation().extras.state;
    if (state) {
      this.anEvent = state.event;
      this.archived = state.archived
    }
    //Subscribe to user information
    this.currentUserSubscription = this.authenticationService.currentUser.subscribe(
      user => {
        this.currentUser = user;
      }
    );
  }

  //Utility method for angular form controls
  get f() {
    return this.viewEventForm.controls;
  }

  ngOnInit() {
    //All fields will be disabled if it is opened for a archived event or shared event.
    let disabled: boolean = this.archived || !this.isCreatedByMe();

    //Initialize the form
    this.viewEventForm = this.formBuilder.group({
      title: [{
        value: this.anEvent ? this.anEvent.title : '',
        disabled: disabled
      }, [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      description: [{value: this.anEvent ? this.anEvent.description : '', disabled: disabled}, []],
      location: [{value: this.anEvent ? this.anEvent.location : '', disabled: disabled}, []],
      startTime: [{
        value: this.anEvent ? formatDate(this.anEvent.startTime, 'yyyy-MM-ddTHH:mm:SS', 'en-US') : '',
        disabled: disabled
      }, [Validators.required]],
      endTime: [{
        value: this.anEvent ? formatDate(this.anEvent.endTime, 'yyyy-MM-ddTHH:mm:SS', 'en-US') : '',
        disabled: disabled
      }, [Validators.required]]
    });
  }

  //Check the passed event is created by logged in user or not.
  isCreatedByMe(): boolean {
    return this.anEvent && this.currentUser.username === this.anEvent.createdBy.username;
  }

  //If user wants to edit an event.
  onSubmit() {
    this.submitted = true;
    let startTimeObj = new Date(this.viewEventForm.get('startTime').value);
    let endTimeObj = new Date(this.viewEventForm.get('endTime').value);

    //Start time can't be greater than end
    if (startTimeObj > endTimeObj) {
      this.viewEventForm.get('endTime').setErrors({'invalid': 'Can\'t be less than start date'});
      return;
    }

    //Invalid form
    if (this.viewEventForm.invalid) {
      return;
    }

    this.loading = true;
    let event = new Event(this.anEvent.id, this.viewEventForm.get('title').value, this.viewEventForm.get('description').value,
      this.viewEventForm.get('location').value, this.viewEventForm.get('startTime').value, this.viewEventForm.get('endTime').value);
    this.eventService.updateEvent(event, event.id).subscribe(
      data => {
        this.alertService.success('Event updated successfully', true);
        //Go to home page
        this.router.navigate(['/'])
      }, (error: HttpErrorResponse) => {
        this.alertService.error(error);
        this.loading = false;
      }
    );
  }

  //Unshare an event with an user.
  unshareEvent(userId: number, eventId: number) {
    if (confirm('Are you sure to unshare this event with this user')) {
      this.eventService.unshareEvent(eventId, userId).subscribe(
        data => {
          this.eventService.getEvent(eventId).subscribe(
            data => {
              //Reload the page.
              this.router.navigateByUrl('/', {skipLocationChange: true}).then(
                () => {
                  //Pass the required event in the state object.
                  this.router.navigate(['/viewEvent'], {state: {event: data, archived: false}});
                }
              );
            }, error => {
              this.alertService.error(error);
            }
          )
        }, error => {
          this.alertService.error(error);
        }
      )
    }
  }
}
