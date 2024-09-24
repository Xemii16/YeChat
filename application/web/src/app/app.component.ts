import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {FilledButtonComponent} from "./button/filled/filled-button.component";
import {IconComponent} from "./icon/icon.component";
import {TextButtonComponent} from "./button/text/text-button.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FilledButtonComponent, IconComponent, TextButtonComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'YeChat';
  protected readonly console = console;
}
