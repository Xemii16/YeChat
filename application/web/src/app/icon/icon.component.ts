import {Component, Input} from '@angular/core';
import {NgStyle} from "@angular/common";

@Component({
  selector: 'app-icon',
  standalone: true,
  templateUrl: './icon.component.html',
  imports: [
    NgStyle
  ],
  styleUrl: './icon.component.scss'
})
export class IconComponent {
  @Input() fill: boolean = false;
}
