export interface Project {
  id: number;
  name: string;
  description: string;
  tags: string[];
  status: 'Producción' | 'En Desarrollo' | 'Mantenimiento';
}   
