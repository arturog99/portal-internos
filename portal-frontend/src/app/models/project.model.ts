// Interfaz que define la estructura de un proyecto
export interface Project {
  id: number;                                    // Identificador único del proyecto
  name: string;                                  // Nombre del proyecto
  description: string;                           // Descripción del proyecto
  tags: string[];                                // Lista de tecnologías usadas
  status: 'Producción' | 'En Desarrollo' | 'Mantenimiento';  // Estado del proyecto
}
