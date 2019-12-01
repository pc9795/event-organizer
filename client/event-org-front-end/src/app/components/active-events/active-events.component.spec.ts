import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveEventsComponent } from './active-events.component';

describe('ActiveEventsComponent', () => {
  let component: ActiveEventsComponent;
  let fixture: ComponentFixture<ActiveEventsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ActiveEventsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActiveEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
