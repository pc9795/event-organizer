import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EventsService} from '../../services/events.service';
import {Router} from '@angular/router';
import {AlertService} from '../../services/alert.service';
import {Event} from '../../models/event';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-add-event',
  templateUrl: './add-event.component.html',
  styleUrls: ['./add-event.component.css']
})
export class AddEventComponent implements OnInit {
  eventForm: FormGroup;
  submitted = false;
  loading = false;

  constructor(private formBuilder: FormBuilder, private eventService: EventsService, private router: Router,
              private alertService: AlertService) {
  }

  // getter for easy access to form fields
  get f() {
    return this.eventForm.controls;
  }

  ngOnInit() {
    this.eventForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      description: ['', []],
      location: ['', []],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required]
    });
  }

  onSubmit() {
    this.submitted = true;
    let startTimeObj = new Date(this.eventForm.get('startTime').value);
    let endTimeObj = new Date(this.eventForm.get('endTime').value);

    if (startTimeObj > endTimeObj) {
      this.eventForm.get('endTime').setErrors({'invalid': 'Can\'t be less than start date'});
      return;
    }

    if (this.eventForm.invalid) {
      return;
    }

    this.loading = true;
    let event = new Event(-1, this.eventForm.get('title').value, this.eventForm.get('description').value,
      this.eventForm.get('location').value, this.eventForm.get('startTime').value, this.eventForm.get('endTime').value);
    this.eventService.createEvent(event).subscribe(
      data => {
        this.alertService.success('Event created successfully', true);
        this.router.navigate(['/'])
      }, (error: HttpErrorResponse) => {
        this.alertService.error(error);
        this.loading = false;
      }
    );
  }

}
