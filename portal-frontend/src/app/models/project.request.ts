/**
 * Datos para crear o actualizar un proyecto (coincide con ProjectRequest del backend).
 */
import { Project } from './project.model';

export interface ProjectRequest {
  /** Nombre del proyecto */
  name: string;
  /** Descripción detallada */
  description: string;
  /** Tecnologías utilizadas (tags) */
  tags: string[];
  /** Estado actual del proyecto */
  status: Project['status'];
}
