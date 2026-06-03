/**
 * Interfaz que define la estructura de un proyecto.
 *
 * Representa un proyecto del catálogo interno con su información
 * básica, tecnologías utilizadas y estado actual.
 */
export interface Project {
  /** Identificador único del proyecto */
  id: number;
  /** Nombre del proyecto */
  name: string;
  /** Descripción detallada del proyecto */
  description: string;
  /** Lista de tecnologías utilizadas en el proyecto (tags) */
  tags: string[];
  /** Estado actual del proyecto */
  status: 'Producción' | 'En Desarrollo' | 'Mantenimiento';
}
