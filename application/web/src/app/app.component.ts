import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {LandingPageComponent} from "./page/landing-page/landing-page.component";
import {FilledButtonComponent} from "./button/filled/filled-button.component";
import {IconComponent} from "./icon/icon.component";
import {DividerComponent} from "./divider/divider.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, LandingPageComponent, FilledButtonComponent, IconComponent, DividerComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'YeChat';

  test() {
    console.log('test');
  }
}
