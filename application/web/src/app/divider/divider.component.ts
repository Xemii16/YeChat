import {Component, Input} from '@angular/core';
import {NgClass, NgStyle} from "@angular/common";

@Component({
  selector: 'app-divider',
  standalone: true,
  imports: [
    NgClass,
    NgStyle
  ],
  templateUrl: './divider.component.html',
  styleUrl: './divider.component.scss'
})
export class DividerComponent {
  @Input() variant: boolean = false;
  @Input() shorter: boolean = false;
}
