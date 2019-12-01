import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedEventsComponent } from './shared-events.component';

describe('SharedEventsComponent', () => {
  let component: SharedEventsComponent;
  let fixture: ComponentFixture<SharedEventsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SharedEventsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SharedEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
