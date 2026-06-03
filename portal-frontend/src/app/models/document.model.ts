/**
 * Interfaz que define la estructura de un documento de avance de un proyecto.
 */
export interface ProjectDocument {
  /** Identificador único del documento */
  id: number;
  /** Nombre original del archivo */
  filename: string;
  /** Tipo MIME del archivo */
  contentType: string;
  /** Tamaño del archivo en bytes */
  size: number;
  /** Usuario que subió el documento */
  uploadedBy: string;
  /** Fecha y hora de la subida (ISO string) */
  uploadedAt: string;
}
