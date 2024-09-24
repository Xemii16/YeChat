import {Component, ContentChildren, QueryList} from '@angular/core';
import {ButtonComponent} from "../button.component";
import {IconComponent} from "../../icon/icon.component";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-outlined-button',
  standalone: true,
  imports: [
    NgClass
  ],
  templateUrl: './outlined-button.component.html',
  styleUrl: './outlined-button.component.scss'
})
export class OutlinedButtonComponent extends ButtonComponent {

  @ContentChildren(IconComponent) icons!: QueryList<IconComponent>;

  changePadding(): string[] {
    if (!this.icons) return [];
    return this.icons.length > 0 ? ['icon'] : [];
  }
}
