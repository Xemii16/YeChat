import {Component, ContentChildren, QueryList} from '@angular/core';
import {ButtonComponent} from "../button.component";
import {NgClass, NgStyle} from "@angular/common";
import {IconComponent} from "../../icon/icon.component";

@Component({
  selector: 'app-filled-button',
  standalone: true,
  imports: [
    NgStyle,
    NgClass
  ],
  templateUrl: './filled-button.component.html',
  styleUrl: './filled-button.component.scss'
})
export class FilledButtonComponent extends ButtonComponent {

  @ContentChildren(IconComponent) icons!: QueryList<IconComponent>;

  changePadding(): string[] {
    if (!this.icons) return [];
    return this.icons.length > 0 ? ['icon'] : [];
  }
}
