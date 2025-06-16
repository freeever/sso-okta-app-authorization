import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './component/header/header.component';
import { AuthService } from 'shared-lib';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    HeaderComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {

  private auth = inject(AuthService);

  ngOnInit(): void {
    this.auth.checkAuthentication();
  }
}
