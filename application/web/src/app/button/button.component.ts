import {Component, EventEmitter, Input, Output} from "@angular/core";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-button-base',
  standalone: true,
  imports: [
    NgClass
  ],
  template: ''
})
/**
 * Base class for all buttons
 * @author Maks Balamut
 */
export class ButtonComponent {
  /**
   * Only use this method if you need worked disabled state
   */
  @Output() onclick = new EventEmitter<void>();
  @Input() disabled = false;

  protected onClick(event: MouseEvent): void {
    if (!this.disabled) {
      this.onclick.emit();
      event.stopImmediatePropagation();
    }
  }
}
