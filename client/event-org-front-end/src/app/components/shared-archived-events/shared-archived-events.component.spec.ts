import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedArchivedEventsComponent } from './shared-archived-events.component';

describe('SharedArchivedEventsComponent', () => {
  let component: SharedArchivedEventsComponent;
  let fixture: ComponentFixture<SharedArchivedEventsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SharedArchivedEventsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SharedArchivedEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
