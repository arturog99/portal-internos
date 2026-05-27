# Documentación Simplificada - Portal Internos

## ¿Qué es Angular?
Angular es como un constructor de páginas web. En lugar de escribir HTML suelto, creas **componentes** (bloques reutilizables) y los vas ensamblando.

---

## Estructura Básica

```
src/app/
├── components/    # Los bloques visuales (tarjetas, botones, etc.)
├── services/      # La lógica (cargar datos, cálculos)
├── models/         # La forma de los datos (qué tiene cada proyecto)
└── app.ts/html     # La página principal
```

---

## Componentes = Bloques de LEGO

Un componente es como un bloque de LEGO que tiene:
- **HTML**: Cómo se ve
- **TypeScript**: Qué hace
- **CSS**: Cómo se viste

Ejemplo: Una tarjeta de proyecto es un componente.

---

## ¿Cómo funciona este proyecto?

1. **app.html** → Es la página principal
2. **app.ts** → Carga el componente de lista de proyectos
3. **project-list.ts** → Carga los datos del JSON
4. **project-list.html** → Muestra las tarjetas con los datos

---

## ¿Qué hace cada archivo?

### `app.ts` + `app.html`
- Es la página principal
- Solo tiene el título y carga el componente de lista

### `project-list.ts` + `project-list.html`
- Muestra todas las tarjetas
- Tiene el buscador
- Filtra los proyectos

### `project.ts` (Service)
- Carga los datos del archivo `projects.json`
- Es como un "fetch" pero de Angular

### `project.model.ts`
- Define qué tiene cada proyecto (id, nombre, descripción, tags, estado)

### `projects.json`
- Archivo con los 8 proyectos de prueba

---

## ¿Qué son los Signals?

Imagina una variable que avisa a Angular cuando cambia:

```typescript
// Variable normal (no avisa)
nombre = "Juan";

// Signal (avisa cuando cambia)
nombre = signal("Juan");
nombre.set("Pedro");  // Angular se entera y actualiza la pantalla
```

**¿Por qué lo usamos?**
- Antes: Angular no sabía cuando cambiaban los datos → no se veían las tarjetas
- Ahora: Con signals, Angular se entera automáticamente → las tarjetas aparecen

---

## ¿Qué es computed?

Es como una fórmula de Excel que se recalcula sola:

```typescript
precio = signal(100);
cantidad = signal(2);

total = computed(() => precio() * cantidad());
// Si cambia precio o cantidad, total se recalcula solo
```

En nuestro proyecto:
- `projects` = todos los proyectos
- `searchTerm` = lo que escribes en el buscador
- `filteredProjects` = proyectos filtrados (se recalcula cuando cambian los otros dos)

---

## Directivas (cosas raras en el HTML)

### `@for` → Repite elementos
```html
@for (proyecto of proyectos) {
  <div>{{ proyecto.nombre }}</div>
}
```
→ Crea un div por cada proyecto

### `[(ngModel)]` → Enlace bidireccional
```html
<input [(ngModel)]="searchTerm">
```
→ Lo que escribes en el input se guarda en `searchTerm` automáticamente

### `[ngClass]` → Clases condicionales
```html
<div [ngClass]="{ 'verde': esVerde, 'rojo': esRojo }">
```
→ Si `esVerde` es true, añade la clase 'verde'

---

## ¿Por qué no se veían las tarjetas al principio?

**El problema:**
- Usábamos variables normales: `projects: Project[] = []`
- Cuando llegaban los datos del JSON, Angular no se enteraba
- La pantalla no se actualizaba

**La solución:**
- Cambiamos a signals: `projects = signal<Project[]>([])`
- Ahora Angular se entera cuando cambian los datos
- La pantalla se actualiza automáticamente

---

## Tailwind CSS

Son clases que hacen el diseño sin escribir CSS:

- `bg-white` → fondo blanco
- `p-5` → padding (espacio interior)
- `rounded-xl` → bordes redondeados
- `shadow-md` → sombra
- `grid-cols-1` → 1 columna (móvil)
- `md:grid-cols-2` → 2 columnas (tablet)
- `lg:grid-cols-3` → 3 columnas (PC)

---

## Flujo completo (simple)

1. Usuario abre la página
2. Angular carga `app.ts`
3. `app.ts` carga `project-list.ts`
4. `project-list.ts` llama al service
5. Service lee `projects.json`
6. Service devuelve los datos
7. `project-list.ts` guarda los datos en un signal
8. Angular detecta el cambio
9. `project-list.html` muestra las tarjetas

---

## Comandos básicos

```bash
ng serve -o      # Arranca la app y abre el navegador
ng build         # Crea la versión para producción
```

---

## Resumen en 3 frases

1. **Componentes** = bloques visuales reutilizables
2. **Services** = lógica para cargar datos
3. **Signals** = variables que avisan cuando cambian

---

## ¿Qué falta por hacer?

Según el enunciado:
- ❌ Filtros por estado y tecnología
- ❌ Login con usuario/contraseña
- ❌ Roles (admin, técnico, visitante)
- ❌ Barra de navegación
- ❌ Autenticación con Google
