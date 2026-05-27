import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ProjectListComponent } from './components/project-list/project-list';
import { Navbar } from './components/navbar/navbar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ProjectListComponent, Navbar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('portal-internos');
}
