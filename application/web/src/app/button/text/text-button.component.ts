import {Component, ContentChildren, QueryList} from '@angular/core';
import {ButtonComponent} from "../button.component";
import {IconComponent} from "../../icon/icon.component";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-text-button',
  standalone: true,
  imports: [
    NgClass
  ],
  templateUrl: './text-button.component.html',
  styleUrl: './text-button.component.scss'
})
export class TextButtonComponent extends ButtonComponent {

  @ContentChildren(IconComponent) icons!: QueryList<IconComponent>;

  changePadding(): string[] {
    if (!this.icons) return [];
    return this.icons.length > 0 ? ['icon'] : [];
  }
}
