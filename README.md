# Operating Systems - University Activities

**Integrantes:**

- Juan David Cardozo Trujillo - 20231020155
- Brayan Estiven Aguirre Aristizabal - 20231020156

---

## 📁 Estructura del Repositorio

```
OperatingSystems/
├── HistorialChat/   # Actividad 1: Análisis de historial de chat con paralelismo
├── SumaVectorial/   # Actividad 2: Suma vectorial secuencial vs paralela
└── README.md        # Este archivo
```

---

## 🎯 Actividad 1: HistorialChat

**Análisis de historial de chat mediante paralelismo de tareas y datos**

Este proyecto procesa un archivo de historial de chat y extrae métricas clave utilizando **dos estrategias de paralelismo** distintas:

| Estrategia                    | Descripción                                                                                                                  |
| ----------------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| **A - Paralelismo de Tareas** | 3 hilos fijos, cada uno ejecuta una tarea diferente (fecha mensaje más largo, día con más actividad, contacto más frecuente) |
| **B - Paralelismo de Datos**  | N hilos configurables, cada hilo procesa un bloque de mensajes y ejecuta las 3 tareas sobre su partición                     |

### 📊 Métricas que calcula

- 📅 **Fecha del mensaje más largo** (y su longitud)
- 📈 **Día de mayor actividad** (número de mensajes)
- 👤 **Contacto más frecuente** (número de mensajes)
- ⏱️ **Tiempo de ejecución** de cada estrategia
- ✅ **Verificación de consistencia** entre ambas estrategias

### 🚀 Ejecución

```bash
cd HistorialChat
java Main.java <archivo_chat> [numHilosEstrategiaB]
# Ejemplo: java Main.java chat_sample.txt 3
```

---

## 🎯 Actividad 2: SumaVectorial

**Comparativa de rendimiento: Suma vectorial secuencial vs paralela**

Implementa y compara dos versiones de suma de un vector de enteros:

| Versión            | Descripción                                                     |
| ------------------ | --------------------------------------------------------------- |
| **1 - Secuencial** | Monohilo, recorre el vector completo                            |
| **2 - Paralela**   | Multihilo, divide el vector en bloques según el número de hilos |

### 🚀 Ejecución interactiva

```bash
cd SumaVectorial
javac *.java
java SumaVectorial.Main
# Ingresa N (≥10,000,000) y número de hilos
```
