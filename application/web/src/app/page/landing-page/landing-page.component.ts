import { Component } from '@angular/core';
import {NgOptimizedImage} from "@angular/common";
import {TextButtonComponent} from "../../button/text/text-button.component";
import {FilledButtonComponent} from "../../button/filled/filled-button.component";
import {IconComponent} from "../../icon/icon.component";
import {RouterLink} from "@angular/router";
import {OutlinedButtonComponent} from "../../button/outlined/outlined-button.component";
import {DividerComponent} from "../../divider/divider.component";

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [
    NgOptimizedImage,
    TextButtonComponent,
    FilledButtonComponent,
    IconComponent,
    RouterLink,
    OutlinedButtonComponent,
    DividerComponent
  ],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.scss'
})
export class LandingPageComponent {
}
