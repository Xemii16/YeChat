import {Component} from '@angular/core';
import {ButtonComponent} from "../button.component";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-icon-button',
  standalone: true,
  imports: [
    NgClass
  ],
  templateUrl: './icon-button.component.html',
  styleUrl: './icon-button.component.scss'
})
export class IconButtonComponent extends ButtonComponent {

}
